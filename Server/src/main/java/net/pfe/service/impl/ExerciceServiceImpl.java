package net.pfe.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.pfe.dto.exercice.ExerciceDTORequest;
import net.pfe.dto.jourFerie.JourFerieDTO;
import net.pfe.entity.Exercice;
import net.pfe.repository.ExerciceRepository;
import net.pfe.service.interf.ExerciceService;
import net.pfe.service.interf.JourFerieService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@Slf4j
public class ExerciceServiceImpl implements ExerciceService {
    private final ExerciceRepository exerciceRepository;
    private  JourFerieService jourFerieService;
    private final ModelMapper modelMapper;
    @Autowired
    public ExerciceServiceImpl(ExerciceRepository exerciceRepository, ModelMapper modelMapper) {
        this.exerciceRepository = exerciceRepository;
        this.modelMapper = modelMapper;
    }
    @Autowired
    public void setJourFerieService(@Lazy JourFerieService jourFerieService) {
        this.jourFerieService = jourFerieService; // Injection avec @Lazy
    }

    //@Scheduled(cron = "0 * * * * *")
    @Scheduled(cron = "0 0 0 1 1 *") // Chaque 1er janvier à minuit
    public void calculateAnnualExercise() {
        int currentYear = LocalDate.now().getYear();
        log.info("Début du calcul automatique de l'exercice pour l'année : {}", currentYear);
        calculerExerciceAnnuel(currentYear);
    }
    @Override
    public ExerciceDTORequest calculerExerciceAnnuel(int annee) {
        log.info("Calcul de l'exercice annuel pour l'année : {}", annee);

        // Récupérer tous les jours fériés
        List<JourFerieDTO> joursFeries = jourFerieService.getJoursFeriesForYear(annee);
        log.info("Jours fériés récupérés pour l'année {} : {}", annee, joursFeries.size());

        // Élargir les jours fériés pour inclure tous les jours de chaque intervalle
        List<LocalDate> holidaysForYear = new ArrayList<>();
        for (JourFerieDTO jourFerie : joursFeries) {
            LocalDate start = jourFerie.getDateDebut();
            LocalDate end = jourFerie.getDateFin();
            while (!start.isAfter(end)) {
                holidaysForYear.add(start);
                start = start.plusDays(1);
            }
        }

        // Calculer les jours ouvrables pour l'année spécifiée en tenant compte des jours fériés
        int nombreJoursOuvrables = calculateWorkingDays(annee, holidaysForYear);
        log.info("Nombre de jours ouvrables calculés pour l'année {} : {}", annee, nombreJoursOuvrables);

        // Chercher l'exercice existant pour l'année
        Optional<Exercice> exerciceOpt = exerciceRepository.findByAnnee(annee);

        Exercice exercice;
        if (exerciceOpt.isPresent()) {
            // Mettre à jour l'exercice existant
            exercice = exerciceOpt.get();
            exercice.setNombreJoursOuvrables(nombreJoursOuvrables);
            exercice.setLastUpdatedDate(LocalDate.now());
            log.info("Exercice existant trouvé pour l'année {}, mise à jour en cours...", annee);
        } else {
            // Créer un nouvel exercice
            exercice = Exercice.builder()
                    .idExercice(UUID.randomUUID())
                    .annee(annee)
                    .nombreJoursOuvrables(nombreJoursOuvrables)
                    .createdDate(LocalDate.now())
                    .lastUpdatedDate(LocalDate.now())
                    .build();
            log.info("Aucun exercice trouvé pour l'année {}, création d'un nouvel exercice...", annee);
        }

        // Sauvegarder l'exercice
        exercice= exerciceRepository.save(exercice);
        ExerciceDTORequest exerciceDTORequest=modelMapper.map(exercice,ExerciceDTORequest.class);
        log.info("Exercice sauvegardé pour l'année {} avec ID : {}", annee, exercice.getIdExercice());
        return exerciceDTORequest;
    }

    private int calculateWorkingDays(int year, List<LocalDate> holidays) {
        log.info("Calcul des jours ouvrables pour l'année {}", year);

        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        int workingDays = 0;
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            // Exclure les weekends (samedi et dimanche)
            if (currentDate.getDayOfWeek() != DayOfWeek.SATURDAY && currentDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
                // Vérifier si la date actuelle est un jour férié
                if (!holidays.contains(currentDate)) {
                    workingDays++;
                }
            }
            // Passer au jour suivant
            currentDate = currentDate.plusDays(1);
        }

        log.info("Nombre de jours ouvrables calculés pour l'année {} : {}", year, workingDays);

        return workingDays;
    }

}
