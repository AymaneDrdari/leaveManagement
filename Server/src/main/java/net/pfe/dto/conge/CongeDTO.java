package net.pfe.dto.conge;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
