package net.pfe.config;

import net.pfe.dto.collab.CollaborateurDTO;
import net.pfe.dto.soldeConge.SoldeCongeDTO;
import net.pfe.entity.Collaborateur;
import net.pfe.entity.SoldeConge;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)  //STRICT , LOOSE, STANDARD pour détecter les erreurs de mappage
                .setSkipNullEnabled(true);

        // Le mappage entre Collaborateur et CollaborateurDTO se fait automatiquement
        // puisque les champs correspondent directement (Equipe et Niveau sont maintenant des objets complets)

        // Règles de mapping pour SoldeConge -> SoldeCongeDTO
        modelMapper.typeMap(SoldeConge.class, SoldeCongeDTO.class)
                .addMapping(src -> src.getCollaborateur().getEmail(), SoldeCongeDTO::setCollaborateurEmail)
                .addMapping(src -> src.getExercice().getAnnee(), SoldeCongeDTO::setAnnee);

        return modelMapper;
    }
}
