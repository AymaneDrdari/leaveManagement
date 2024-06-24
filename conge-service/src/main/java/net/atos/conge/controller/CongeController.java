package net.atos.conge.controller;

import jakarta.validation.Valid;
import net.atos.conge.dto.CongeDTO;
import net.atos.conge.dto.CongeDTORequest;
import net.atos.conge.exception.RessourceNotFoundException;
import net.atos.conge.response.ApiResponse;

import net.atos.conge.service.interf.CongeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/conges")
public class CongeController {

    private final CongeService congeService;

    public CongeController(CongeService congeService) {
        this.congeService = congeService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CongeDTO>> createConge(@Valid @RequestBody CongeDTORequest congeDTORequest) {
        CongeDTO createdConge = congeService.createConge(congeDTORequest);
        ApiResponse<CongeDTO> response = ApiResponse.<CongeDTO>builder()
                .message(String.format("Votre congé a été déclaré avec succès pour %.1f jour.", createdConge.getNombreJoursPris()))
                .code(HttpStatus.CREATED.value())
                .data(createdConge)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CongeDTO>>> getAllConges() {
        List<CongeDTO> conges = congeService.getAllConges();
        ApiResponse<List<CongeDTO>> response = ApiResponse.<List<CongeDTO>>builder()
                .message("Tous les congés trouvés")
                .code(HttpStatus.OK.value())
                .data(conges)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CongeDTO>> getCongeById(@PathVariable UUID id) {
        try {
            CongeDTO congeDTO = congeService.getCongeById(id);
            ApiResponse<CongeDTO> response = ApiResponse.<CongeDTO>builder()
                    .message("Congé trouvé")
                    .code(HttpStatus.OK.value())
                    .data(congeDTO)
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.ok(response);
        } catch (RessourceNotFoundException e) {
            ApiResponse<CongeDTO> response = ApiResponse.<CongeDTO>builder()
                    .error(e.getMessage())
                    .code(HttpStatus.NOT_FOUND.value())
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PutMapping
    public ResponseEntity<ApiResponse<CongeDTO>> updateConge(@RequestBody CongeDTORequest congeDTORequest) {
        CongeDTO updatedConge = congeService.updateConge(congeDTORequest);
        ApiResponse<CongeDTO> response = ApiResponse.<CongeDTO>builder()
                .message("Congé mis à jour avec succès")
                .code(HttpStatus.OK.value())
                .data(updatedConge)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

//    @DeleteMapping("/{id}")
//    public ResponseEntity<ApiResponse<Void>> deleteConge(@PathVariable UUID id) {
//
//            ApiResponse<Void> response = ApiResponse.<Void>builder()
//                    .message("Congé supprimé")
//                    .code(HttpStatus.NO_CONTENT.value())
//                    .timestamp(LocalDateTime.now())
//                    .build();
//            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteConge(@PathVariable UUID id) {
        try {
            congeService.deleteConge(id);

            ApiResponse<Void> response = ApiResponse.<Void>builder()
                    .message("Congé supprimé")
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
    @GetMapping("/collaborateurs")
    public ResponseEntity<ApiResponse<List<CongeDTO>>> getCongesByCollaborateur(@RequestParam(required = false) String nom, @RequestParam(required = false) String prenom) {
        List<CongeDTO> congesDTO = congeService.getCongesByCollaborateur(nom, prenom);
        String message;
        if (nom != null && prenom != null) {
            message = "Congés trouvés pour le collaborateur " + nom +" "+  prenom ;
        } else if (nom != null) {
            message = "Congés trouvés pour le collaborateur " + nom ;
        } else if (prenom != null) {
            message = "Congés trouvés pour le collaborateur '" + prenom + "'";
        } else {
            message = "Congés trouvés pour le collaborateur";
        }
        ApiResponse<List<CongeDTO>> response = ApiResponse.<List<CongeDTO>>builder()
                .message(message)
                .code(HttpStatus.OK.value())
                .data(congesDTO)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }
}
