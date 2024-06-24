package net.atos.jourferie.config;

import net.atos.jourferie.dto.JourFerieDTO;
import net.atos.jourferie.entity.JourFerie;
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

        modelMapper.typeMap(JourFerie.class, JourFerieDTO.class);

        return modelMapper;
    }
}
