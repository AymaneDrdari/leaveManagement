package net.pfe.service.interf;

import net.pfe.dto.jourFerie.JourFerieDTO;
import net.pfe.dto.jourFerie.JourFerieRequestDTO;

import java.util.List;
import java.util.UUID;

public interface JourFerieService {
    JourFerieDTO creerJourFerie(JourFerieRequestDTO jourFerieRequestDTO);

    List<JourFerieDTO> getJoursFeriesForYear(int annee);

    JourFerieDTO getJourFerieById(UUID id);

    JourFerieDTO updateJourFerie(UUID id, JourFerieRequestDTO jourFerieRequestDTO);

    void deleteJourFerie(UUID id);

    List<JourFerieDTO> getAllJoursFeries();

    List<JourFerieDTO> getJoursFeriesPage(int page, int size);
}
