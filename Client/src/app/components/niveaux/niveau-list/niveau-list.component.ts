import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatDialog } from '@angular/material/dialog';
import { NiveauService } from '../../../services/niveau.service';
import { AddEditNiveauComponent } from '../add-edit-niveau/add-edit-niveau.component';
import {Niveau} from "../../../models/niveau.model";

@Component({
  selector: 'app-niveau-list',
  templateUrl: './niveau-list.component.html',
  styleUrls: ['./niveau-list.component.css']
})
export class NiveauListComponent implements OnInit, AfterViewInit {
  displayedColumns: string[] = ['index', 'nom', 'description', 'actions'];
  dataSource = new MatTableDataSource<Niveau>();
  showDeleteConfirmation = false;
  selectedNiveauId: string | null = null;
  niveaux: Niveau[] = [];

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  constructor(
    private niveauService: NiveauService,
    public dialog: MatDialog
  ) { }

  ngOnInit(): void {
    this.loadNiveaux();
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
  }

  loadNiveaux(): void {
    this.niveauService.getAllNiveaux().subscribe(response => {
      this.niveaux = response.data;
      this.dataSource.data = this.niveaux; // Mise à jour des données du tableau
    }, error => {
      console.error('Error loading niveaux:', error);
    });
  }

  openAddEditNiveauModal(niveau?: Niveau): void {
    const dialogRef = this.dialog.open(AddEditNiveauComponent, {
      width: '700px',
      data: niveau || null
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadNiveaux(); // Rafraîchissement des données après la fermeture du modal
      }
    });
  }

  deleteNiveau(id: string): void {
    this.showDeleteConfirmation = true;
    this.selectedNiveauId = id;
  }

  confirmDelete(): void {
    if (this.selectedNiveauId !== null) {
      this.niveauService.deleteNiveau(this.selectedNiveauId).subscribe(() => {
        this.loadNiveaux();
        this.closeDeleteConfirmation();
      }, error => {
        console.error('Error deleting niveau:', error);
      });
    }
  }

  closeDeleteConfirmation(): void {
    this.showDeleteConfirmation = false;
    this.selectedNiveauId = null;
  }
}
