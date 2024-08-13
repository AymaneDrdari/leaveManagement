import { Component, ViewChild, OnInit, AfterViewInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatDialog } from '@angular/material/dialog';
import { AddEditCollaborateurComponent } from '../add-edit-collaborateur/add-edit-collaborateur.component';
import { Collaborateur } from '../../../models/collaborateur';
import { CollaborateurService } from '../../../services/collaborateur.service';
import { ApiResponse } from '../../../models/ApiResponse';

@Component({
  selector: 'app-collaborateur-list',
  templateUrl: './collaborateur-list.component.html',
  styleUrls: ['./collaborateur-list.component.css']
})
export class CollaborateurListComponent implements OnInit, AfterViewInit {
  displayedColumns: string[] = ['index','nom', 'prenom', 'email', 'equipeNom', 'niveauNom', 'type', 'role', 'actions'];
  dataSource = new MatTableDataSource<Collaborateur>();
  showDeleteConfirmation = false;
  selectedCollaborateurId: string | null = null;
  errorMessage: string | null = null;
  pageSize = 5;
  currentPage = 0;
  totalItems = 0;

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  constructor(
    private collaborateurService: CollaborateurService,
    public dialog: MatDialog
  ) { }

  ngOnInit(): void {
    this.loadCollaborateurs();
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
  }

  loadCollaborateurs(page: number = 0): void {
    this.collaborateurService.findAllCollaborateurs(page, this.pageSize).subscribe((response: ApiResponse<Collaborateur[]>) => {
      this.dataSource.data = response.data;
      this.totalItems = response.totalElements;
      this.currentPage = page;
    }, (error: any) => {
      console.error('Error loading collaborateurs:', error);
      this.errorMessage = 'Une erreur est survenue lors du chargement des collaborateurs';
    });
  }

  openAddEditCollaborateurModal(collaborateur?: Collaborateur): void {
    const dialogRef = this.dialog.open(AddEditCollaborateurComponent, {
      width: '600px',
      data: collaborateur || {}
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadCollaborateurs(this.currentPage);
      }
    });
  }

  deleteCollaborateur(id: string): void {
    this.showDeleteConfirmation = true;
    this.selectedCollaborateurId = id;
  }

  confirmDelete(): void {
    if (this.selectedCollaborateurId !== null) {
      this.collaborateurService.deleteCollaborateur(this.selectedCollaborateurId).subscribe({
        next: () => {
          this.loadCollaborateurs(this.currentPage);
          this.closeDeleteConfirmation();
        },
        error: (error: any) => {
          console.error('Error deleting collaborateur:', error);
          this.errorMessage = 'Une erreur est survenue lors de la suppression';
        }
      });
    }
  }

  closeDeleteConfirmation(): void {
    this.showDeleteConfirmation = false;
    this.selectedCollaborateurId = null;
  }

  onPageChange(event: any): void {
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadCollaborateurs(this.currentPage);
  }

  get totalPages(): number {
    return Math.max(Math.ceil(this.totalItems / this.pageSize), 1);
  }

  paginationArray(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i);
  }
}
