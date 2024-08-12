package net.pfe.dto.collab;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollaborateursEnCongeRequestDTO {
    private String nomEquipe;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateStartCalenderie;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateEndCalenderie;
}
