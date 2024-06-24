package net.atos.conge.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class CongeDTO {
    private UUID idConge;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String description;
    private String collaborateurEmail;
    private double NombreJoursPris;

    @JsonProperty("demi_journee_matin")
    private boolean demiJourneeMatin;

    @JsonProperty("demi_journee_soir")
    private boolean demiJourneeSoir;
}
