package net.atos.collaborateur;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@EnableEurekaClient
@EnableFeignClients
@ComponentScan(basePackages = {"net.atos.collaborateur", "net.atos.common"})
@EntityScan(basePackages = {"net.atos.collaborateur.entity", "net.atos.common.entity"})
@EnableJpaRepositories(basePackages = {"net.atos.collaborateur.repository", "net.atos.common.repository"})
@SpringBootApplication
public class CollaborateurServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CollaborateurServiceApplication.class, args);
    }
}
