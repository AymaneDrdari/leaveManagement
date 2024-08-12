package net.pfe.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
@Entity
@Table(name = "solde_conge")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SoldeConge {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "exercice_id", nullable = false)
    private Exercice exercice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collaborateur_email", referencedColumnName = "email")
    private Collaborateur collaborateur;

    @Column(name = "solde_initial", nullable = false)
    private double soldeInitial;

    @Column(name = "solde_consomme", nullable = false)
    private double soldeConsomme;

    @Column(name = "solde_restant", nullable = false)
    private double soldeRestant;

    // Méthode pour calculer le solde restant
    public void calculerSoldeRestant() {
        this.soldeRestant = this.soldeInitial - this.soldeConsomme;
    }
}
