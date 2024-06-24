package net.atos.soldeconge.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.atos.common.client.CollaborateurServiceClient;
import net.atos.common.client.ExerciceServiceClient;
import net.atos.common.client.JourFerieServiceClient;
import net.atos.common.client.SoldeCongeServiceClient;
import net.atos.common.dto.collab.CollaborateurDTO;
import net.atos.common.dto.exercice.ExerciceDTO;
import net.atos.common.dto.jourFerie.JourFerieDTO;
import net.atos.common.dto.soldeConge.SoldeCongeDTO;
import net.atos.common.dto.soldeConge.SoldeCongeDTORequest;
import net.atos.common.response.ApiResponse;
import net.atos.soldeconge.exception.RessourceNotFoundException;
import net.atos.soldeconge.service.interf.SoldeCongeService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional
public class SoldeCongeServiceImpl implements SoldeCongeService {

    private final CollaborateurServiceClient collaborateurServiceClient;
    private final ExerciceServiceClient exerciceServiceClient;
    private final SoldeCongeServiceClient soldeCongeServiceClient;
    private final JourFerieServiceClient jourFerieServiceClient;
    private final ModelMapper modelMapper;

    public SoldeCongeServiceImpl(CollaborateurServiceClient collaborateurServiceClient,
                                 ExerciceServiceClient exerciceServiceClient,
                                 SoldeCongeServiceClient soldeCongeServiceClient,
                                 JourFerieServiceClient jourFerieServiceClient,
                                 ModelMapper modelMapper) {
        this.collaborateurServiceClient = collaborateurServiceClient;
        this.exerciceServiceClient = exerciceServiceClient;
        this.soldeCongeServiceClient = soldeCongeServiceClient;
        this.jourFerieServiceClient = jourFerieServiceClient;
        this.modelMapper = modelMapper;
    }

    @Override
    public SoldeCongeDTO createOrUpdateSoldeConge(SoldeCongeDTORequest soldeCongeDTORequest) {
        log.info("Début de la méthode createOrUpdateSoldeConge...");

        String collaborateurEmail = soldeCongeDTORequest.getCollaborateurEmail();
        ApiResponse<CollaborateurDTO> collabResponse = collaborateurServiceClient.getCollaborateurByEmail(collaborateurEmail);

        if (collabResponse.getData() == null) {
            throw new RessourceNotFoundException("Collaborateur non trouvé avec l'e-mail: " + collaborateurEmail);
        }
        CollaborateurDTO collaborateur = collabResponse.getData();

        int year = soldeCongeDTORequest.getYear();
        ApiResponse<ExerciceDTO> exerciceResponse = exerciceServiceClient.getExerciceByYear(year);

        if (exerciceResponse.getData() == null) {
            throw new RessourceNotFoundException("Exercice non trouvé pour l'année: " + year);
        }
        ExerciceDTO exercice = exerciceResponse.getData();

        double nombreJoursOuvrables = exercice.getNombreJoursOuvrables();
        double nombreJoursPayesMois = collaborateur.getNombreJoursPayesMois();
        double soldeInitial = nombreJoursOuvrables - (12 * nombreJoursPayesMois);

        ApiResponse<SoldeCongeDTO> soldeCongeResponse = soldeCongeServiceClient.getSoldeCongeByCollaborateurAndYear(collaborateur.getId(), year);
        SoldeCongeDTO soldeConge = soldeCongeResponse.getData();

        if (soldeConge != null) {
            soldeConge.setSoldeInitial(soldeInitial);
            soldeConge.setSoldeRestant(soldeInitial - soldeConge.getSoldeConsomme());
        } else {
            soldeConge = SoldeCongeDTO.builder()
                    .collaborateurEmail(collaborateurEmail)
                    .annee(year)
                    .soldeInitial(soldeInitial)
                    .soldeConsomme(0)
                    .soldeRestant(soldeInitial)
                    .build();
        }

        ApiResponse<SoldeCongeDTO> savedSoldeCongeResponse = soldeCongeServiceClient.addSoldeConge(soldeConge);
        SoldeCongeDTO savedSoldeCongeDTO = savedSoldeCongeResponse.getData();

        log.info("Solde de congé créé/mis à jour avec succès: {}", savedSoldeCongeDTO);

        return savedSoldeCongeDTO;
    }

    @Override
    public SoldeCongeDTO getSoldeConge(String email, int annee) {
        log.info("Début de la méthode getSoldeConge...");

        ApiResponse<CollaborateurDTO> collabResponse = collaborateurServiceClient.getCollaborateurByEmail(email);

        if (collabResponse.getData() == null) {
            throw new RessourceNotFoundException("Collaborateur non trouvé avec l'e-mail: " + email);
        }
        CollaborateurDTO collaborateur = collabResponse.getData();

        ApiResponse<ExerciceDTO> exerciceResponse = exerciceServiceClient.getExerciceByYear(annee);

        if (exerciceResponse.getData() == null) {
            throw new RessourceNotFoundException("Exercice non trouvé pour l'année: " + annee);
        }
        ExerciceDTO exercice = exerciceResponse.getData();

        List<JourFerieDTO> joursFeries = jourFerieServiceClient.getJoursFeriesForYear(annee).getData();
        double nombreJoursOuvrables = exercice.getNombreJoursOuvrables();
        double nombreJoursPayesMois = collaborateur.getNombreJoursPayesMois();

        double soldeInitial = nombreJoursOuvrables - (12 * nombreJoursPayesMois);

        ApiResponse<SoldeCongeDTO> soldeCongeResponse = soldeCongeServiceClient.getSoldeCongeByCollaborateurAndYear(collaborateur.getId(), annee);
        SoldeCongeDTO soldeConge = soldeCongeResponse.getData();

        if (soldeConge != null) {
            soldeConge.setSoldeInitial(soldeInitial);
            soldeConge.setSoldeRestant(soldeInitial - soldeConge.getSoldeConsomme());
        } else {
            soldeConge = SoldeCongeDTO.builder()
                    .collaborateurEmail(email)
                    .annee(annee)
                    .soldeInitial(soldeInitial)
                    .soldeConsomme(0)
                    .soldeRestant(soldeInitial)
                    .build();
        }

        ApiResponse<SoldeCongeDTO> savedSoldeCongeResponse = soldeCongeServiceClient.addSoldeConge(soldeConge);
        SoldeCongeDTO savedSoldeCongeDTO = savedSoldeCongeResponse.getData();

        log.info("Solde de congé calculé avec succès: {}", savedSoldeCongeDTO);

        return savedSoldeCongeDTO;
    }
}
