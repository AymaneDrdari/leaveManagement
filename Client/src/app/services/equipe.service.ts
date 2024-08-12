import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { EquipeDTO } from "../models/equipe.model";

@Injectable({
  providedIn: 'root'
})
export class EquipeService {
  private apiUrl = `${environment.apiUrl}/equipes`; // Assurez-vous que `apiUrl` est défini dans votre `environment.ts`

  constructor(private http: HttpClient) { }

  // Méthode pour ajouter une nouvelle équipe
  addEquipe(equipe: EquipeDTO): Observable<EquipeDTO> {
    return this.http.post<EquipeDTO>(this.apiUrl, equipe);
  }

  // Méthode pour récupérer toutes les équipes
  getAllEquipes(): Observable<EquipeDTO[]> {
    return this.http.get<{ message: string, code: number, data: EquipeDTO[] }>(this.apiUrl)
      .pipe(map(response => response.data));
  }

  // Méthode pour récupérer une équipe par son code
  getEquipeById(code: string): Observable<EquipeDTO> {
    return this.http.get<EquipeDTO>(`${this.apiUrl}/${code}`);
  }

  // Méthode pour mettre à jour une équipe
  updateEquipe(code: string, equipe: EquipeDTO): Observable<EquipeDTO> {
    return this.http.put<EquipeDTO>(`${this.apiUrl}/${code}`, equipe);
  }

  // Méthode pour supprimer une équipe
  deleteEquipe(code: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${code}`);
  }
}
