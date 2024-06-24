package net.atos.common.dto.jourFerie;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class JourFerieDTO {
    private UUID id;
    private String description;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Boolean isFixe;


}
