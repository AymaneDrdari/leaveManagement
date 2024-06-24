package net.atos.niveau.config;

import net.atos.niveau.dto.NiveauDTO;
import net.atos.niveau.entity.Niveau;
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

        modelMapper.typeMap(Niveau.class, NiveauDTO.class);

        return modelMapper;
    }
}
