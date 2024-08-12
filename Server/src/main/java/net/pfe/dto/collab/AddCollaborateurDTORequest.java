package net.pfe.dto.collab;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.*;
import net.pfe.entity.Equipe;
import net.pfe.entity.Niveau;
import net.pfe.entity.enums.RoleCollaborateur;
import net.pfe.entity.enums.TypeCollaborateur;

import java.util.Date;
import java.util.UUID;

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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date dateEntreeProjet;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date dateSortieProjet;
    @PositiveOrZero(message = "Le nombre de jours travaillés par mois doit être positif ou nul")
    private double nombreJoursPayesMois;
    private String password;
    private UUID id;
}