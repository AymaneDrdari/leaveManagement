import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {Leave} from "../../../models/leave";

@Component({
  selector: 'app-leave-modal',
  templateUrl: './leave-modal.component.html',
  styleUrl: './leave-modal.component.css'
})
export class LeaveModalComponent {
  constructor(
    public dialogRef: MatDialogRef<LeaveModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: Leave
  ) { }


}
