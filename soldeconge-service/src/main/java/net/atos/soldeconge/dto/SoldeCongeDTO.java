package net.atos.soldeconge.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class SoldeCongeDTO {
    private UUID id;
    private String collaborateurEmail;
    private int annee;
    private double soldeInitial;
    private double soldeConsomme;
    private double soldeRestant;
}
