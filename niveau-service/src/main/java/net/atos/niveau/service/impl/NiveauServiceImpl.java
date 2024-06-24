package net.atos.niveau.service.impl;

import net.atos.common.client.NiveauServiceClient;  // Import correct
import net.atos.common.dto.NiveauDTO;  // Import correct
import net.atos.common.response.ApiResponse;  // Import correct
import net.atos.niveau.exception.RessourceAlreadyExistsException;
import net.atos.niveau.exception.RessourceNotFoundException;
import net.atos.niveau.service.interf.NiveauService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class NiveauServiceImpl implements NiveauService {
    private final NiveauServiceClient niveauServiceClient;
    private final ModelMapper modelMapper;
    private static final Logger logger = LoggerFactory.getLogger(NiveauServiceImpl.class);

    @Autowired
    public NiveauServiceImpl(NiveauServiceClient niveauServiceClient, ModelMapper modelMapper) {
        this.niveauServiceClient = niveauServiceClient;
        this.modelMapper = modelMapper;
    }

    @Override
    public NiveauDTO addNiveau(NiveauDTO niveauDTO) {
        logger.info("Ajout d'un nouveau niveau avec les données : {}", niveauDTO);

        if (niveauDTO.getNom() == null || niveauDTO.getNom().isEmpty()) {
            logger.error("Le nom du niveau est requis");
            throw new IllegalArgumentException("Le nom du niveau est requis");
        }
        if (niveauDTO.getDescription() == null || niveauDTO.getDescription().isEmpty()) {
            logger.error("La description du niveau est requise");
            throw new IllegalArgumentException("La description du niveau est requise");
        }

        ApiResponse<UUID> response = niveauServiceClient.findCodeNiveauByNom(niveauDTO.getNom());
        if (response.getData() != null) {
            logger.error("Un niveau avec ce nom existe déjà : {}", niveauDTO.getNom());
            throw new RessourceAlreadyExistsException("Un niveau avec ce nom existe déjà.");
        }

        try {
            ApiResponse<NiveauDTO> addedNiveau = niveauServiceClient.addNiveau(niveauDTO);
            return addedNiveau.getData();
        } catch (Exception ex) {
            logger.error("Erreur lors de l'ajout du niveau : {}", ex.getMessage());
            throw new RuntimeException("Erreur lors de l'ajout du niveau", ex);
        }
    }

    @Override
    public UUID findCodeNiveauByNom(String nomNiveau) {
        logger.info("Recherche du code du niveau par son nom : {}", nomNiveau);

        ApiResponse<UUID> response = niveauServiceClient.findCodeNiveauByNom(nomNiveau);
        UUID code = response.getData();
        if (code == null) {
            logger.warn("Aucun niveau trouvé avec le nom : {}", nomNiveau);
        } else {
            logger.info("Code du niveau trouvé : {}", code);
        }
        return code;
    }

    @Override
    public List<NiveauDTO> getAllNiveaux() {
        logger.info("Récupération de tous les niveaux");

        ApiResponse<List<NiveauDTO>> response = niveauServiceClient.getAllNiveaux();
        List<NiveauDTO> niveaux = response.getData();
        if (niveaux == null || niveaux.isEmpty()) {
            logger.warn("Aucun niveau n'a été trouvé.");
            throw new RessourceNotFoundException("Aucun niveau n'a été trouvé.");
        }
        return niveaux;
    }

    @Override
    public NiveauDTO getNiveauById(UUID code) {
        logger.info("Récupération du niveau par son ID : {}", code);

        ApiResponse<NiveauDTO> response = niveauServiceClient.getNiveauById(code);
        NiveauDTO niveau = response.getData();
        if (niveau == null) {
            logger.warn("Le niveau avec le code spécifié n'existe pas : {}", code);
            throw new RessourceNotFoundException("Le niveau avec le code spécifié n'existe pas.");
        }
        return niveau;
    }
}
