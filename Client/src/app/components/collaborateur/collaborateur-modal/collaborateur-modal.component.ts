import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-collaborateur-modal',
  templateUrl: './collaborateur-modal.component.html',
  styleUrls: ['./collaborateur-modal.component.css']
})
export class CollaborateurModalComponent {
  constructor(@Inject(MAT_DIALOG_DATA) public data: any) {}
}
