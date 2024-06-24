package net.atos.common.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "equipe")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Equipe {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "code",nullable = false, unique = true)
    private UUID code;
    @Column(unique = true)
    private String nom;
    private String description;
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_creation", nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP") //
    private Date dateCreation;
    @OneToMany(mappedBy = "equipe")
    //@JsonManagedReference
    private List<Collaborateur> collaborateurs;

    public Equipe(UUID code, String nom) {
        this.code = code;
        this.nom = nom;
    }
}
