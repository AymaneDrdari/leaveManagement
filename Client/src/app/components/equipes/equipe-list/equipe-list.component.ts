import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatDialog } from '@angular/material/dialog';
import { EquipeService } from '../../../services/equipe.service';
import { AddEditEquipeComponent } from '../add-edit-equipe/add-edit-equipe.component';
import { EquipeDTO } from "../../../models/equipe.model";

@Component({
  selector: 'app-equipe-list',
  templateUrl: './equipe-list.component.html',
  styleUrls: ['./equipe-list.component.css']
})
export class EquipeListComponent implements OnInit, AfterViewInit {
  // Colonnes affichées dans le tableau
  displayedColumns: string[] = ['index', 'nom', 'description', 'couleur', 'actions'];

  // Source de données pour le tableau
  dataSource = new MatTableDataSource<EquipeDTO>();

  showDeleteConfirmation = false;
  selectedEquipeCode: string | null = null;

  // Paginator pour la pagination du tableau
  @ViewChild(MatPaginator) paginator!: MatPaginator;

  constructor(
    private equipeService: EquipeService,
    public dialog: MatDialog
  ) { }

  // Initialisation du composant
  ngOnInit(): void {
    this.loadEquipes(); // Charger les équipes au démarrage
  }

  // Configuration du paginator après l'initialisation de la vue
  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
  }

  // Charger la liste des équipes depuis le backend
  loadEquipes(): void {
    this.equipeService.getAllEquipes().subscribe(response => {
      this.dataSource.data = response; // Mise à jour des données du tableau
    }, error => {
      console.error('Erreur lors du chargement des équipes:', error);
    });
  }

  // Ouvrir le modal pour ajouter ou modifier une équipe
  openAddEditEquipeModal(equipe?: EquipeDTO): void {
    const dialogRef = this.dialog.open(AddEditEquipeComponent, {
      width: '700px',
      data: equipe || null
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadEquipes(); // Rafraîchir la liste des équipes après l'ajout ou la mise à jour
      }
    });
  }

  // Demander confirmation avant de supprimer une équipe
  deleteEquipe(code: string): void {
    this.showDeleteConfirmation = true;
    this.selectedEquipeCode = code;
  }

  // Confirmer la suppression de l'équipe
  confirmDelete(): void {
    if (this.selectedEquipeCode !== null) {
      this.equipeService.deleteEquipe(this.selectedEquipeCode).subscribe(() => {
        this.loadEquipes(); // Rafraîchir la liste des équipes après la suppression
        this.closeDeleteConfirmation();
      }, error => {
        console.error('Erreur lors de la suppression de l\'équipe:', error);
      });
    }
  }

  // Fermer la confirmation de suppression
  closeDeleteConfirmation(): void {
    this.showDeleteConfirmation = false;
    this.selectedEquipeCode = null;
  }

}
