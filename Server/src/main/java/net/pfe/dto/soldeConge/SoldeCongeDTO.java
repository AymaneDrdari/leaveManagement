package net.pfe.dto.soldeConge;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class SoldeCongeDTO {
    private UUID id;
    private String collaborateurEmail;
    private int annee;
    private double soldeInitial;
    private double soldeConsomme;
    private double soldeRestant;
}
