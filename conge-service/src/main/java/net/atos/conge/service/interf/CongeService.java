package net.atos.conge.service.interf;



import net.atos.conge.dto.CongeDTO;
import net.atos.conge.dto.CongeDTORequest;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface CongeService {
    CongeDTO createConge(CongeDTORequest congeDTORequest);

    CongeDTO getCongeById(UUID id);
    List<CongeDTO> getAllConges();

    CongeDTO updateConge(CongeDTORequest congeDTORequest);

    List<CongeDTO> getCongesByCollaborateur(String nom, String prenom);

    void deleteConge(UUID id);
    //pour savoir si un congé declarer d'un collab pour la meme periode
    boolean congeExistsForCollaborateur(String collaborateurEmail, LocalDate dateDebut, LocalDate dateFin);

}
