import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { EquipeService } from '../../../services/equipe.service';
import { EquipeDTO } from '../../../models/equipe.model';

@Component({
  selector: 'app-add-edit-equipe',
  templateUrl: './add-edit-equipe.component.html',
  styleUrls: ['./add-edit-equipe.component.css']
})
export class AddEditEquipeComponent {
  equipeForm: FormGroup;
  isEdit = false;

  constructor(
    public dialogRef: MatDialogRef<AddEditEquipeComponent>,
    @Inject(MAT_DIALOG_DATA) public data: EquipeDTO | null,
    private fb: FormBuilder,
    private equipeService: EquipeService
  ) {
    this.isEdit = !!data;
    this.equipeForm = this.fb.group({
      nom: [data?.nom || '', Validators.required],
      code: [{ value: data?.code || '', disabled: this.isEdit }],
      description: [data?.description || ''],
      couleur: ['', Validators.required] // Assurez-vous que ce champ est bien vide initialement et obligatoire
    });

  }

  onSave(): void {
    if (this.equipeForm.valid) {
      const equipe: EquipeDTO = this.equipeForm.value;
      if (this.isEdit && this.data) {
        // Mettre à jour l'équipe existante
        this.equipeService.updateEquipe(this.data.code, equipe).subscribe(() => {
          this.dialogRef.close(equipe); // Informer le parent de la mise à jour
        });
      } else {
        // Ajouter une nouvelle équipe
        this.equipeService.addEquipe(equipe).subscribe(() => {
          this.dialogRef.close(equipe); // Informer le parent de l'ajout
        });
      }
    }
  }

  onManualColorInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    const colorPicker = document.getElementById('actualColorPicker') as HTMLInputElement;

    // Mettre à jour la valeur du sélecteur de couleur pour refléter la saisie manuelle
    colorPicker.value = input.value;
  }

  openColorPicker(): void {
    const colorPicker = document.getElementById('actualColorPicker') as HTMLInputElement;
    if (colorPicker) {
      colorPicker.click();
    }
  }

  onColorChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.equipeForm.patchValue({ couleur: input.value });

    // Mettre à jour le champ de saisie avec la valeur du sélecteur de couleur
    const colorInput = document.getElementById('colorPicker') as HTMLInputElement;
    if (colorInput) {
      colorInput.value = input.value;
    }
  }



  onCancel(): void {
    this.dialogRef.close();
  }
}
