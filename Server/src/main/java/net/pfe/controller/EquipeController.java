package net.pfe.controller;

import jakarta.validation.Valid;
import net.pfe.dto.EquipeDTO;
import net.pfe.exception.RessourceAlreadyExistsException;
import net.pfe.exception.RessourceNotFoundException;
import net.pfe.response.ApiResponse;
import net.pfe.service.interf.EquipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/equipes")
@CrossOrigin(origins = "http://localhost:4200")
public class EquipeController {
    private final EquipeService equipeService;

    @Autowired
    public EquipeController(EquipeService equipeService) {
        this.equipeService = equipeService;
    }
    // Mapping pour gérer les requêtes POST vers /equipes

    // Mapping pour gérer les requêtes POST vers /equipes
    @PostMapping
    public ResponseEntity<ApiResponse<EquipeDTO>> addEquipe(@Valid @RequestBody EquipeDTO equipeDTO) {
        try {
            EquipeDTO equipe = equipeService.addEquipe(equipeDTO);
            ApiResponse<EquipeDTO> response = ApiResponse.<EquipeDTO>builder()
                    .message(String.format("Equipe créée avec succès, code : %s", equipe.getCode()))
                    .code(HttpStatus.CREATED.value()).data(equipe)
                    .timestamp(LocalDateTime.now()).build();
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RessourceAlreadyExistsException e) {
            ApiResponse<EquipeDTO> response = ApiResponse.<EquipeDTO>builder()
                    .error(e.getMessage())
                    .code(HttpStatus.CONFLICT.value())
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<EquipeDTO>>> getAllEquipes() {
        List<EquipeDTO> equipes = equipeService.getAllEquipes();

        // Construire la réponse en fonction de la présence ou non d'équipes
        ApiResponse<List<EquipeDTO>> response = ApiResponse.<List<EquipeDTO>>builder()
                .message(equipes.isEmpty() ? "Aucune équipe n'a été trouvée" : "Liste de toutes les équipes récupérée avec succès")
                .code(HttpStatus.OK.value())
                .data(equipes)
                .timestamp(LocalDateTime.now())
                .build();

        // Retourner toujours un code 200 OK, même si la liste est vide
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{code}")
    public ResponseEntity<ApiResponse<EquipeDTO>> getEquipeById(@PathVariable UUID code) {
        Optional<EquipeDTO> equipeDTOOptional = equipeService.getEquipeById(code);

        if (equipeDTOOptional.isPresent()) {
            ApiResponse<EquipeDTO> response = ApiResponse.<EquipeDTO>builder()
                    .message("Équipe récupérée avec succès")
                    .data(equipeDTOOptional.get())
                    .build();
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<EquipeDTO> response = ApiResponse.<EquipeDTO>builder()
                    .message("Équipe non trouvée")
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    // Mapping pour gérer les requêtes PUT vers /equipes/{code}
    @PutMapping("/{code}")
    public ResponseEntity<ApiResponse<EquipeDTO>> updateEquipe(@PathVariable UUID code, @Valid @RequestBody EquipeDTO equipeDTO) {
        try {
            EquipeDTO updatedEquipe = equipeService.updateEquipe(code, equipeDTO);
            ApiResponse<EquipeDTO> response = ApiResponse.<EquipeDTO>builder()
                    .message("Équipe mise à jour avec succès")
                    .data(updatedEquipe)
                    .code(HttpStatus.OK.value())
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.ok(response);
        } catch (RessourceNotFoundException e) {
            ApiResponse<EquipeDTO> response = ApiResponse.<EquipeDTO>builder()
                    .error(e.getMessage())
                    .code(HttpStatus.NOT_FOUND.value())
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    // Endpoint pour supprimer une équipe
    @DeleteMapping("/{code}")
    public ResponseEntity<ApiResponse<Void>> deleteEquipe(@PathVariable UUID code) {
        try {
            equipeService.deleteEquipe(code);
            ApiResponse<Void> response = ApiResponse.<Void>builder()
                    .message("Équipe supprimée avec succès")
                    .code(HttpStatus.NO_CONTENT.value())
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.noContent().build();
        } catch (RessourceNotFoundException e) {
            ApiResponse<Void> response = ApiResponse.<Void>builder()
                    .error(e.getMessage())
                    .code(HttpStatus.NOT_FOUND.value())
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }


}