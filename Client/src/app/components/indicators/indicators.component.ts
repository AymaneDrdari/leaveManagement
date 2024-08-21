import { Component, OnInit } from '@angular/core';
import { LeaveService } from '../../services/leave.service';
import { EquipeService } from '../../services/equipe.service'; // Importer le service d'équipe
import { EquipeDTO } from '../../models/equipe.model'; // Importer le modèle d'équipe

@Component({
  selector: 'app-indicators',
  templateUrl: './indicators.component.html',
  styleUrls: ['./indicators.component.css']
})
export class IndicatorsComponent implements OnInit {
  leaveCounts: { [key: string]: number } = {};
  activeCards: Set<string> = new Set();
  equipes: EquipeDTO[] = []; // Liste des équipes récupérées

  constructor(
    private leaveService: LeaveService,
    private equipeService: EquipeService // Injecter le service d'équipe
  ) { }

  ngOnInit(): void {
    this.loadEquipes(); // Charger les équipes au démarrage
  }

  // Méthode pour charger les équipes depuis le service
  loadEquipes(): void {
<<<<<<< HEAD
    this.equipeService.getAllEquipes().subscribe(equipes => {
      this.equipes = equipes;
      this.loadLeaveCounts(); // Charger les congés après avoir récupéré les équipes
    }, error => {
      console.error('Error fetching equipes:', error);
=======
    this.equipeService.getAllEquipes().subscribe({
      next: (equipes) => {
        this.equipes = equipes;
        this.loadLeaveCounts();
      },
      error: (error) => {
        console.error('Error fetching equipes:', error);
      }
>>>>>>> 9e4dd60a9398335148b55cbf2009edcf8fb4d507
    });
  }

  // Charger les compteurs de congés
  loadLeaveCounts(): void {
    this.equipes.forEach(equipe => {
<<<<<<< HEAD
      this.leaveService.getCountCollaborateursEnConge(equipe.nom).subscribe(response => {
=======
      this.leaveService.getCountCollaborateursEnConge(equipe.nom)
        .subscribe(response => {
>>>>>>> 9e4dd60a9398335148b55cbf2009edcf8fb4d507
        this.leaveCounts[equipe.nom] = response.data || 0;
      }, error => {
        console.error(`Error fetching leave count for team ${equipe.nom}:`, error);
        this.leaveCounts[equipe.nom] = 0;
      });
    });
  }

  toggleActiveCard(card: string) {
    if (this.activeCards.has(card)) {
      this.activeCards.delete(card);
    } else {
      this.activeCards.add(card);
    }
    this.leaveService.setSelectedTeam(card);
  }

  isActiveCard(card: string): boolean {
    return this.activeCards.has(card);
  }

  isHovered(card: string): boolean {
    return this.isActiveCard(card);
  }
}
