import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Leave } from '../../../models/leave';
import { LeaveService } from '../../../services/leave.service';
import { Collaborateur } from '../../../models/collaborateur';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { ApiResponse } from '../../../models/ApiResponse';
import {CollaborateurService} from "../../../services/collaborateur.service";

@Component({
  selector: 'app-add-edit-leave',
  templateUrl: './add-edit-leave.component.html',
  styleUrls: ['./add-edit-leave.component.css']
})
export class AddEditLeaveComponent implements OnInit, OnDestroy {
  congeForm: FormGroup;
  collaborateurs: Collaborateur[] = [];
  isEdit: boolean;
  private isSubmitting = false;
  private onDestroy$ = new Subject<void>();

  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<AddEditLeaveComponent>,
    @Inject(MAT_DIALOG_DATA) public data: Leave,
    private collaborateurService: CollaborateurService,
    private leaveService: LeaveService
  ) {
    this.isEdit = !!data.id_conge;
    console.log('Editing Leave:', this.data);

    this.congeForm = this.fb.group({
      idConge: [data.id_conge || null],
      date_debut: [data.date_debut || '', Validators.required],
      date_fin: [data.date_fin || '', Validators.required],
      collaborateur_email: [data.collaborateur_email || '', Validators.required],
      description: [data.description || '', Validators.required],
      nombreJoursPris: [data.nombre_jours_pris || 0],
      demi_journee_matin: [data.demi_journee_matin || false],
      demi_journee_soir: [data.demi_journee_soir || false],
    });

    if (this.data) {
      this.congeForm.patchValue(this.data);
    }
  }

  ngOnInit(): void {
    this.collaborateurService.getCollaborateurs()
      .pipe(takeUntil(this.onDestroy$))
      .subscribe(
        (response: ApiResponse<Collaborateur[]>) => {
          this.collaborateurs = response.data;
          console.log('Fetched Collaborateurs:', this.collaborateurs);

          if (this.data && this.data.collaborateur_email) {
            const currentCollaborateur = this.collaborateurs.find(c => c.email === this.data.collaborateur_email);
            console.log('Current Collaborateur:', currentCollaborateur);
            if (currentCollaborateur) {
              this.congeForm.patchValue({ collaborateur_email: currentCollaborateur.email });
            }
          }
        },
        (error) => {
          console.error('Error fetching collaborateurs', error);
        }
      );
  }

  onSave(): void {
    if (this.isSubmitting) return;
    if (this.congeForm.valid) {
      this.isSubmitting = true;
      let leave = {
        id_conge: this.congeForm.get('idConge')?.value,
        date_debut: this.congeForm.get('date_debut')?.value,
        date_fin: this.congeForm.get('date_fin')?.value,
        collaborateur_email: this.congeForm.get('collaborateur_email')?.value,
        description: this.congeForm.get('description')?.value,
        nombre_jours_pris: this.congeForm.get('nombreJoursPris')?.value,
        demi_journee_matin: this.congeForm.get('demi_journee_matin')?.value,
        demi_journee_soir: this.congeForm.get('demi_journee_soir')?.value
      };

      // Log les valeurs avant toute manipulation
      console.log("Before conversion to UTC:", leave.date_debut, leave.date_fin);

      // Conversion des dates en UTC
      let dateDebut = new Date(leave.date_debut);
      dateDebut.setMinutes(dateDebut.getMinutes() - dateDebut.getTimezoneOffset());
      leave.date_debut = dateDebut.toISOString().split('T')[0];
      console.log("Date début UTC:", leave.date_debut);

      let dateFin = new Date(leave.date_fin);
      dateFin.setMinutes(dateFin.getMinutes() - dateFin.getTimezoneOffset());
      leave.date_fin = dateFin.toISOString().split('T')[0];
      console.log("Date fin UTC:", leave.date_fin);

      // Log avant envoi
      console.log("Data to be sent:", leave);

      const saveObservable = leave.id_conge
        ? this.leaveService.updateConge(leave)
        : this.leaveService.createConge(leave);

      saveObservable.subscribe({
        next: (response) => {
          console.log('Réponse après sauvegarde:', response);
          this.isSubmitting = false;
          this.dialogRef.close(leave);
        },
        error: (error: any) => {
          this.isSubmitting = false;
          console.error("Erreur lors de la sauvegarde:", error);
        }
      });
    } else {
      console.error('Formulaire invalide:', this.congeForm.errors);
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  ngOnDestroy(): void {
    this.onDestroy$.next();
    this.onDestroy$.complete();
  }
}
