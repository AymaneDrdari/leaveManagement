package net.atos.collaborateur.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.atos.collaborateur.entity.Collaborateur;
import net.atos.collaborateur.exception.RessourceAlreadyExistsException;
import net.atos.collaborateur.exception.RessourceNotFoundException;
import net.atos.collaborateur.repository.CollaborateurRepository;
import net.atos.collaborateur.service.interf.CollaborateurService;
import net.atos.common.client.EquipeServiceClient;
import net.atos.common.client.NiveauServiceClient;

import net.atos.common.dto.collab.AddCollaborateurDTORequest;
import net.atos.common.dto.collab.CollaborateurDTO;
import net.atos.common.dto.collab.UpdateCollaborateurDTORequest;
import net.atos.common.entity.Equipe;
import net.atos.common.entity.Niveau;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class CollaborateurServiceImpl implements CollaborateurService {
    private static final Logger logger = LoggerFactory.getLogger(CollaborateurServiceImpl.class);

    private final CollaborateurRepository collaborateurRepository;
    private final ModelMapper modelMapper;
    private final EquipeServiceClient equipeServiceClient;
    private final NiveauServiceClient niveauServiceClient;

    @Autowired
    public CollaborateurServiceImpl(CollaborateurRepository collaborateurRepository,
                                    ModelMapper modelMapper,
                                    @Qualifier("equipeServiceClient") EquipeServiceClient equipeServiceClient,
                                    @Qualifier("niveauServiceClient") NiveauServiceClient niveauServiceClient) {
        this.collaborateurRepository = collaborateurRepository;
        this.modelMapper = modelMapper;
        this.equipeServiceClient = equipeServiceClient;
        this.niveauServiceClient = niveauServiceClient;
    }

    @Override
    public CollaborateurDTO createCollaborateur(AddCollaborateurDTORequest addCollaborateurDTORequest) {
        String email = addCollaborateurDTORequest.getEmail();
        logger.info("Tentative de création d'un nouveau collaborateur avec email : {}", email);

        if (collaborateurRepository.existsByEmail(email)) {
            logger.warn("La création du collaborateur a échoué car l'email {} existe déjà.", email);
            throw new RessourceAlreadyExistsException("L'email du collaborateur existe déjà.");
        }

        UUID equipeId = addCollaborateurDTORequest.getEquipe().getCode();
        UUID niveauId = addCollaborateurDTORequest.getNiveau().getCode();

        if (equipeId != null && equipeServiceClient.getEquipeById(equipeId).getData() == null) {
            throw new RessourceNotFoundException("Équipe non trouvée avec l'identifiant : " + equipeId);
        }

        if (niveauId != null && niveauServiceClient.getNiveauById(niveauId).getData() == null) {
            throw new RessourceNotFoundException("Niveau non trouvé avec l'identifiant : " + niveauId);
        }

        Collaborateur collaborateur = modelMapper.map(addCollaborateurDTORequest, Collaborateur.class);

        logger.debug("Mapping de l'objet AddCollaborateurDTORequest vers Collaborateur : {}", addCollaborateurDTORequest);
        collaborateur = modelMapper.map(addCollaborateurDTORequest, Collaborateur.class);

        logger.info("Enregistrement du collaborateur dans la base de données : {}", collaborateur);
        Collaborateur savedCollaborateur = collaborateurRepository.save(collaborateur);

        logger.debug("Mapping du collaborateur sauvegardé vers CollaborateurDTO : {}", savedCollaborateur);
        CollaborateurDTO collaborateurDTO;
        try {
            collaborateurDTO = modelMapper.map(savedCollaborateur, CollaborateurDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Le mapping du collaborateur sauvegardé vers CollaborateurDTO a échoué", e);
        }

        logger.info("Collaborateur créé avec succès, id : {}", collaborateurDTO.getId());
        return collaborateurDTO;
    }

    @Override
    public CollaborateurDTO updateCollaborateur(UpdateCollaborateurDTORequest updateCollaborateurDTORequest) {
        UUID id = updateCollaborateurDTORequest.getId();

        Collaborateur collaborateur = collaborateurRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("Collaborateur non trouvé avec l'identifiant : " + id));

        if (updateCollaborateurDTORequest.getEquipeNom() != null) {
            UUID equipeCode = equipeServiceClient.findCodeEquipeByNom(updateCollaborateurDTORequest.getEquipeNom()).getData();
            if (equipeCode == null) {
                throw new RessourceNotFoundException("Équipe non trouvée avec le nom : " + updateCollaborateurDTORequest.getEquipeNom());
            }
            collaborateur.setEquipe(new Equipe(equipeCode, updateCollaborateurDTORequest.getEquipeNom()));
        }

        if (updateCollaborateurDTORequest.getNiveauNom() != null) {
            UUID niveauCode = niveauServiceClient.findCodeNiveauByNom(updateCollaborateurDTORequest.getNiveauNom()).getData();
            if (niveauCode == null) {
                throw new RessourceNotFoundException("Niveau non trouvé avec le nom : " + updateCollaborateurDTORequest.getNiveauNom());
            }
            collaborateur.setNiveau(new Niveau(niveauCode, updateCollaborateurDTORequest.getNiveauNom()));
        }

        modelMapper.map(updateCollaborateurDTORequest, collaborateur);

        Collaborateur updatedCollaborateur = collaborateurRepository.save(collaborateur);

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
        UUID equipeCode = equipeServiceClient.findCodeEquipeByNom(nomEquipe).getData();
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
        UUID niveauCode = niveauServiceClient.findCodeNiveauByNom(nomNiveau).getData();
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
                .map(collaborateur -> modelMapper.map(collaborateur, CollaborateurDTO.class))
                .collect(Collectors.toList());
        return collaborateurDTOS;
    }

    private List<CollaborateurDTO> mapCollaborateursToDTOs(List<Collaborateur> collaborateurs) {
        return collaborateurs.stream()
                .map(collaborateur -> {
                    CollaborateurDTO collaborateurDTO = modelMapper.map(collaborateur, CollaborateurDTO.class);
                    if (collaborateur.getEquipe() != null) {
                        collaborateurDTO.setEquipeNom(collaborateur.getEquipe().getNom());
                    }
                    if (collaborateur.getNiveau() != null) {
                        collaborateurDTO.setNiveauNom(collaborateur.getNiveau().getNom());
                    }
                    return collaborateurDTO;
                })
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
}
