package net.pfe.dto.collab;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
public class CollaborateurDTO {
    private UUID id;
    private String nom;
    private String prenom;
    private String email;
    private RoleCollaborateur role;
    private TypeCollaborateur type;
    private Equipe equipe;
    private Niveau niveau;
    private double nombreJoursPayesMois;
    private Date dateEntreeProjet;
    private Date dateSortieProjet;
}
