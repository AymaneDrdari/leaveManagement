package net.atos.collaborateur.service.interf;



import net.atos.common.dto.collab.AddCollaborateurDTORequest;
import net.atos.common.dto.collab.CollaborateurDTO;
import net.atos.common.dto.collab.UpdateCollaborateurDTORequest;

import java.util.List;
import java.util.UUID;

public interface CollaborateurService {
    CollaborateurDTO createCollaborateur(AddCollaborateurDTORequest addCollaborateurDTORequest);
    CollaborateurDTO updateCollaborateur(UpdateCollaborateurDTORequest updateCollaborateurDTORequest);
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
}