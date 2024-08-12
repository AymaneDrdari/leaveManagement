package net.pfe.dto.exercice;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;


@Data
public class ExerciceDTORequest {
    private UUID idExercice;
    private int annee;
    private int nombreJoursOuvrables;
    private LocalDate createdDate;
    private LocalDate lastUpdatedDate;
}
