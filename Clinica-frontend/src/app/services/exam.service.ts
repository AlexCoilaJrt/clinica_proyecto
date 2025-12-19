import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Exam, ExamRequest, SubExam, SubExamRequest, ExamType, PageResponse } from '../models/exam.model';
import { ApiResponse } from '../features/auth/services/auth.service';


@Injectable({
    providedIn: 'root'
})
export class ExamService {
    private apiUrl = `http://localhost:8080/api/exams`;
    private subExamApiUrl = `http://localhost:8080/api/sub-exams`;

    constructor(private http: HttpClient) { }

    // ========== EXÁMENES ==========

    createExam(request: ExamRequest): Observable<ApiResponse<Exam>> {
        return this.http.post<ApiResponse<Exam>>(this.apiUrl, request);
    }

    updateExam(id: number, request: ExamRequest): Observable<ApiResponse<Exam>> {
        return this.http.put<ApiResponse<Exam>>(`${this.apiUrl}/${id}`, request);
    }

    getExamById(id: number): Observable<ApiResponse<Exam>> {
        return this.http.get<ApiResponse<Exam>>(`${this.apiUrl}/${id}`);
    }

    getAllExams(page: number = 0, size: number = 10, sort: string = 'nombre', direction: string = 'asc'): Observable<ApiResponse<PageResponse<Exam>>> {
        const params = new HttpParams()
            .set('page', page.toString())
            .set('size', size.toString())
            .set('sort', sort)
            .set('direction', direction);

        return this.http.get<ApiResponse<PageResponse<Exam>>>(this.apiUrl, { params });
    }

    getExamsByArea(areaId: number, page: number = 0, size: number = 10): Observable<ApiResponse<PageResponse<Exam>>> {
        const params = new HttpParams()
            .set('page', page.toString())
            .set('size', size.toString());

        return this.http.get<ApiResponse<PageResponse<Exam>>>(`${this.apiUrl}/area/${areaId}`, { params });
    }

    searchExams(query: string, page: number = 0, size: number = 10): Observable<ApiResponse<PageResponse<Exam>>> {
        const params = new HttpParams()
            .set('q', query)
            .set('page', page.toString())
            .set('size', size.toString());

        return this.http.get<ApiResponse<PageResponse<Exam>>>(`${this.apiUrl}/search`, { params });
    }

    getExamsByTipo(tipoExamenId: number, page: number = 0, size: number = 10): Observable<ApiResponse<PageResponse<Exam>>> {
        const params = new HttpParams()
            .set('page', page.toString())
            .set('size', size.toString());

        return this.http.get<ApiResponse<PageResponse<Exam>>>(`${this.apiUrl}/tipo/${tipoExamenId}`, { params });
    }

    getPerfiles(): Observable<ApiResponse<Exam[]>> {
        return this.http.get<ApiResponse<Exam[]>>(`${this.apiUrl}/perfiles`);
    }

    getAllActiveExams(): Observable<ApiResponse<Exam[]>> {
        return this.http.get<ApiResponse<Exam[]>>(`${this.apiUrl}/active`);
    }

    deleteExam(id: number): Observable<ApiResponse<void>> {
        return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${id}`);
    }

    toggleExamStatus(id: number, active: boolean): Observable<ApiResponse<void>> {
        return this.http.patch<ApiResponse<void>>(`${this.apiUrl}/${id}/status?active=${active}`, {});
    }

    // ========== SUBEXÁMENES ==========

    createSubExam(request: SubExamRequest): Observable<ApiResponse<SubExam>> {
        return this.http.post<ApiResponse<SubExam>>(this.subExamApiUrl, request);
    }

    updateSubExam(id: number, request: SubExamRequest): Observable<ApiResponse<SubExam>> {
        return this.http.put<ApiResponse<SubExam>>(`${this.subExamApiUrl}/${id}`, request);
    }

    getSubExamById(id: number): Observable<ApiResponse<SubExam>> {
        return this.http.get<ApiResponse<SubExam>>(`${this.subExamApiUrl}/${id}`);
    }

    getSubExamsByExam(examenId: number): Observable<ApiResponse<SubExam[]>> {
        return this.http.get<ApiResponse<SubExam[]>>(`${this.subExamApiUrl}/exam/${examenId}`);
    }

    deleteSubExam(id: number): Observable<ApiResponse<void>> {
        return this.http.delete<ApiResponse<void>>(`${this.subExamApiUrl}/${id}`);
    }

    toggleSubExamStatus(id: number, active: boolean): Observable<ApiResponse<void>> {
        return this.http.patch<ApiResponse<void>>(`${this.subExamApiUrl}/${id}/status?active=${active}`, {});
    }

    reorderSubExams(examenId: number, subExamIds: number[]): Observable<ApiResponse<void>> {
        return this.http.put<ApiResponse<void>>(`${this.subExamApiUrl}/exam/${examenId}/reorder`, subExamIds);
    }
}