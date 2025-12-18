import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { ApiResponse } from '../../features/auth/services/auth.service';

export interface ChangePasswordRequest {
    oldPassword: string;
    newPassword: string;
    confirmPassword: string;
}

@Injectable({
    providedIn: 'root'
})
export class UserService {
    private apiUrl = 'http://localhost:8080/api/v1/users';

    constructor(private http: HttpClient) { }

    /**
     * Cambia la contraseña de un usuario
     */
    changePassword(userId: number, request: ChangePasswordRequest): Observable<boolean> {
        const token = localStorage.getItem('token');
        if (!token) {
            return throwError(() => new Error('No existe token de autenticación'));
        }

        const headers = new HttpHeaders({
            'Authorization': `Bearer ${token}`
        });

        return this.http.post<ApiResponse<void>>(`${this.apiUrl}/${userId}/change-password`, request, { headers })
            .pipe(
                map(response => response.success),
                catchError(error => {
                    console.error('Error cambiando contraseña:', error);
                    return throwError(() => error);
                })
            );
    }
}
