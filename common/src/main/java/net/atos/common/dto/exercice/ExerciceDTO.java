package net.atos.common.dto.exercice;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;


@Data
public class ExerciceDTO {
    private UUID idExercice;
    private int annee;
    private int nombreJoursOuvrables;
    private LocalDate createdDate;
    private LocalDate lastUpdatedDate;
}
