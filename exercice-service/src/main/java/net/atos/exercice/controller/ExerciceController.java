package net.atos.exercice.controller;


import net.atos.exercice.dto.ExerciceDTO;
import net.atos.exercice.response.ApiResponse;
import net.atos.exercice.service.interf.ExerciceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/exercices")
public class ExerciceController {

    private final ExerciceService exerciceService;

    @Autowired
    public ExerciceController(ExerciceService exerciceService) {
        this.exerciceService = exerciceService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ExerciceDTO>> calculateExercice(@RequestParam("annee") int annee) {
        try {
            ExerciceDTO exercice = exerciceService.calculerExerciceAnnuel(annee);
            ApiResponse<ExerciceDTO> response = ApiResponse.<ExerciceDTO>builder()
                    .message(String.format("Exercice pour l'année %d calculé avec succès.", annee))
                    .code(HttpStatus.OK.value())
                    .data(exercice)
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<ExerciceDTO> response = ApiResponse.<ExerciceDTO>builder()
                    .error(e.getMessage())
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
