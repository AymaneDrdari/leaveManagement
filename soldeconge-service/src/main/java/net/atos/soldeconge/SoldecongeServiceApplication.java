package net.atos.soldeconge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
@EntityScan(basePackages = {"net.atos.soldeconge.entity", "net.atos.common.entity"})
public class SoldecongeServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SoldecongeServiceApplication.class, args);
	}

}
