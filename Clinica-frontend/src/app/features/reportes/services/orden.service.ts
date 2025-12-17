import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Orden, OrdenFilter } from '../models/orden.model';

@Injectable({
    providedIn: 'root'
})
export class OrdenService {
    private apiUrl = 'http://localhost:8080/api/ordenes';

    constructor(private http: HttpClient) { }

    /**
     * Obtiene el token del localStorage para las peticiones
     */
    private getHeaders(): HttpHeaders {
        const token = localStorage.getItem('token');
        return new HttpHeaders({
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        });
    }

    /**
     * Filtra órdenes según criterios
     */
    filtrarOrdenes(filtro: OrdenFilter): Observable<Orden[]> {
        return this.http.post<Orden[]>(`${this.apiUrl}/filtrar`, filtro, {
            headers: this.getHeaders()
        });
    }

    /**
     * Obtiene todas las órdenes (según rol del usuario)
     */
    obtenerTodasLasOrdenes(): Observable<Orden[]> {
        return this.http.get<Orden[]>(this.apiUrl, {
            headers: this.getHeaders()
        });
    }

    /**
     * Obtiene una orden por ID
     */
    obtenerOrdenPorId(id: number): Observable<Orden> {
        return this.http.get<Orden>(`${this.apiUrl}/${id}`, {
            headers: this.getHeaders()
        });
    }

    /**
     * Actualiza el estado de una orden
     */
    actualizarEstado(id: number, estado: string): Observable<Orden> {
        return this.http.put<Orden>(`${this.apiUrl}/${id}/estado`, { estado }, {
            headers: this.getHeaders()
        });
    }
}
