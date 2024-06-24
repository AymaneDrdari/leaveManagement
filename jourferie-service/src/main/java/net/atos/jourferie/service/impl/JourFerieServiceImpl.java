package net.atos.jourferie.service.impl;

import net.atos.common.client.JourFerieServiceClient;
import net.atos.common.dto.jourFerie.JourFerieDTO;
import net.atos.common.dto.jourFerie.JourFerieRequestDTO;
import net.atos.common.response.ApiResponse;
import net.atos.jourferie.exception.RessourceNotFoundException;
import net.atos.jourferie.service.interf.JourFerieService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class JourFerieServiceImpl implements JourFerieService {
    private static final Logger logger = LoggerFactory.getLogger(JourFerieServiceImpl.class);

    private final JourFerieServiceClient jourFerieServiceClient;
    private final ModelMapper modelMapper;

    @Autowired
    public JourFerieServiceImpl(JourFerieServiceClient jourFerieServiceClient, ModelMapper modelMapper) {
        this.jourFerieServiceClient = jourFerieServiceClient;
        this.modelMapper = modelMapper;
    }

    @Override
    public JourFerieDTO creerJourFerie(JourFerieRequestDTO jourFerieRequestDTO) {
        if (jourFerieRequestDTO.getDescription() == null) {
            throw new IllegalArgumentException("Description cannot be null");
        }

        if (jourFerieRequestDTO.getDateFin().isBefore(jourFerieRequestDTO.getDateDebut())) {
            throw new IllegalArgumentException("La date de fin doit être postérieure ou égale à la date de début.");
        }

        ApiResponse<JourFerieDTO> response = jourFerieServiceClient.creerJourFerie(jourFerieRequestDTO);
        return response.getData();
    }

    @Override
    public List<JourFerieDTO> getJoursFeriesForYear(int annee) {
        logger.info("Début de la récupération des jours fériés pour l'année : {}", annee);

        ApiResponse<List<JourFerieDTO>> response = jourFerieServiceClient.getJoursFeriesForYear(annee);
        List<JourFerieDTO> joursFeries = response.getData();
        if (joursFeries == null || joursFeries.isEmpty()) {
            logger.warn("Aucun jour férié trouvé pour l'année : {}", annee);
            throw new RessourceNotFoundException("Aucun jour férié trouvé pour l'année : " + annee);
        }

        return joursFeries;
    }

    @Override
    public JourFerieDTO getJourFerieById(UUID id) {
        ApiResponse<JourFerieDTO> response = jourFerieServiceClient.getJourFerieById(id);
        JourFerieDTO jourFerie = response.getData();
        if (jourFerie == null) {
            throw new RessourceNotFoundException("Jour férié non trouvé avec l'identifiant : " + id);
        }
        return jourFerie;
    }

    @Override
    public JourFerieDTO updateJourFerie(UUID id, JourFerieRequestDTO jourFerieRequestDTO) {
        if (id == null) {
            throw new IllegalArgumentException("L'identifiant du jour férié est requis pour la mise à jour.");
        }

        if (jourFerieRequestDTO.getDescription() == null) {
            throw new IllegalArgumentException("Description cannot be null");
        }

        if (jourFerieRequestDTO.getDateFin().isBefore(jourFerieRequestDTO.getDateDebut())) {
            throw new IllegalArgumentException("La date de fin doit être postérieure ou égale à la date de début.");
        }

        ApiResponse<JourFerieDTO> response = jourFerieServiceClient.updateJourFerie(id, jourFerieRequestDTO);
        return response.getData();
    }

    @Override
    public void deleteJourFerie(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("L'identifiant du jour férié est requis pour la suppression.");
        }

        ApiResponse<Void> response = jourFerieServiceClient.deleteJourFerie(id);
        if (response.getData() == null) {
            throw new RessourceNotFoundException("Jour férié non trouvé avec l'identifiant : " + id);
        }
    }
}
