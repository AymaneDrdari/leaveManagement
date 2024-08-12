package net.pfe.service.impl;

import net.pfe.dto.collab.CollaborateurDTO;
import net.pfe.dto.conge.CongeDTO;
import net.pfe.dto.conge.CongeDTORequest;
import net.pfe.dto.conge.CongeDetailDTO;
import net.pfe.dto.jourFerie.JourFerieDTO;
import net.pfe.entity.*;
import net.pfe.exception.InsufficientLeaveBalanceException;
import net.pfe.exception.RessourceNotFoundException;
import net.pfe.repository.CollaborateurRepository;
import net.pfe.repository.CongeRepository;
import net.pfe.repository.ExerciceRepository;
import net.pfe.repository.SoldeCongeRepository;
import net.pfe.service.interf.CollaborateurService;
import net.pfe.service.interf.CongeService;
import net.pfe.service.interf.EquipeService;
import net.pfe.service.interf.JourFerieService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class CongeServiceImpl implements CongeService {

    private static final Logger logger = LoggerFactory.getLogger(CongeServiceImpl.class);

    private final CongeRepository congeRepository;
    private final EquipeService equipeService;
    private final JourFerieService jourFerieService;
    private final CollaborateurRepository collaborateurRepository;
    private final CollaborateurService collaborateurService;
    private final SoldeCongeRepository soldeCongeRepository;
    private final ModelMapper modelMapper;
    private final ExerciceRepository exerciceRepository;

    @Autowired
    public CongeServiceImpl(CongeRepository congeRepository, EquipeService equipeService, JourFerieService jourFerieService,
                            CollaborateurRepository collaborateurRepository, @Lazy CollaborateurService collaborateurService,
                            SoldeCongeRepository soldeCongeRepository, ModelMapper modelMapper, ExerciceRepository exerciceRepository) {
        this.congeRepository = congeRepository;
        this.equipeService = equipeService;
        this.jourFerieService = jourFerieService;
        this.collaborateurRepository = collaborateurRepository;
        this.collaborateurService = collaborateurService;
        this.soldeCongeRepository = soldeCongeRepository;
        this.modelMapper = modelMapper;
        this.exerciceRepository = exerciceRepository;
    }

    @Override
    public CongeDTO createConge(CongeDTORequest congeDTORequest) {
        logger.info("Création d'un nouveau congé avec les données : {}", congeDTORequest);

        // Vérifier si un congé identique existe déjà pour éviter de tenter une insertion qui échouera
        boolean congeExists = congeExistsForCollaborateur(congeDTORequest.getCollaborateurEmail(),
                congeDTORequest.getDateDebut(),
                congeDTORequest.getDateFin());
        if (congeExists) {
            logger.warn("Un congé identique existe déjà pour le collaborateur: {}", congeDTORequest.getCollaborateurEmail());
            throw new IllegalArgumentException("Un congé avec les mêmes dates existe déjà pour ce collaborateur.");
        }

        try {
            // Convertir la requête DTO en entité Conge
            Conge conge = modelMapper.map(congeDTORequest, Conge.class);

            // Récupérer le collaborateur par e-mail
            Collaborateur collaborateur = getCollaborateurByEmail(congeDTORequest.getCollaborateurEmail());
            conge.setCollaborateur(collaborateur);

            LocalDate dateDebut = congeDTORequest.getDateDebut();
            LocalDate dateFin = congeDTORequest.getDateFin();

            // Récupérer les jours fériés pour l'année en cours
            int currentYear = dateDebut.getYear();
            List<JourFerieDTO> holidays = jourFerieService.getJoursFeriesForYear(currentYear);

            // Calculer le nombre de jours ouvrables pour le congé
            double joursConge = calculateWorkingDaysForConge(dateDebut, dateFin, holidays, congeDTORequest.isDemiJourneeMatin(), congeDTORequest.isDemiJourneeSoir());
            conge.setNombreJoursPris(joursConge);

            // Récupérer l'exercice de l'année en cours
            Exercice exercice = getExerciceByYear(currentYear);

            // Récupérer ou créer le solde de congé pour le collaborateur et l'année en cours
            SoldeConge soldeConge = soldeCongeRepository.findByCollaborateurAndExercice_Annee(collaborateur, currentYear)
                    .orElseGet(() -> SoldeConge.builder()
                            .collaborateur(collaborateur)
                            .exercice(exercice)
                            .soldeInitial(20)
                            .soldeConsomme(0)
                            .soldeRestant(20)
                            .build());

            // Vérifier si le solde restant est suffisant
            if (soldeConge.getSoldeRestant() < joursConge) {
                throw new InsufficientLeaveBalanceException("Votre solde de congé est insuffisant. Il vous reste seulement " + soldeConge.getSoldeRestant() + " jours.");
            }

            // Mettre à jour le solde consommé et le solde restant
            soldeConge.setSoldeConsomme(soldeConge.getSoldeConsomme() + joursConge);
            soldeConge.calculerSoldeRestant();

            // Sauvegarder les entités mises à jour
            soldeCongeRepository.save(soldeConge);
            conge = congeRepository.save(conge);

            logger.info("Congé créé avec succès avec ID : {}", conge.getIdConge());
            return mapCongeToDTO(conge);
        } catch (DataIntegrityViolationException ex) {
            logger.error("Tentative de création d'un congé dupliqué interceptée : {}", congeDTORequest, ex);
            throw new IllegalStateException("Un congé avec les mêmes dates et collaborateur existe déjà. Veuillez vérifier les informations et réessayer.", ex);
        }
    }

    // Calculer le nombre de jours ouvrables pour un congé donné
    public double calculateWorkingDaysForConge(LocalDate dateDebut, LocalDate dateFin, List<JourFerieDTO> holidays, boolean demiJourneeMatin, boolean demiJourneeSoir) {
        double workingDays = 0;
        LocalDate currentDate = dateDebut;

        while (!currentDate.isAfter(dateFin)) {
            if (isWorkingDay(currentDate, holidays)) {
                workingDays++;
            }
            currentDate = currentDate.plusDays(1);
        }

        // Ajuster pour les demi-journées
        if (demiJourneeMatin && isWorkingDay(dateDebut, holidays)) {
            workingDays -= 0.5;
            logger.debug("Demi-journée matin ajustée : {}", workingDays);
        }

        if (demiJourneeSoir && isWorkingDay(dateFin, holidays)) {
            workingDays -= 0.5;
            logger.debug("Demi-journée soir ajustée : {}", workingDays);
        }

        logger.debug("Jours ouvrables calculés pour le congé de {} à {} : {}", dateDebut, dateFin, workingDays);
        return workingDays;
    }

    // Vérifier si un jour donné est un jour ouvrable
    private boolean isWorkingDay(LocalDate date, List<JourFerieDTO> holidays) {
        if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return false;
        }
        for (JourFerieDTO holiday : holidays) {
            if (!date.isBefore(holiday.getDateDebut()) && !date.isAfter(holiday.getDateFin())) {
                return false;
            }
        }
        return true;
    }

    // Récupérer un collaborateur par e-mail
    private Collaborateur getCollaborateurByEmail(String email) {
        logger.info("Récupération du collaborateur par e-mail : {}", email);

        Collaborateur collaborateur = collaborateurRepository.findByEmail(email);
        if (collaborateur == null) {
            logger.error("Collaborateur non trouvé avec l'e-mail : {}", email);
            throw new RessourceNotFoundException("Collaborateur non trouvé avec l'e-mail : " + email);
        }
        logger.info("Collaborateur trouvé : {}", collaborateur);
        return collaborateur;
    }

    // Récupérer un exercice par année
    private Exercice getExerciceByYear(int year) {
        return exerciceRepository.findByAnnee(year)
                .orElseThrow(() -> new RessourceNotFoundException("Exercice non trouvé pour l'année : " + year));
    }

    @Override
    public CongeDTO getCongeById(UUID id) {
        logger.info("Récupération du congé par ID : {}", id);

        // Récupérer le congé par son ID
        Conge conge = congeRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Congé non trouvé avec l'ID : {}", id);
                    return new RessourceNotFoundException("Congé non trouvé avec l'ID : " + id);
                });
        logger.info("Congé trouvé : {}", conge);
        return mapCongeToDTO(conge);
    }

    @Override
    public List<CongeDTO> getAllConges() {
        logger.info("Récupération de tous les congés");

        // Récupérer tous les congés
        List<Conge> conges = congeRepository.findAll();
        logger.info("Nombre de congés trouvés : {}", conges.size());
        return mapCongesToDTOs(conges);
    }

    @Override
    public CongeDTO updateConge(UUID id, CongeDTORequest congeDTORequest) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID du congé est requis.");
        }

        logger.info("Mise à jour du congé avec l'ID : {}", congeDTORequest.getIdConge());

        // Récupérer le congé existant par son ID
        Conge existingConge = getExistingConge(congeDTORequest.getIdConge());

        // Mettre à jour les champs du congé existant
        modelMapper.map(congeDTORequest, existingConge);

        LocalDate dateDebut = existingConge.getDateDebut();
        LocalDate dateFin = existingConge.getDateFin();

        int currentYear = dateDebut.getYear();
        List<JourFerieDTO> holidays = jourFerieService.getJoursFeriesForYear(currentYear);

        double joursConge = calculateWorkingDaysForConge(dateDebut, dateFin, holidays, congeDTORequest.isDemiJourneeMatin(), congeDTORequest.isDemiJourneeSoir());

        Collaborateur collaborateur = existingConge.getCollaborateur();
        double ancienNombreJoursPris = existingConge.getNombreJoursPris();
        double differenceJoursPris = joursConge - ancienNombreJoursPris;

        // Récupérer le solde de congé pour le collaborateur et l'année en cours
        SoldeConge soldeConge = soldeCongeRepository.findByCollaborateurAndExercice_Annee(collaborateur, currentYear)
                .orElseThrow(() -> new RessourceNotFoundException("Solde de congé non trouvé pour le collaborateur et l'année spécifiée"));

        // Mettre à jour le solde consommé et le solde restant
        soldeConge.setSoldeConsomme(soldeConge.getSoldeConsomme() + differenceJoursPris);
        soldeConge.calculerSoldeRestant();

        existingConge.setNombreJoursPris(joursConge);

        soldeCongeRepository.save(soldeConge);
        existingConge = congeRepository.save(existingConge);

        logger.info("Congé mis à jour avec succès avec l'ID : {}", existingConge.getIdConge());
        return mapCongeToDTO(existingConge);
    }

    // Récupérer un congé existant par ID
    private Conge getExistingConge(UUID idConge) {
        logger.info("Récupération du congé existant par ID : {}", idConge);

        return congeRepository.findById(idConge)
                .orElseThrow(() -> {
                    logger.error("Congé non trouvé avec l'ID : {}", idConge);
                    return new RessourceNotFoundException("Congé non trouvé avec l'ID : " + idConge);
                });
    }

    @Override
    public List<CongeDTO> getCongesByCollaborateur(String nom, String prenom) {
        // Récupérer les collaborateurs par nom et/ou prénom
        List<CollaborateurDTO> collaborateursDTO;
        if (nom != null && prenom != null) {
            collaborateursDTO = collaborateurService.getCollaborateursByNomAndPrenom(nom, prenom);
        } else if (nom != null) {
            collaborateursDTO = collaborateurService.getCollaborateursByNom(nom);
        } else {
            collaborateursDTO = collaborateurService.getCollaborateursByPrenom(prenom);
        }

        if (collaborateursDTO.isEmpty()) {
            logger.warn("Aucun collaborateur trouvé avec le nom '{}' et le prénom '{}'", nom, prenom);
            throw new RessourceNotFoundException("Aucun collaborateur trouvé avec le nom '" + nom + "' et le prénom '" + prenom + "'");
        }

        // Supposant qu'un seul collaborateur est retourné, vous pouvez prendre le premier de la liste
        CollaborateurDTO collaborateurDTO = collaborateursDTO.get(0);
        Collaborateur collaborateur = modelMapper.map(collaborateurDTO, Collaborateur.class);

        logger.info("Récupération des congés pour le collaborateur : {}", collaborateur);

        // Récupérer les congés pour le collaborateur
        List<Conge> conges = congeRepository.findByCollaborateur(collaborateur);
        if (conges.isEmpty()) {
            logger.warn("Aucun congé trouvé pour le collaborateur : {}", collaborateur);
            throw new RessourceNotFoundException("Ce collaborateur n'a aucun congé enregistré.");
        }

        logger.info("Nombre de congés trouvés pour le collaborateur : {}", conges.size());
        return mapCongesToDTOs(conges);
    }

    @Override
    public boolean congeExistsForCollaborateur(String collaborateurEmail, LocalDate dateDebut, LocalDate dateFin) {
        logger.info("Vérification de l'existence d'un congé pour l'email : {}, du : {}, au : {}", collaborateurEmail, dateDebut, dateFin);

        // Récupérer le collaborateur par e-mail
        Collaborateur collaborateur = collaborateurRepository.findByEmail(collaborateurEmail);
        if (collaborateur == null) {
            logger.error("Aucun collaborateur trouvé avec l'email : {}", collaborateurEmail);
            return false;  // On pourrait aussi lancer une exception selon la manière dont vous souhaitez gérer ce cas.
        }

        // Vérifier l'existence d'un congé avec les dates spécifiées pour ce collaborateur
        boolean exists = congeRepository.existsByCollaborateurAndDateDebutAndDateFin(collaborateur, dateDebut, dateFin);
        logger.info("Existence d'un congé pour l'email {} entre les dates {} et {} : {}", collaborateurEmail, dateDebut, dateFin, exists);

        return exists;
    }


    @Override
    public void deleteConge(UUID id) {
        logger.info("Suppression du congé avec l'ID : {}", id);

        // Récupérer le congé par son ID
        Conge conge = congeRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("Congé non trouvé avec l'ID : " + id));

        Collaborateur collaborateur = conge.getCollaborateur();
        double joursPrisCongé = conge.getNombreJoursPris();
        int annee = conge.getDateDebut().getYear(); // Récupérer l'année du congé

        // Récupérer le solde de congé pour le collaborateur et l'année spécifiée
        SoldeConge soldeConge = soldeCongeRepository.findByCollaborateurAndExercice_Annee(collaborateur, annee)
                .orElseThrow(() -> new RessourceNotFoundException("Solde de congé non trouvé pour le collaborateur et l'année spécifiée"));

        // Soustraire les jours consommés du solde consommé
        soldeConge.setSoldeConsomme(soldeConge.getSoldeConsomme() - joursPrisCongé);
        soldeConge.calculerSoldeRestant();

        soldeCongeRepository.save(soldeConge);
        congeRepository.delete(conge);

        logger.info("Congé supprimé avec succès avec l'ID : {}", id);
    }

    // Mapper un congé vers son DTO correspondant
    private CongeDTO mapCongeToDTO(Conge conge) {
        CongeDTO congeDTO = modelMapper.map(conge, CongeDTO.class);
        if (conge.getCollaborateur() != null) {
            congeDTO.setCollaborateurEmail(conge.getCollaborateur().getEmail());
        }
        return congeDTO;
    }

    // Mapper une liste de congés vers une liste de leurs DTOs correspondants
    private List<CongeDTO> mapCongesToDTOs(List<Conge> conges) {
        return conges.stream()
                .map(this::mapCongeToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isCollaborateurEnConge(Collaborateur collaborateur, LocalDate dateStart, LocalDate dateEnd) {
        List<Conge> conges = congeRepository.findByCollaborateur(collaborateur);
        return conges.stream().anyMatch(conge ->
                !conge.getDateFin().isBefore(dateStart) && !conge.getDateDebut().isAfter(dateEnd)
        );
    }

    @Override
    public List<CongeDTO> getJCongéPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Conge> congePage = congeRepository.findAll(pageable);
        List<Conge> conges = congePage.getContent();
        List<CongeDTO> congeDTOList=mapCongesToDTOs(conges);
        return congeDTOList;
    }

    @Override
    public int countCollaborateursEnCongeParEquipeAnnee(String nomEquipe) {
        UUID equipeCode = equipeService.findCodeEquipeByNom(nomEquipe);
        if (equipeCode == null) {
            throw new RessourceNotFoundException("Équipe introuvable avec le nom : " + nomEquipe);
        }

        List<Collaborateur> collaborateurs = collaborateurRepository.findByEquipeCode(equipeCode);
        if (collaborateurs.isEmpty()) {
            throw new RessourceNotFoundException("Aucun collaborateur n'a été trouvé dans cette équipe.");
        }

        long count = collaborateurs.stream()
                .filter(collaborateur -> isCollaborateurEnConge(collaborateur, LocalDate.now().withDayOfYear(1), LocalDate.now().withDayOfYear(LocalDate.now().lengthOfYear())))
                .count();

        return (int) count;
    }

    @Override
    public List<CongeDetailDTO> findCongesByEquipe(String nomEquipe) {
        List<Conge> conges = congeRepository.findByCollaborateur_Equipe_Nom(nomEquipe);
        return conges.stream().map(this::mapToCongeDetailDTO).collect(Collectors.toList());
    }

    private CongeDetailDTO mapToCongeDetailDTO(Conge conge) {
        double duree = ChronoUnit.DAYS.between(conge.getDateDebut(), conge.getDateFin()) + 1;
        return CongeDetailDTO.builder()
                .collaborateurNom(conge.getCollaborateur().getNom())
                .collaborateurPrenom(conge.getCollaborateur().getPrenom())
                .dateDebut(conge.getDateDebut())
                .dateFin(conge.getDateFin())
                .duree(duree)
                .build();
    }

}