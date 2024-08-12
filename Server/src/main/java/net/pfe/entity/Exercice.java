package net.pfe.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Builder // pour créer des instances de cette classe
@Table(name = "exercice")
@Getter
@Setter // getters, setters, equals, hashCode et toString
@AllArgsConstructor
@NoArgsConstructor
public class Exercice {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_exercice")
    private UUID idExercice;
    @Column(name = "annee", nullable = false)
    private int annee;
    @Column(name = "nombre_jours_ouvres", nullable = false)
    private int nombreJoursOuvrables;
//    @OneToMany(mappedBy = "exercice")
//    private List<JourFerie> jourFeries;

    @OneToMany(mappedBy = "exercice")
    private List<SoldeConge> soldesConge;

    private LocalDate createdDate;
    private LocalDate lastUpdatedDate;

    // Méthode pour mettre à jour les dates avant la persistance
    @PrePersist
    protected void onCreate() {
        createdDate = LocalDate.now();
        lastUpdatedDate = LocalDate.now();
    }

    // Méthode pour mettre à jour la date de modification avant la mise à jour
    @PreUpdate
    protected void onUpdate() {
        lastUpdatedDate = LocalDate.now();
    }
}

