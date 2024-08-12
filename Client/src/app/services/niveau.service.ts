import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Niveau } from '../models/niveau.model';

@Injectable({
  providedIn: 'root'
})
export class NiveauService {
  private apiUrl = `${environment.apiUrl}/niveaux`;

  constructor(private http: HttpClient) {}

  // Ajouter un nouveau niveau
  addNiveau(niveau: Niveau): Observable<any> {
    console.log("niveau from servece : ",niveau)
    return this.http.post<any>(this.apiUrl, niveau, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    });
  }

  // Récupérer tous les niveaux
  getAllNiveaux(): Observable<any> {
    return this.http.get<any>(this.apiUrl);
  }

  // Récupérer un niveau par son code
  getNiveauById(code: string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${code}`);
  }

  // Mettre à jour un niveau
  updateNiveau(code: string, niveau: Niveau): Observable<any> {
    console.log("update from server : " ,code,"avec",niveau)
    return this.http.put<any>(`${this.apiUrl}/${code}`, niveau, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    });
  }

  // Supprimer un niveau
  deleteNiveau(code: string): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/${code}`);
  }

}
