package net.pfe.controller;

import jakarta.validation.Valid;
import net.pfe.dto.collab.CollaborateurDTO;
import net.pfe.dto.jourFerie.JourFerieDTO;
import net.pfe.dto.jourFerie.JourFerieRequestDTO;
import net.pfe.exception.RessourceNotFoundException;
import net.pfe.response.ApiResponse;
import net.pfe.service.interf.JourFerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/jours-feries")
@CrossOrigin(origins = "http://localhost:4200")
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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Ou renvoyez une réponse d'erreur appropriée
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<JourFerieDTO>>> getJoursFeriesPourAnnee(@RequestParam(value = "annee") int annee) {
        List<JourFerieDTO> joursFeries = jourFerieService.getJoursFeriesForYear(annee);
        joursFeries.forEach(holiday ->
                System.out.println("Holidays: " +holiday.getDateDebut() +
                        " - " + holiday.getDateFin()));
        ApiResponse<List<JourFerieDTO>> response = ApiResponse.<List<JourFerieDTO>>builder()
                .message("Jours fériés récupérés avec succès pour l'année " + annee)
                .code(HttpStatus.OK.value())
                .data(joursFeries)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<JourFerieDTO>>> getAllJoursFeries() {
        List<JourFerieDTO> joursFeries = jourFerieService.getAllJoursFeries();
        joursFeries.forEach(holiday ->
                System.out.println("Holidays: " +holiday.getDateDebut() +
                        " - " + holiday.getDateFin()));
        ApiResponse<List<JourFerieDTO>> response = ApiResponse.<List<JourFerieDTO>>builder()
                .message("Jours fériés récupérés avec succès")
                .code(HttpStatus.OK.value())
                .data(joursFeries)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @GetMapping("/page")
    public ResponseEntity<ApiResponse<List<JourFerieDTO>>> getJoursFeriesPage(@RequestParam int page, @RequestParam int size) {
        List<JourFerieDTO> jourFerieDTOS = jourFerieService.getJoursFeriesPage(page, size);
        ApiResponse<List<JourFerieDTO>> response = ApiResponse.<List<JourFerieDTO>>builder()
                .message("Page de jours fériés trouvée")
                .code(HttpStatus.OK.value())
                .data(jourFerieDTOS)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
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

    @PutMapping
    public ResponseEntity<ApiResponse<JourFerieDTO>> updateJourFerie(
            @Valid @RequestBody JourFerieRequestDTO jourFerieRequestDTO) {
        try {
            JourFerieDTO updatedJourFerie = jourFerieService.updateJourFerie(jourFerieRequestDTO.getId(), jourFerieRequestDTO);
            ApiResponse<JourFerieDTO> response = ApiResponse.<JourFerieDTO>builder()
                    .message("Jour férié mis à jour avec succès")
                    .code(HttpStatus.OK.value())
                    .data(updatedJourFerie)
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            ApiResponse<JourFerieDTO> response = ApiResponse.<JourFerieDTO>builder()
                    .message(e.getMessage()) // Inclure le message d'erreur ici
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