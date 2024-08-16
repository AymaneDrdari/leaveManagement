import { Component, OnInit } from '@angular/core';
import { LeaveService } from '../../services/leave.service';  // Ensure LeaveService is correctly imported

@Component({
  selector: 'app-indicators',
  templateUrl: './indicators.component.html',
  styleUrls: ['./indicators.component.css']
})
export class IndicatorsComponent implements OnInit {
  leaveCounts: { [key: string]: number } = {};  // Store leave counts for each team
  activeCards: Set<string> = new Set();  // Use a Set to track active cards
  teams:any[]=[{id:1,nom:'equipe1', couleur:'red'},
    {id:2,nom:'equipe2', couleur:'green'}];

  constructor(private leaveService: LeaveService) { }

  // Toggle the active state of a card
  toggleActiveCard(card: string) {
    if (this.activeCards.has(card)) {
      this.activeCards.delete(card);
    } else {
      this.activeCards.add(card);
    }
    this.leaveService.setSelectedTeam(card);  // Notify service of selected team
  }

  // Check if a card is active
  isActiveCard(card: string): boolean {
    return this.activeCards.has(card);
  }

  // Check if a card is hovered (or active)
  isHovered(card: string): boolean {
    return this.isActiveCard(card);
  }

  // Lifecycle hook - On component initialization
  ngOnInit(): void {
    this.loadLeaveCounts();  // Load leave counts when the component initializes
  }

  // Method to load leave counts from the service
  loadLeaveCounts(): void {
    const teams = ['royal', 'gold', 'mauve', 'blue','1','2'];  // Define your teams

    teams.forEach(team => {
      this.leaveService.getCountCollaborateursEnConge(team).subscribe(response => {
        this.leaveCounts[team] = response.data || 0;  // Store the count in the leaveCounts object
      }, error => {
        console.error(`Error fetching leave count for team ${team}:`, error);
        this.leaveCounts[team] = 0;  // In case of an error, set the count to 0
      });
    });
  }
}

// import { Component, OnInit } from '@angular/core';
// import { LeaveService } from '../../services/leave.service';
// import { EquipeService } from '../../services/equipe.service'; // Importer le service d'équipe
// import { EquipeDTO } from '../../models/equipe.model'; // Importer le modèle d'équipe
//
// @Component({
//   selector: 'app-indicators',
//   templateUrl: './indicators.component.html',
//   styleUrls: ['./indicators.component.css']
// })
// export class IndicatorsComponent implements OnInit {
//   leaveCounts: { [key: string]: number } = {};
//   activeCards: Set<string> = new Set();
//   equipes: EquipeDTO[] = []; // Liste des équipes récupérées
//
//   constructor(
//     private leaveService: LeaveService,
//     private equipeService: EquipeService // Injecter le service d'équipe
//   ) { }
//
//   ngOnInit(): void {
//     this.loadEquipes(); // Charger les équipes au démarrage
//   }
//
//   // Méthode pour charger les équipes depuis le service
//   loadEquipes(): void {
//     this.equipeService.getAllEquipes().subscribe({
//       next: (equipes) => {
//         this.equipes = equipes;
//         this.loadLeaveCounts();
//       },
//       error: (error) => {
//         console.error('Error fetching equipes:', error);
//       }
//     });
//   }
//
//   // Charger les compteurs de congés
//   loadLeaveCounts(): void {
//     this.equipes.forEach(equipe => {
//       this.leaveService.getCountCollaborateursEnConge(equipe.nom)
//         .subscribe(response => {
//         this.leaveCounts[equipe.nom] = response.data || 0;
//       }, error => {
//         console.error(`Error fetching leave count for team ${equipe.nom}:`, error);
//         this.leaveCounts[equipe.nom] = 0;
//       });
//     });
//   }
//
//   toggleActiveCard(card: string) {
//     if (this.activeCards.has(card)) {
//       this.activeCards.delete(card);
//     } else {
//       this.activeCards.add(card);
//     }
//     this.leaveService.setSelectedTeam(card);
//   }
//
//   isActiveCard(card: string): boolean {
//     return this.activeCards.has(card);
//   }
//
//   isHovered(card: string): boolean {
//     return this.isActiveCard(card);
//   }
// }
