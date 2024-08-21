import { Component, OnInit, OnDestroy } from '@angular/core';
import { LeaveService } from '../../services/leave.service';
import { EquipeService } from '../../services/equipe.service';
import { EquipeDTO } from '../../models/equipe.model';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-indicators',
  templateUrl: './indicators.component.html',
  styleUrls: ['./indicators.component.css']
})
export class IndicatorsComponent implements OnInit, OnDestroy {
  leaveCounts: { [key: string]: number } = {};
  activeCards: Set<string> = new Set();
  equipes: EquipeDTO[] = [];
  moisSelectionne: number = new Date().getMonth() +1; // Mois actuel
  anneeSelectionnee: number = new Date().getFullYear(); // Année actuelle
  private selectedDateSubscription!: Subscription;

  constructor(
    private leaveService: LeaveService,
    private equipeService: EquipeService,
  ) { }

  ngOnInit(): void {
    this.loadEquipes();

    // Vérifiez si une date est sélectionnée au démarrage
    this.selectedDateSubscription = this.leaveService.selectedDate$.subscribe(date => {
      if (date) {
        const [annee, mois] = this.extractYearMonth(date);
        this.updateMoisAnnee(mois, annee);
      } else {
        // Si aucune date n'est sélectionnée, chargez les congés du mois et de l'année actuels
        this.loadLeaveCounts(this.moisSelectionne, this.anneeSelectionnee);
      }
    });

    // Chargez les congés pour le mois et l'année actuels au démarrage
    this.loadLeaveCounts(this.moisSelectionne, this.anneeSelectionnee);
  }



  ngOnDestroy(): void {
    if (this.selectedDateSubscription) {
      this.selectedDateSubscription.unsubscribe();
    }
  }

  private extractYearMonth(date: string): [number, number] {
    const [year, month] = date.split('-').map(Number);
    return [year, month];
  }

  loadEquipes(): void {
    this.equipeService.getAllEquipes().subscribe({
      next: (equipes) => {
        this.equipes = equipes;
        // Charger les congés pour le mois et l'année actuels après avoir chargé les équipes
        this.loadLeaveCounts(this.moisSelectionne, this.anneeSelectionnee);
        console.log('mois selctionnée:', this.moisSelectionne)
      },
      error: (error) => {
        console.error('Error fetching equipes:', error);
      }
    });
  }


  // Nouvelle méthode pour mettre à jour mois et année
  updateMoisAnnee(mois: number, annee: number): void {
    if (this.moisSelectionne !== mois || this.anneeSelectionnee !== annee) {
      this.moisSelectionne = mois;
      this.anneeSelectionnee = annee;
      this.loadLeaveCounts(mois, annee);
    }
  }


  loadLeaveCounts(mois: number, annee: number): void {
    console.log(`Loading leave counts for month ${mois} and year ${annee}`);
    this.leaveCounts = {};

    this.equipes.forEach(equipe => {
      console.log(`Requesting leave counts for team ${equipe.nom} for month ${mois} and year ${annee}`);
      this.leaveService.getCountCollaborateursEnConge(equipe.nom, mois, annee).subscribe({
        next: (response) => {
          console.log(`Received leave counts for team ${equipe.nom}:`, response);
          if (response.code === 200) {
            const leaveCount = response.data || 0;
            this.leaveCounts[equipe.nom] = leaveCount;
          } else {
            console.error(`Error: ${response.message}`);
          }
        },
        error: (error) => {
          console.error(`Error fetching leave counts for team ${equipe.nom}:`, error);
        }
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
