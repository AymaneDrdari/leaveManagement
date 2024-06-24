package net.atos.niveau.repository;


import net.atos.niveau.entity.Niveau;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface NiveauRepository extends JpaRepository<Niveau, UUID> {
    Optional<Niveau> findByNom(String nom);

}
