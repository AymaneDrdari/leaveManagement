package net.pfe.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.pfe.dto.jourFerie.JourFerieDTO;
import net.pfe.dto.soldeConge.SoldeCongeDTO;
import net.pfe.dto.soldeConge.SoldeCongeDTORequest;
import net.pfe.entity.Collaborateur;
import net.pfe.entity.Exercice;
import net.pfe.entity.SoldeConge;
import net.pfe.exception.RessourceNotFoundException;
import net.pfe.repository.CollaborateurRepository;
import net.pfe.repository.ExerciceRepository;
import net.pfe.repository.SoldeCongeRepository;
import net.pfe.service.interf.JourFerieService;
import net.pfe.service.interf.SoldeCongeService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional
public class SoldeCongeServiceImpl implements SoldeCongeService {

    // Déclaration des dépendances requises pour ce service
    private final CollaborateurRepository collaborateurRepository;
    private final ExerciceRepository exerciceRepository;
    private final SoldeCongeRepository soldeCongeRepository;
    private final JourFerieService jourFerieService;
    private final ModelMapper modelMapper;

    // Constructeur pour l'injection de dépendances
    public SoldeCongeServiceImpl(ExerciceRepository exerciceRepository, CollaborateurRepository collaborateurRepository,
                                 SoldeCongeRepository soldeCongeRepository, JourFerieService jourFerieService, ModelMapper modelMapper) {
        this.exerciceRepository = exerciceRepository;
        this.collaborateurRepository = collaborateurRepository;
        this.soldeCongeRepository = soldeCongeRepository;
        this.jourFerieService = jourFerieService;
        this.modelMapper = modelMapper;
    }

    // Méthode pour créer ou mettre à jour le solde de congé d'un collaborateur
    @Override
    public SoldeCongeDTO createOrUpdateSoldeConge(SoldeCongeDTORequest soldeCongeDTORequest) {
        log.info("Début de la méthode createOrUpdateSoldeConge...");

        String collaborateurEmail = soldeCongeDTORequest.getCollaborateurEmail();

        // Récupérer le collaborateur par e-mail
        Collaborateur collaborateur = collaborateurRepository.findByEmail(collaborateurEmail);
        if (collaborateur == null) {
            throw new RessourceNotFoundException("Collaborateur non trouvé avec l'e-mail: " + collaborateurEmail);
        }

        int year = soldeCongeDTORequest.getYear();
        // Récupérer l'exercice correspondant à l'année
        Exercice exercice = exerciceRepository.findByAnnee(year)
                .orElseThrow(() -> new RessourceNotFoundException("Exercice non trouvé pour l'année: " + year));

        // Calculer le solde initial basé sur les jours ouvrables et les jours payés par mois
        double nombreJoursOuvrables = exercice.getNombreJoursOuvrables();
        double nombreJoursPayesMois = collaborateur.getNombreJoursPayesMois();
        double soldeInitial = nombreJoursOuvrables - (12 * nombreJoursPayesMois);

        // Vérifier si le solde de congé existe pour le collaborateur et l'exercice donné
        SoldeConge soldeConge = soldeCongeRepository.findByCollaborateurAndExercice_Annee(collaborateur, year).orElse(null);

        if (soldeConge != null) {
            // Mettre à jour le solde de congé existant
            soldeConge.setSoldeInitial(soldeInitial);
            soldeConge.setSoldeRestant(soldeInitial - soldeConge.getSoldeConsomme());
        } else {
            // Créer un nouveau solde de congé
            soldeConge = SoldeConge.builder()
                    .collaborateur(collaborateur)
                    .exercice(exercice)
                    .soldeInitial(soldeInitial)
                    .soldeConsomme(0)
                    .soldeRestant(soldeInitial)
                    .build();
        }

        // Enregistrer le solde de congé en base de données
        SoldeConge savedSoldeConge = soldeCongeRepository.save(soldeConge);

        // Mapper l'entité SoldeConge vers SoldeCongeDTO
        SoldeCongeDTO savedSoldeCongeDTO = modelMapper.map(savedSoldeConge, SoldeCongeDTO.class);

        log.info("Solde de congé créé/mis à jour avec succès: {}", savedSoldeCongeDTO);

        return savedSoldeCongeDTO;
    }

    // Méthode pour calculer le solde de congé d'un collaborateur pour une année donnée
    @Override
    public SoldeCongeDTO getSoldeConge(String email, int annee) {
        log.info("Début de la méthode calculerSoldeConge...");

        // Récupérer le collaborateur par e-mail
        Collaborateur collaborateur = collaborateurRepository.findByEmail(email);
        if (collaborateur == null) {
            throw new RessourceNotFoundException("Collaborateur non trouvé avec l'e-mail: " + email);
        }

        // Récupérer l'exercice correspondant à l'année
        Exercice exercice = exerciceRepository.findByAnnee(annee)
                .orElseThrow(() -> new RessourceNotFoundException("Exercice non trouvé pour l'année: " + annee));

        // Récupérer la liste des jours fériés pour l'année
        List<JourFerieDTO> joursFeries = jourFerieService.getJoursFeriesForYear(annee);
        double nombreJoursOuvrables = exercice.getNombreJoursOuvrables();
        double nombreJoursPayesMois = collaborateur.getNombreJoursPayesMois();

        // Calculer le solde initial
        double soldeInitial = nombreJoursOuvrables - (12 * nombreJoursPayesMois);

        // Vérifier si le solde de congé existe pour le collaborateur et l'exercice donné
        SoldeConge soldeConge = soldeCongeRepository.findByCollaborateurAndExercice_Annee(collaborateur, annee).orElse(null);

        if (soldeConge != null) {
            soldeConge.setSoldeInitial(soldeInitial);
            soldeConge.setSoldeRestant(soldeInitial - soldeConge.getSoldeConsomme());
        } else {
            soldeConge = SoldeConge.builder()
                    .collaborateur(collaborateur)
                    .exercice(exercice)
                    .soldeInitial(soldeInitial)
                    .soldeConsomme(0)
                    .soldeRestant(soldeInitial)
                    .build();
        }

        // Enregistrer le solde de congé en base de données
        SoldeConge savedSoldeConge = soldeCongeRepository.save(soldeConge);
        // Mapper l'entité SoldeConge vers SoldeCongeDTO
        SoldeCongeDTO savedSoldeCongeDTO = modelMapper.map(savedSoldeConge, SoldeCongeDTO.class);

        log.info("Solde de congé calculé avec succès: {}", savedSoldeCongeDTO);

        return savedSoldeCongeDTO;
    }
}
