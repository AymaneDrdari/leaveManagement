package net.pfe.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.pfe.dto.collab.AddCollaborateurDTORequest;
import net.pfe.dto.collab.CollaborateurDTO;
import net.pfe.entity.*;
import net.pfe.entity.enums.RoleCollaborateur;
import net.pfe.exception.RessourceAlreadyExistsException;
import net.pfe.exception.RessourceNotFoundException;
import net.pfe.repository.*;
import net.pfe.service.EmailService;
import net.pfe.service.interf.CollaborateurService;
import net.pfe.service.interf.CongeService;
import net.pfe.service.interf.EquipeService;
import net.pfe.service.interf.NiveauService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private final CongeRepository congeRepository;
    private final JourFerieRepository jourFerieRepository;
    private final EmailService emailService;
    public final EquipeService equipeService;
    public final NiveauService niveauService;
    public final ModelMapper modelMapper;
    private final CongeService congeService;


    @Autowired
<<<<<<< HEAD
    public CollaborateurServiceImpl(CollaborateurRepository collaborateurRepository, EquipeRepository equipeRepository, NiveauRepository niveauRepository, CongeRepository congeRepository, EquipeService equipeService, NiveauService niveauService, ModelMapper modelMapper, EmailService emailService,@Lazy CongeService congeService) {
=======
    public CollaborateurServiceImpl(CollaborateurRepository collaborateurRepository, EquipeRepository equipeRepository, NiveauRepository niveauRepository, CongeRepository congeRepository, JourFerieRepository jourFerieRepository, EquipeService equipeService, NiveauService niveauService, ModelMapper modelMapper, CongeService congeService, EmailService emailService) {
>>>>>>> 2d6bf080d8ad68680644cd0fd6a6a341e330420b
        this.collaborateurRepository = collaborateurRepository;
        this.equipeRepository = equipeRepository;
        this.congeRepository = congeRepository;
        this.jourFerieRepository = jourFerieRepository;
        this.emailService = emailService;
        this.niveauRepository = niveauRepository;
        this.equipeService = equipeService;
        this.niveauService = niveauService;
        this.modelMapper = modelMapper;
        this.congeService = congeService;
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




    //pour email:

    @Override
    public void sendDailyReportToTeamLeaders(LocalDate date) {
        Map<String, List<Conge>> rapport = generateDailyReportForTeamLeaders(date);

        // Filter teams with collaborators on leave
        Map<String, List<Conge>> rapportFiltre = rapport.entrySet().stream()
                .filter(entry -> !entry.getValue().isEmpty())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // Calculate the total number of people on leave
        long nombreTotalEnConge = rapportFiltre.values().stream()
                .flatMap(List::stream)
                .count();

        // Retrieve team leaders for each team
        List<Equipe> equipes = equipeRepository.findAll();
        // Get remaining holidays for the current month
        List<LocalDate> joursFeriesRestants = getRemainingHolidaysForCurrentMonth();


        for (Equipe equipe : equipes) {
            String nomEquipe = equipe.getNom();
            List<Collaborateur> chefs = collaborateurRepository.findByEquipe_NomAndRole(nomEquipe, RoleCollaborateur.CHEF_EQUIPE);

            // Convert leaders to DTOs
            List<CollaborateurDTO> chefsDTO = mapCollaborateursToDTOs(chefs);

            for (CollaborateurDTO chef : chefsDTO) {
                String email = chef.getEmail();
                String subject = "Rapport quotidien des congés";
                StringBuilder text = new StringBuilder("Bonjour,\n\n");
                // Include remaining holidays for the month
                if (!joursFeriesRestants.isEmpty()) {
                    text.append("Jours fériés restants pour ce mois :\n");
                    joursFeriesRestants.forEach(jour -> text.append("- ").append(jour).append("\n"));
                    text.append("\n");
                }

                // Add the total number of people on leave
                text.append(nombreTotalEnConge).append(" personne(s) absente(s) aujourd'hui :\n\n");

                rapportFiltre.forEach((equipeNom, conges) -> {
                    text.append(equipeNom).append(" :\n");
                    conges.forEach(conge -> {
                        Collaborateur collaborateur = conge.getCollaborateur();
                        text.append("- ").append(collaborateur.getPrenom())
                                .append(" ").append(collaborateur.getNom())
                                .append(" (du ").append(conge.getDateDebut())
                                .append(" au ").append(conge.getDateFin()).append(")\n");
                    });
                    text.append("\n");
                });

                text.append("Merci.");

                emailService.sendEmail(email, subject, text.toString());
            }
        }
    }
    private List<LocalDate> getRemainingHolidaysForCurrentMonth() {
        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(today);

        // Définir le début et la fin du mois
        LocalDate startOfMonth = currentMonth.atDay(1);
        LocalDate endOfMonth = currentMonth.atEndOfMonth();

        // Récupérer les jours fériés pour le mois
        List<JourFerie> joursFeries = jourFerieRepository.findAllHolidaysForMonth(startOfMonth, endOfMonth);

        // Filtrer les jours fériés pour garder uniquement ceux qui sont à venir
        return joursFeries.stream()
                .map(JourFerie::getDateDebut)
                .filter(jour -> jour.isAfter(today) || jour.isEqual(today))
                .collect(Collectors.toList());
    }




    public Map<String, List<Conge>> generateDailyReportForTeamLeaders(LocalDate date) {
        List<Equipe> equipes = equipeRepository.findAll();
        Map<String, List<Conge>> rapport = new HashMap<>();

        for (Equipe equipe : equipes) {
            String equipeNom = equipe.getNom();
            List<Conge> congesEnCours = congeRepository.findByEquipeAndDate(equipeNom, date);
            rapport.put(equipeNom, congesEnCours);
        }

        return rapport;
    }


}