package net.atos.jourferie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@EnableEurekaClient
public class JourferieServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(JourferieServiceApplication.class, args);
	}

}
