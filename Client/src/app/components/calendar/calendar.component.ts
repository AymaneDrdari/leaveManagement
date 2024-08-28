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
  @ViewChild('contextMenuRef') contextMenuRef!: ElementRef; // Référence à la zone de menu contextuel
  selectedTeams: string[] = [];  // Liste des équipes sélectionnées
  teamSubscription?: Subscription; // Abonnement pour suivre les changements des équipes
  holidays: any[] = []; // Liste des jours fériés
  currentMonth: number; // Mois courant
  currentYear: number; // Année courante
  contextMenuX: number = 0; // Position X du menu contextuel
  contextMenuY: number = 0; // Position Y du menu contextuel
  showContextMenu: boolean = false; // Indicateur pour afficher le menu contextuel
  clickedDate: string = ''; // Date sur laquelle l'utilisateur a cliqué
  calendarEvents: any[] = []; // Événements à afficher sur le calendrier

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
    // S'abonner aux changements d'équipe
    this.teamSubscription = this.leaveService.selectedTeam$.subscribe(team => {
      this.setActiveCard(team); // Mise à jour des équipes sélectionnées
    });

    // S'abonner aux mises à jour des demandes de congé
    this.leaveService.leavesUpdated$.subscribe(() => {
      this.updateCalendarEvents(); // Mise à jour des événements du calendrier
    });

    this.loadAllTeamsLeaves(); // Charger les congés de toutes les équipes
    this.fetchHolidays(this.currentYear); // Charger les jours fériés pour l'année actuelle
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

  // Mettre à jour les événements du calendrier
  updateCalendarEvents(): void {
    this.calendarEvents = [...this.holidays]; // Ajouter les jours fériés aux événements du calendrier
    if (this.selectedTeams.length > 0) {
      const leaveRequests: Observable<Leave[]>[] = []; // Tableau d'Observables pour les demandes de congé
      const startYear = new Date().getFullYear() - 5; // Année de début pour les demandes de congé
      const endYear = new Date().getFullYear(); // Année de fin pour les demandes de congé

      // Pour chaque équipe sélectionnée
      this.selectedTeams.forEach(team => {
        // Boucle pour chaque année de début à fin
        for (let year = startYear; year <= endYear; year++) {
          // Récupérer les congés par équipe et année
          leaveRequests.push(
            this.leaveService.getCongesByEquipe(team, year).pipe(
              map((leaveResponse: ApiResponse<CongeDetailDTO[]>) => {
                if (leaveResponse && leaveResponse.data) {
                  // Mapper les détails de congé pour chaque événement
                  return leaveResponse.data.map(congeDetail => {
                    const dateStart = new Date(congeDetail.date_debut);
                    const dateEnd = new Date(congeDetail.date_fin);
                    dateEnd.setDate(dateEnd.getDate() + 1); // Ajouter 1 jour à la date de fin
                    return {
                      title: `${congeDetail.collaborateur_nom} ${congeDetail.collaborateur_prenom}`,
                      start: dateStart.toISOString().split('T')[0], // Format de date ISO
                      end: dateEnd.toISOString().split('T')[0], // Format de date ISO
                      color: congeDetail.couleur_equipe || '#000000', // Couleur de l'équipe
                      extendedProps: congeDetail // Propriétés étendues pour les détails
                    } as any; // Cast pour contourner les problèmes de type
                  }).filter(event => event.start && event.end); // Filtrer les événements sans dates valides
                }
                return []; // Retourner un tableau vide si aucune donnée
              })
            )
          );
        }
      });

      // Utiliser forkJoin pour attendre que toutes les requêtes soient complètes
      forkJoin(leaveRequests).subscribe(allLeaves => {
        allLeaves.forEach(leaves => {
          if (leaves.length) {
            this.calendarEvents.push(...leaves); // Ajouter tous les congés récupérés aux événements du calendrier
          }
        });
        this.updateCalendarWithEvents(); // Mettre à jour le calendrier avec les nouveaux événements
      });
    } else {
      this.updateCalendarWithEvents(); // Mettre à jour le calendrier si aucune équipe n'est sélectionnée
    }
  }

  // Mettre à jour le calendrier avec les événements uniques
  updateCalendarWithEvents(): void {
    const uniqueEvents = Array.from(new Set(this.calendarEvents.map(e => JSON.stringify(e))))
      .map(e => JSON.parse(e)); // Supprimer les doublons
    this.calendarOptions = { ...this.calendarOptions, events: uniqueEvents }; // Mettre à jour les options du calendrier
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

            // Vérifiez si le jour férié est récurrent
            const isRecurring = !holiday.is_fixe; // Supposons que `is_fixe` indique si c'est récurrent

            // Pour les jours fériés fixes, les ajouter pour chaque année dans la plage spécifiée
            if (!isRecurring) {
              for (let y = year - 10; y <= year + 10; y++) {
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
              // Pour les jours fériés non fixes, ajouter uniquement pour l'année spécifiée
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
              color: '#FF0000',
              extendedProps: holiday
            }));
          }).filter(event => event.start && event.end);

          this.updateCalendarEvents(); // Mettre à jour les événements avec les jours fériés
        }
        // Appeler ici loadLeaveCounts pour mettre à jour les comptes après avoir chargé les jours fériés
        // this.loadLeaveCounts(this.currentMonth, this.currentYear);
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


  // Gestion des clics sur le document
  onDocumentClick(event: MouseEvent): void {
    const target = event.target as HTMLElement;
    // Vérifier si le clic est en dehors du menu contextuel
    if (this.showContextMenu && !this.contextMenuRef.nativeElement.contains(target)) {
      this.showContextMenu = false; // Cacher le menu contextuel
    }
  }

}
