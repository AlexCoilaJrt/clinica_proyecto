import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface AuditLog {
    id: number;
    userId: number;
    username: string;
    module: string;
    action: string;
    details: string;
    ipAddress: string;
    status: string;
    userAgent: string;
    url: string;
    method: string;
    timestamp: string;
}

@Injectable({
    providedIn: 'root'
})
export class AuditService {
    private apiUrl = 'http://localhost:8080/api/reports/audit';

    constructor(private http: HttpClient) { }

    getLogs(filters: any = {}): Observable<AuditLog[]> {
        let params = new HttpParams();

        if (filters.username) params = params.set('username', filters.username);
        if (filters.action && filters.action !== 'Todas') params = params.set('action', filters.action);
        if (filters.startDate) params = params.set('startDate', filters.startDate);
        if (filters.endDate) params = params.set('endDate', filters.endDate);

        return this.http.get<AuditLog[]>(this.apiUrl, { params });
    }
}
