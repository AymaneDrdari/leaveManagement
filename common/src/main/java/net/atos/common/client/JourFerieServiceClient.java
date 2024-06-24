package net.atos.common.client;

import net.atos.common.dto.jourFerie.JourFerieDTO;
import net.atos.common.dto.jourFerie.JourFerieRequestDTO;
import net.atos.common.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "jourferie-service")
public interface JourFerieServiceClient {

    @PostMapping("/api/joursferies")
    ApiResponse<JourFerieDTO> creerJourFerie(@RequestBody JourFerieRequestDTO jourFerieRequestDTO);

    @GetMapping("/api/joursferies/{annee}")
    ApiResponse<List<JourFerieDTO>> getJoursFeriesForYear(@PathVariable int annee);

    @GetMapping("/api/joursferies/{id}")
    ApiResponse<JourFerieDTO> getJourFerieById(@PathVariable UUID id);

    @PutMapping("/api/joursferies/{id}")
    ApiResponse<JourFerieDTO> updateJourFerie(@PathVariable UUID id, @RequestBody JourFerieRequestDTO jourFerieRequestDTO);

    @DeleteMapping("/api/joursferies/{id}")
    ApiResponse<Void> deleteJourFerie(@PathVariable UUID id);
}
