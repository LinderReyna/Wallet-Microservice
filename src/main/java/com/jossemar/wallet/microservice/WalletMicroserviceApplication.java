package com.jossemar.wallet.microservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;

@SpringBootApplication
@EnableEurekaClient
@EnableReactiveMongoAuditing
@EnableCaching
public class WalletMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(WalletMicroserviceApplication.class, args);
	}

}
