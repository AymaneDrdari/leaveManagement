package net.pfe.service.interf;

import net.pfe.dto.EquipeDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EquipeService {
    EquipeDTO addEquipe(EquipeDTO equipeDTO);

    UUID findCodeEquipeByNom(String nomEquipe);

    List<EquipeDTO> getAllEquipes();
    Optional<EquipeDTO> getEquipeById(UUID code);

    // Méthode pour modifier une équipe existante
    EquipeDTO updateEquipe(UUID code, EquipeDTO equipeDTO);

    // Méthode pour supprimer une équipe par son ID
    void deleteEquipe(UUID code);
}
