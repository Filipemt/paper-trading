package com.filipecode.papertrading;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class PaperTradingApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaperTradingApplication.class, args);
	}

}
