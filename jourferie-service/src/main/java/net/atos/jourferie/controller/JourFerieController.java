package net.atos.jourferie.controller;

import jakarta.validation.Valid;
import net.atos.common.dto.jourFerie.JourFerieDTO;
import net.atos.common.dto.jourFerie.JourFerieRequestDTO;
import net.atos.common.response.ApiResponse;
import net.atos.jourferie.exception.RessourceNotFoundException;
import net.atos.jourferie.service.interf.JourFerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/jours-feries")
public class JourFerieController {
    private final JourFerieService jourFerieService;

    @Autowired
    public JourFerieController(JourFerieService jourFerieService) {
        this.jourFerieService = jourFerieService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<JourFerieDTO>> creerJourFerieFixe(@Valid @RequestBody JourFerieRequestDTO jourFerieRequestDTO) {
        try {
            JourFerieDTO jourFerieDTO = jourFerieService.creerJourFerie(jourFerieRequestDTO);
            ApiResponse<JourFerieDTO> response = ApiResponse.<JourFerieDTO>builder()
                    .message("Jour férié créé avec succès")
                    .code(HttpStatus.CREATED.value())
                    .data(jourFerieDTO)
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            ApiResponse<JourFerieDTO> response = ApiResponse.<JourFerieDTO>builder()
                    .message(e.getMessage())
                    .code(HttpStatus.BAD_REQUEST.value())
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<JourFerieDTO>>> getJoursFeriesPourAnnee(@RequestParam(value = "annee") int annee) {
        List<JourFerieDTO> joursFeries = jourFerieService.getJoursFeriesForYear(annee);
        ApiResponse<List<JourFerieDTO>> response = ApiResponse.<List<JourFerieDTO>>builder()
                .message("Jours fériés récupérés avec succès pour l'année " + annee)
                .code(HttpStatus.OK.value())
                .data(joursFeries)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JourFerieDTO>> getJourFerieById(@PathVariable("id") UUID id) {
        JourFerieDTO jourFerieDTO = jourFerieService.getJourFerieById(id);
        ApiResponse<JourFerieDTO> response = ApiResponse.<JourFerieDTO>builder()
                .message("Jour férié récupéré avec succès")
                .code(HttpStatus.OK.value())
                .data(jourFerieDTO)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<JourFerieDTO>> updateJourFerie(
            @PathVariable UUID id, @Valid @RequestBody JourFerieRequestDTO jourFerieRequestDTO) {
        try {
            JourFerieDTO updatedJourFerie = jourFerieService.updateJourFerie(id, jourFerieRequestDTO);
            ApiResponse<JourFerieDTO> response = ApiResponse.<JourFerieDTO>builder()
                    .message("Jour férié mis à jour avec succès")
                    .code(HttpStatus.OK.value())
                    .data(updatedJourFerie)
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            ApiResponse<JourFerieDTO> response = ApiResponse.<JourFerieDTO>builder()
                    .message(e.getMessage())
                    .code(HttpStatus.BAD_REQUEST.value())
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteJourFerie(@PathVariable UUID id) {
        try {
            jourFerieService.deleteJourFerie(id);
            ApiResponse<Void> response = ApiResponse.<Void>builder()
                    .message("Jour férié supprimé avec succès")
                    .code(HttpStatus.NO_CONTENT.value())
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        } catch (RessourceNotFoundException e) {
            ApiResponse<Void> response = ApiResponse.<Void>builder()
                    .message(e.getMessage())
                    .code(HttpStatus.NOT_FOUND.value())
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}
