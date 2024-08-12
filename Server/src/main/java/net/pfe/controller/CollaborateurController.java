package net.pfe.controller;

import jakarta.validation.Valid;
import net.pfe.dto.collab.AddCollaborateurDTORequest;
import net.pfe.dto.collab.CollaborateurDTO;
import net.pfe.exception.RessourceNotFoundException;
import net.pfe.response.ApiResponse;
import net.pfe.service.interf.CollaborateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/collaborateurs")
@CrossOrigin(origins = "http://localhost:4200")
public class CollaborateurController {
    private final CollaborateurService collaborateurService;

    @Autowired
    public CollaborateurController(CollaborateurService collaborateurService) {
        this.collaborateurService = collaborateurService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CollaborateurDTO>> createCollaborateur(@Valid @RequestBody AddCollaborateurDTORequest request) {
        try {
            CollaborateurDTO collaborateur = collaborateurService.createCollaborateur(request);
            ApiResponse<CollaborateurDTO> response = ApiResponse.<CollaborateurDTO>builder()
                    .message(String.format("Collaborateur créé avec succès, id : %s", collaborateur.getId()))
                    .code(HttpStatus.CREATED.value())
                    .data(collaborateur)
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            ApiResponse<CollaborateurDTO> response = ApiResponse.<CollaborateurDTO>builder()
                    .error(e.getMessage())
                    .code(HttpStatus.BAD_REQUEST.value())
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CollaborateurDTO>> findCollaborateurById(@PathVariable UUID id) {
        CollaborateurDTO collaborateur = collaborateurService.findCollaborateurById(id);
        ApiResponse<CollaborateurDTO> response = ApiResponse.<CollaborateurDTO>builder()
                .message("Collaborateur trouvé")
                .code(HttpStatus.OK.value())
                .data(collaborateur)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<ApiResponse<CollaborateurDTO>> updateCollaborateur(@Valid @RequestBody CollaborateurDTO collaborateurDTO) {
        try {
            CollaborateurDTO updatedCollaborateur = collaborateurService.updateCollaborateur(collaborateurDTO);
            ApiResponse<CollaborateurDTO> response = ApiResponse.<CollaborateurDTO>builder()
                    .message("Collaborateur mis à jour avec succès")
                    .code(HttpStatus.OK.value())
                    .data(collaborateurDTO)
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.ok(response);
        } catch (RessourceNotFoundException e) {
            ApiResponse<CollaborateurDTO> response = ApiResponse.<CollaborateurDTO>builder()
                    .error(e.getMessage())
                    .code(HttpStatus.NOT_FOUND.value())
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCollaborateur(@PathVariable UUID id) {
        try {
            collaborateurService.deleteCollaborateur(id);
            ApiResponse<Void> response = ApiResponse.<Void>builder()
                    .message("Collaborateur supprimé")
                    .code(HttpStatus.NO_CONTENT.value())
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        } catch (RessourceNotFoundException e) {
            ApiResponse<Void> response = ApiResponse.<Void>builder()
                    .error(e.getMessage())
                    .code(HttpStatus.NOT_FOUND.value())
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/equipe")
    public ResponseEntity<ApiResponse<List<CollaborateurDTO>>> findCollaborateursByEquipe(@RequestParam String nomEquipe) {
        List<CollaborateurDTO> collaborateursDTO = collaborateurService.findCollaborateursByEquipe(nomEquipe);
        ApiResponse<List<CollaborateurDTO>> response = ApiResponse.<List<CollaborateurDTO>>builder()
                .message("Collaborateurs trouvés")
                .code(HttpStatus.OK.value())
                .data(collaborateursDTO)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/niveau")
    public ResponseEntity<ApiResponse<List<CollaborateurDTO>>> findCollaborateursByNiveau(@RequestParam String nomNiveau) {
        List<CollaborateurDTO> collaborateurDTOList = collaborateurService.findCollaborateursByNiveau(nomNiveau);
        ApiResponse<List<CollaborateurDTO>> response = ApiResponse.<List<CollaborateurDTO>>builder()
                .message("Collaborateurs trouvés")
                .code(HttpStatus.OK.value())
                .data(collaborateurDTOList)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CollaborateurDTO>>> findAllCollaborateurs() {
        List<CollaborateurDTO> collaborateurs = collaborateurService.findAllCollaborateurs();
        ApiResponse<List<CollaborateurDTO>> response = ApiResponse.<List<CollaborateurDTO>>builder()
                .message("Tous les collaborateurs trouvés")
                .code(HttpStatus.OK.value())
                .data(collaborateurs)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/autocomplete")
    public ResponseEntity<ApiResponse<Object>> autocomplete(@RequestParam(required = false) String search) {
        ApiResponse<Object> response = new ApiResponse<>();
        if (search == null || search.isEmpty()) {
            response.setError("Aucun critère de recherche n'a été précisé!");
            response.setCode(HttpStatus.BAD_REQUEST.value());
            response.setTimestamp(LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        List<CollaborateurDTO> collaborateurs = collaborateurService.findCollaborateursBySearch(search);
        if (collaborateurs.isEmpty()) {
            response.setMessage("Aucun collaborateur ne répond à votre critère de recherche");
            response.setCode(HttpStatus.NOT_FOUND.value());
            response.setTimestamp(LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        response.setMessage("Collaborateurs trouvés");
        response.setCode(HttpStatus.OK.value());
        response.setData(collaborateurs);
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/page")
    public ResponseEntity<ApiResponse<List<CollaborateurDTO>>> getCollaborateursPage(@RequestParam int page, @RequestParam int size) {
        List<CollaborateurDTO> collaborateurDTOS = collaborateurService.getCollaborateursPage(page, size);
        ApiResponse<List<CollaborateurDTO>> response = ApiResponse.<List<CollaborateurDTO>>builder()
                .message("Page de collaborateurs trouvée")
                .code(HttpStatus.OK.value())
                .data(collaborateurDTOS)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Integer>> countCollaborateursEnCongeParEquipeEtPeriode(
            @RequestParam String nomEquipe,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateStartCalenderie,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateEndCalenderie) {

        int count = collaborateurService.countCollaborateursEnCongeParEquipeEtPeriode(
                nomEquipe, dateStartCalenderie, dateEndCalenderie);

        ApiResponse<Integer> response = ApiResponse.<Integer>builder()
                .message("Nombre de collaborateurs en congé trouvés")
                .code(HttpStatus.OK.value())
                .data(count)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/en-conge")
    public ResponseEntity<ApiResponse<List<CollaborateurDTO>>> findCollaborateursEnCongeParEquipeAnnee(
            @RequestParam String nomEquipe) {

        List<CollaborateurDTO> collaborateurs = collaborateurService.findCollaborateursEnCongeParEquipeAnnee(nomEquipe);

        ApiResponse<List<CollaborateurDTO>> response = ApiResponse.<List<CollaborateurDTO>>builder()
                .message("Collaborateurs de l'équipe " + nomEquipe + " en congé trouvés")
                .code(HttpStatus.OK.value())
                .data(collaborateurs)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }
}
