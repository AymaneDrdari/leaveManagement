package net.pfe.controller;

import net.pfe.dto.exercice.ExerciceDTORequest;
import net.pfe.response.ApiResponse;
import net.pfe.service.interf.ExerciceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/exercices")
@CrossOrigin(origins = "http://localhost:4200")
public class ExerciceController {

    private final ExerciceService exerciceService;

    @Autowired
    public ExerciceController(ExerciceService exerciceService) {
        this.exerciceService = exerciceService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ExerciceDTORequest>> calculateExercice(@RequestParam("annee") int annee) {
        try {
            ExerciceDTORequest exercice = exerciceService.calculerExerciceAnnuel(annee);
            ApiResponse<ExerciceDTORequest> response = ApiResponse.<ExerciceDTORequest>builder()
                    .message(String.format("Exercice pour l'année %d calculé avec succès.", annee))
                    .code(HttpStatus.OK.value())
                    .data(exercice)
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<ExerciceDTORequest> response = ApiResponse.<ExerciceDTORequest>builder()
                    .error(e.getMessage())
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}