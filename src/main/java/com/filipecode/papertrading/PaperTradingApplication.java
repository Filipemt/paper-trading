package com.filipecode.papertrading;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EnableFeignClients
public class PaperTradingApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaperTradingApplication.class, args);
	}

}
