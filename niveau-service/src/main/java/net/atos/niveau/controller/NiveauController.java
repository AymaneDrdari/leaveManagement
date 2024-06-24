package net.atos.niveau.controller;

import jakarta.validation.Valid;
import net.atos.common.dto.NiveauDTO;
import net.atos.common.response.ApiResponse;
import net.atos.niveau.exception.RessourceAlreadyExistsException;
import net.atos.niveau.exception.RessourceNotFoundException;
import net.atos.niveau.service.interf.NiveauService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/niveaux")
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
                    .message(String.format("Niveau créé avec succès, code : %s", niveau.getCode()))
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
        } catch (IllegalArgumentException e) {
            ApiResponse<NiveauDTO> response = ApiResponse.<NiveauDTO>builder()
                    .error(e.getMessage())
                    .code(HttpStatus.BAD_REQUEST.value())
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
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
        try {
            NiveauDTO niveauDTO = niveauService.getNiveauById(code);
            ApiResponse<NiveauDTO> response = ApiResponse.<NiveauDTO>builder()
                    .message("Niveau trouvé")
                    .code(HttpStatus.OK.value())
                    .data(niveauDTO)
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.ok(response);
        } catch (RessourceNotFoundException e) {
            ApiResponse<NiveauDTO> response = ApiResponse.<NiveauDTO>builder()
                    .error(e.getMessage())
                    .code(HttpStatus.NOT_FOUND.value())
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}
