package net.pfe.repository;

import net.pfe.entity.Collaborateur;
import net.pfe.entity.Conge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.UUID;
@Repository
public interface CongeRepository extends JpaRepository<Conge, UUID> {
    //liste de congés d'un collaborateur
    List<Conge> findByCollaborateur(Collaborateur collaborateur);

    //pour ne pas ecrire la date fin moins que la date debut
    boolean existsByCollaborateurAndDateDebutAndDateFin(Collaborateur collaborateur, LocalDate dateDebut, LocalDate dateFin);
    List<Conge> findByCollaborateurInAndDateDebutBetween(List<Collaborateur> collaborateurs, LocalDate startDate, LocalDate endDate);

    @Query("SELECT c FROM Conge c WHERE c.collaborateur.equipe.nom = :equipeNom AND :date BETWEEN c.dateDebut AND c.dateFin")
    List<Conge> findByEquipeAndDate(@Param("equipeNom") String equipeNom, @Param("date") LocalDate date);

    List<Conge> findByCollaborateur_Equipe_NomAndDateDebutAfterAndDateFinBefore(String nomEquipe, LocalDate startOfYear, LocalDate endOfYear);
}
