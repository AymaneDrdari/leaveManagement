import { Component, ViewChild, OnInit, AfterViewInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatDialog } from '@angular/material/dialog';
import { AddEditLeaveComponent } from '../add-edit-leave/add-edit-leave.component';
import { Leave } from '../../../models/leave';
import { LeaveService } from '../../../services/leave.service';
import {Holiday} from "../../../models/holiday";
import {AddEditHolidayComponent} from "../../holiday/add-edit-holiday/add-edit-holiday.component";

@Component({
  selector: 'app-leave-list',
  templateUrl: './leave-list.component.html',
  styleUrls: ['./leave-list.component.css']
})
export class LeaveListComponent implements OnInit, AfterViewInit {
  displayedColumns: string[] = ['dateDebut', 'dateFin', 'collaborateur', 'description','nombreJoursPris', 'actions'];
  dataSource = new MatTableDataSource<Leave>();
  showDeleteConfirmation = false;
  selectedLeaveId: string | null = null;
  errorMessage: string | null = null;
  pageSize = 5;
  currentPage = 0;
  totalItems = 0;

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  constructor(
    private leaveService: LeaveService,
    public dialog: MatDialog
  ) { }

  ngOnInit(): void {
    this.loadLeaves();
  }

  ngAfterViewInit(): void {
    if (this.paginator) {
      this.dataSource.paginator = this.paginator;
    }
  }

  loadLeaves(page: number = 0): void {
    this.leaveService.getAllLeaves(page, this.pageSize).subscribe(response => {
      console.log('Loaded leaves:', response)
      this.dataSource.data = response.data;
      this.totalItems = response.totalElements;
      this.currentPage = page;
    }, error => {
      console.error('Error loading leaves:', error);
      this.errorMessage = 'Une erreur est survenue lors du chargement des congés';
    });
  }

  openAddEditLeaveModal(leave?: Leave): void {
    console.log('Opening leave modal with data:', leave);
    const dialogRef = this.dialog.open(AddEditLeaveComponent, {
      width: '600px',
      data: leave || {}
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('Modal closed with result:', result);
      if (result) {
        this.loadLeaves(this.currentPage);
      }
    });
  }


  deleteLeave(id: number): void {
    this.showDeleteConfirmation = true;
    this.selectedLeaveId = id.toString();
  }

  confirmDelete(): void {
    console.log('selected leave ID: ', this.selectedLeaveId);
    if (this.selectedLeaveId !== null) {
      this.leaveService.deleteConge(this.selectedLeaveId).subscribe({
        next: (response) => {
          console.log('Delete response: ', response)
          this.loadLeaves(this.currentPage);
          this.closeDeleteConfirmation();
        },
        error: (error: any) => {
          console.error('Error deleting leave:', error);
          this.errorMessage = 'Une erreur est survenue lors de la suppression';
        }
      });
    }
  }

  closeDeleteConfirmation(): void {
    this.showDeleteConfirmation = false;
    this.selectedLeaveId = null;
  }

  //Gère les changements de page dans le tableau de jours fériés.
  onPageChange(page: number): void {
    if (page < 0 || page >= Math.ceil(this.totalItems / this.pageSize)) return;
    this.currentPage = page;
    this.loadLeaves(page);
  }

  get totalPages(): number {
    return Math.max(Math.ceil(this.totalItems / this.pageSize), 1);
  }

  // Génère un tableau pour la pagination.
  paginationArray(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i);
  }
}
