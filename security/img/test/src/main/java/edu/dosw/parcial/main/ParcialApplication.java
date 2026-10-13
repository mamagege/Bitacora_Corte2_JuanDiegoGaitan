package edu.dosw.parcial.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication(scanBasePackages = "edu.dosw.parcial")
@EnableMongoRepositories(basePackages = "edu.dosw.parcial.persistence.repositories")
public class ParcialApplication {
    public static void main(String[] args) {
        SpringApplication.run(ParcialApplication.class, args);
    }
}
