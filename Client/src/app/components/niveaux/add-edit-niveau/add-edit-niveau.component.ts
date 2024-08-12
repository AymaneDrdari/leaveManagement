import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { NiveauService } from '../../../services/niveau.service';
import { Niveau } from '../../../models/niveau.model';

@Component({
  selector: 'app-add-edit-niveau',
  templateUrl: './add-edit-niveau.component.html',
  styleUrls: ['./add-edit-niveau.component.css']
})
export class AddEditNiveauComponent implements OnInit {
  niveauForm: FormGroup;
  isEdit: boolean;

  constructor(
    private fb: FormBuilder,
    private niveauService: NiveauService,
    public dialogRef: MatDialogRef<AddEditNiveauComponent>,
    @Inject(MAT_DIALOG_DATA) public data: Niveau
  ) {
    this.isEdit = !!data; // Si des données sont passées, c'est une édition

    this.niveauForm = this.fb.group({
      nom: [data?.nom || '', Validators.required],
      description: [data?.description || ''],
      dateCreation: [{ value: data?.date_creation || '', disabled: true }] // Champ désactivé en mode ajout
    });
  }

  ngOnInit(): void {}

  onSave(): void {
    if (this.niveauForm.valid) {
      const niveauData: Niveau = this.niveauForm.value;
      if (this.isEdit) {
        this.niveauService.updateNiveau(this.data?.code ?? '', niveauData).subscribe(() => {
          this.dialogRef.close(true);
        });
      } else {
        this.niveauService.addNiveau(niveauData).subscribe(() => {
          this.dialogRef.close(true);
        });
      }
    }
  }

  onCancel(): void {
    this.dialogRef.close(false); // Ferme le dialogue sans enregistrer
  }
}
