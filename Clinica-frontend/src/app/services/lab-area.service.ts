import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LabArea, LabAreaRequest, PageResponse } from '../models/lab-area.model';
import { ApiResponse } from '../features/auth/services/auth.service';


@Injectable({
    providedIn: 'root'
})
export class LabAreaService {
    private apiUrl = `http://localhost:8080/api/lab-areas`;

    constructor(private http: HttpClient) { }

    create(request: LabAreaRequest): Observable<ApiResponse<LabArea>> {
        return this.http.post<ApiResponse<LabArea>>(this.apiUrl, request);
    }

    update(id: number, request: LabAreaRequest): Observable<ApiResponse<LabArea>> {
        return this.http.put<ApiResponse<LabArea>>(`${this.apiUrl}/${id}`, request);
    }

    getById(id: number): Observable<ApiResponse<LabArea>> {
        return this.http.get<ApiResponse<LabArea>>(`${this.apiUrl}/${id}`);
    }

    getAll(page: number = 0, size: number = 10, sort: string = 'id', direction: string = 'asc'): Observable<ApiResponse<PageResponse<LabArea>>> {
        const params = new HttpParams()
            .set('page', page.toString())
            .set('size', size.toString())
            .set('sort', sort)
            .set('direction', direction);

        return this.http.get<ApiResponse<PageResponse<LabArea>>>(this.apiUrl, { params });
    }

    search(query: string, page: number = 0, size: number = 10): Observable<ApiResponse<PageResponse<LabArea>>> {
        const params = new HttpParams()
            .set('q', query)
            .set('page', page.toString())
            .set('size', size.toString());

        return this.http.get<ApiResponse<PageResponse<LabArea>>>(`${this.apiUrl}/search`, { params });
    }

    getAllActive(): Observable<ApiResponse<LabArea[]>> {
        return this.http.get<ApiResponse<LabArea[]>>(`${this.apiUrl}/active`);
    }

    delete(id: number): Observable<ApiResponse<void>> {
        return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${id}`);
    }

    toggleStatus(id: number, active: boolean): Observable<ApiResponse<void>> {
        return this.http.patch<ApiResponse<void>>(`${this.apiUrl}/${id}/status?active=${active}`, {});
    }
}