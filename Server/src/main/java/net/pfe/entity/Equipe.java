package net.pfe.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.validation.constraints.NotBlank;


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
    @Column(name = "code", nullable = false, unique = true)
    private UUID code;

    @Column(unique = true)
    private String nom;

    private String description;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_creation", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date dateCreation;

    @OneToMany(mappedBy = "equipe")
    @JsonIgnore
    private List<Collaborateur> collaborateurs;

    @NotBlank(message = "La couleur ne peut pas être vide")
    @Column(length = 7) // Code couleur HEX avec "#", exemple: #FFFFFF
    private String couleur;
}
