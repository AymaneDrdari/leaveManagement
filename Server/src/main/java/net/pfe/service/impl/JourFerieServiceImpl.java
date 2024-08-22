package net.pfe.service.impl;

import net.pfe.dto.exercice.ExerciceDTORequest;
import net.pfe.dto.jourFerie.JourFerieDTO;
import net.pfe.dto.jourFerie.JourFerieRequestDTO;
import net.pfe.entity.JourFerie;
import net.pfe.exception.RessourceNotFoundException;
import net.pfe.repository.JourFerieRepository;
import net.pfe.service.interf.ExerciceService;
import net.pfe.service.interf.JourFerieService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class JourFerieServiceImpl implements JourFerieService {
    private static final Logger logger = LoggerFactory.getLogger(CollaborateurServiceImpl.class);

    public final JourFerieRepository jourFerieRepository;
    public final ExerciceService exerciceService;
    public final ModelMapper modelMapper;
    @Autowired
    public JourFerieServiceImpl(JourFerieRepository jourFerieRepository,  ModelMapper modelMapper,ExerciceService exerciceService) {
        this.jourFerieRepository = jourFerieRepository;
        this.exerciceService = exerciceService;
        this.modelMapper = modelMapper;
    }


    @Override
    public JourFerieDTO creerJourFerie(JourFerieRequestDTO jourFerieRequestDTO) {
        // Vérifications des champs obligatoires
        if (jourFerieRequestDTO.getDescription() == null) {
            throw new IllegalArgumentException("Description cannot be null");
        }

        if (jourFerieRequestDTO.getDateFin().isBefore(jourFerieRequestDTO.getDateDebut())) {
            throw new IllegalArgumentException("La date de fin doit être postérieure ou égale à la date de début.");
        }

        JourFerie jourFerie = modelMapper.map(jourFerieRequestDTO, JourFerie.class);
        jourFerieRepository.save(jourFerie);

        // Recalcul de l'exercice après l'ajout d'un jour férié
        exerciceService.calculerExerciceAnnuel(jourFerie.getDateDebut().getYear());

        return modelMapper.map(jourFerie, JourFerieDTO.class);
    }



    @Override
    public List<JourFerieDTO> getJoursFeriesForYear(int annee) {
        logger.info("Début de la récupération des jours fériés pour l'année : {}", annee);

        List<JourFerie> joursFeriesNonFixes = jourFerieRepository.findNonFixeByYear(annee);

        // Vérifie si les jours fériés non fixés sont nuls
        if (joursFeriesNonFixes == null) {
            logger.info("Aucun jour férié non fixe n'a été trouvé pour l'année {}", annee);
            joursFeriesNonFixes = Collections.emptyList(); // Utilise une liste vide pour éviter les problèmes avec les opérations ultérieures
        } else {
            logger.info("Nombre total de jours fériés non fixes récupérés pour l'année {} : {}", annee, joursFeriesNonFixes.size());
        }
        logger.info("Nombre total de jours fériés non fixes récupérés pour l'année {} : {}", annee, joursFeriesNonFixes.size());
        List<JourFerie> joursFeriesFixes = jourFerieRepository.findFixe();
        logger.info("Nombre total de jours fériés fixes récupérés : {}", joursFeriesFixes.size());

        List<JourFerieDTO> joursFeries = joursFeriesNonFixes.stream()
                .map(jourFerie -> modelMapper.map(jourFerie, JourFerieDTO.class))
                .collect(Collectors.toList());

        joursFeries.addAll(joursFeriesFixes.stream()
                .map(jourFerie -> {
                    LocalDate dateDebutAvecAnnee = jourFerie.getDateDebut().withYear(annee);
                    LocalDate dateFinAvecAnnee = jourFerie.getDateFin().withYear(annee);
                    return new JourFerieDTO(jourFerie.getId(), jourFerie.getDescription(), dateDebutAvecAnnee, dateFinAvecAnnee, jourFerie.getIsFixe());
                })
                .toList());
        logger.info("Fin de la récupération des jours fériés pour l'année : {}", annee);
        return joursFeries;
    }

    @Override
    public JourFerieDTO getJourFerieById(UUID id) {
        JourFerie jourFerie = jourFerieRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("Jour férié non trouvé avec l'identifiant : " + id));
        return modelMapper.map(jourFerie, JourFerieDTO.class);
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

        JourFerie existingJourFerie = jourFerieRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("Jour férié non trouvé avec l'identifiant : " + id));

        existingJourFerie.setDescription(jourFerieRequestDTO.getDescription());
        existingJourFerie.setDateDebut(jourFerieRequestDTO.getDateDebut());
        existingJourFerie.setDateFin(jourFerieRequestDTO.getDateFin());
        existingJourFerie.setIsFixe(jourFerieRequestDTO.getIsFixe());

        jourFerieRepository.save(existingJourFerie);

        // Recalcul de l'exercice après la mise à jour d'un jour férié
        exerciceService.calculerExerciceAnnuel(existingJourFerie.getDateDebut().getYear());

        return modelMapper.map(existingJourFerie, JourFerieDTO.class);
    }


    @Override
    public void deleteJourFerie(UUID id) {
        if (id == null) {
            throw new RessourceNotFoundException("L'identifiant du jour férié est requis pour la suppression.");
        }
        JourFerie jourFerie = jourFerieRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("Jour férié non trouvé avec l'identifiant : " + id));

        jourFerieRepository.delete(jourFerie);

        // Recalcul de l'exercice après la suppression d'un jour férié
        exerciceService.calculerExerciceAnnuel(jourFerie.getDateDebut().getYear());
    }

    @Override
    public List<JourFerieDTO> getAllJoursFeries() {
        logger.info("Début de la récupération de tous les jours fériés");

        List<JourFerie> joursFeries = jourFerieRepository.findAll();

        List<JourFerieDTO> joursFeriesDTO = joursFeries.stream()
                .map(jourFerie -> modelMapper.map(jourFerie, JourFerieDTO.class))
                .collect(Collectors.toList());

        logger.info("Fin de la récupération de tous les jours fériés. Nombre total: {}", joursFeriesDTO.size());
        return joursFeriesDTO;
    }

    @Override
    public List<JourFerieDTO> getJoursFeriesPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<JourFerie> jourFeriePage = jourFerieRepository.findAll(pageable);
        List<JourFerieDTO> joursFeriesDTO = jourFeriePage.getContent().stream()
                .map(jourFerie -> modelMapper.map(jourFerie, JourFerieDTO.class))
                .collect(Collectors.toList());
        return joursFeriesDTO;
    }




}
