package net.atos.exercice.repository;

import net.atos.exercice.entity.Exercice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExerciceRepository extends JpaRepository<Exercice, UUID> {
    Optional<Exercice> findByAnnee(int annee);
}
