package net.pfe.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "jour_ferie")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JourFerie {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private UUID id;

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;
    private Boolean isFixe;
    @Column(name = "date_fin", nullable = false)
    private LocalDate dateFin;
    @Column(name = "description")
    private String description;
    // Relation avec Exercice
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JsonIgnoreProperties("jour_ferie")
//    private Exercice exercice;

    public JourFerie(LocalDate localDate, String nouvelAn, boolean b, Exercice exercice) {
    }
}