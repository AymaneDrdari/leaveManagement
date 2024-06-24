package net.atos.equipe.controller;

import jakarta.validation.Valid;
import net.atos.common.dto.EquipeDTO;
import net.atos.common.response.ApiResponse;
import net.atos.equipe.exception.RessourceAlreadyExistsException;
import net.atos.equipe.exception.RessourceNotFoundException;
import net.atos.equipe.service.interf.EquipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/equipes")
public class EquipeController {
    private final EquipeService equipeService;

    @Autowired
    public EquipeController(EquipeService equipeService) {
        this.equipeService = equipeService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EquipeDTO>> addEquipe(@Valid @RequestBody EquipeDTO equipeDTO) {
        try {
            EquipeDTO equipe = equipeService.addEquipe(equipeDTO);
            ApiResponse<EquipeDTO> response = ApiResponse.<EquipeDTO>builder()
                    .message(String.format("Équipe créée avec succès, code : %s", equipe.getCode()))
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
        } catch (IllegalArgumentException e) {
            ApiResponse<EquipeDTO> response = ApiResponse.<EquipeDTO>builder()
                    .error(e.getMessage())
                    .code(HttpStatus.BAD_REQUEST.value())
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<EquipeDTO>>> getAllEquipes() {
        List<EquipeDTO> equipes = equipeService.getAllEquipes();
        ApiResponse<List<EquipeDTO>> response = ApiResponse.<List<EquipeDTO>>builder()
                .message("Liste de toutes les équipes récupérée avec succès")
                .code(HttpStatus.OK.value())
                .data(equipes)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{code}")
    public ResponseEntity<ApiResponse<EquipeDTO>> getEquipeById(@PathVariable UUID code) {
        try {
            EquipeDTO equipeDTO = equipeService.getEquipeById(code);
            ApiResponse<EquipeDTO> response = ApiResponse.<EquipeDTO>builder()
                    .message("Équipe trouvée")
                    .code(HttpStatus.OK.value())
                    .data(equipeDTO)
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
}
