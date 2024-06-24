package net.atos.common.client;

import net.atos.common.dto.NiveauDTO;
import net.atos.common.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "niveau-service")
public interface NiveauServiceClient {

    @PostMapping("/api/niveaux")
    ApiResponse<NiveauDTO> addNiveau(@RequestBody NiveauDTO niveauDTO);

    @GetMapping("/api/niveaux")
    ApiResponse<List<NiveauDTO>> getAllNiveaux();

    @GetMapping("/api/niveaux/{code}")
    ApiResponse<NiveauDTO> getNiveauById(@PathVariable UUID code);

    @GetMapping("/api/niveaux/findByNom")
    ApiResponse<UUID> findCodeNiveauByNom(@RequestParam String nomNiveau);
}
