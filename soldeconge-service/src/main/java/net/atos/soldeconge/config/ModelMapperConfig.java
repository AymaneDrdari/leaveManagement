package net.atos.soldeconge.config;

import net.atos.soldeconge.dto.SoldeCongeDTO;
import net.atos.soldeconge.entity.SoldeConge;
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
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setSkipNullEnabled(true);

        modelMapper.typeMap(SoldeConge.class, SoldeCongeDTO.class)
                .addMapping(src -> src.getCollaborateur().getEmail(), SoldeCongeDTO::setCollaborateurEmail)
                .addMapping(src -> src.getExercice().getAnnee(), SoldeCongeDTO::setAnnee);

        return modelMapper;
    }
}
