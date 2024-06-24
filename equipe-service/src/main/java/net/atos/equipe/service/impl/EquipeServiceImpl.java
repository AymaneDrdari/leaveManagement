package net.atos.equipe.service.impl;

import net.atos.common.client.EquipeServiceClient;
import net.atos.common.dto.EquipeDTO;
import net.atos.common.response.ApiResponse;
import net.atos.equipe.exception.RessourceAlreadyExistsException;
import net.atos.equipe.exception.RessourceNotFoundException;
import net.atos.equipe.service.interf.EquipeService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EquipeServiceImpl implements EquipeService {
    private final EquipeServiceClient equipeServiceClient;
    private final ModelMapper modelMapper;
    private static final Logger logger = LoggerFactory.getLogger(EquipeServiceImpl.class);

    @Autowired
    public EquipeServiceImpl(EquipeServiceClient equipeServiceClient, ModelMapper modelMapper) {
        this.equipeServiceClient = equipeServiceClient;
        this.modelMapper = modelMapper;
    }

    @Override
    public EquipeDTO addEquipe(EquipeDTO equipeDTO) {
        logger.info("Ajout d'une nouvelle équipe avec les données : {}", equipeDTO);

        if (equipeDTO.getNom() == null || equipeDTO.getNom().isEmpty()) {
            logger.error("Le nom de l'équipe est requis");
            throw new IllegalArgumentException("Le nom de l'équipe est requis");
        }
        if (equipeDTO.getDescription() == null || equipeDTO.getDescription().isEmpty()) {
            logger.error("La description de l'équipe est requise");
            throw new IllegalArgumentException("La description de l'équipe est requise");
        }

        ApiResponse<UUID> response = equipeServiceClient.findCodeEquipeByNom(equipeDTO.getNom());
        if (response.getData() != null) {
            logger.error("Une équipe avec ce nom existe déjà : {}", equipeDTO.getNom());
            throw new RessourceAlreadyExistsException("Une équipe avec ce nom existe déjà.");
        }

        try {
            ApiResponse<EquipeDTO> addedEquipe = equipeServiceClient.addEquipe(equipeDTO);
            return addedEquipe.getData();
        } catch (Exception ex) {
            logger.error("Erreur lors de l'ajout de l'équipe : {}", ex.getMessage());
            throw new RuntimeException("Erreur lors de l'ajout de l'équipe", ex);
        }
    }

    @Override
    public UUID findCodeEquipeByNom(String nomEquipe) {
        logger.info("Recherche du code de l'équipe par son nom : {}", nomEquipe);

        ApiResponse<UUID> response = equipeServiceClient.findCodeEquipeByNom(nomEquipe);
        UUID code = response.getData();
        if (code == null) {
            logger.warn("Aucune équipe trouvée avec le nom : {}", nomEquipe);
        } else {
            logger.info("Code de l'équipe trouvé : {}", code);
        }
        return code;
    }

    @Override
    public List<EquipeDTO> getAllEquipes() {
        logger.info("Récupération de toutes les équipes");

        ApiResponse<List<EquipeDTO>> response = equipeServiceClient.getAllEquipes();
        List<EquipeDTO> equipes = response.getData();
        if (equipes == null || equipes.isEmpty()) {
            logger.warn("Aucune équipe n'a été trouvée.");
            throw new RessourceNotFoundException("Aucune équipe n'a été trouvée.");
        }
        return equipes;
    }

    @Override
    public EquipeDTO getEquipeById(UUID code) {
        logger.info("Récupération de l'équipe par son ID : {}", code);

        ApiResponse<EquipeDTO> response = equipeServiceClient.getEquipeById(code);
        EquipeDTO equipe = response.getData();
        if (equipe == null) {
            logger.warn("L'équipe avec le code spécifié n'existe pas : {}", code);
            throw new RessourceNotFoundException("L'équipe avec le code spécifié n'existe pas.");
        }
        return equipe;
    }
}
