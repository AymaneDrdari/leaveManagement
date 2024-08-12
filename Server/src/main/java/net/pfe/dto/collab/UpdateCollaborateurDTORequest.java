package net.pfe.dto.collab;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.pfe.entity.enums.RoleCollaborateur;
import net.pfe.entity.enums.TypeCollaborateur;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateCollaborateurDTORequest {
    @NotNull
    private UUID id;
    @NotNull
    @Size(min = 2, max = 100)
    private String nom;
    @NotNull
    @Size(min = 2, max = 100)
    private String prenom;
    @NotNull
    @Email
    private String email;
    private String equipeNom;
    private String niveauNom;
    private RoleCollaborateur role;
    private TypeCollaborateur type;
    private double nombreJoursPayesMois;
    private Date dateSortieProjet;
    private Date dateEntreeProjet;

}