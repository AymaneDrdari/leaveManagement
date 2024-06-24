package net.atos.equipe.service.interf;

import net.atos.common.dto.EquipeDTO;

import java.util.List;
import java.util.UUID;

public interface EquipeService {
    EquipeDTO addEquipe(EquipeDTO equipeDTO);

    UUID findCodeEquipeByNom(String nomEquipe);

    List<EquipeDTO> getAllEquipes();

    EquipeDTO getEquipeById(UUID code);
}
