import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatDialog } from '@angular/material/dialog';
import { EquipeService } from '../../../services/equipe.service';
import { AddEditEquipeComponent } from '../add-edit-equipe/add-edit-equipe.component'; // Assurez-vous que ce composant existe
import { EquipeDTO } from "../../../models/equipe.model";

@Component({
  selector: 'app-equipe-list',
  templateUrl: './equipe-list.component.html',
  styleUrls: ['./equipe-list.component.css']
})
export class EquipeListComponent implements OnInit, AfterViewInit {
  displayedColumns: string[] = ['index', 'nom', 'description', 'actions'];
  // displayedColumns: string[] = ['index', 'nom', 'code', 'description', 'actions'];

  dataSource = new MatTableDataSource<EquipeDTO>();
  showDeleteConfirmation = false;
  selectedEquipeCode: string | null = null;

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  constructor(
    private equipeService: EquipeService,
    public dialog: MatDialog
  ) { }

  ngOnInit(): void {
    this.loadEquipes();
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
  }

  loadEquipes(): void {
    this.equipeService.getAllEquipes().subscribe(response => {
      this.dataSource.data = response; // Mise à jour des données du tableau
    }, error => {
      console.error('Erreur lors du chargement des équipes:', error);
    });
  }

  openAddEditEquipeModal(equipe?: EquipeDTO): void {
    const dialogRef = this.dialog.open(AddEditEquipeComponent, {
      width: '700px',
      data: equipe || null
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadEquipes(); // Rafraîchissement des données après la fermeture du modal
      }
    });
  }

  deleteEquipe(code: string): void {
    this.showDeleteConfirmation = true;
    this.selectedEquipeCode = code;
  }

  confirmDelete(): void {
    if (this.selectedEquipeCode !== null) {
      this.equipeService.deleteEquipe(this.selectedEquipeCode).subscribe(() => {
        this.loadEquipes();
        this.closeDeleteConfirmation();
      }, error => {
        console.error('Erreur lors de la suppression de l\'équipe:', error);
      });
    }
  }

  closeDeleteConfirmation(): void {
    this.showDeleteConfirmation = false;
    this.selectedEquipeCode = null;
  }
}
