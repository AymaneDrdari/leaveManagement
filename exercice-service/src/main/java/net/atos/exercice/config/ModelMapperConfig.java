package net.atos.exercice.config;

import net.atos.exercice.dto.ExerciceDTO;
import net.atos.exercice.entity.Exercice;
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

        modelMapper.typeMap(Exercice.class, ExerciceDTO.class);

        return modelMapper;
    }
}
