package net.atos.common.dto.conge;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class CongeDTORequest {
    private UUID idConge;
    @NotNull(message = "mentionnez la date de debut du votre congé")
    private LocalDate dateDebut;
    @NotNull(message = "mentionnez la date de fin du votre congé")
    private LocalDate dateFin;
    private String description;
    @NotNull(message = "veuillez entrer l'email du collaborateur.")
    private String collaborateurEmail;
    @JsonProperty("demi_journee_matin")
    private boolean demiJourneeMatin;

    @JsonProperty("demi_journee_soir")
    private boolean demiJourneeSoir;

}
