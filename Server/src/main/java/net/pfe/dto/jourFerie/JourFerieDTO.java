package net.pfe.dto.jourFerie;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
public class JourFerieDTO {
    private UUID id;
    private String description;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Boolean isFixe;

    public JourFerieDTO(UUID id, String description, LocalDate dateDebut, LocalDate dateFin, Boolean isFixe) {
        this.id = id;
        this.description = description;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.isFixe = isFixe;
    }
}
