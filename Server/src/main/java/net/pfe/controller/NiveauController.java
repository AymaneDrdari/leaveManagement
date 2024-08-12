package net.pfe.controller;

import jakarta.validation.Valid;
import net.pfe.dto.NiveauDTO;
import net.pfe.exception.RessourceAlreadyExistsException;
import net.pfe.response.ApiResponse;
import net.pfe.service.interf.NiveauService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/niveaux")
@CrossOrigin(origins = "http://localhost:4200")
public class NiveauController {
    private final NiveauService niveauService;

    @Autowired
    public NiveauController(NiveauService niveauService) {
        this.niveauService = niveauService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<NiveauDTO>> addNiveau(@Valid @RequestBody NiveauDTO niveauDTO){
        try {
            NiveauDTO niveau = niveauService.addNiveau(niveauDTO);
            ApiResponse<NiveauDTO> response = ApiResponse.<NiveauDTO>builder()
                    .message(String.format("Niveau créée avec succès, code : %s", niveau.getCode()))
                    .code(HttpStatus.CREATED.value()).data(niveau)
                    .timestamp(LocalDateTime.now()).build();
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RessourceAlreadyExistsException e) {
            ApiResponse<NiveauDTO> response = ApiResponse.<NiveauDTO>builder()
                    .error(e.getMessage())
                    .code(HttpStatus.CONFLICT.value())
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<NiveauDTO>>> getAllNiveaux(){
        List<NiveauDTO> niveaux = niveauService.getAllNiveaux();
        ApiResponse<List<NiveauDTO>> response = ApiResponse.<List<NiveauDTO>>builder()
                .message("Liste de tous les niveaux récupérée avec succès")
                .code(HttpStatus.OK.value())
                .data(niveaux)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{code}")
    public ResponseEntity<ApiResponse<NiveauDTO>> getNiveauById(@PathVariable UUID code){
        Optional<NiveauDTO> niveauDTOOptional = niveauService.getNiveauById(code);

        if (niveauDTOOptional.isPresent()) {
            ApiResponse<NiveauDTO> response = ApiResponse.<NiveauDTO>builder()
                    .message("Niveau trouvé")
                    .code(HttpStatus.OK.value())
                    .data(niveauDTOOptional.get())
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<NiveauDTO> response = ApiResponse.<NiveauDTO>builder()
                    .message("Niveau non trouvé")
                    .code(HttpStatus.NOT_FOUND.value())
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PutMapping("/{code}")
    public ResponseEntity<ApiResponse<NiveauDTO>> updateNiveau(@PathVariable UUID code, @Valid @RequestBody NiveauDTO niveauDTO){
        try {
            NiveauDTO updatedNiveau = niveauService.updateNiveau(code, niveauDTO);
            ApiResponse<NiveauDTO> response = ApiResponse.<NiveauDTO>builder()
                    .message(String.format("Niveau modifié avec succès, code : %s", updatedNiveau.getCode()))
                    .code(HttpStatus.OK.value())
                    .data(updatedNiveau)
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<NiveauDTO> response = ApiResponse.<NiveauDTO>builder()
                    .error(e.getMessage())
                    .code(HttpStatus.BAD_REQUEST.value())
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<ApiResponse<Void>> deleteNiveau(@PathVariable UUID code) {
        try {
            niveauService.deleteNiveau(code);
            ApiResponse<Void> response = ApiResponse.<Void>builder()
                    .message(String.format("Niveau supprimé avec succès, code : %s", code))
                    .code(HttpStatus.NO_CONTENT.value())
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        } catch (Exception e) {
            ApiResponse<Void> response = ApiResponse.<Void>builder()
                    .error(e.getMessage())
                    .code(HttpStatus.BAD_REQUEST.value())
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

}
