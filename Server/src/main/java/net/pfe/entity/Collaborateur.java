package net.pfe.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.pfe.entity.enums.RoleCollaborateur;
import net.pfe.entity.enums.TypeCollaborateur;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "collaborateurs")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Collaborateur {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false, unique = true)
    private String email;

    @ManyToOne
    @JoinColumn(name = "equipe_id")
    private Equipe equipe;

    @ManyToOne
    @JoinColumn(name = "niveau_id")
    private Niveau niveau;

    @OneToMany(mappedBy = "collaborateur", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Conge> conges;

    @OneToMany(mappedBy = "collaborateur", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<SoldeConge> soldesConge;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_collaborateur")
    private TypeCollaborateur type;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_collaborateur")
    private RoleCollaborateur role;

    @Temporal(TemporalType.DATE)
    @Column(name = "date_entree_projet", nullable = false)
    private Date dateEntreeProjet;

    @Column(name = "date_sortie_projet")
    @Temporal(TemporalType.DATE)
    private Date dateSortieProjet;

    @Column(name = "nombre_jours_payés")
    private double nombreJoursPayesMois;

    public String getEquipeNom() {
        return this.equipe != null ? this.equipe.getNom() : null;
    }

    public String getNiveauNom() {
        return this.niveau != null ? this.niveau.getNom() : null;
    }

    public void setDateEntreeProjet(Date dateEntreeProjet) {
        this.dateEntreeProjet = dateEntreeProjet;
    }

    @Override
    public String toString() {
        return "Collaborateur{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", type=" + type +
                ", equipeNom='" + getEquipeNom() + '\'' +
                ", niveauNom='" + getNiveauNom() + '\'' +
                ", nombreJoursPayesMois=" + nombreJoursPayesMois +
                ", dateEntreeProjet=" + dateEntreeProjet +
                ", dateSortieProjet=" + dateSortieProjet +
                '}';
    }
}
