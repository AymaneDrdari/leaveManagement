package net.pfe.service.interf;

import net.pfe.dto.soldeConge.SoldeCongeDTO;
import net.pfe.dto.soldeConge.SoldeCongeDTORequest;

public interface SoldeCongeService {
    SoldeCongeDTO createOrUpdateSoldeConge(SoldeCongeDTORequest soldeCongeDTORequest);
    SoldeCongeDTO getSoldeConge(String email, int annee); // Nouvelle méthode
}
