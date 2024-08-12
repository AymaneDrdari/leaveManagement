package net.pfe.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.pfe.dto.collab.AddCollaborateurDTORequest;
import net.pfe.dto.collab.CollaborateurDTO;
import net.pfe.entity.Collaborateur;
import net.pfe.entity.Equipe;
import net.pfe.entity.Niveau;
import net.pfe.exception.RessourceAlreadyExistsException;
import net.pfe.exception.RessourceNotFoundException;
import net.pfe.repository.CollaborateurRepository;
import net.pfe.repository.EquipeRepository;
import net.pfe.repository.NiveauRepository;
import net.pfe.service.interf.CollaborateurService;
import net.pfe.service.interf.CongeService;
import net.pfe.service.interf.EquipeService;
import net.pfe.service.interf.NiveauService;
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
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class CollaborateurServiceImpl implements CollaborateurService {
    private static final Logger logger = LoggerFactory.getLogger(CollaborateurServiceImpl.class);

    public final CollaborateurRepository collaborateurRepository;
    private final EquipeRepository equipeRepository;
    private final NiveauRepository niveauRepository;
    private final CongeService congeService;
    public final EquipeService equipeService;
    public final NiveauService niveauService;
    public final ModelMapper modelMapper;

    @Autowired
    public CollaborateurServiceImpl(CollaborateurRepository collaborateurRepository, EquipeRepository equipeRepository, NiveauRepository niveauRepository, EquipeService equipeService, NiveauService niveauService, ModelMapper modelMapper, CongeService congeService) {
        this.collaborateurRepository = collaborateurRepository;
        this.equipeRepository = equipeRepository;
        this.congeService = congeService;
        this.niveauRepository = niveauRepository;
        this.equipeService = equipeService;
        this.niveauService = niveauService;
        this.modelMapper = modelMapper;
    }

    @Override
    public CollaborateurDTO createCollaborateur(AddCollaborateurDTORequest addCollaborateurDTORequest) {
        String email = addCollaborateurDTORequest.getEmail();
        logger.info("Tentative de création d'un nouveau collaborateur avec email : {}", email);

        // Vérifie si l'email existe déjà dans la base de données
        if (collaborateurRepository.existsByEmail(email)) {
            logger.warn("La création du collaborateur a échoué car l'email {} existe déjà.", email);
            throw new RessourceAlreadyExistsException("L'email du collaborateur existe déjà.");
        }

        // Vérifie si l'équipe et le niveau existent
        UUID equipeId = addCollaborateurDTORequest.getEquipe().getCode();
        UUID niveauId = addCollaborateurDTORequest.getNiveau().getCode();

        Equipe equipe = equipeRepository.findById(equipeId)
                .orElseThrow(() -> new RessourceNotFoundException("Équipe non trouvée avec l'identifiant : " + equipeId));
        Niveau niveau = niveauRepository.findById(niveauId)
                .orElseThrow(() -> new RessourceNotFoundException("Niveau non trouvé avec l'identifiant : " + niveauId));

        // Mappage de l'objet AddCollaborateurDTORequest vers Collaborateur
        logger.debug("Mapping de l'objet AddCollaborateurDTORequest vers Collaborateur : {}", addCollaborateurDTORequest);
        Collaborateur collaborateur = modelMapper.map(addCollaborateurDTORequest, Collaborateur.class);
        collaborateur.setEquipe(equipe);
        collaborateur.setNiveau(niveau);

        // Sauvegarde du collaborateur dans la base de données
        logger.info("Enregistrement du collaborateur dans la base de données : {}", collaborateur);
        Collaborateur savedCollaborateur = collaborateurRepository.save(collaborateur);

        // Retourner le CollaborateurDTO
        return mapCollaborateurToDTO(savedCollaborateur);
    }

    @Override
    public CollaborateurDTO updateCollaborateur(CollaborateurDTO collaborateurDTO) {
        UUID id = collaborateurDTO.getId();

        // Vérification si le collaborateur existe
        Collaborateur collaborateur = collaborateurRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("Collaborateur non trouvé avec l'identifiant : " + id));

        // Rechercher et assigner l'équipe et le niveau s'ils sont spécifiés
        if (collaborateurDTO.getEquipe() != null) {
            Equipe equipe = equipeRepository.findById(collaborateurDTO.getEquipe().getCode())
                    .orElseThrow(() -> new RessourceNotFoundException("Équipe non trouvée avec l'identifiant : " + collaborateurDTO.getEquipe().getCode()));
            collaborateur.setEquipe(equipe);
        }

        if (collaborateurDTO.getNiveau() != null) {
            Niveau niveau = niveauRepository.findById(collaborateurDTO.getNiveau().getCode())
                    .orElseThrow(() -> new RessourceNotFoundException("Niveau non trouvé avec l'identifiant : " + collaborateurDTO.getNiveau().getCode()));
            collaborateur.setNiveau(niveau);
        }

        // Mettre à jour les propriétés du collaborateur
        modelMapper.map(collaborateurDTO, collaborateur);

        // Enregistrer les modifications dans la base de données
        Collaborateur updatedCollaborateur = collaborateurRepository.save(collaborateur);

        // Retourner le CollaborateurDTO mis à jour
        return mapCollaborateurToDTO(updatedCollaborateur);
    }

    @Override
    public void deleteCollaborateur(UUID id) {
        Collaborateur collaborateur = collaborateurRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("Collaborateur non trouvé avec l'identifiant : " + id));
        collaborateurRepository.delete(collaborateur);
    }

    @Override
    public CollaborateurDTO findCollaborateurById(UUID id) {
        Collaborateur collaborateur = collaborateurRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("Collaborateur introuvable avec l'identifiant : " + id));
        return mapCollaborateurToDTO(collaborateur);
    }

    @Override
    public List<CollaborateurDTO> findAllCollaborateurs() {
        List<Collaborateur> collaborateurs = collaborateurRepository.findAll();
        if (collaborateurs.isEmpty()) {
            throw new RessourceNotFoundException("Aucun collaborateur n'a été trouvé.");
        }
        return mapCollaborateursToDTOs(collaborateurs);
    }

    @Override
    public List<CollaborateurDTO> getCollaborateursByNom(String nom) {
        List<Collaborateur> collaborateurs = collaborateurRepository.findByNom(nom);
        return mapCollaborateursToDTOs(collaborateurs);
    }

    @Override
    public List<CollaborateurDTO> getCollaborateursByPrenom(String prenom) {
        List<Collaborateur> collaborateurs = collaborateurRepository.findByPrenom(prenom);
        return mapCollaborateursToDTOs(collaborateurs);
    }

    @Override
    public List<CollaborateurDTO> getCollaborateursByNomAndPrenom(String nom, String prenom) {
        List<Collaborateur> collaborateurs = collaborateurRepository.findByNomAndPrenom(nom, prenom);
        return mapCollaborateursToDTOs(collaborateurs);
    }

    @Override
    public List<CollaborateurDTO> findCollaborateursByEquipe(String nomEquipe) {
        UUID equipeCode = equipeService.findCodeEquipeByNom(nomEquipe);
        if (equipeCode == null) {
            throw new RessourceNotFoundException("Équipe introuvable avec le nom : " + nomEquipe);
        }
        List<Collaborateur> collaborateurs = collaborateurRepository.findByEquipeCode(equipeCode);
        if (collaborateurs.isEmpty()) {
            throw new RessourceNotFoundException("Aucun collaborateur n'a été trouvé dans cette équipe.");
        }
        return mapCollaborateursToDTOs(collaborateurs);
    }

    @Override
    public List<CollaborateurDTO> findCollaborateursByNiveau(String nomNiveau) {
        UUID niveauCode = niveauService.findCodeNiveauByNom(nomNiveau);
        if (niveauCode == null) {
            throw new RessourceNotFoundException("Niveau introuvable avec le nom : " + nomNiveau);
        }
        List<Collaborateur> collaborateurList = collaborateurRepository.findByNiveauCode(niveauCode);
        if (collaborateurList.isEmpty()) {
            throw new RessourceNotFoundException("Aucun collaborateur n'a été trouvé dans ce niveau.");
        }
        return mapCollaborateursToDTOs(collaborateurList);
    }

    @Override
    public List<CollaborateurDTO> getCollaborateursPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Collaborateur> collaborateurPage = collaborateurRepository.findAll(pageable);
        List<CollaborateurDTO> collaborateurDTOS = collaborateurPage.getContent().stream()
                .map(this::mapCollaborateurToDTO)
                .collect(Collectors.toList());
        return collaborateurDTOS;
    }

    private List<CollaborateurDTO> mapCollaborateursToDTOs(List<Collaborateur> collaborateurs) {
        return collaborateurs.stream()
                .map(this::mapCollaborateurToDTO)
                .collect(Collectors.toList());
    }

    private CollaborateurDTO mapCollaborateurToDTO(Collaborateur collaborateur) {
        CollaborateurDTO collaborateurDTO = modelMapper.map(collaborateur, CollaborateurDTO.class);
        if (collaborateurDTO == null) {
            throw new IllegalStateException("CollaborateurDTO mapping resulted in null");
        }
        return collaborateurDTO;
    }

    @Override
    public List<CollaborateurDTO> findCollaborateursBySearch(String search) {
        List<Collaborateur> collaborateurs;
        if (search.endsWith("*")) {
            String searchValue = search.substring(0, search.length() - 1).toLowerCase();
            collaborateurs = collaborateurRepository.findByNomStartingWithIgnoreCaseOrPrenomStartingWithIgnoreCase(searchValue, searchValue);
        } else {
            String searchValue = search.toLowerCase();
            collaborateurs = collaborateurRepository.findByNomIgnoreCaseOrPrenomIgnoreCase(searchValue, searchValue);
        }
        return mapCollaborateursToDTOs(collaborateurs);
    }

    @Override
    public List<CollaborateurDTO> findCollaborateursEnCongeParEquipeEtPeriode(String nomEquipe, LocalDate dateStartCalenderie, LocalDate dateEndCalenderie) {
        UUID equipeCode = equipeService.findCodeEquipeByNom(nomEquipe);
        if (equipeCode == null) {
            throw new RessourceNotFoundException("Équipe introuvable avec le nom : " + nomEquipe);
        }

        List<Collaborateur> collaborateurs = collaborateurRepository.findByEquipeCode(equipeCode);
        if (collaborateurs.isEmpty()) {
            throw new RessourceNotFoundException("Aucun collaborateur n'a été trouvé dans cette équipe.");
        }

        // Filtrer les collaborateurs qui sont en congé durant la période donnée
        List<Collaborateur> collaborateursEnConge = collaborateurs.stream()
                .filter(collaborateur -> congeService.isCollaborateurEnConge(collaborateur, dateStartCalenderie, dateEndCalenderie))
                .collect(Collectors.toList());

        return mapCollaborateursToDTOs(collaborateursEnConge);
    }

    @Override
    public int countCollaborateursEnCongeParEquipeEtPeriode(String nomEquipe, LocalDate dateStartCalenderie, LocalDate dateEndCalenderie) {
        UUID equipeCode = equipeService.findCodeEquipeByNom(nomEquipe);
        if (equipeCode == null) {
            throw new RessourceNotFoundException("Équipe introuvable avec le nom : " + nomEquipe);
        }

        List<Collaborateur> collaborateurs = collaborateurRepository.findByEquipeCode(equipeCode);
        if (collaborateurs.isEmpty()) {
            throw new RessourceNotFoundException("Aucun collaborateur n'a été trouvé dans cette équipe.");
        }

        long count = collaborateurs.stream()
                .filter(collaborateur -> congeService.isCollaborateurEnConge(collaborateur, dateStartCalenderie, dateEndCalenderie))
                .count();

        return (int) count;
    }

    @Override
    public List<CollaborateurDTO> findCollaborateursEnCongeParEquipeAnnee(String nomEquipe) {
        UUID equipeCode = equipeService.findCodeEquipeByNom(nomEquipe);
        if (equipeCode == null) {
            throw new RessourceNotFoundException("Équipe introuvable avec le nom : " + nomEquipe);
        }

        List<Collaborateur> collaborateurs = collaborateurRepository.findByEquipeCode(equipeCode);
        if (collaborateurs.isEmpty()) {
            throw new RessourceNotFoundException("Aucun collaborateur n'a été trouvé dans cette équipe.");
        }

        // Filtrer les collaborateurs qui sont en congé durant l'année en cours
        List<Collaborateur> collaborateursEnConge = collaborateurs.stream()
                .filter(collaborateur -> congeService.isCollaborateurEnConge(collaborateur, LocalDate.now().withDayOfYear(1), LocalDate.now().withDayOfYear(LocalDate.now().lengthOfYear())))
                .collect(Collectors.toList());

        return mapCollaborateursToDTOs(collaborateursEnConge);
    }
}
