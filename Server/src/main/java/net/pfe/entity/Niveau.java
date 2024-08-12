package net.pfe.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "niveau")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Niveau {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "code", nullable = false, updatable = false, unique = true, columnDefinition = "BINARY(16)")
    private UUID code;

    private String nom;
    private String description;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_creation", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date dateCreation;

    @OneToMany(mappedBy = "niveau")
    @JsonIgnore
    private List<Collaborateur> collaborateurs;
}
