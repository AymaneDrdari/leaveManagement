package net.pfe.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NiveauDTO {
    private UUID code;
    @NotBlank(message = "Le nom ne peut pas être vide")
    @Size(max = 100, message = "Le nom ne peut pas dépasser {max} caractères")
    private String nom;
    @Size(max = 255, message = "La description ne peut pas dépasser {max} caractères")
    private String description;
    private Date dateCreation;




}