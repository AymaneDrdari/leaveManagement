import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import {BehaviorSubject, catchError, Observable, throwError} from 'rxjs';
import { environment } from '../../environments/environment';
import { ApiResponse } from '../models/ApiResponse';
import { Holiday } from '../models/holiday';

@Injectable({
  providedIn: 'root'
})
export class HolidayService {
  private apiUrl = `${environment.apiUrl}/jours-feries`;
  private holidaysVisibleSubject = new BehaviorSubject<boolean>(true);
  holidaysVisible$ = this.holidaysVisibleSubject.asObservable();

  private httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' })
  };

  constructor(private http: HttpClient) { }

  // toggleHolidaysVisibility(): void {
  // const currentState = this.holidaysVisibleSubject.value;
  //this.holidaysVisibleSubject.next(!currentState);
  // }
 // toggleHolidaysVisibility(): void {
   // const currentState = this.holidaysVisibleSubject.value;
    //this.holidaysVisibleSubject.next(!currentState);
 // }
  getHolidays(year: number): Observable<ApiResponse<Holiday[]>> {
    return this.http.get<ApiResponse<Holiday[]>>(`${this.apiUrl}?annee=${year}`).pipe(
      catchError(this.handleError)
    );
  }

  getAllHolidays(page: number, size: number): Observable<ApiResponse<Holiday[]>> {
    return this.http.get<ApiResponse<Holiday[]>>(`${this.apiUrl}/page?page=${page}&size=${size}`).pipe(
      catchError(this.handleError)
    );
  }

  createHoliday(holiday: Holiday): Observable<Holiday> {
    console.log('Creating holiday:', holiday);
    return this.http.post<Holiday>(this.apiUrl, holiday, this.httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  updateHoliday(holiday: Holiday): Observable<Holiday> {
    return this.http.put<Holiday>(this.apiUrl, holiday, this.httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  deleteHoliday(id: string): Observable<ApiResponse<null>> {
    return this.http.delete<ApiResponse<null>>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.handleError)
    );
  }

  private handleError(error: any) {
    let errorMessage = 'Une erreur est survenue !';
    if (error.error instanceof ErrorEvent) {
      errorMessage = `Erreur : ${error.error.message}`;
    } else {
      errorMessage = `Code d'erreur : ${error.status}\nMessage : ${error.message}`;
    }
    return throwError(errorMessage);
  }
}
