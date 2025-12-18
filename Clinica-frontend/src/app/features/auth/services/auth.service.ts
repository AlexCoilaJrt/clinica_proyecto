import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, BehaviorSubject, throwError, of } from 'rxjs';
import { map, catchError, tap, finalize } from 'rxjs/operators';
import { Router } from '@angular/router';

export interface LoginRequest {
    username: string;
    password: string;
}
export interface TokenInfo {
    timeRemainingMs: number;
    timeRemainingSeconds: number;
    expirationTimeMs: number;
    isExpired: boolean;
}
// Estructura de respuesta envuelta por el backend
export interface ApiResponse<T> {
    success: boolean;
    message: string;
    data: T;
    timestamp: string;
}

// Datos reales del login dentro de 'data'
export interface LoginData {
    token: string;
    type: string;  // "Bearer"
    userId: number;
    username: string;
    email: string;
    firstName: string;
    lastName: string;
    sexo: string;
    roles: string[];  // Array of role names
    permissions: string[];  // Array of permission names
}


export interface Usuario {
    username: string;
    firstName: string;
    lastName: string;
    rol: string;
    token: string;
    sexo: string;
}


@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private apiUrl = 'http://localhost:8080/api/auth';
    private currentUserSubject: BehaviorSubject<Usuario | null>;
    public currentUser: Observable<Usuario | null>;

    constructor(
        private http: HttpClient,
        private router: Router
    ) {
        const userStorage = localStorage.getItem('currentUser');
        this.currentUserSubject = new BehaviorSubject<Usuario | null>(
            userStorage ? JSON.parse(userStorage) : null
        );
        this.currentUser = this.currentUserSubject.asObservable();
    }

    /**
     * Obtiene el valor actual del usuario
     */
    public get currentUserValue(): Usuario | null {
        return this.currentUserSubject.value;
    }

    /**
     * Verifica si el usuario está autenticado
     */
    public get isAuthenticated(): boolean {
        return !!this.currentUserValue && !!this.currentUserValue.token;
    }

    /**
     * Realiza el login del usuario
     */
    login(username: string, password: string): Observable<LoginData> {
        return this.http.post<ApiResponse<LoginData>>(`${this.apiUrl}/login`, {
            username,
            password
        }).pipe(
            map(apiResponse => apiResponse.data),
            tap(loginData => {
                console.log('Backend login data:', loginData);

                // Extraer el rol principal (el primero del array, o 'PACIENTE' por defecto)
                const rolPrincipal = loginData.roles && loginData.roles.length > 0
                    ? loginData.roles[0]
                    : 'PACIENTE';

                // Crear un objeto user basado en los valores que devuelve el backend
                const user: Usuario = {
                    username: loginData.username,
                    rol: rolPrincipal,
                    token: loginData.token,
                    sexo: loginData.sexo,
                    firstName: loginData.firstName,
                    lastName: loginData.lastName
                };

                // Guardar en localStorage
                localStorage.setItem('currentUser', JSON.stringify(user));
                localStorage.setItem('token', loginData.token);

                // Guardar datos adicionales solo si están disponibles
                if (loginData.userId) {
                    localStorage.setItem('userId', loginData.userId.toString());
                }
                if (loginData.firstName && loginData.lastName) {
                    localStorage.setItem('userFullName', `${loginData.firstName} ${loginData.lastName}`);
                }

                // Actualizar observable
                this.currentUserSubject.next(user);
            }),
            catchError(error => {
                console.error('Error en login:', error);
                return throwError(() => error);
            })
        );
    }


    /**
     * Cierra la sesión del usuario
     */
    logout(): void {
        const token = localStorage.getItem('token');

        if (token) {
            const headers = new HttpHeaders({
                'Authorization': `Bearer ${token}`
            });

            // Llamar al backend y esperar respuesta (o timeout corto) antes de limpiar
            this.http.post(`${this.apiUrl}/logout`, {}, { headers })
                .pipe(
                    // Timeout de 1 segundo para no bloquear al usuario si el servidor está lento
                    // finalize se ejecuta tanto en éxito como en error
                    finalize(() => this.doLogoutCleanup())
                )
                .subscribe({
                    next: () => console.log('Logout registrado exitosamente'),
                    error: (err) => console.error('Error en logout:', err)
                });
        } else {
            // Si no hay token, limpiar directamente
            this.doLogoutCleanup();
        }
    }

    private doLogoutCleanup(): void {
        // Remover usuario del localStorage
        localStorage.removeItem('currentUser');
        localStorage.removeItem('token');
        localStorage.removeItem('usuario');
        localStorage.removeItem('sucursal');

        // Actualizar el BehaviorSubject
        this.currentUserSubject.next(null);

        // Redirigir al login
        this.router.navigate(['/login']);
    }

    /**
     * Actualiza la sucursal del usuario
     */
    actualizarSucursal(sucursal: string): Observable<any> {
        const usuario = this.currentUserValue;
        if (!usuario) {
            return throwError(() => new Error('Usuario no autenticado'));
        }

        return this.http.put(`${this.apiUrl}/sucursal`, {
            sucursal
        }).pipe(
            tap(() => {
                // Actualizar usuario en localStorage
                localStorage.setItem('currentUser', JSON.stringify(usuario));
                localStorage.setItem('sucursal', sucursal);
                this.currentUserSubject.next(usuario);
            }),
            catchError(error => {
                console.error('Error al actualizar sucursal:', error);
                return throwError(() => error);
            })
        );
    }

    /**
     * Cambia la contraseña del usuario
     */
    cambiarContraseña(contraseñaActual: string, contraseñaNueva: string): Observable<any> {
        return this.http.put(`${this.apiUrl}/cambiar-contraseña`, {
            contraseñaActual,
            contraseñaNueva
        }).pipe(
            catchError(error => {
                console.error('Error al cambiar contraseña:', error);
                return throwError(() => error);
            })
        );
    }

    verificarToken(): Observable<boolean> {
        const token = localStorage.getItem('token');
        if (!token) {
            return of(false);
        }

        return this.http.get<{ valid: boolean }>(`${this.apiUrl}/verificar-token`).pipe(
            map(response => response.valid),
            catchError(() => {
                this.logout();
                return of(false);
            })
        );
    }


    getToken(): string | null {
        return localStorage.getItem('token');
    }
    getTokenInfo(): Observable<ApiResponse<TokenInfo>> {
        const token = localStorage.getItem('token');
        const headers = new HttpHeaders({
            'Authorization': `Bearer ${token}`
        });

        return this.http.get<ApiResponse<TokenInfo>>(`${this.apiUrl}/token-info`, { headers });
    }
}
