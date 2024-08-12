package net.pfe.service.interf;

import net.pfe.dto.NiveauDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NiveauService {
    NiveauDTO addNiveau(NiveauDTO niveauDTO);

    UUID findCodeNiveauByNom(String nomNiveau);

    List<NiveauDTO> getAllNiveaux();
    Optional<NiveauDTO> getNiveauById(UUID code);

    // Met à jour un niveau
    NiveauDTO updateNiveau(UUID code, NiveauDTO niveauDTO);

    // Supprime un niveau
    void deleteNiveau(UUID code);
}
