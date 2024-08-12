import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Collaborateur} from "../models/collaborateur";
import {ApiResponse} from "../models/ApiResponse";
@Injectable({
  providedIn: 'root'
})
export class CollaboratorService {
  private apiUrl = `${environment.apiUrl}/collaborateurs`;

  constructor(private http: HttpClient) {}

  getCollaborateurs(): Observable<ApiResponse<Collaborateur[]>> {
    return this.http.get<ApiResponse<Collaborateur[]>>(this.apiUrl);
  }
}
