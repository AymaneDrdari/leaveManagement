package net.pfe.repository;

import net.pfe.entity.Collaborateur;
import net.pfe.entity.Conge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
@Repository
public interface CongeRepository extends JpaRepository<Conge, UUID> {
    //liste de congés d'un collaborateur
    List<Conge> findByCollaborateur(Collaborateur collaborateur);

    //pour ne pas ecrire la date fin moins que la date debut
    boolean existsByCollaborateurAndDateDebutAndDateFin(Collaborateur collaborateur, LocalDate dateDebut, LocalDate dateFin);
    boolean existsByCollaborateurAndDateDebutLessThanEqualAndDateFinGreaterThanEqual(Collaborateur collaborateur, LocalDate dateFin, LocalDate dateDebut);
    List<Conge> findByCollaborateur_Equipe_Nom(String nomEquipe);
}
