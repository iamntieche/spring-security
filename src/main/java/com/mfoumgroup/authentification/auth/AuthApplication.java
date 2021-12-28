package com.mfoumgroup.authentification.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class AuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthApplication.class, args);
	}

	//cette instanciation est impérative sini la fonction de hashage dans securityConfig  ne fonctionnera pas
	@Bean
	public BCryptPasswordEncoder getPE(){
		return new BCryptPasswordEncoder();
	}
}
