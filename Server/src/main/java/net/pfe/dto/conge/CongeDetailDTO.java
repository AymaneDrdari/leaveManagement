package net.pfe.dto.conge;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CongeDetailDTO {
    private String collaborateurNom;
    private String collaborateurPrenom;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private double duree;  // Nombre de jours de congé
}
