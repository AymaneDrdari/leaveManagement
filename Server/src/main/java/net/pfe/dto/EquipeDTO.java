package net.pfe.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EquipeDTO {
    private UUID code;
    @NotBlank(message = "Le nom ne peut pas être vide")
    @Size(max = 100, message = "Le nom ne peut pas dépasser {max} caractères")
    private String nom;
    private String description;
    private Date dateCreation;
    @Size(max = 7, message = "Le code couleur ne peut pas dépasser {max} caractères")
    private String couleur;
}