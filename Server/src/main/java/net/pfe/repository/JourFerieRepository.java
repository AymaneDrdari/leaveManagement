package net.pfe.repository;

import net.pfe.entity.JourFerie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JourFerieRepository extends JpaRepository<JourFerie, UUID> {

    @Query("SELECT j FROM JourFerie j WHERE j.isFixe = false AND (YEAR(j.dateDebut) = ?1 OR YEAR(j.dateFin) = ?1)")
    List<JourFerie> findNonFixeByYear(int year);

    @Query("SELECT j FROM JourFerie j WHERE j.isFixe = true")
    List<JourFerie> findFixe();
}
