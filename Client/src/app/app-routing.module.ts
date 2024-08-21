import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { CalendarComponent } from './components/calendar/calendar.component';
import { HolidayListComponent } from './components/holiday/holiday-list/holiday-list.component';
import { CollaborateurListComponent } from './components/collaborateur/collaborateur-list/collaborateur-list.component';
import {LeaveListComponent} from "./components/leave/leave-list/leave-list.component";
import {NiveauListComponent} from "./components/niveaux/niveau-list/niveau-list.component";
import {EquipeListComponent} from "./components/equipes/equipe-list/equipe-list.component";


const routes: Routes = [
  { path: '', redirectTo: 'calendar', pathMatch: 'full' },
  { path: 'calendar', component: CalendarComponent },
  { path: 'jours-feries', component: HolidayListComponent },

  { path: 'collaborateurs', component: CollaborateurListComponent },

  { path: 'conges', component: LeaveListComponent },
  { path: 'niveau', component: NiveauListComponent },
  { path:'equipe' , component: EquipeListComponent}

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
