import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import {BehaviorSubject, Observable, Subject, tap, throwError} from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { Leave } from '../models/leave';
import { ApiResponse } from '../models/ApiResponse';
import {Collaborateur} from "../models/collaborateur";
import {CongeDetailDTO} from "../models/conge-detail-dto.model";

@Injectable({
  providedIn: 'root'
})
export class LeaveService {
  private apiUrl = `${environment.apiUrl}/conges`;

  private selectedTeamSubject = new BehaviorSubject<string | null>(null);
  selectedTeam$ = this.selectedTeamSubject.asObservable();
  private selectedDateSubject = new BehaviorSubject<string | null>(null);
  selectedDate$ = this.selectedDateSubject.asObservable();
  // Nouveau Subject pour notifier les mises à jour de congés
  private leavesUpdatedSubject = new Subject<void>();
  leavesUpdated$ = this.leavesUpdatedSubject.asObservable();

  private httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' })
  };

  constructor(private http: HttpClient) { }


  // Méthode pour mettre à jour la date sélectionnée
  setSelectedDate(date: string): void {
    this.selectedDateSubject.next(date);
  }



  setSelectedTeam(team: string): void {
    this.selectedTeamSubject.next(team);
  }


  // Méthode pour récupérer tous les congés

  getCountCollaborateursEnConge(nomEquipe: string, mois: number, annee: number): Observable<ApiResponse<number>> {
    return this.http.get<ApiResponse<number>>(`${this.apiUrl}/count`, {
      params: { nomEquipe, annee: annee.toString(), mois: mois.toString() } // Convertir en chaîne de caractères
    }).pipe(catchError(this.handleError));
  }

  getCongesByEquipe(nomEquipe: string, annee: number): Observable<ApiResponse<CongeDetailDTO[]>> {
    const params = new HttpParams()
      .set('nomEquipe', nomEquipe)
      .set('annee', annee.toString());
    return this.http.get<ApiResponse<CongeDetailDTO[]>>(`${this.apiUrl}/equipe`, { params });
  }


  getAllLeaves(page: number, size: number): Observable<ApiResponse<Leave[]>> {
    return this.http.get<ApiResponse<Leave[]>>(`${this.apiUrl}/page?page=${page}&size=${size}`).pipe(
      catchError(this.handleError)
    );
  }

  // Méthode pour créer un nouveau congé
  createConge(leave: Leave): Observable<Leave> {
    console.log('Creating leave:', leave);
    return this.http.post<Leave>(this.apiUrl, leave)
      .pipe(catchError(this.handleError),
        tap(() => this.leavesUpdatedSubject.next()) // Notifier après la création d'un congé
      );
  }

  // Méthode pour mettre à jour un congé existant
  updateConge(leave: Leave): Observable<Leave> {
    return this.http.put<Leave>(this.apiUrl, leave, this.httpOptions).pipe(
      catchError(this.handleError),
      tap(() => this.leavesUpdatedSubject.next()) // Notifier après la mise à jour d'un congé
    );
  }


  // Méthode pour récupérer un congé par son ID
  getCongeById(id: string): Observable<Leave> {
    return this.http.get<Leave>(`${this.apiUrl}/${id}`)
      .pipe(catchError(this.handleError));
  }

  // Méthode pour supprimer un congé
  deleteConge(id: string): Observable<ApiResponse<null>> {
    return this.http.delete<ApiResponse<null>>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.handleError)
    );
  }

  // Gestion des erreurs
  private handleError(error: any) {
    let errorMessage = 'Une erreur est survenue !';
    if (error.error instanceof ErrorEvent) {
      // Erreur côté client
      errorMessage = `Erreur : ${error.error.message}`;
    } else {
      // Erreur côté serveur
      errorMessage = `Code d'erreur : ${error.status}\nMessage : ${error.message}`;
    }
    return throwError(errorMessage);
  }
}
