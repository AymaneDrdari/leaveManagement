// HolidayListComponent.ts

import { AddEditHolidayComponent } from "../add-edit-holiday/add-edit-holiday.component";
import { Holiday } from "../../../models/holiday";
import { MatDialog } from "@angular/material/dialog";
import { HolidayService } from "../../../services/holiday.service";
import { AfterViewInit, Component, OnInit, ViewChild } from "@angular/core";
import { MatTableDataSource } from "@angular/material/table";
import {MatPaginator, PageEvent} from "@angular/material/paginator";

@Component({
  selector: 'app-holiday-list',
  templateUrl: './holiday-list.component.html',
  styleUrls: ['./holiday-list.component.css']
})
export class HolidayListComponent implements OnInit, AfterViewInit {
  displayedColumns: string[] = ['index','date_debut', 'date_fin', 'description', 'actions'];
  dataSource = new MatTableDataSource<Holiday>();
  showDeleteConfirmation = false;
  selectedHolidayId: string | null = null;
  errorMessage: string | null = null;
  holidays: Holiday[] = [];
  pageSize = 6; // Taille de page par défaut
  currentPage = 0; // Page actuelle
  totalItems = 0; // Nombre total d'éléments

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  //Initialisation du Composant et Chargement des Jours Fériés
  constructor(
    private holidayService: HolidayService,
    public dialog: MatDialog
  ) { }

  ngOnInit(): void {
    this.loadHolidays();
  }

  ngAfterViewInit(): void {
    // Assigner le paginator au dataSource après l'initialisation de la vue
    if (this.paginator) {
      this.dataSource.paginator = this.paginator;
    }
  }

  //Charge les jours fériés depuis le backend et
  // met à jour le tableau de données.
  loadHolidays(page: number = 0): void {
    this.holidayService.getAllHolidays(page, this.pageSize).subscribe(response => {
      console.log('Received holidays:', response.data); // Vérifiez les données ici
      this.holidays = response.data;
      this.totalItems = response.totalElements;
      this.currentPage = page;
      this.dataSource.data = this.holidays; // Mise à jour des données du tableau
    }, error => {
      console.error('Error loading holidays:', error); // Gestion des erreurs
    });
  }

  //Ouvre un modal pour ajouter ou éditer un jour férié.
  //Recharge la liste des jours fériés après la fermeture du modal
  // si des modifications ont été effectuées.
  openAddEditHolidayModal(holiday?: Holiday): void {
    const dialogRef = this.dialog.open(AddEditHolidayComponent, {
      width: '700px',
      data: holiday || {}
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadHolidays(); // Rafraîchissement des données après la fermeture du modal
      }
    });
  }

  //Active la confirmation de suppression pour un jour férié sélectionné
  deleteHoliday(id: number): void {
    this.showDeleteConfirmation = true;
    this.selectedHolidayId = id.toString();
  }

  //Supprime le jour férié sélectionné et recharge la liste
  confirmDelete(): void {
    console.log('selected leave ID: ', this.selectedHolidayId);
    if (this.selectedHolidayId !== null) {

      this.holidayService.deleteHoliday(this.selectedHolidayId).subscribe({
        next: () => {
          this.loadHolidays();
          this.closeDeleteConfirmation();
        },
        error: (error: string) => {
          this.errorMessage = error;
        }
      });
    }
  }

  closeDeleteConfirmation(): void {
    this.showDeleteConfirmation = false;
    this.selectedHolidayId = null;
  }

  //Gère les changements de page dans le tableau de jours fériés.
  onPageChange(page: number): void {
    if (page < 0 || page >= Math.ceil(this.totalItems / this.pageSize)) return;
    this.currentPage = page;
    this.loadHolidays(page);
  }

  get totalPages(): number {
    return Math.max(Math.ceil(this.totalItems / this.pageSize), 1);
  }

  // Génère un tableau pour la pagination.
  paginationArray(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i);
  }


}
