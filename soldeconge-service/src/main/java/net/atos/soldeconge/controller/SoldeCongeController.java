package net.atos.soldeconge.controller;

import net.atos.common.dto.soldeConge.SoldeCongeDTO;
import net.atos.common.dto.soldeConge.SoldeCongeDTORequest;
import net.atos.common.response.ApiResponse;
import net.atos.soldeconge.service.interf.SoldeCongeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/solde-conge")
public class SoldeCongeController {

    private final SoldeCongeService soldeCongeService;

    public SoldeCongeController(SoldeCongeService soldeCongeService) {
        this.soldeCongeService = soldeCongeService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SoldeCongeDTO>> createOrUpdateSoldeConge(@RequestBody SoldeCongeDTORequest soldeCongeDTORequest) {
        SoldeCongeDTO soldeCongeDTO = soldeCongeService.createOrUpdateSoldeConge(soldeCongeDTORequest);
        ApiResponse<SoldeCongeDTO> response = ApiResponse.<SoldeCongeDTO>builder()
                .message("Solde de congé créé/mis à jour avec succès")
                .code(HttpStatus.CREATED.value())
                .data(soldeCongeDTO)
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<SoldeCongeDTO>> getSoldeConge(@RequestParam String email, @RequestParam int annee) {
        SoldeCongeDTO soldeCongeDTO = soldeCongeService.getSoldeConge(email, annee);
        ApiResponse<SoldeCongeDTO> response = ApiResponse.<SoldeCongeDTO>builder()
                .message("Solde de congé calculé avec succès")
                .code(HttpStatus.OK.value())
                .data(soldeCongeDTO)
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
