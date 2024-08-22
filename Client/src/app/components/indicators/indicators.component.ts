import { Component, OnInit, OnDestroy } from '@angular/core';
import { LeaveService } from '../../services/leave.service';
import { EquipeService } from '../../services/equipe.service';
import { EquipeDTO } from '../../models/equipe.model';
import { forkJoin, map, Subscription } from 'rxjs';

@Component({
  selector: 'app-indicators',
  templateUrl: './indicators.component.html',
  styleUrls: ['./indicators.component.css']
})
export class IndicatorsComponent implements OnInit, OnDestroy {
  // Dictionnaire pour stocker les comptes de congé par équipe
  leaveCounts: { [key: string]: number } = {};
  // Ensemble pour suivre les cartes actives (dynamique de l'interface)
  activeCards: Set<string> = new Set();
  // Liste des équipes
  equipes: EquipeDTO[] = [];
  // Abonnement pour surveiller les changements de dates sélectionnées
  private selectedDateSubscription!: Subscription;

  constructor(
    private leaveService: LeaveService,
    private equipeService: EquipeService,
  ) { }

  ngOnInit(): void {
    this.loadEquipes(); // Charger les équipes lors de l'initialisation du composant

    // S'abonner aux changements de dates sélectionnées
    this.selectedDateSubscription = this.leaveService.selectedDate$.subscribe(dateRange => {
      if (dateRange) {
        const { startDate, endDate } = dateRange;
        this.loadLeaveCounts(startDate, endDate); // Charger les comptes de congé lorsque les dates changent
      }
    });
  }

  ngOnDestroy(): void {
    // Se désabonner pour éviter les fuites de mémoire
    if (this.selectedDateSubscription) {
      this.selectedDateSubscription.unsubscribe();
    }
  }

  loadEquipes(): void {
    // Charger toutes les équipes depuis le service
    this.equipeService.getAllEquipes().subscribe({
      next: (equipes) => {
        this.equipes = equipes; // Stocker les équipes dans le tableau

        // Définir les dates de départ et de fin dynamiquement
        const today = new Date();
        const initialStartDate = new Date(today.getFullYear(), today.getMonth(), 1); // Premier jour du mois actuel
        const initialEndDate = new Date(today.getFullYear(), today.getMonth() + 1, 0); // Dernier jour du mois actuel

        // Convertir les dates au format string
        const startDateString = initialStartDate.toISOString().split('T')[0];
        const endDateString = initialEndDate.toISOString().split('T')[0];

        // Mettre à jour le service avec les dates initiales
        this.leaveService.setSelectedDate(startDateString, endDateString);

        // Charger les comptes de congé avec la plage de dates initiale
        this.loadLeaveCounts(startDateString, endDateString);
      },
      error: (error) => {
        // Gérer les erreurs lors de la récupération des équipes
        console.error('Erreur lors de la récupération des équipes:', error);
      }
    });
  }

  handleDatesSet(startDate: Date, endDate: Date): void {
    // Conversion des dates au format string
    const startDateString = startDate.toISOString().split('T')[0];
    const endDateString = endDate.toISOString().split('T')[0];

    // Mettre à jour le service avec les nouvelles dates sélectionnées
    this.leaveService.setSelectedDate(startDateString, endDateString);

    // Charger les comptes de congé avec les nouvelles dates
    this.loadLeaveCounts(startDateString, endDateString);
  }

  loadLeaveCounts(startDate: string, endDate: string): void {
    this.leaveCounts = {}; // Réinitialiser les comptes précédents

    // Créer des demandes pour chaque équipe afin de récupérer le compte de congé
    const leaveCountRequests = this.equipes.map(equipe =>
      this.leaveService.getCountCollaborateursConge(equipe.nom, startDate, endDate).pipe(
        map(response => {
          if (response.code === 200) {
            return { equipe: equipe.nom, leaveCount: response.data || 0 }; // Récupérer le compte de congé
          } else {
            console.error(`Erreur: ${response.message}`);
            return { equipe: equipe.nom, leaveCount: 0 }; // Retourner 0 en cas d'erreur
          }
        })
      )
    );

    // Exécuter toutes les demandes en parallèle et traiter les résultats
    forkJoin(leaveCountRequests).subscribe(results => {
      results.forEach(result => {
        this.leaveCounts[result.equipe] = result.leaveCount; // Mettre à jour leaveCounts
      });
      console.log('Comptes de congés chargés:', this.leaveCounts);
    });
  }

  toggleActiveCard(card: string) {
    // Basculer l'état actif de la carte (afficher/cacher)
    if (this.activeCards.has(card)) {
      this.activeCards.delete(card);
    } else {
      this.activeCards.add(card);
    }
    this.leaveService.setSelectedTeam(card); // Mettre à jour l'équipe sélectionnée dans le service
  }

  isActiveCard(card: string): boolean {
    // Vérifier si la carte est active
    return this.activeCards.has(card);
  }

  isHovered(card: string): boolean {
    // Vérifier si la carte est survolée
    return this.isActiveCard(card);
  }
}
