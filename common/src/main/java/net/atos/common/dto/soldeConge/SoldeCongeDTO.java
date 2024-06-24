package net.atos.common.dto.soldeConge;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class SoldeCongeDTO {
    private UUID id;
    private String collaborateurEmail;
    private int annee;
    private double soldeInitial;
    private double soldeConsomme;
    private double soldeRestant;
}
