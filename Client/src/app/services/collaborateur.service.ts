import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { Collaborateur } from '../models/collaborateur';
import { ApiResponse } from '../models/ApiResponse';
import { EquipeDTO } from "../models/equipe.model";
import { Niveau } from "../models/niveau.model";

@Injectable({
  providedIn: 'root'
})
export class CollaborateurService {
  private apiUrl = `${environment.apiUrl}/collaborateurs`;

  private selectedTeamSubject = new BehaviorSubject<string | null>(null);
  selectedTeam$ = this.selectedTeamSubject.asObservable();

  private httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' })
  };

  constructor(private http: HttpClient) {}

  setSelectedTeam(team: string): void {
    this.selectedTeamSubject.next(team);
  }

  createCollaborateur(collaborateur: Collaborateur): Observable<ApiResponse<Collaborateur>> {
    return this.http.post<ApiResponse<Collaborateur>>(this.apiUrl, collaborateur, this.httpOptions)
      .pipe(catchError(this.handleError));
  }

  updateCollaborateur(collaborateur: Collaborateur): Observable<ApiResponse<Collaborateur>> {
    return this.http.put<ApiResponse<Collaborateur>>(this.apiUrl, collaborateur, this.httpOptions)
      .pipe(catchError(this.handleError));
  }

  deleteCollaborateur(id: string): Observable<ApiResponse<null>> {
    return this.http.delete<ApiResponse<null>>(`${this.apiUrl}/${id}`, this.httpOptions)
      .pipe(catchError(this.handleError));
  }

  findCollaborateurById(id: string): Observable<ApiResponse<Collaborateur>> {
    return this.http.get<ApiResponse<Collaborateur>>(`${this.apiUrl}/${id}`)
      .pipe(catchError(this.handleError));
  }

  findAllCollaborateurs(page: number, size: number): Observable<ApiResponse<Collaborateur[]>> {
    return this.http.get<ApiResponse<Collaborateur[]>>(`${this.apiUrl}?page=${page}&size=${size}`)
      .pipe(catchError(this.handleError));
  }

  getCollaborateursPage(page: number, size: number): Observable<ApiResponse<Collaborateur[]>> {
    return this.http.get<ApiResponse<Collaborateur[]>>(`${this.apiUrl}?page=${page}&size=${size}`)
      .pipe(catchError(this.handleError));
  }

  getAllEquipes(): Observable<ApiResponse<EquipeDTO[]>> {
    return this.http.get<ApiResponse<EquipeDTO[]>>(`${environment.apiUrl}/equipes`)
      .pipe(catchError(this.handleError));
  }

  getAllNiveaux(): Observable<ApiResponse<Niveau[]>> {
    return this.http.get<ApiResponse<Niveau[]>>(`${environment.apiUrl}/niveaux`)
      .pipe(catchError(this.handleError));
  }

  private handleError(error: any): Observable<never> {
    let errorMessage = 'Une erreur est survenue !';
    if (error.error instanceof ErrorEvent) {
      errorMessage = `Erreur : ${error.error.message}`;
    } else {
      errorMessage = `Code d'erreur : ${error.status}\nMessage : ${error.message}`;
    }
    return throwError(errorMessage);
  }
}
