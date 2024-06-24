package net.atos.common.client;

import net.atos.common.dto.collab.CollaborateurDTO;
import net.atos.common.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "collaborateur-service")
public interface CollaborateurServiceClient {

    @PostMapping("/api/collaborateurs")
    ApiResponse<CollaborateurDTO> createCollaborateur(@RequestBody CollaborateurDTO collaborateurDTO);

    @GetMapping("/api/collaborateurs/{id}")
    ApiResponse<CollaborateurDTO> findCollaborateurById(@PathVariable UUID id);

    @PutMapping("/api/collaborateurs")
    ApiResponse<CollaborateurDTO> updateCollaborateur(@RequestBody CollaborateurDTO collaborateurDTO);

    @DeleteMapping("/api/collaborateurs/{id}")
    ApiResponse<Void> deleteCollaborateur(@PathVariable UUID id);

    @GetMapping("/api/collaborateurs")
    ApiResponse<List<CollaborateurDTO>> findAllCollaborateurs();

    @GetMapping("/api/collaborateurs/email")
    ApiResponse<CollaborateurDTO> getCollaborateurByEmail(@RequestParam String email);

    @GetMapping("/api/collaborateurs/nom-prenom")
    ApiResponse<List<CollaborateurDTO>> getCollaborateursByNomAndPrenom(@RequestParam String nom, @RequestParam String prenom);

    @GetMapping("/api/collaborateurs/nom")
    ApiResponse<List<CollaborateurDTO>> getCollaborateursByNom(@RequestParam String nom);

    @GetMapping("/api/collaborateurs/prenom")
    ApiResponse<List<CollaborateurDTO>> getCollaborateursByPrenom(@RequestParam String prenom);
}
