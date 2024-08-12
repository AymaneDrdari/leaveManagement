import { Component, OnInit, OnDestroy } from '@angular/core';
import { CalendarOptions, EventClickArg, DatesSetArg } from '@fullcalendar/core';
import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridPlugin from '@fullcalendar/timegrid';
import interactionPlugin from '@fullcalendar/interaction';
import { Subscription } from 'rxjs';
import { MatDialog } from '@angular/material/dialog';
import { LeaveService } from '../../services/leave.service';
import { ApiResponse } from '../../models/ApiResponse';
import { HolidayService } from '../../services/holiday.service';
import { HolidayModalComponent } from '../holiday/holiday-modal/holiday-modal.component';
import { Holiday } from '../../models/holiday';
import { CongeDetailDTO } from "../../models/conge-detail-dto.model";
import { AddEditLeaveComponent } from '../leave/add-edit-leave/add-edit-leave.component';

@Component({
  selector: 'app-calendar',
  templateUrl: './calendar.component.html',
  styleUrls: ['./calendar.component.css']
})
export class CalendarComponent implements OnInit, OnDestroy {

  holidaysVisible: boolean = true;
  selectedTeam: string | null = null;
  holidaysSubscription?: Subscription;
  teamSubscription?: Subscription;

  calendarOptions: CalendarOptions = {
    plugins: [dayGridPlugin, timeGridPlugin, interactionPlugin],
    initialView: 'dayGridMonth',
    headerToolbar: {
      left: 'prev,next today',
      center: 'title',
      right: 'dayGridMonth,timeGridWeek,timeGridDay'
    },
    events: [],
    height: 500,
    contentHeight: 100,
    themeSystem: 'bootstrap',
    eventClick: this.handleEventClick.bind(this),
    datesSet: this.handleDatesSet.bind(this),
    dateClick: this.handleDateClick.bind(this) // Gestionnaire pour le clic sur les dates
  };

  constructor(
    private holidayService: HolidayService,
    private leaveService: LeaveService,
    public dialog: MatDialog
  ) { }

  ngOnInit(): void {
    this.holidaysSubscription = this.holidayService.holidaysVisible$.subscribe(visible => {
      this.holidaysVisible = visible;
      this.updateCalendarEvents();
    });

    this.teamSubscription = this.leaveService.selectedTeam$.subscribe(team => {
      this.selectedTeam = team;
      this.updateCalendarEvents();
    });

    // Abonnement pour mettre à jour les événements après la création/mise à jour des congés
    this.leaveService.leavesUpdated$.subscribe(() => {
      this.updateCalendarEvents();
    });

    // Chargement initial
    this.updateCalendarEvents();
  }


  ngOnDestroy(): void {
    if (this.holidaysSubscription) {
      this.holidaysSubscription.unsubscribe();
    }
    if (this.teamSubscription) {
      this.teamSubscription.unsubscribe();
    }
  }

  updateCalendarEvents(): void {
    if (this.holidaysVisible) {
      const currentYear = new Date().getFullYear();
      this.fetchHolidays(currentYear);
    }
    if (this.selectedTeam) {
      this.fetchLeavesByTeam(this.selectedTeam);
    } else {
      this.calendarOptions = {
        ...this.calendarOptions,
        events: []
      };
    }
  }

  fetchLeavesByTeam(team: string): void {
    console.log("fetch conge by team ", team);
    this.leaveService.getCongesByEquipe(team).subscribe((leaveResponse: ApiResponse<CongeDetailDTO[]>) => {
      if (leaveResponse && leaveResponse.data) {
        console.log("fetched conge is :", leaveResponse)
        const leaves = leaveResponse.data.map(congeDetail => {
          const dateStart = new Date(congeDetail.date_debut);
          console.log("fetched conge exactly is :", congeDetail)
          const dateEnd = new Date(congeDetail.date_fin);
          // Ajouter un jour à dateEnd
          dateEnd.setDate(dateEnd.getDate() + 1)
          return {
            title: `${congeDetail.collaborateur_nom} ${congeDetail.collaborateur_prenom}`,
            start: dateStart.toISOString().split('T')[0],
            end: dateEnd.toISOString().split('T')[0],
            color: this.getTeamColor(team),
            extendedProps: congeDetail
          };
        }).filter(event => event.start && event.end);
        this.calendarOptions = {
          ...this.calendarOptions,
          events: leaves
        };
      }
    }, error => {
      console.error('Error fetching leaves:', error);
    });
  }

  getTeamColor(team: string): string {
    const teamColors: { [key: string]: string } = {
      'royal': '#4169e1', // Bleu royal
      'gold': '#ffd700',  // Or
      'mauve': '#8902e1', // Mauve
      'blue': '#1e90ff',  // Bleu
      // Ajoutez d'autres couleurs si nécessaire
    };
    return teamColors[team] || '#000000'; // Retourne noir par défaut si l'équipe n'a pas de couleur spécifique
  }

  fetchHolidays(year: number): void {
    this.holidayService.getHolidays(year).subscribe((holidayResponse: ApiResponse<Holiday[]>) => {
      if (holidayResponse && holidayResponse.data) {
        const holidays = holidayResponse.data.map(holiday => {
          const dateStart = new Date(holiday.date_debut);
          const dateEnd = new Date(holiday.date_fin);
          if (holiday.is_fixe) {
            dateStart.setFullYear(year);
            dateEnd.setFullYear(year);
          }
          return {
            title: holiday.description || 'Jour férié',
            start: dateStart.toISOString().split('T')[0],
            end: dateEnd.toISOString().split('T')[0],
            extendedProps: holiday
          };
        }).filter(event => event.start && event.end);
        this.calendarOptions = {
          ...this.calendarOptions,
          events: holidays
        };
      }
    }, error => {
      console.error('Error fetching holidays:', error);
    });
  }

  handleDatesSet(arg: DatesSetArg): void {
    const year = arg.start.getFullYear();
    this.fetchHolidays(year);
  }

  handleEventClick(arg: EventClickArg): void {
    const holiday = arg.event.extendedProps as Holiday;
    this.dialog.open(HolidayModalComponent, {
      width: '400px',
      data: holiday
    });
  }

  handleDateClick(arg: any): void {  // Utilisation de 'any' si le type spécifique n'est pas disponible
    this.dialog.open(AddEditLeaveComponent, {
      width: '500px',
      data: {
        date_debut: arg.dateStr,  // Préremplir la date de début avec la date cliquée
        date_fin: arg.dateStr,    // Vous pouvez également préremplir la date de fin ici si nécessaire
        collaborateur_email: '',
        description: '',
        nombreJoursPris: 0,
        demi_journee_matin: false,
        demi_journee_soir: false
      }
    });
  }
}
