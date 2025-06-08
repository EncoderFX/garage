package com.garage;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.fasterxml.jackson.core.type.TypeReference;
import com.garage.service.DataService;

@SpringBootApplication
@EnableScheduling
public class GarageApplication {

	public static void main(String[] args) {
		SpringApplication.run(GarageApplication.class, args);
	}

	@Bean
    CommandLineRunner runner(DataService service) {
        return args -> {
            InputStream inputStream = TypeReference.class.getResourceAsStream("/json/data.json");
            service.processDataObjects(new String(inputStream.readAllBytes(), StandardCharsets.UTF_8));
                
        };
    }
}
