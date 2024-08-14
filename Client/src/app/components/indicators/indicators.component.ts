import { Component, OnInit } from '@angular/core';
import { LeaveService } from '../../services/leave.service';

@Component({
  selector: 'app-indicators',
  templateUrl: './indicators.component.html',
  styleUrls: ['./indicators.component.css']
})
export class IndicatorsComponent implements OnInit {
  leaveCounts: { [key: string]: number } = {};  // Stocker le nombre de collaborateurs en congé par équipe

  constructor(private leaveService: LeaveService) { }

  activeCard: string = '';

  setActiveCard(card: string) {
    if (this.activeCard === card) {
      this.activeCard = '';
    } else {
      this.activeCard = card;
      this.leaveService.setSelectedTeam(card);  // Déclenche la récupération des congés pour l'équipe sélectionnée
    }
  }

  isHovered(card: string): boolean {
    return this.activeCard === card;
  }

  ngOnInit(): void {
    this.loadLeaveCounts();  // Charger le nombre de collaborateurs en congé au démarrage
  }

  // Charger le nombre de collaborateurs en congé pour chaque équipe
  loadLeaveCounts(): void {
    const teams = ['royal', 'gold', 'mauve', 'blue'];  // Liste des équipes

    teams.forEach(team => {
      this.leaveService.getCountCollaborateursEnConge(team).subscribe(response => {
        this.leaveCounts[team] = response.data || 0;  // Stocker le nombre de congés dans la carte correspondante
      }, error => {
        console.error(`Error fetching leave count for team ${team}:`, error);
        this.leaveCounts[team] = 0;  // En cas d'erreur, afficher 0
      });
    });
  }
}
