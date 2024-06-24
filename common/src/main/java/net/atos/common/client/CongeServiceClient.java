package net.atos.common.client;

import net.atos.common.dto.conge.CongeDTO;
import net.atos.common.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "conge-service")
public interface CongeServiceClient {

    @PostMapping("/api/conges")
    ApiResponse<CongeDTO> createConge(@RequestBody CongeDTO congeDTO);

    @GetMapping("/api/conges/{id}")
    ApiResponse<CongeDTO> findCongeById(@PathVariable UUID id);

    @PutMapping("/api/conges")
    ApiResponse<CongeDTO> updateConge(@RequestBody CongeDTO congeDTO);

    @DeleteMapping("/api/conges/{id}")
    ApiResponse<Void> deleteConge(@PathVariable UUID id);

    @GetMapping("/api/conges")
    ApiResponse<List<CongeDTO>> findAllConges();
}
