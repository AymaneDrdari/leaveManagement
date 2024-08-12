import { Component, Inject, OnInit, OnDestroy } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CollaborateurService } from '../../../services/collaborateur.service';
import { Collaborateur } from '../../../models/collaborateur';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { ApiResponse } from '../../../models/ApiResponse';
import { Niveau } from "../../../models/niveau.model";
import { EquipeDTO } from "../../../models/equipe.model";

@Component({
  selector: 'app-add-edit-collaborateur',
  templateUrl: './add-edit-collaborateur.component.html',
  styleUrls: ['./add-edit-collaborateur.component.css']
})
export class AddEditCollaborateurComponent implements OnInit, OnDestroy {
  collaborateurForm: FormGroup;
  isEdit: boolean;
  niveaux: Niveau[] = [];
  equipes: EquipeDTO[] = [];
  private isSubmitting = false;
  private onDestroy$ = new Subject<void>();

  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<AddEditCollaborateurComponent>,
    @Inject(MAT_DIALOG_DATA) public data: Collaborateur,
    private collaborateurService: CollaborateurService
  ) {
    this.isEdit = !!data.id;
    this.collaborateurForm = this.fb.group({
      nom: [data.nom || '', Validators.required],
      prenom: [data.prenom || '', Validators.required],
      email: [data.email || '', [Validators.required, Validators.email]],
      equipe: [data.equipe || null, Validators.required],
      niveau: [data.niveau || null, Validators.required],
      role: [data.role || '', Validators.required],
      type: [data.type || '', Validators.required],
      date_entree_projet: [data.date_entree_projet || '', Validators.required],
      date_sortie_projet: [data.date_sortie_projet || ''],
      nombre_jours_payes_mois: [data.nombre_jours_payes_mois || 0, [Validators.required, Validators.min(0)]]
    });
  }

  ngOnInit(): void {
    this.loadEquipes();
    this.loadNiveaux();
    this.patchFormValues();
  }

  loadEquipes(): void {
    this.collaborateurService.getAllEquipes().pipe(takeUntil(this.onDestroy$)).subscribe(
      (response: ApiResponse<EquipeDTO[]>) => {
        this.equipes = response.data;
        this.patchFormValues();
      },
      (error: any) => {
        console.error('Error fetching equipes', error);
      }
    );
  }

  loadNiveaux(): void {
    this.collaborateurService.getAllNiveaux().pipe(takeUntil(this.onDestroy$)).subscribe(
      (response: ApiResponse<Niveau[]>) => {
        this.niveaux = response.data;
        this.patchFormValues();
      },
      (error: any) => {
        console.error('Error fetching niveaux', error);
      }
    );
  }

  patchFormValues(): void {
    if (this.isEdit && this.niveaux.length > 0 && this.equipes.length > 0) {
      const selectedEquipe = this.equipes.find(e => e.code === this.data.equipe?.code);
      const selectedNiveau = this.niveaux.find(n => n.code === this.data.niveau?.code);

      this.collaborateurForm.patchValue({
        equipe: selectedEquipe || null,
        niveau: selectedNiveau || null,
        nombre_jours_payes_mois: this.data.nombre_jours_payes_mois || 0
      });
    }
  }

  onSave(): void {
    if (this.isSubmitting) return;

    if (this.collaborateurForm.valid) {
      this.isSubmitting = true;

      const dateEntreeProjet = this.collaborateurForm.value.date_entree_projet;
      const dateSortieProjet = this.collaborateurForm.value.date_sortie_projet;

      const collaborateur: Collaborateur = {
        ...this.collaborateurForm.value,
        id: this.data?.id, // Include the ID for updates
        date_entree_projet: dateEntreeProjet
          ? new Date(dateEntreeProjet).toISOString().split('T')[0]
          : null,
        date_sortie_projet: dateSortieProjet
          ? new Date(dateSortieProjet).toISOString().split('T')[0]
          : null
      };
      console.log("collaborateur to save : ", collaborateur)

      const saveObservable = this.isEdit
        ? this.collaborateurService.updateCollaborateur(collaborateur)
        : this.collaborateurService.createCollaborateur(collaborateur);

      saveObservable.subscribe({
        next: (savedCollaborateur) => {
          this.isSubmitting = false;
          this.dialogRef.close(savedCollaborateur);
        },
        error: (error: any) => {
          this.isSubmitting = false;
          console.error('Error while saving collaborateur:', error);
        }
      });
    } else {
      console.error('Form is invalid:', this.collaborateurForm.errors);
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
