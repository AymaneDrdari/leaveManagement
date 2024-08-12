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
      code: [data?.code || ''],
      description: [data?.description || '']
    });
  }

  onSave(): void {
    if (this.equipeForm.valid) {
      const equipe: EquipeDTO = this.equipeForm.value;
      if (this.isEdit && this.data) {
        // Update existing equipe
        this.equipeService.updateEquipe(this.data.code, equipe).subscribe(() => {
          this.dialogRef.close(equipe);
        });
      } else {
        // Add new equipe
        this.equipeService.addEquipe(equipe).subscribe(() => {
          this.dialogRef.close(equipe);
        });
      }
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}
