package net.atos.collaborateur.config;

import net.atos.collaborateur.entity.Collaborateur;
import net.atos.common.dto.collab.CollaborateurDTO;
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

        modelMapper.typeMap(Collaborateur.class, CollaborateurDTO.class)
                .addMapping(Collaborateur::getEquipeNom, CollaborateurDTO::setEquipeNom)
                .addMapping(Collaborateur::getNiveauNom, CollaborateurDTO::setNiveauNom);

        return modelMapper;
    }
}
