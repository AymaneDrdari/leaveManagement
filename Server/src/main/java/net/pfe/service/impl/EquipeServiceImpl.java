package net.pfe.service.impl;

import net.pfe.dto.EquipeDTO;
import net.pfe.entity.Equipe;
import net.pfe.exception.RessourceAlreadyExistsException;
import net.pfe.exception.RessourceNotFoundException;
import net.pfe.repository.EquipeRepository;
import net.pfe.service.interf.EquipeService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory; // Import pour le logging
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EquipeServiceImpl implements EquipeService {

    private final EquipeRepository equipeRepository;
    private final ModelMapper modelMapper;
    private static final Logger logger = LoggerFactory.getLogger(EquipeServiceImpl.class); // Initialisation du logger

    @Autowired
    public EquipeServiceImpl(EquipeRepository equipeRepository, ModelMapper modelMapper) {
        this.equipeRepository = equipeRepository;
        this.modelMapper = modelMapper;
    }

    // Méthode pour ajouter une nouvelle équipe
    @Override
    public EquipeDTO addEquipe(EquipeDTO equipeDTO) {
        logger.info("Tentative d'ajout d'une nouvelle équipe avec le nom : {}", equipeDTO.getNom());

        // Vérifie si equipeDTO est null
        if (equipeDTO == null) {
            throw new IllegalArgumentException("L'équipe ne peut pas être null.");
        }

        // Vérifie si le nom de l'équipe est null ou vide
        if (equipeDTO.getNom() == null || equipeDTO.getNom().isEmpty()) {
            throw new IllegalArgumentException("Le nom de l'équipe ne peut pas être null ou vide.");
        }

        // Vérifier si une équipe avec le même nom existe déjà
        Optional<Equipe> existingEquipe = equipeRepository.findByNom(equipeDTO.getNom());
        if (existingEquipe.isPresent()) {
            throw new RessourceAlreadyExistsException("Une équipe avec ce nom existe déjà.");
        }

        // Conversion de l'objet EquipeDTO en entité Equipe
        Equipe equipe = modelMapper.map(equipeDTO, Equipe.class);

        // Vérifier si la sauvegarde réussit
        try {
            equipe = equipeRepository.save(equipe);
        } catch (DataAccessException ex) {
            logger.error("Erreur lors de l'ajout de l'équipe : {}", ex.getMessage());
            throw ex;
        }

        logger.info("Équipe ajoutée avec succès avec le nom : {}", equipe.getNom());
        return modelMapper.map(equipe, EquipeDTO.class);
    }


    // Méthode pour trouver le code de l'équipe par son nom
    @Override
    public UUID findCodeEquipeByNom(String nomEquipe) {
        logger.info("Recherche du code de l'équipe avec le nom : {}", nomEquipe);

        // Récupérer l'équipe par son nom
        Optional<Equipe> equipeOptional = equipeRepository.findByNom(nomEquipe);
        // Gérer le cas où l'équipe avec ce nom n'est pas trouvée
        return equipeOptional.map(Equipe::getCode).orElse(null);
    }

    // Méthode pour récupérer toutes les équipes
    @Override
    public List<EquipeDTO> getAllEquipes() {
        logger.info("Récupération de toutes les équipes.");

        // Récupérer toutes les équipes
        List<Equipe> equipes = equipeRepository.findAll();
        if (equipes.isEmpty()) {
            throw new RessourceNotFoundException("Aucune équipe n'a été trouvée.");
        }

        // Mapper les entités Equipe en DTOs EquipeDTO
        return equipes.stream()
                .map(equipe -> modelMapper.map(equipe, EquipeDTO.class))
                .collect(Collectors.toList());
    }

    // Méthode pour récupérer une équipe par son ID
    @Override
    public Optional<EquipeDTO> getEquipeById(UUID code) {
        logger.info("Recherche de l'équipe avec l'ID : {}", code);

        // Récupérer l'équipe par son ID
        Optional<Equipe> equipeOptional = equipeRepository.findById(code);
        // Gérer le cas où l'équipe avec cet ID n'est pas trouvée
        if (equipeOptional.isEmpty()) {
            throw new RessourceNotFoundException("L'équipe avec l'ID spécifié n'existe pas.");
        }

        // Mapper l'entité Equipe en EquipeDTO et la retourner
        return equipeOptional.map(equipe -> modelMapper.map(equipe, EquipeDTO.class));
    }
    // Méthode pour modifier une équipe existante
    @Override
    public EquipeDTO updateEquipe(UUID code, EquipeDTO equipeDTO) {
        logger.info("Tentative de mise à jour de l'équipe avec le code : {}", code);

        // Vérifie si equipeDTO est null
        if (equipeDTO == null) {
            throw new IllegalArgumentException("L'équipe ne peut pas être null.");
        }

        // Vérifie si le nom de l'équipe est null ou vide
        if (equipeDTO.getNom() == null || equipeDTO.getNom().isEmpty()) {
            throw new IllegalArgumentException("Le nom de l'équipe ne peut pas être null ou vide.");
        }

        // Récupérer l'équipe existante
        Optional<Equipe> equipeOptional = equipeRepository.findById(code);
        if (equipeOptional.isEmpty()) {
            throw new RessourceNotFoundException("L'équipe avec le code spécifié n'existe pas.");
        }

        Equipe equipe = equipeOptional.get();

        // Mettre à jour les informations de l'équipe
        equipe.setNom(equipeDTO.getNom());
        equipe.setDescription(equipeDTO.getDescription());
        // Vous pouvez ajouter d'autres champs à mettre à jour ici

        // Sauvegarder les modifications
        try {
            equipe = equipeRepository.save(equipe);
        } catch (DataAccessException ex) {
            logger.error("Erreur lors de la mise à jour de l'équipe : {}", ex.getMessage());
            throw ex;
        }

        logger.info("Équipe mise à jour avec succès avec le code : {}", equipe.getCode());
        return modelMapper.map(equipe, EquipeDTO.class);
    }
    // Méthode pour supprimer une équipe par son ID
    @Override
    public void deleteEquipe(UUID code) {
        logger.info("Tentative de suppression de l'équipe avec le code : {}", code);

        // Vérifier si l'équipe existe avant de la supprimer
        Optional<Equipe> equipeOptional = equipeRepository.findById(code);
        if (equipeOptional.isEmpty()) {
            throw new RessourceNotFoundException("L'équipe avec le code spécifié n'existe pas.");
        }

        try {
            equipeRepository.deleteById(code);
        } catch (DataAccessException ex) {
            logger.error("Erreur lors de la suppression de l'équipe : {}", ex.getMessage());
            throw ex;
        }

        logger.info("Équipe supprimée avec succès avec le code : {}", code);
    }


}
