package net.pfe.service.interf;

import net.pfe.dto.collab.AddCollaborateurDTORequest;
import net.pfe.dto.collab.CollaborateurDTO;
import net.pfe.dto.collab.CollaborateursEnCongeRequestDTO;
import net.pfe.dto.collab.UpdateCollaborateurDTORequest;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface CollaborateurService {
    CollaborateurDTO createCollaborateur(AddCollaborateurDTORequest addCollaborateurDTORequest);
    CollaborateurDTO updateCollaborateur(CollaborateurDTO collaborateurDTO);
    void deleteCollaborateur(UUID id);
    CollaborateurDTO findCollaborateurById(UUID id);
    List<CollaborateurDTO> findAllCollaborateurs();
    List<CollaborateurDTO> getCollaborateursByNom(String nom);

    List<CollaborateurDTO> getCollaborateursByPrenom(String prenom);

    List<CollaborateurDTO> getCollaborateursByNomAndPrenom(String nom, String prenom);

    List<CollaborateurDTO> findCollaborateursByEquipe(String nomEquipe);


    List<CollaborateurDTO> findCollaborateursByNiveau(String nomNiveau);

    List<CollaborateurDTO> getCollaborateursPage(int page, int size);

    // boolean collaborateurExistsByEmail(String email);

    List<CollaborateurDTO> findCollaborateursBySearch(String search);
    List<CollaborateurDTO> findCollaborateursEnCongeParEquipeEtPeriode(String nomEquipe, LocalDate dateStartCalenderie, LocalDate dateEndCalenderie);
    // int countCollaborateursEnCongeParEquipeEtPeriode(String nomEquipe, LocalDate dateStartCalenderie, LocalDate dateEndCalenderie);


    //List<CollaborateurDTO> findCollaborateursEnCongeParEquipeAnnee(String nomEquipe);

    //pour email 2 methode:
    //List<CollaborateurDTO> getCollaborateursEnConge(LocalDate date);
    //List<String> getChefsEquipeEmails(String equipe);

}