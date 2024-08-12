package net.pfe.dto.soldeConge;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SoldeCongeDTORequest {
    private String collaborateurEmail;
    private int year;
}
