package net.atos.jourferie.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JourFerieRequestDTO {
    private UUID id;

    @NotNull(message = "La date de début ne peut pas être nulle")
    private LocalDate dateDebut;
    @NotNull(message = "La date de fin ne peut pas être nulle")
    private LocalDate dateFin;
    private String description;
    @NotNull
    private Boolean isFixe;

}
