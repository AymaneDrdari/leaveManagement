package net.pfe.service.interf;

import net.pfe.dto.collab.CollaborateursEnCongeRequestDTO;
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

   // int countCollaborateursEnCongeParEquipeAnnee(String nomEquipe);

    //    @Override
    //    public int countCollaborateursEnCongeParEquipeAnnee(String nomEquipe) {
    //        UUID equipeCode = equipeService.findCodeEquipeByNom(nomEquipe);
    //        if (equipeCode == null) {
    //            throw new RessourceNotFoundException("Équipe introuvable avec le nom : " + nomEquipe);
    //        }
    //
    //        List<Collaborateur> collaborateurs = collaborateurRepository.findByEquipeCode(equipeCode);
    //        if (collaborateurs.isEmpty()) {
    //            throw new RessourceNotFoundException("Aucun collaborateur n'a été trouvé dans cette équipe.");
    //        }
    //
    //        long count = collaborateurs.stream()
    //                .filter(collaborateur -> isCollaborateurEnConge(collaborateur, LocalDate.now().withDayOfYear(1), LocalDate.now().withDayOfYear(LocalDate.now().lengthOfYear())))
    //                .count();
    //
    //        return (int) count;
    //    }
    //int countCollaborateursEnCongeParEquipeEtPeriode(String nomEquipe, LocalDate dateStartCalenderie, LocalDate dateEndCalenderie);

    //    @Override
    //    public int countCollaborateursEnCongeParEquipeAnnee(String nomEquipe) {
    //        UUID equipeCode = equipeService.findCodeEquipeByNom(nomEquipe);
    //        if (equipeCode == null) {
    //            throw new RessourceNotFoundException("Équipe introuvable avec le nom : " + nomEquipe);
    //        }
    //
    //        List<Collaborateur> collaborateurs = collaborateurRepository.findByEquipeCode(equipeCode);
    //        if (collaborateurs.isEmpty()) {
    //            throw new RessourceNotFoundException("Aucun collaborateur n'a été trouvé dans cette équipe.");
    //        }
    //
    //        long count = collaborateurs.stream()
    //                .filter(collaborateur -> isCollaborateurEnConge(collaborateur, LocalDate.now().withDayOfYear(1), LocalDate.now().withDayOfYear(LocalDate.now().lengthOfYear())))
    //                .count();
    //
    //        return (int) count;
    //    }

    //int countCollaborateursEnCongeParEquipeEtPeriode(CollaborateursEnCongeRequestDTO request);

    //    @Override
    //    public int countCollaborateursEnCongeParEquipeAnnee(String nomEquipe) {
    //        UUID equipeCode = equipeService.findCodeEquipeByNom(nomEquipe);
    //        if (equipeCode == null) {
    //            throw new RessourceNotFoundException("Équipe introuvable avec le nom : " + nomEquipe);
    //        }
    //
    //        List<Collaborateur> collaborateurs = collaborateurRepository.findByEquipeCode(equipeCode);
    //        if (collaborateurs.isEmpty()) {
    //            throw new RessourceNotFoundException("Aucun collaborateur n'a été trouvé dans cette équipe.");
    //        }
    //
    //        long count = collaborateurs.stream()
    //                .filter(collaborateur -> isCollaborateurEnConge(collaborateur, LocalDate.now().withDayOfYear(1), LocalDate.now().withDayOfYear(LocalDate.now().lengthOfYear())))
    //                .count();
    //
    //        return (int) count;
    //    }
   // int countCollaborateursEnCongeParEquipeEtPeriode(CollaborateursEnCongeRequestDTO request);

    //    @Override
    //    public int countCollaborateursEnCongeParEquipeAnnee(String nomEquipe) {
    //        UUID equipeCode = equipeService.findCodeEquipeByNom(nomEquipe);
    //        if (equipeCode == null) {
    //            throw new RessourceNotFoundException("Équipe introuvable avec le nom : " + nomEquipe);
    //        }
    //
    //        List<Collaborateur> collaborateurs = collaborateurRepository.findByEquipeCode(equipeCode);
    //        if (collaborateurs.isEmpty()) {
    //            throw new RessourceNotFoundException("Aucun collaborateur n'a été trouvé dans cette équipe.");
    //        }
    //
    //        long count = collaborateurs.stream()
    //                .filter(collaborateur -> isCollaborateurEnConge(collaborateur, LocalDate.now().withDayOfYear(1), LocalDate.now().withDayOfYear(LocalDate.now().lengthOfYear())))
    //                .count();
    //
    //        return (int) count;
    //    }
    int countCollaborateursEnCongeParEquipeEtParPeriode(CollaborateursEnCongeRequestDTO request);

    List<CongeDetailDTO> findCongesByEquipe(String nomEquipe);
}