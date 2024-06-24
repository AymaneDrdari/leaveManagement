package net.atos.common.client;

import net.atos.common.dto.soldeConge.SoldeCongeDTO;
import net.atos.common.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "soldeconge-service")
public interface SoldeCongeServiceClient {

    @PostMapping("/api/soldeconges")
    ApiResponse<SoldeCongeDTO> addSoldeConge(@RequestBody SoldeCongeDTO soldeCongeDTO);

    @GetMapping("/api/soldeconges")
    ApiResponse<List<SoldeCongeDTO>> getAllSoldeConges();

    @GetMapping("/api/soldeconges/{code}")
    ApiResponse<SoldeCongeDTO> getSoldeCongeById(@PathVariable UUID code);

    @GetMapping("/api/soldeconges/findByNom")
    ApiResponse<UUID> findCodeSoldeCongeByNom(@RequestParam String nomSoldeConge);

    @GetMapping("/api/soldeconges/collaborateur/{collaborateurId}/year/{year}")
    ApiResponse<SoldeCongeDTO> getSoldeCongeByCollaborateurAndYear(@PathVariable UUID collaborateurId, @PathVariable int year);
}
