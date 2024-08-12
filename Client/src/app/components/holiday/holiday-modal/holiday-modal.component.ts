import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-holiday-modal',
  templateUrl: './holiday-modal.component.html',
  styleUrls: ['./holiday-modal.component.css']
})
export class HolidayModalComponent {
  constructor(

    @Inject(MAT_DIALOG_DATA) public data: any
  ) {}

}
