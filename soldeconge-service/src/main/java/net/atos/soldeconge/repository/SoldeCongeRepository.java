package net.atos.soldeconge.repository;

import net.atos.common.entity.Collaborateur;
import net.atos.soldeconge.entity.SoldeConge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface SoldeCongeRepository extends JpaRepository<SoldeConge, UUID> {
    Optional<SoldeConge> findByCollaborateurAndExercice_Annee(Collaborateur collaborateur, int annee);

}
