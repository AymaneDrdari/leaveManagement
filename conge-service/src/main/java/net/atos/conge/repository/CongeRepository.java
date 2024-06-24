package net.atos.conge.repository;

import net.atos.common.entity.Collaborateur;
import net.atos.common.entity.Conge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CongeRepository extends JpaRepository<Conge, UUID> {
    // List of leaves for a collaborator
    List<Conge> findByCollaborateur(Collaborateur collaborateur);

    // To prevent end date being less than start date
    boolean existsByCollaborateurAndDateDebutLessThanEqualAndDateFinGreaterThanEqual(Collaborateur collaborateur, LocalDate dateDebut, LocalDate dateFin);
    Optional<Conge> findById(UUID id);
    List<Conge> findAll();
}
