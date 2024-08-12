package net.pfe.dto.jourFerie;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class JourFerieRequestDTO {
    private UUID id;

    @NotNull(message = "La date de début ne peut pas être nulle")
    private LocalDate dateDebut;
    @NotNull(message = "La date de fin ne peut pas être nulle")
    private LocalDate dateFin;
    private String description;
    private Boolean isFixe;

}
