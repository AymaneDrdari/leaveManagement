package net.atos.niveau.service.interf;

import net.atos.common.dto.NiveauDTO;  // Import correct

import java.util.List;
import java.util.UUID;

public interface NiveauService {
    NiveauDTO addNiveau(NiveauDTO niveauDTO);

    UUID findCodeNiveauByNom(String nomNiveau);

    List<NiveauDTO> getAllNiveaux();

    NiveauDTO getNiveauById(UUID code);
}
