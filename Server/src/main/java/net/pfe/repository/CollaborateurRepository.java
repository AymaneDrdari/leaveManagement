package net.pfe.repository;

import net.pfe.entity.Collaborateur;
import net.pfe.entity.enums.RoleCollaborateur;
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


   // pour email:
  //  List<Collaborateur> findByEquipe_NomAndRole(String equipeNom, RoleCollaborateur role);


}