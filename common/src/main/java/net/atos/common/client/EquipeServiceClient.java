package net.atos.common.client;

import net.atos.common.dto.EquipeDTO;
import net.atos.common.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "equipe-service")
public interface EquipeServiceClient {

    @PostMapping("/api/equipes")
    ApiResponse<EquipeDTO> addEquipe(@RequestBody EquipeDTO equipeDTO);

    @GetMapping("/api/equipes")
    ApiResponse<List<EquipeDTO>> getAllEquipes();

    @GetMapping("/api/equipes/{code}")
    ApiResponse<EquipeDTO> getEquipeById(@PathVariable UUID code);

    @GetMapping("/api/equipes/findByNom")
    ApiResponse<UUID> findCodeEquipeByNom(@RequestParam String nomEquipe);
}
