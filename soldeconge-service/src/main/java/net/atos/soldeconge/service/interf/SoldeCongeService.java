package net.atos.soldeconge.service.interf;

import net.atos.common.dto.soldeConge.SoldeCongeDTO;
import net.atos.common.dto.soldeConge.SoldeCongeDTORequest;

public interface SoldeCongeService {
    SoldeCongeDTO createOrUpdateSoldeConge(SoldeCongeDTORequest soldeCongeDTORequest);
    SoldeCongeDTO getSoldeConge(String email, int annee);
}
