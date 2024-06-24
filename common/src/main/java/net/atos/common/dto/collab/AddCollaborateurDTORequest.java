package net.atos.common.dto.collab;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.atos.common.entity.Equipe;
import net.atos.common.entity.Niveau;
import net.atos.common.entity.enums.RoleCollaborateur;
import net.atos.common.entity.enums.TypeCollaborateur;


import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddCollaborateurDTORequest {
    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 255, message = "Le nom ne peut pas dépasser 255 caractères")
    private String nom;
    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 255, message = "Le prénom ne peut pas dépasser 255 caractères")
    private String prenom;
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    @Size(max = 255, message = "L'email ne peut pas dépasser 255 caractères")
    private String email;
    private Equipe equipe;
    private Niveau niveau;
    @NotNull(message = "Le role est obligatoire , COLLABORATEUR ou CHEF_EQUIPE")
    private RoleCollaborateur role;
    @NotNull(message = "Le type est obligatoire, SALARIE ou INDEPENDANT")
    private TypeCollaborateur type;
    private Date dateEntreeProjet;
    @PositiveOrZero(message = "Le nombre de jours travaillés par mois doit être positif ou nul")
    private double nombreJoursPayesMois;


}