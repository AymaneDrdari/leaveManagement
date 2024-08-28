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
        String collaborateurEmail = soldeCongeDTORequest.getCollaborateurEmail();

        // Retrieve the collaborator by email
        Collaborateur collaborateur = collaborateurRepository.findByEmail(collaborateurEmail);
        if (collaborateur == null) {
            throw new RessourceNotFoundException("Collaborateur non trouvé avec l'e-mail: " + collaborateurEmail);
        }

        int year = soldeCongeDTORequest.getYear();
        // Retrieve the exercise for the given year
        Exercice exercice = exerciceRepository.findByAnnee(year)
                .orElseThrow(() -> new RessourceNotFoundException("Exercice non trouvé pour l'année: " + year));

        // Calculate the initial balance based on working days and paid days per month
        double nombreJoursOuvrables = exercice.getNombreJoursOuvrables();
        double nombreJoursPayesMois = collaborateur.getNombreJoursPayesMois();
        double soldeInitial = nombreJoursOuvrables - (12 * nombreJoursPayesMois);

        // Check if the leave balance already exists for the collaborator and the given year
        SoldeConge soldeConge = soldeCongeRepository.findByCollaborateurAndExercice_Annee(collaborateur, year).orElse(null);

        if (soldeConge != null) {
            // Update the existing leave balance
            soldeConge.setSoldeInitial(soldeInitial);
            soldeConge.setSoldeRestant(soldeInitial - soldeConge.getSoldeConsomme());
        } else {
            // Create a new leave balance
            soldeConge = SoldeConge.builder()
                    .collaborateur(collaborateur)
                    .exercice(exercice)
                    .soldeInitial(soldeInitial)
                    .soldeConsomme(0)
                    .soldeRestant(soldeInitial)
                    .build();
        }

        // Save the leave balance to the database
        SoldeConge savedSoldeConge = soldeCongeRepository.save(soldeConge);

        // Map the SoldeConge entity to a SoldeCongeDTO
        SoldeCongeDTO savedSoldeCongeDTO = modelMapper.map(savedSoldeConge, SoldeCongeDTO.class);

        return savedSoldeCongeDTO;
    }



    // Méthode pour calculer le solde de congé d'un collaborateur pour une année donnée
    @Override
    public SoldeCongeDTO getSoldeConge(String email, int annee) {
        Collaborateur collaborateur = collaborateurRepository.findByEmail(email);
        if (collaborateur == null) {
            throw new RessourceNotFoundException("Collaborateur non trouvé avec l'e-mail: " + email);
        }

        Exercice exercice = exerciceRepository.findByAnnee(annee)
                .orElseThrow(() -> new RessourceNotFoundException("Exercice non trouvé pour l'année: " + annee));

        double nombreJoursOuvrables = exercice.getNombreJoursOuvrables();
        double nombreJoursPayesMois = collaborateur.getNombreJoursPayesMois();

        double soldeInitial = nombreJoursOuvrables - (12 * nombreJoursPayesMois);

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

        SoldeConge savedSoldeConge = soldeCongeRepository.save(soldeConge);
        return modelMapper.map(savedSoldeConge, SoldeCongeDTO.class);
    }

}
