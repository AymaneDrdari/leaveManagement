package net.atos.conge.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.atos.common.entity.Collaborateur;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Builder // pour créer des instances de cette classe
@Table(name = "conges")
@Data // getters, setters, equals, hashCode et toString
@AllArgsConstructor
@NoArgsConstructor
public class Conge {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID idConge;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private LocalDate dateDebut;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private LocalDate dateFin;
    private String description;

    private double nombreJoursPris;

    @Column(name = "demi_journee_matin")
    private boolean demiJourneeMatin;

    @Column(name = "demi_journee_soir")
    private boolean demiJourneeSoir;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collaborateur_email", referencedColumnName = "email")
    private Collaborateur collaborateur;
}
