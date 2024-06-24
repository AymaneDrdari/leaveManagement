package net.atos.common.dto.collab;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.atos.common.entity.enums.RoleCollaborateur;
import net.atos.common.entity.enums.TypeCollaborateur;


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
    private String equipeNom;
    private String niveauNom;
    private double nombreJoursPayesMois;
    private Date dateEntreeProjet;
    private Date dateSortieProjet;


}