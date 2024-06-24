package net.atos.common.dto.soldeConge;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SoldeCongeDTORequest {
    private String collaborateurEmail;
    private int year;
}
