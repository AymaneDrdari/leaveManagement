import { Component, Inject, OnDestroy } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Holiday } from '../../../models/holiday';
import { HolidayService } from '../../../services/holiday.service';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-add-edit-holiday',
  templateUrl: './add-edit-holiday.component.html',
  styleUrls: ['./add-edit-holiday.component.css']
})
export class AddEditHolidayComponent implements OnDestroy {
  holidayForm: FormGroup;
  isEdit: boolean;
  private isSubmitting = false;
  private onDestroy$ = new Subject<void>();

  // Constructeur pour initialiser les services et les données nécessaires au composant.
  // Le composant reçoit les données d'un jour férié (Holiday) via l'injection de données MAT_DIALOG_DATA.
  // FormBuilder est utilisé pour créer un formulaire réactif.
  // Le dialogRef permet de fermer le dialogue modal et de passer des données en retour.
  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<AddEditHolidayComponent>,
    @Inject(MAT_DIALOG_DATA) public data: Holiday,
    private holidayService: HolidayService
  ) {
    // Déterminer si le composant est en mode édition en vérifiant la présence d'un ID dans les données.
    this.isEdit = !!data.id;

    // Initialisation des contrôles du formulaire avec des valeurs par défaut et des validateurs.
    // Validators.required s'assure que les champs obligatoires sont remplis avant la soumission.
    this.holidayForm = this.fb.group({
      id: [data.id || null],
      date_debut: [data.date_debut || '', Validators.required],
      date_fin: [data.date_fin || '', Validators.required],
      is_fixe: [data.is_fixe || false],
      description: [data.description || '', Validators.required]
    });
  }

  // Méthode pour sauvegarder les données du formulaire.
  // Elle vérifie d'abord si une soumission est déjà en cours pour éviter les soumissions multiples.
  // Si le formulaire est valide, les dates sont converties en format UTC pour une gestion cohérente.
  // Ensuite, selon qu'il s'agit d'une création ou d'une mise à jour, la méthode correspondante du service est appelée.
  onSave(): void {
    if (this.isSubmitting) return;

    if (this.holidayForm.valid) {
      this.isSubmitting = true;
      let holiday = this.holidayForm.value;

      console.log("Before conversion to UTC:", holiday.date_debut, holiday.date_fin);

      // Conversion des dates en UTC avec ajustement pour éviter les problèmes de fuseau horaire.
      let dateDebut = new Date(holiday.date_debut);
      dateDebut.setMinutes(dateDebut.getMinutes() - dateDebut.getTimezoneOffset());

      let dateFin = new Date(holiday.date_fin);
      dateFin.setMinutes(dateFin.getMinutes() - dateFin.getTimezoneOffset());

      holiday.date_debut = dateDebut.toISOString();
      holiday.date_fin = dateFin.toISOString();

      console.log("After conversion to UTC:", holiday.date_debut, holiday.date_fin);

      // Appel du service pour créer ou mettre à jour le jour férié.
      const currentYear= new Date().getFullYear();
      const saveObservable = holiday.id
        ? this.holidayService.updateHoliday(holiday)
        : this.holidayService.createHoliday(holiday);

      // Gestion de la réponse du service, réinitialisation de l'état de soumission, et fermeture du dialogue.
      saveObservable.subscribe({
        next: () => {
          this.isSubmitting = false;
          this.dialogRef.close(holiday);
        },
        error: (error: string) => {
          this.isSubmitting = false;
          console.error("Error while saving holiday:", error);
        }
      });
    }
  }

  // Méthode pour fermer le dialogue sans enregistrer les changements.
  onCancel(): void {
    this.dialogRef.close();
  }

  // Nettoyage des observables pour éviter les fuites de mémoire lorsque le composant est détruit.
  ngOnDestroy(): void {
    this.onDestroy$.next();
    this.onDestroy$.complete();
  }
}
