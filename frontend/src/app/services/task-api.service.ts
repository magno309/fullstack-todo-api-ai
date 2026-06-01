import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, catchError, map, throwError } from 'rxjs';
import { ApiErrorResponse, ApiResponse } from '../models/api.models';
import { CreateTaskRequest, Task, UpdateTaskRequest } from '../models/task.models';

@Injectable({
  providedIn: 'root'
})
export class TaskApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8080/api/tasks';

  getTasks(): Observable<Task[]> {
    return this.http.get<ApiResponse<Task[]>>(this.baseUrl).pipe(
      map((response) => response.data),
      catchError((error) => this.handleError(error))
    );
  }

  createTask(payload: CreateTaskRequest): Observable<Task> {
    return this.http.post<ApiResponse<Task>>(this.baseUrl, payload).pipe(
      map((response) => response.data),
      catchError((error) => this.handleError(error))
    );
  }

  updateTask(id: number, payload: UpdateTaskRequest): Observable<Task> {
    return this.http.put<ApiResponse<Task>>(`${this.baseUrl}/${id}`, payload).pipe(
      map((response) => response.data),
      catchError((error) => this.handleError(error))
    );
  }

  deleteTask(id: number): Observable<void> {
    return this.http.delete<ApiResponse<null>>(`${this.baseUrl}/${id}`).pipe(
      map(() => void 0),
      catchError((error) => this.handleError(error))
    );
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    if (error.error) {
      const apiError = error.error as ApiErrorResponse;

      if (apiError.validationDetails && Object.keys(apiError.validationDetails).length > 0) {
        const messages = Object.values(apiError.validationDetails).join(', ');
        return throwError(() => new Error(messages));
      }

      if (apiError.message) {
        return throwError(() => new Error(apiError.message));
      }
    }

    return throwError(() => new Error('Unexpected error while calling API'));
  }
}
