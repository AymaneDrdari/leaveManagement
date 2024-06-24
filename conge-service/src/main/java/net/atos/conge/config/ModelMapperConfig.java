package net.atos.conge.config;

import net.atos.conge.dto.CongeDTO;
import net.atos.conge.entity.Conge;
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

        modelMapper.typeMap(Conge.class, CongeDTO.class)
                .addMapping(src -> src.getCollaborateur().getEmail(), CongeDTO::setCollaborateurEmail);

        return modelMapper;
    }
}
