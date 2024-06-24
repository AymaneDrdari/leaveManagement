package net.atos.jourferie.service.interf;

import net.atos.common.dto.jourFerie.JourFerieDTO;
import net.atos.common.dto.jourFerie.JourFerieRequestDTO;

import java.util.List;
import java.util.UUID;

public interface JourFerieService {
    JourFerieDTO creerJourFerie(JourFerieRequestDTO jourFerieRequestDTO);

    List<JourFerieDTO> getJoursFeriesForYear(int annee);

    JourFerieDTO getJourFerieById(UUID id);

    JourFerieDTO updateJourFerie(UUID id, JourFerieRequestDTO jourFerieRequestDTO);

    void deleteJourFerie(UUID id);
}
