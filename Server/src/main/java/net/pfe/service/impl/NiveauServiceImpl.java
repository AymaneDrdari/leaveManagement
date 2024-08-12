//package net.atos.service.impl;
//
//import net.atos.dto.NiveauDTO;
//import net.atos.entity.Niveau;
//import net.atos.exception.RessourceAlreadyExistsException;
//import net.atos.exception.RessourceNotFoundException;
//import net.atos.repository.NiveauRepository;
//import net.atos.service.interf.NiveauService;
//import org.modelmapper.ModelMapper;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.dao.DataAccessException;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//import java.util.stream.Collectors;
//
//@Service
//public class NiveauServiceImpl implements NiveauService {
//    private final NiveauRepository niveauRepository;
//    private final ModelMapper modelMapper;
//    private static final Logger logger = LoggerFactory.getLogger(NiveauServiceImpl.class);
//
//    @Autowired
//    public NiveauServiceImpl(NiveauRepository niveauRepository, ModelMapper modelMapper) {
//        this.niveauRepository = niveauRepository;
//        this.modelMapper = modelMapper;
//    }
//
//    // Ajoute un niveau
//    @Override
//    public NiveauDTO addNiveau(NiveauDTO niveauDTO) {
//        logger.info("Ajout d'un nouveau niveau avec les données : {}", niveauDTO);
//
//        // Vérifier si un niveau avec le même nom existe déjà
//        Optional<Niveau> existingNiveau = niveauRepository.findByNom(niveauDTO.getNom());
//        if (existingNiveau.isPresent()) {
//            logger.error("Un niveau avec ce nom existe déjà : {}", niveauDTO.getNom());
//            throw new RessourceAlreadyExistsException("Un niveau avec ce nom existe déjà.");
//        }
//        // Conversion de l'objet NiveauDTO en entité Niveau
//        Niveau niveau = modelMapper.map(niveauDTO, Niveau.class);
//
//        // Vérifier si la sauvegarde réussit
//        try {
//            niveau = niveauRepository.save(niveau);
//        } catch (DataAccessException ex) {
//            logger.error("Erreur lors de l'ajout du niveau : {}", ex.getMessage());
//            throw ex;
//        }
//        // Retourner le niveau ajouté
//        logger.info("Niveau ajouté avec succès avec l'ID : {}", niveau.getCode());
//        return modelMapper.map(niveau, NiveauDTO.class);
//    }
//
//    // Trouve le code d'un niveau par son nom
//    @Override
//    public UUID findCodeNiveauByNom(String nomNiveau) {
//        logger.info("Recherche du code du niveau par son nom : {}", nomNiveau);
//
//        Optional<Niveau> niveauOptional = niveauRepository.findByNom(nomNiveau);
//        // Gérez le cas où le niveau avec ce nom n'est pas trouvé
//        UUID code = niveauOptional.map(Niveau::getCode).orElse(null);
//        if (code == null) {
//            logger.warn("Aucun niveau trouvé avec le nom : {}", nomNiveau);
//        } else {
//            logger.info("Code du niveau trouvé : {}", code);
//        }
//        return code;
//    }
//
//    // Récupère tous les niveaux
//    @Override
//    public List<NiveauDTO> getAllNiveaux() {
//        logger.info("Récupération de tous les niveaux");
//
//        List<Niveau> niveaux = niveauRepository.findAll();
//        if (niveaux.isEmpty()){
//            logger.warn("Aucun niveau n'a été trouvé.");
//            throw new RessourceNotFoundException("Aucun niveau n'a été trouvé.");
//        }
//        return niveaux.stream()
//                .map(niveau -> modelMapper.map(niveau, NiveauDTO.class))
//                .collect(Collectors.toList());
//    }
//
//    // Récupère un niveau par son ID
//    @Override
//    public Optional<NiveauDTO> getNiveauById(UUID code) {
//        logger.info("Récupération du niveau par son ID : {}", code);
//
//        Optional<Niveau> niveauOptional = niveauRepository.findById(code);
//        if (niveauOptional.isEmpty()){
//            logger.warn("Le niveau avec le code spécifié n'existe pas : {}", code);
//            throw new RessourceNotFoundException("Le niveau avec le code spécifié n'existe pas.");
//        }
//        return niveauOptional.map(niveau ->
//                modelMapper.map(niveau, NiveauDTO.class));
//    }
//}
package net.pfe.service.impl;

import net.pfe.dto.NiveauDTO;
import net.pfe.entity.Niveau;
import net.pfe.exception.RessourceAlreadyExistsException;
import net.pfe.exception.RessourceNotFoundException;
import net.pfe.repository.NiveauRepository;
import net.pfe.service.interf.NiveauService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class NiveauServiceImpl implements NiveauService {
    private final NiveauRepository niveauRepository;
    private final ModelMapper modelMapper;
    private static final Logger logger = LoggerFactory.getLogger(NiveauServiceImpl.class);

    @Autowired
    public NiveauServiceImpl(NiveauRepository niveauRepository, ModelMapper modelMapper) {
        this.niveauRepository = niveauRepository;
        this.modelMapper = modelMapper;
    }

    // Ajoute un niveau
    @Override
    public NiveauDTO addNiveau(NiveauDTO niveauDTO) {
        logger.info("Ajout d'un nouveau niveau avec les données : {}", niveauDTO);

        // Validation des champs requis
        if (niveauDTO.getNom() == null || niveauDTO.getNom().isEmpty()) {
            logger.error("Le nom du niveau est requis");
            throw new IllegalArgumentException("Le nom du niveau est requis");
        }
        if (niveauDTO.getDescription() == null || niveauDTO.getDescription().isEmpty()) {
            logger.error("La description du niveau est requise");
            throw new IllegalArgumentException("La description du niveau est requise");
        }

        // Vérifier si un niveau avec le même nom existe déjà
        Optional<Niveau> existingNiveau = niveauRepository.findByNom(niveauDTO.getNom());
        if (existingNiveau.isPresent()) {
            logger.error("Un niveau avec ce nom existe déjà : {}", niveauDTO.getNom());
            throw new RessourceAlreadyExistsException("Un niveau avec ce nom existe déjà.");
        }
        // Conversion de l'objet NiveauDTO en entité Niveau
        Niveau niveau = modelMapper.map(niveauDTO, Niveau.class);

        // Vérifier si la sauvegarde réussit
        try {
            niveau = niveauRepository.save(niveau);
        } catch (DataAccessException ex) {
            logger.error("Erreur lors de l'ajout du niveau : {}", ex.getMessage());
            throw ex;
        }
        // Retourner le niveau ajouté
        logger.info("Niveau ajouté avec succès avec l'ID : {}", niveau.getCode());
        return modelMapper.map(niveau, NiveauDTO.class);
    }

    // Trouve le code d'un niveau par son nom
    @Override
    public UUID findCodeNiveauByNom(String nomNiveau) {
        logger.info("Recherche du code du niveau par son nom : {}", nomNiveau);

        Optional<Niveau> niveauOptional = niveauRepository.findByNom(nomNiveau);
        // Gérez le cas où le niveau avec ce nom n'est pas trouvé
        UUID code = niveauOptional.map(Niveau::getCode).orElse(null);
        if (code == null) {
            logger.warn("Aucun niveau trouvé avec le nom : {}", nomNiveau);
        } else {
            logger.info("Code du niveau trouvé : {}", code);
        }
        return code;
    }

    // Récupère tous les niveaux
    @Override
    public List<NiveauDTO> getAllNiveaux() {
        logger.info("Récupération de tous les niveaux");

        List<Niveau> niveaux = niveauRepository.findAll();
        if (niveaux.isEmpty()){
            logger.warn("Aucun niveau n'a été trouvé.");
            throw new RessourceNotFoundException("Aucun niveau n'a été trouvé.");
        }
        return niveaux.stream()
                .map(niveau -> modelMapper.map(niveau, NiveauDTO.class))
                .collect(Collectors.toList());
    }

    // Récupère un niveau par son ID
    @Override
    public Optional<NiveauDTO> getNiveauById(UUID code) {
        logger.info("Récupération du niveau par son ID : {}", code);

        Optional<Niveau> niveauOptional = niveauRepository.findById(code);
        if (niveauOptional.isEmpty()){
            logger.warn("Le niveau avec le code spécifié n'existe pas : {}", code);
            throw new RessourceNotFoundException("Le niveau avec le code spécifié n'existe pas.");
        }
        return niveauOptional.map(niveau ->
                modelMapper.map(niveau, NiveauDTO.class));
    }

    // Met à jour un niveau
    @Override
    public NiveauDTO updateNiveau(UUID code, NiveauDTO niveauDTO) {
        logger.info("Mise à jour du niveau avec le code : {}", code);

        // Vérifiez si le niveau existe
        Optional<Niveau> niveauOptional = niveauRepository.findById(code);
        if (niveauOptional.isEmpty()) {
            logger.warn("Le niveau avec le code spécifié n'existe pas : {}", code);
            throw new RessourceNotFoundException("Le niveau avec le code spécifié n'existe pas.");
        }

        // Mise à jour du niveau
        Niveau niveau = niveauOptional.get();
        modelMapper.map(niveauDTO, niveau);

        // Sauvegarde du niveau mis à jour
        try {
            niveau = niveauRepository.save(niveau);
        } catch (DataAccessException ex) {
            logger.error("Erreur lors de la mise à jour du niveau : {}", ex.getMessage());
            throw ex;
        }
        logger.info("Niveau mis à jour avec succès avec l'ID : {}", niveau.getCode());
        return modelMapper.map(niveau, NiveauDTO.class);
    }

    // Supprime un niveau
    @Override
    public void deleteNiveau(UUID code) {
        logger.info("Suppression du niveau avec le code : {}", code);

        // Vérifiez si le niveau existe
        Optional<Niveau> niveauOptional = niveauRepository.findById(code);
        if (niveauOptional.isEmpty()) {
            logger.warn("Le niveau avec le code spécifié n'existe pas : {}", code);
            throw new RessourceNotFoundException("Le niveau avec le code spécifié n'existe pas.");
        }

        // Suppression du niveau
        try {
            niveauRepository.deleteById(code);
        } catch (DataAccessException ex) {
            logger.error("Erreur lors de la suppression du niveau : {}", ex.getMessage());
            throw ex;
        }
        logger.info("Niveau supprimé avec succès avec l'ID : {}", code);
    }
}