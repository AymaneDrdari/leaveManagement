package net.atos.conge.service.impl;

import net.atos.common.client.CollaborateurServiceClient;
import net.atos.common.client.ExerciceServiceClient;
import net.atos.common.client.JourFerieServiceClient;
import net.atos.common.client.SoldeCongeServiceClient;
import net.atos.common.dto.collab.CollaborateurDTO;
import net.atos.common.dto.exercice.ExerciceDTO;
import net.atos.common.dto.jourFerie.JourFerieDTO;
import net.atos.common.dto.soldeConge.SoldeCongeDTO;
import net.atos.common.entity.Collaborateur;
import net.atos.common.entity.Conge;
import net.atos.common.entity.Exercice;
import net.atos.common.entity.SoldeConge;
import net.atos.common.exception.InsufficientLeaveBalanceException;
import net.atos.common.exception.RessourceNotFoundException;
import net.atos.conge.dto.CongeDTO;
import net.atos.conge.dto.CongeDTORequest;
import net.atos.conge.repository.CongeRepository;
import net.atos.conge.service.interf.CongeService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class CongeServiceImpl implements CongeService {

    private static final Logger logger = LoggerFactory.getLogger(CongeServiceImpl.class);

    private final CongeRepository congeRepository;
    private final CollaborateurServiceClient collaborateurServiceClient;
    private final SoldeCongeServiceClient soldeCongeServiceClient;
    private final JourFerieServiceClient jourFerieServiceClient;
    private final ModelMapper modelMapper;
    private final ExerciceServiceClient exerciceServiceClient;

    public CongeServiceImpl(CongeRepository congeRepository,
                            CollaborateurServiceClient collaborateurServiceClient,
                            SoldeCongeServiceClient soldeCongeServiceClient,
                            JourFerieServiceClient jourFerieServiceClient,
                            ModelMapper modelMapper,
                            ExerciceServiceClient exerciceServiceClient) {
        this.congeRepository = congeRepository;
        this.collaborateurServiceClient = collaborateurServiceClient;
        this.soldeCongeServiceClient = soldeCongeServiceClient;
        this.jourFerieServiceClient = jourFerieServiceClient;
        this.modelMapper = modelMapper;
        this.exerciceServiceClient = exerciceServiceClient;
    }

    @Override
    public CongeDTO createConge(CongeDTORequest congeDTORequest) {
        logger.info("Création d'un nouveau congé avec les données : {}", congeDTORequest);

        Conge conge = modelMapper.map(congeDTORequest, Conge.class);
        Collaborateur collaborateur = getCollaborateurByEmail(congeDTORequest.getCollaborateurEmail());

        LocalDate dateDebut = congeDTORequest.getDateDebut();
        LocalDate dateFin = congeDTORequest.getDateFin();

        int currentYear = dateDebut.getYear();
        List<JourFerieDTO> holidays = jourFerieServiceClient.getJoursFeriesForYear(currentYear).getData();

        double joursConge = calculateWorkingDaysForConge(dateDebut, dateFin, holidays, congeDTORequest.isDemiJourneeMatin(), congeDTORequest.isDemiJourneeSoir());
        Exercice exercice = getExerciceByYear(currentYear);

        SoldeCongeDTO soldeCongeDTO = soldeCongeServiceClient.getSoldeCongeByCollaborateurAndYear(collaborateur.getId(), currentYear).getData();
        SoldeConge soldeConge = modelMapper.map(soldeCongeDTO, SoldeConge.class);

        if (soldeConge.getSoldeRestant() < joursConge) {
            throw new InsufficientLeaveBalanceException("Votre solde de congé est insuffisant. Il vous reste seulement " + soldeConge.getSoldeRestant() + " jours.");
        }

        soldeConge.setSoldeConsomme(soldeConge.getSoldeConsomme() + joursConge);
        soldeConge.calculerSoldeRestant();

        conge.setNombreJoursPris(joursConge);
        conge.setCollaborateur(collaborateur);

        soldeCongeServiceClient.addSoldeConge(modelMapper.map(soldeConge, SoldeCongeDTO.class));
        conge = congeRepository.save(conge);

        logger.info("Congé créé avec succès avec ID : {}", conge.getIdConge());
        return mapCongeToDTO(conge);
    }

    double calculateWorkingDaysForConge(LocalDate dateDebut, LocalDate dateFin, List<JourFerieDTO> holidays, boolean demiJourneeMatin, boolean demiJourneeSoir) {
        double workingDays = 0;
        LocalDate currentDate = dateDebut;

        while (!currentDate.isAfter(dateFin)) {
            if (isWorkingDay(currentDate, holidays)) {
                workingDays++;
            }
            currentDate = currentDate.plusDays(1);
        }

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

    private Collaborateur getCollaborateurByEmail(String email) {
        logger.info("Récupération du collaborateur par e-mail : {}", email);

        CollaborateurDTO collaborateurDTO = collaborateurServiceClient.getCollaborateurByEmail(email).getData();
        if (collaborateurDTO == null) {
            logger.error("Collaborateur non trouvé avec l'e-mail : {}", email);
            throw new RessourceNotFoundException("Collaborateur non trouvé avec l'e-mail : " + email);
        }
        logger.info("Collaborateur trouvé : {}", collaborateurDTO);
        return modelMapper.map(collaborateurDTO, Collaborateur.class);
    }

    private Exercice getExerciceByYear(int year) {
        ExerciceDTO exerciceDTO = exerciceServiceClient.getExerciceByYear(year).getData();
        return modelMapper.map(exerciceDTO, Exercice.class);
    }

    @Override
    public CongeDTO getCongeById(UUID id) {
        logger.info("Récupération du congé par ID : {}", id);

        Conge conge = congeRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("Congé non trouvé avec l'ID : " + id));

        return mapCongeToDTO(conge);
    }

    @Override
    public List<CongeDTO> getAllConges() {
        logger.info("Récupération de tous les congés");

        List<Conge> conges = congeRepository.findAll();
        logger.info("Nombre de congés trouvés : {}", conges.size());
        return mapCongesToDTOs(conges);
    }

    @Override
    public CongeDTO updateConge(CongeDTORequest congeDTORequest) {
        if (congeDTORequest.getIdConge() == null) {
            logger.error("L'ID du congé est absent dans la requête.");
            throw new IllegalArgumentException("L'ID du congé est requis.");
        }

        logger.info("Mise à jour du congé avec l'ID : {}", congeDTORequest.getIdConge());

        Conge existingConge = getExistingConge(congeDTORequest.getIdConge());

        modelMapper.map(congeDTORequest, existingConge);

        LocalDate dateDebut = existingConge.getDateDebut();
        LocalDate dateFin = existingConge.getDateFin();

        int currentYear = dateDebut.getYear();
        List<JourFerieDTO> holidays = jourFerieServiceClient.getJoursFeriesForYear(currentYear).getData();

        double joursConge = calculateWorkingDaysForConge(dateDebut, dateFin, holidays, congeDTORequest.isDemiJourneeMatin(), congeDTORequest.isDemiJourneeSoir());

        Collaborateur collaborateur = existingConge.getCollaborateur();
        double ancienNombreJoursPris = existingConge.getNombreJoursPris();
        double differenceJoursPris = joursConge - ancienNombreJoursPris;

        SoldeCongeDTO soldeCongeDTO = soldeCongeServiceClient.getSoldeCongeByCollaborateurAndYear(collaborateur.getId(), currentYear).getData();
        SoldeConge soldeConge = modelMapper.map(soldeCongeDTO, SoldeConge.class);

        soldeConge.setSoldeConsomme(soldeConge.getSoldeConsomme() + differenceJoursPris);
        soldeConge.calculerSoldeRestant();

        existingConge.setNombreJoursPris(joursConge);

        soldeCongeServiceClient.addSoldeConge(modelMapper.map(soldeConge, SoldeCongeDTO.class));
        existingConge = congeRepository.save(existingConge);

        logger.info("Congé mis à jour avec succès avec l'ID : {}", existingConge.getIdConge());
        return mapCongeToDTO(existingConge);
    }

    private Conge getExistingConge(UUID idConge) {
        logger.info("Récupération du congé existant par ID : {}", idConge);

        return congeRepository.findById(idConge)
                .orElseThrow(() -> new RessourceNotFoundException("Congé non trouvé avec l'ID : " + idConge));
    }

    @Override
    public List<CongeDTO> getCongesByCollaborateur(String nom, String prenom) {
        List<CollaborateurDTO> collaborateursDTO;
        if (nom != null && prenom != null) {
            collaborateursDTO = collaborateurServiceClient.getCollaborateursByNomAndPrenom(nom, prenom).getData();
        } else if (nom != null) {
            collaborateursDTO = collaborateurServiceClient.getCollaborateursByNom(nom).getData();
        } else {
            collaborateursDTO = collaborateurServiceClient.getCollaborateursByPrenom(prenom).getData();
        }

        if (collaborateursDTO.isEmpty()) {
            logger.warn("Aucun collaborateur trouvé avec le nom '{}' et le prénom '{}'", nom, prenom);
            throw new RessourceNotFoundException("Aucun collaborateur trouvé avec le nom '" + nom + "' et le prénom '" + prenom + "'");
        }

        CollaborateurDTO collaborateurDTO = collaborateursDTO.get(0);
        Collaborateur collaborateur = modelMapper.map(collaborateurDTO, Collaborateur.class);

        logger.info("Récupération des congés pour le collaborateur : {}", collaborateur);

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
        logger.info("Vérification si un congé existe pour le collaborateur avec l'e-mail : {} entre {} et {}", collaborateurEmail, dateDebut, dateFin);

        Collaborateur collaborateur = getCollaborateurByEmail(collaborateurEmail);

        boolean exists = congeRepository.existsByCollaborateurAndDateDebutLessThanEqualAndDateFinGreaterThanEqual(collaborateur, dateDebut, dateFin);
        logger.info("Le congé existe pour le collaborateur : {} entre {} et {} : {}", collaborateurEmail, dateDebut, dateFin, exists);
        return exists;
    }

    @Override
    public void deleteConge(UUID id) {
        logger.info("Suppression du congé avec l'ID : {}", id);

        Conge conge = congeRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("Congé non trouvé avec l'ID : " + id));

        Collaborateur collaborateur = conge.getCollaborateur();
        double joursPrisConge = conge.getNombreJoursPris();
        int annee = conge.getDateDebut().getYear();

        SoldeCongeDTO soldeCongeDTO = soldeCongeServiceClient.getSoldeCongeByCollaborateurAndYear(collaborateur.getId(), annee).getData();
        SoldeConge soldeConge = modelMapper.map(soldeCongeDTO, SoldeConge.class);

        soldeConge.setSoldeConsomme(soldeConge.getSoldeConsomme() - joursPrisConge);
        soldeConge.calculerSoldeRestant();

        soldeCongeServiceClient.addSoldeConge(modelMapper.map(soldeConge, SoldeCongeDTO.class));
        congeRepository.delete(conge);

        logger.info("Congé supprimé avec succès avec l'ID : {}", id);
    }

    private CongeDTO mapCongeToDTO(Conge conge) {
        CongeDTO congeDTO = modelMapper.map(conge, CongeDTO.class);
        if (conge.getCollaborateur() != null) {
            congeDTO.setCollaborateurEmail(conge.getCollaborateur().getEmail());
        }
        return congeDTO;
    }

    private List<CongeDTO> mapCongesToDTOs(List<Conge> conges) {
        return conges.stream()
                .map(this::mapCongeToDTO)
                .collect(Collectors.toList());
    }
}
