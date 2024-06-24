package net.atos.collaborateur.repository;

import net.atos.collaborateur.entity.Collaborateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CollaborateurRepository extends JpaRepository<Collaborateur, UUID> {

    List<Collaborateur> findAll();
    List<Collaborateur> findByEquipeCode(UUID equipeCode);
    List<Collaborateur> findByNiveauCode(UUID niveauCode);
    List<Collaborateur> findByNom(String nom);
    List<Collaborateur> findByPrenom(String prenom);

    List<Collaborateur> findByNomAndPrenom(String nom, String prenom);
    boolean existsByEmail(String email);
    //utiliser dans la declaration d'un congé
    Collaborateur findByEmail(String email);

    List<Collaborateur> findByNomIgnoreCaseOrPrenomIgnoreCase(String nom, String prenom);
    List<Collaborateur> findByNomStartingWithIgnoreCaseOrPrenomStartingWithIgnoreCase(String nom, String prenom);


}