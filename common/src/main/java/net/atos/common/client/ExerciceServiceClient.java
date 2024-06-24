package net.atos.common.client;

import net.atos.common.dto.exercice.ExerciceDTO;
import net.atos.common.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "exercice-service")
public interface ExerciceServiceClient {

    @GetMapping("/api/exercices/{annee}")
    ApiResponse<ExerciceDTO> getExerciceByYear(@PathVariable int annee);

    @GetMapping("/api/exercices")
    ApiResponse<List<ExerciceDTO>> getAllExercices();

    @GetMapping("/api/exercices/findByNom")
    ApiResponse<UUID> findCodeExerciceByNom(@RequestParam String nomExercice);
}
