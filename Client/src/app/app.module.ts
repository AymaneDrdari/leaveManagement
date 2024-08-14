import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatCardModule } from '@angular/material/card';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatDialogModule } from '@angular/material/dialog';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule, MatOptionModule } from '@angular/material/core';
import { NgxPaginationModule } from 'ngx-pagination';
import { MatSelectModule } from '@angular/material/select';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatRadioModule } from '@angular/material/radio';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { NavbarComponent } from './layouts/navbar/navbar.component';
import { SidebarComponent } from './layouts/sidebar/sidebar.component';
import { FooterComponent } from './layouts/footer/footer.component';
import { IndicatorsComponent } from './components/indicators/indicators.component';
import { CalendarComponent } from './components/calendar/calendar.component';
import { LeaveModalComponent } from './components/leave/leave-modal/leave-modal.component';
import { HolidayModalComponent } from './components/holiday/holiday-modal/holiday-modal.component';
import { HolidayListComponent } from './components/holiday/holiday-list/holiday-list.component';
import { FullCalendarModule } from '@fullcalendar/angular';
import { NgOptimizedImage } from '@angular/common';
import { AddEditHolidayComponent } from './components/holiday/add-edit-holiday/add-edit-holiday.component';
import { AddEditLeaveComponent } from './components/leave/add-edit-leave/add-edit-leave.component';
import { LeaveListComponent } from './components/leave/leave-list/leave-list.component';
import { CollaborateurListComponent } from './components/collaborateur/collaborateur-list/collaborateur-list.component';
import { AddEditCollaborateurComponent } from './components/collaborateur/add-edit-collaborateur/add-edit-collaborateur.component';
import { CollaborateurModalComponent } from './components/collaborateur/collaborateur-modal/collaborateur-modal.component';
import { AddEditNiveauComponent } from './components/niveaux/add-edit-niveau/add-edit-niveau.component';
import {NiveauListComponent} from "./components/niveaux/niveau-list/niveau-list.component";
import {EquipeListComponent} from "./components/equipes/equipe-list/equipe-list.component";
import { AddEditEquipeComponent } from './components/equipes/add-edit-equipe/add-edit-equipe.component';
import { ColorPickerModule } from 'ngx-color-picker';

@NgModule({
  declarations: [
    AppComponent,
    NavbarComponent,
    SidebarComponent,
    FooterComponent,
    IndicatorsComponent,
    CalendarComponent,
    LeaveModalComponent,
    HolidayModalComponent,
    HolidayListComponent,
    AddEditHolidayComponent,
    AddEditLeaveComponent,
    LeaveListComponent,
    CollaborateurListComponent,
    AddEditCollaborateurComponent,
    CollaborateurModalComponent,
    NiveauListComponent,
    AddEditNiveauComponent,
    EquipeListComponent,
    AddEditEquipeComponent,
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    MatToolbarModule,
    MatIconModule,
    MatButtonModule,
    MatInputModule,
    MatMenuModule,
    MatListModule,
    MatCardModule,
    MatSidenavModule,
    MatDialogModule,
    MatTableModule,
    MatPaginatorModule,
    MatDatepickerModule,
    MatNativeDateModule,
    FullCalendarModule,
    NgOptimizedImage,
    MatCheckboxModule,
    NgxPaginationModule,
    MatSelectModule,
    MatOptionModule,
    MatRadioModule,
    MatAutocompleteModule,
    ColorPickerModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
