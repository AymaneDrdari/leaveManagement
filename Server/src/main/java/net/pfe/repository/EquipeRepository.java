package net.pfe.repository;

import net.pfe.entity.Equipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EquipeRepository extends JpaRepository<Equipe, UUID> {

    Optional<Equipe> findByNom(String nom);
    boolean existsById(UUID id);
}