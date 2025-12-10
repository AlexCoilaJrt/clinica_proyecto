import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, BehaviorSubject, throwError, of } from 'rxjs';
import { map, catchError, tap } from 'rxjs/operators';
import { Router } from '@angular/router';

export interface LoginRequest {
    username: string;
    password: string;
}


export interface LoginResponse {
    token: string;
    role: string;
    message: string;
}


export interface Usuario {
    username: string;
    rol: string;
    token: string;
}


@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private apiUrl = 'http://localhost:8080/api/auth'; // Cambia esto según tu API
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
    login(username: string, password: string): Observable<LoginResponse> {
        return this.http.post<LoginResponse>(`${this.apiUrl}/login`, {
            username,
            password
        }).pipe(
            tap(response => {

                // Crear un objeto user basado SOLO en los valores que devuelve tu backend
                const user: Usuario = {
                    username: username,         // Lo enviamos desde frontend
                    rol: response.role,         // Viene del backend
                    token: response.token       // Viene del backend
                };

                // Guardar en localStorage
                localStorage.setItem('currentUser', JSON.stringify(user));
                localStorage.setItem('token', response.token);

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
}
