import {Component, OnInit, OnDestroy, ElementRef, ViewChild, AfterViewInit} from '@angular/core';
import { CalendarOptions, EventClickArg, DatesSetArg } from '@fullcalendar/core';
import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridPlugin from '@fullcalendar/timegrid';
import interactionPlugin, {DateClickArg} from '@fullcalendar/interaction';
import {forkJoin, map, Observable, Subscription} from 'rxjs';
import { MatDialog } from '@angular/material/dialog';
import { LeaveService } from '../../services/leave.service';
import { ApiResponse } from '../../models/ApiResponse';
import { HolidayService } from '../../services/holiday.service';
import { HolidayModalComponent } from '../holiday/holiday-modal/holiday-modal.component';
import { Holiday } from '../../models/holiday';
import { CongeDetailDTO } from "../../models/conge-detail-dto.model";
import { AddEditLeaveComponent } from '../leave/add-edit-leave/add-edit-leave.component';
import { AddEditHolidayComponent } from '../holiday/add-edit-holiday/add-edit-holiday.component';
import {EquipeService} from "../../services/equipe.service";
import {EquipeDTO} from "../../models/equipe.model";
import {Leave} from "../../models/leave";

@Component({
  selector: 'app-calendar',
  templateUrl: './calendar.component.html',
  styleUrls: ['./calendar.component.css']
})
export class CalendarComponent implements OnInit, OnDestroy, AfterViewInit {
  @ViewChild('contextMenuRef') contextMenuRef!: ElementRef;
  selectedTeams: string[] = [];  // Liste des équipes sélectionnées
  teamSubscription?: Subscription;
  holidays: any[] = [];
  leaveRequests: Observable<CongeDetailDTO[]>[] = [];  // Déclarez leaveRequests comme tableau d'Observables
  currentMonth: number;
  currentYear: number;
  contextMenuX: number = 0;
  contextMenuY: number = 0;
  showContextMenu: boolean = false;
  clickedDate: string = '';
  calendarEvents: any[] = [];

  calendarOptions: CalendarOptions = {
    plugins: [dayGridPlugin, timeGridPlugin, interactionPlugin],
    initialView: 'dayGridMonth',
    headerToolbar: {
      left: 'prev,next today',
      center: 'title',
      right: 'dayGridMonth,timeGridWeek,timeGridDay'
    },
    events: [],
    height: 600,
    contentHeight: 100,
    themeSystem: 'bootstrap',
    eventClick: this.handleEventClick.bind(this),
    datesSet: this.handleDatesSet.bind(this),
    dateClick: this.handleDateClick.bind(this),
  };

  constructor(
    private holidayService: HolidayService,
    private leaveService: LeaveService,
    private equipeService:EquipeService,
    public dialog: MatDialog,
  ) {
    const date = new Date();
    this.currentMonth = date.getMonth() + 1; // Current month
    this.currentYear = date.getFullYear(); // Current year
  }

  ngOnInit(): void {
    this.teamSubscription = this.leaveService.selectedTeam$.subscribe(team => {
      this.setActiveCard(team); // Mise à jour des équipes sélectionnées
    });

    this.leaveService.leavesUpdated$.subscribe(() => {
      this.updateCalendarEvents(); // Mise à jour des événements du calendrier
    });

    this.loadAllTeamsLeaves();
    this.fetchHolidays(this.currentYear); // Charger les jours fériés
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

  // Charger les congés de toutes les équipes au démarrage
  loadAllTeamsLeaves(): void {
    this.equipeService.getAllEquipes().subscribe(
      (equipes: EquipeDTO[]) => {
        this.selectedTeams = equipes.map(equipe => equipe.nom); // Suppose que 'nom' est la propriété du nom d'équipe
        this.updateCalendarEvents();
      },
      error => {
        console.error('Error fetching teams:', error);
      }
    );
  }

  updateCalendarEvents(): void {
    this.calendarEvents = [...this.holidays];
    if (this.selectedTeams.length > 0) {
      const leaveRequests: Observable<Leave[]>[] = []; // Initialize leaveRequests here
      const fetchedTeams = new Set<string>();
      const startYear = new Date().getFullYear() - 5;
      const endYear = new Date().getFullYear();

      this.selectedTeams.forEach(team => {
        for (let year = startYear; year <= endYear; year++) {
          leaveRequests.push(
            this.leaveService.getCongesByEquipe(team, year).pipe(
              map((leaveResponse: ApiResponse<CongeDetailDTO[]>) => {
                if (leaveResponse && leaveResponse.data) {
                  return leaveResponse.data.map(congeDetail => {
                    const dateStart = new Date(congeDetail.date_debut);
                    const dateEnd = new Date(congeDetail.date_fin);
                    dateEnd.setDate(dateEnd.getDate() + 1);
                    return {
                      title: `${congeDetail.collaborateur_nom} ${congeDetail.collaborateur_prenom}`,
                      start: dateStart.toISOString().split('T')[0],
                      end: dateEnd.toISOString().split('T')[0],
                      color: congeDetail.couleur_equipe || '#000000',
                      extendedProps: congeDetail
                    } as any; // Cast to any to bypass the type issue
                  }).filter(event => event.start && event.end);
                }
                return [];
              })
            )
          );
        }
      });

      // Use forkJoin to wait for all requests to complete
      forkJoin(leaveRequests).subscribe(allLeaves => {
        allLeaves.forEach(leaves => {
          if (leaves.length) {
            this.calendarEvents.push(...leaves);
          }
        });
        this.updateCalendarWithEvents();
      });
    } else {
      this.updateCalendarWithEvents();
    }
  }


  updateCalendarWithEvents(): void {
    const uniqueEvents = Array.from(new Set(this.calendarEvents.map(e => JSON.stringify(e))))
      .map(e => JSON.parse(e));
    this.calendarOptions = { ...this.calendarOptions, events: uniqueEvents };
  }

  setActiveCard(card: string | null) {
    if (card) {
      const index = this.selectedTeams.indexOf(card);
      if (index === -1) {
        this.selectedTeams.push(card);  // Ajouter l'équipe à la liste si elle n'est pas encore sélectionnée
      } else {
        this.selectedTeams.splice(index, 1);  // Supprimer l'équipe si elle est déjà sélectionnée
      }
      this.updateCalendarEvents();  // Mettre à jour les événements en fonction des équipes sélectionnées
    }
  }

  fetchHolidays(year: number): void {
    this.holidayService.getHolidays(year).subscribe(
      (holidayResponse: ApiResponse<Holiday[]>) => {
        if (holidayResponse && holidayResponse.data) {
          this.holidays = holidayResponse.data.flatMap(holiday => {
            const startDates = [];
            const endDates: any[] = [];

            // Pour chaque jour férié, ajoutez les dates pour les années passées et futures
            const isRecurring = !holiday.is_fixe;  // Supposons que `is_fixe` indique si c'est récurrent

            if (isRecurring) {
              for (let y = year - 10; y <= year + 10; y++) {  // Plage d'années, ajustez selon vos besoins
                const dateStart = new Date(holiday.date_debut);
                const dateEnd = new Date(holiday.date_fin);

                dateStart.setFullYear(y);
                dateEnd.setFullYear(y);

                // Ajouter 1 jour à la date de fin pour inclure le dernier jour
                dateEnd.setDate(dateEnd.getDate() + 1);

                startDates.push(dateStart.toISOString().split('T')[0]);
                endDates.push(dateEnd.toISOString().split('T')[0]);
              }
            } else {
              const dateStart = new Date(holiday.date_debut);
              const dateEnd = new Date(holiday.date_fin);

              dateStart.setFullYear(year);
              dateEnd.setFullYear(year);

              // Ajouter 1 jour à la date de fin pour inclure le dernier jour
              dateEnd.setDate(dateEnd.getDate() + 1);

              startDates.push(dateStart.toISOString().split('T')[0]);
              endDates.push(dateEnd.toISOString().split('T')[0]);
            }

            return startDates.map((start, index) => ({
              title: holiday.description || 'Jour férié',
              start: start,
              end: endDates[index],
              color: 'red',
              extendedProps: holiday
            }));
          }).filter(event => event.start && event.end);

          this.updateCalendarEvents(); // Mettre à jour les événements avec les jours fériés
        }
        // Appeler ici loadLeaveCounts pour mettre à jour les comptes après avoir chargé les jours fériés
        //this.loadLeaveCounts(this.currentMonth, this.currentYear);
      },
      error => {
        console.error('Error fetching holidays:', error);
      }
    );
  }


  handleDatesSet(arg: DatesSetArg): void {
    // Récupération de l'année à partir de l'objet date
    const year = arg.start.getFullYear();
    this.currentYear = year;

    const startDate = arg.start.toISOString().split('T')[0];
    const endDate = arg.end.toISOString().split('T')[0];

    console.log(`Date Start: ${startDate}, Date End: ${endDate}`);

    // Appeler le service pour définir la date sélectionnée
    this.leaveService.setSelectedDate(startDate, endDate);
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
    console.log('Date clicked :', this.clickedDate)
    this.contextMenuX = arg.jsEvent.clientX;
    this.contextMenuY = arg.jsEvent.clientY;
    this.showContextMenu = true;
    // this.leaveService.setSelectedDate(arg.dateStr);
    //this.loadLeaveCounts(this.currentMonth, this.currentYear); // Ensure leave counts are updated based on selected date

  }


  handleContextMenuAction(action: string): void {
    this.showContextMenu = false;
    if (action === 'addLeave') {
      this.dialog.open(AddEditLeaveComponent, {
        width: '500px',
        data: { date_debut: this.clickedDate, date_fin: this.clickedDate }
      });
    } else if (action === 'addHoliday') {
      const dialogRef = this.dialog.open(AddEditHolidayComponent, {
        width: '500px',
        data: { date_debut: this.clickedDate, date_fin: this.clickedDate }
      });

      // Ajoutez un callback pour mettre à jour les jours fériés après la fermeture du dialog
      dialogRef.afterClosed().subscribe(result => {
        if (result) {
          // Mettez à jour les jours fériés avec les nouvelles données
          this.fetchHolidays(new Date().getFullYear());
        }
      });
    }
  }

  onDocumentClick(event: MouseEvent): void {
    if (!this.contextMenuRef || !this.contextMenuRef.nativeElement) {
      return; // Ne fait rien si `contextMenuRef` n'est pas défini
    }

    const clickedInsideContextMenu = this.contextMenuRef.nativeElement.contains(event.target);
    if (!clickedInsideContextMenu) {
      this.showContextMenu = false;
    }
  }

}
