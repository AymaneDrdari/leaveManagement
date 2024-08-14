import { Component, OnInit, OnDestroy, ElementRef, ViewChild, AfterViewInit } from '@angular/core';
import { CalendarOptions, EventClickArg, DatesSetArg } from '@fullcalendar/core';
import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridPlugin from '@fullcalendar/timegrid';
import interactionPlugin, { DateClickArg } from '@fullcalendar/interaction';
import { Subscription } from 'rxjs';
import { MatDialog } from '@angular/material/dialog';
import { LeaveService } from '../../services/leave.service';
import { ApiResponse } from '../../models/ApiResponse';
import { HolidayService } from '../../services/holiday.service';
import { HolidayModalComponent } from '../holiday/holiday-modal/holiday-modal.component';
import { Holiday } from '../../models/holiday';
import { CongeDetailDTO } from "../../models/conge-detail-dto.model";
import { AddEditLeaveComponent } from '../leave/add-edit-leave/add-edit-leave.component';
import { AddEditHolidayComponent } from '../holiday/add-edit-holiday/add-edit-holiday.component';

@Component({
  selector: 'app-calendar',
  templateUrl: './calendar.component.html',
  styleUrls: ['./calendar.component.css']
})
export class CalendarComponent implements OnInit, OnDestroy, AfterViewInit {
  @ViewChild('contextMenuRef') contextMenuRef!: ElementRef;
  selectedTeam: string | null = null;
  teamSubscription?: Subscription;
  holidays: any[] = [];
  contextMenuX: number = 0;
  contextMenuY: number = 0;
  showContextMenu: boolean = false;
  clickedDate: string = '';

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
    dateClick: this.handleDateClick.bind(this),
  };

  constructor(
    private holidayService: HolidayService,
    private leaveService: LeaveService,
    public dialog: MatDialog,
  ) { }

  ngOnInit(): void {
    this.teamSubscription = this.leaveService.selectedTeam$.subscribe(team => {
      this.selectedTeam = team;
      this.updateCalendarEvents();
    });

    this.leaveService.leavesUpdated$.subscribe(() => {
      this.updateCalendarEvents();
    });

    this.updateCalendarEvents();
  }

  ngAfterViewInit(): void {
    document.addEventListener('click', this.onDocumentClick.bind(this));
  }

  ngOnDestroy(): void {
    if (this.teamSubscription) {
      this.teamSubscription.unsubscribe();
    }
    document.removeEventListener('click', this.onDocumentClick.bind(this));
  }

  updateCalendarEvents(): void {
    this.fetchHolidays(new Date().getFullYear()); // Chargement initial des jours fériés

    if (this.selectedTeam) {
      this.fetchLeavesByTeam(this.selectedTeam);
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
          dateEnd.setDate(dateEnd.getDate() + 1);
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
      'royal': '#4169e1',
      'gold': '#ffd700',
      'mauve': '#8902e1',
      'blue': '#1e90ff',
    };
    return teamColors[team] || '#000000';
  }

  fetchHolidays(year: number): void {
    this.holidayService.getHolidays(year).subscribe(
      (holidayResponse: ApiResponse<Holiday[]>) => {
        if (holidayResponse && holidayResponse.data) {
          this.holidays = holidayResponse.data.map(holiday => {
            const dateStart = new Date(holiday.date_debut);
            const dateEnd = new Date(holiday.date_fin);
            if (holiday.is_fixe) {
              dateStart.setFullYear(year);
              dateEnd.setFullYear(year);
            }
            // Ajouter 1 jour à dateEnd pour inclure la fin de la journée
            dateEnd.setDate(dateEnd.getDate() + 1);

            return {
              title: holiday.description || 'Jour férié',
              start: dateStart.toISOString().split('T')[0],
              end: dateEnd.toISOString().split('T')[0],
              color: 'red',
              extendedProps: holiday
            };
          }).filter(event => event.start && event.end);

          this.updateCalendarWithEvents(this.holidays);
        }
      }, error => {
        console.error('Error fetching holidays:', error);
      });
  }

  updateCalendarWithEvents(events: any[]): void {
    this.calendarOptions = {
      ...this.calendarOptions,
      events: events
    };
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

  handleDateClick(arg: DateClickArg): void {
    this.clickedDate = arg.dateStr;
    this.contextMenuX = arg.jsEvent.clientX;
    this.contextMenuY = arg.jsEvent.clientY;
    this.showContextMenu = true;
  }

  handleContextMenuAction(action: string): void {
    this.showContextMenu = false;
    if (action === 'addLeave') {
      this.dialog.open(AddEditLeaveComponent, {
        width: '500px',
        data: { date_debut: this.clickedDate, date_fin: this.clickedDate }
      });
    } else if (action === 'addHoliday') {
      this.dialog.open(AddEditHolidayComponent, {
        width: '500px',
        data: { date_debut: this.clickedDate, date_fin: this.clickedDate }
      });
    }
  }

  onDocumentClick(event: MouseEvent): void {
    const contextMenuElement = this.contextMenuRef.nativeElement as HTMLElement;

    if (contextMenuElement && !contextMenuElement.contains(event.target as Node)) {
      this.showContextMenu = false;
    }
  }
}
