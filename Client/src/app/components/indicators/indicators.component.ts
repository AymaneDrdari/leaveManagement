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
    const teams = ['royal', 'gold', 'mauve', 'blue'];  // Define your teams

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
