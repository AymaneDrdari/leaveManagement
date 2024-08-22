package net.pfe.service.interf;

import net.pfe.dto.conge.CongeDTO;
import net.pfe.dto.conge.CongeDTORequest;
import net.pfe.dto.conge.CongeDetailDTO;
import net.pfe.entity.Collaborateur;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface CongeService {
    CongeDTO createConge(CongeDTORequest congeDTORequest);

    CongeDTO getCongeById(UUID id);
    List<CongeDTO> getAllConges();

    CongeDTO updateConge(UUID id,CongeDTORequest congéDTORequest);

    List<CongeDTO> getCongesByCollaborateur(String nom, String prenom);

    void deleteConge(UUID id);
    //pour savoir si un congé declarer d'un collab pour la meme periode
    boolean congeExistsForCollaborateur(String collaborateurEmail, LocalDate dateDebut, LocalDate dateFin);
    boolean isCollaborateurEnConge(Collaborateur collaborateur, LocalDate dateStart, LocalDate dateEnd);

    List<CongeDTO> getJCongéPage(int page, int size);

    //int countCollaborateursEnCongeParEquipeAnnee(String nomEquipe);

    //List<CongeDetailDTO> findCongesByEquipe(String nomEquipe);

    int countCollaborateursEnCongeParEquipeMois(String nomEquipe, int mois, int annee);

    List<CongeDetailDTO> findCongesByEquipe(String nomEquipe, int annee);

    int countCollaborateursEnCongeParEquipeParPeriode(String nomEquipe, LocalDate dateStart, LocalDate dateEnd);

    //List<CongeDTO> findCongesByEquipeAndPeriod(String nomEquipe, LocalDate startDate, LocalDate endDate);
}