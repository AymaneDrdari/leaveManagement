package net.pfe.controller;

import jakarta.validation.Valid;
import net.pfe.dto.conge.CongeDTO;
import net.pfe.dto.conge.CongeDTORequest;
import net.pfe.dto.conge.CongeDetailDTO;
import net.pfe.dto.jourFerie.JourFerieDTO;
import net.pfe.exception.RessourceNotFoundException;
import net.pfe.response.ApiResponse;
import net.pfe.service.interf.CongeService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/conges")
@CrossOrigin(origins = "http://localhost:4200")
public class CongeController {

    private final CongeService congeService;

    public CongeController(CongeService congeService) {
        this.congeService = congeService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CongeDTO>> createConge(@Valid @RequestBody CongeDTORequest congeDTORequest) {
        CongeDTO createdConge = congeService.createConge(congeDTORequest);
        ApiResponse<CongeDTO> response = ApiResponse.<CongeDTO>builder()
                .message(String.format("Votre congé a été déclaré avec succès pour %.1f jour.", createdConge.getNombreJoursPris()))
                .code(HttpStatus.CREATED.value())
                .data(createdConge)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CongeDTO>>> getAllConges() {
        List<CongeDTO> conges = congeService.getAllConges();
        ApiResponse<List<CongeDTO>> response = ApiResponse.<List<CongeDTO>>builder()
                .message("Tous les congés trouvés")
                .code(HttpStatus.OK.value())
                .data(conges)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/page")
    public ResponseEntity<ApiResponse<List<CongeDTO>>> getCongésPage(@RequestParam int page, @RequestParam int size) {
        List<CongeDTO> congeDTOList = congeService.getJCongéPage(page, size);
        ApiResponse<List<CongeDTO>> response = ApiResponse.<List<CongeDTO>>builder()
                .message("Page de congé trouvée")
                .code(HttpStatus.OK.value())
                .data(congeDTOList)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CongeDTO>> getCongeById(@PathVariable UUID id) {
        try {
            CongeDTO congeDTO = congeService.getCongeById(id);
            ApiResponse<CongeDTO> response = ApiResponse.<CongeDTO>builder()
                    .message("Congé trouvé")
                    .code(HttpStatus.OK.value())
                    .data(congeDTO)
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.ok(response);
        } catch (RessourceNotFoundException e) {
            ApiResponse<CongeDTO> response = ApiResponse.<CongeDTO>builder()
                    .error(e.getMessage())
                    .code(HttpStatus.NOT_FOUND.value())
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PutMapping
    public ResponseEntity<ApiResponse<CongeDTO>> updateConge(
            @Valid @RequestBody CongeDTORequest congeDTORequest) {
        try {
            CongeDTO updatedConge = congeService.updateConge(congeDTORequest.getIdConge(),congeDTORequest);
            ApiResponse<CongeDTO> response = ApiResponse.<CongeDTO>builder()
                    .message("Congé mis à jour avec succès")
                    .code(HttpStatus.OK.value())
                    .data(updatedConge)
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            ApiResponse<CongeDTO> response = ApiResponse.<CongeDTO>builder()
                    .message(e.getMessage()) // Inclure le message d'erreur ici
                    .code(HttpStatus.BAD_REQUEST.value())
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteConge(@PathVariable UUID id) {
        try {
            congeService.deleteConge(id);

            ApiResponse<Void> response = ApiResponse.<Void>builder()
                    .message("Congé supprimé")
                    .code(HttpStatus.NO_CONTENT.value())
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        } catch (RessourceNotFoundException e) {
            ApiResponse<Void> response = ApiResponse.<Void>builder()
                    .error(e.getMessage())
                    .code(HttpStatus.NOT_FOUND.value())
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<CongeDTO>>> getCongesByCollaborateur(@RequestParam(required = false) String nom, @RequestParam(required = false) String prenom) {
        List<CongeDTO> congesDTO = congeService.getCongesByCollaborateur(nom, prenom);
        String message;
        if (nom != null && prenom != null) {
            message = "Congés trouvés pour le collaborateur " + nom +" "+  prenom ;
        } else if (nom != null) {
            message = "Congés trouvés pour le collaborateur " + nom ;
        } else if (prenom != null) {
            message = "Congés trouvés pour le collaborateur '" + prenom + "'";
        } else {
            message = "Congés trouvés pour le collaborateur";
        }
        ApiResponse<List<CongeDTO>> response = ApiResponse.<List<CongeDTO>>builder()
                .message(message)
                .code(HttpStatus.OK.value())
                .data(congesDTO)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    //    @GetMapping("/count")
//    public ResponseEntity<ApiResponse<Integer>> countCollaborateursEnCongeParEquipeAnnee(
//            @RequestParam String nomEquipe) {
//
//        int count = congeService.countCollaborateursEnCongeParEquipeAnnee(nomEquipe);
//
//        ApiResponse<Integer> response = ApiResponse.<Integer>builder()
//                .message("Nombre de collaborateurs en congé trouvés")
//                .code(HttpStatus.OK.value())
//                .data(count)
//                .timestamp(LocalDateTime.now())
//                .build();
//        return ResponseEntity.ok(response);
//    }
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Integer>> countCollaborateursEnCongeParEquipeMois(
            @RequestParam String nomEquipe,
            @RequestParam int mois,
            @RequestParam int annee) {

        int count = congeService.countCollaborateursEnCongeParEquipeMois(nomEquipe, mois, annee);

        ApiResponse<Integer> response = ApiResponse.<Integer>builder()
                .message("Nombre de collaborateurs en congé trouvés")
                .code(HttpStatus.OK.value())
                .data(count)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }


    @GetMapping("/equipe")
    public ResponseEntity<ApiResponse<List<CongeDetailDTO>>> getCongesByEquipe(
            @RequestParam String nomEquipe,
            @RequestParam int annee) {
        List<CongeDetailDTO> conges = congeService.findCongesByEquipe(nomEquipe, annee);
        ApiResponse<List<CongeDetailDTO>> response = ApiResponse.<List<CongeDetailDTO>>builder()
                .message("Congés trouvés pour l'équipe " + nomEquipe + " pour l'année " + annee)
                .code(HttpStatus.OK.value())
                .data(conges)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/count-by-period")
    public ResponseEntity<ApiResponse<Long>> countCongesByEquipeAndPeriod(
            @RequestParam String nomEquipe,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        try {
            // Appeler le service pour compter les congés
            long count = congeService.countCollaborateursEnCongeParEquipeParPeriode(nomEquipe, startDate, endDate);

            // Créer la réponse API
            ApiResponse<Long> response = ApiResponse.<Long>builder()
                    .message("Nombre de congés trouvés pour l'équipe " + nomEquipe + " entre " + startDate + " et " + endDate)
                    .code(HttpStatus.OK.value())
                    .data(count)
                    .timestamp(LocalDateTime.now())
                    .build();

            // Retourner la réponse
            return ResponseEntity.ok(response);
        } catch (RessourceNotFoundException e) {
            // Gérer le cas où l'équipe ou les collaborateurs ne sont pas trouvés
            ApiResponse<Long> response = ApiResponse.<Long>builder()
                    .message(e.getMessage())
                    .code(HttpStatus.NOT_FOUND.value())
                    .data(0L)
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            // Gérer toute autre exception
            ApiResponse<Long> response = ApiResponse.<Long>builder()
                    .message("Erreur lors du traitement de la demande")
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .data(0L)
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


//    @GetMapping("/by-period")
//    public ResponseEntity<ApiResponse<List<CongeDTO>>> getCongesByEquipeAndPeriod(
//            @RequestParam String nomEquipe,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
//
//        try {
//            // Appeler le service pour récupérer les congés
//            List<CongeDTO> conges = congeService.findCongesByEquipeAndPeriod(nomEquipe, startDate, endDate);
//
//            // Créer la réponse API
//            ApiResponse<List<CongeDTO>> response = ApiResponse.<List<CongeDTO>>builder()
//                    .message("Congés trouvés pour l'équipe " + nomEquipe + " entre " + startDate + " et " + endDate)
//                    .code(HttpStatus.OK.value())
//                    .data(conges)
//                    .timestamp(LocalDateTime.now())
//                    .build();
//
//            // Retourner la réponse
//            return ResponseEntity.ok(response);
//        } catch (RessourceNotFoundException e) {
//            // Gérer le cas où l'équipe ou les congés ne sont pas trouvés
//            ApiResponse<List<CongeDTO>> response = ApiResponse.<List<CongeDTO>>builder()
//                    .message(e.getMessage())
//                    .code(HttpStatus.NOT_FOUND.value())
//                    .data(Collections.emptyList())
//                    .timestamp(LocalDateTime.now())
//                    .build();
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//        } catch (Exception e) {
//            // Gérer toute autre exception
//            ApiResponse<List<CongeDTO>> response = ApiResponse.<List<CongeDTO>>builder()
//                    .message("Erreur lors du traitement de la demande")
//                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
//                    .data(Collections.emptyList())
//                    .timestamp(LocalDateTime.now())
//                    .build();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }




}