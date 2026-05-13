package com.example.restapi;

import io.github.cdimascio.dotenv.Dotenv;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RestApiApplication {

    public static void main(String[] args) {
        loadDotEnvIfPresent();
        SpringApplication.run(RestApiApplication.class, args);
    }

    private static void loadDotEnvIfPresent() {
        Path dotenvPath = Path.of(".env");

        if (!Files.exists(dotenvPath)) {
            return;
        }

        Dotenv dotenv = Dotenv.configure()
                .directory(".")
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();

        setSpringPropertyFromDotEnvIfNoRealEnv(
                dotenv,
                "SPRING_DATASOURCE_URL",
                "spring.datasource.url"
        );

        setSpringPropertyFromDotEnvIfNoRealEnv(
                dotenv,
                "SPRING_DATASOURCE_USERNAME",
                "spring.datasource.username"
        );

        setSpringPropertyFromDotEnvIfNoRealEnv(
                dotenv,
                "SPRING_DATASOURCE_PASSWORD",
                "spring.datasource.password"
        );

        setSpringPropertyFromDotEnvIfNoRealEnv(
                dotenv,
                "SPRING_DATASOURCE_DRIVER_CLASS_NAME",
                "spring.datasource.driver-class-name"
        );

        setSpringPropertyFromDotEnvIfNoRealEnv(
                dotenv,
                "SPRING_JPA_DATABASE_PLATFORM",
                "spring.jpa.database-platform"
        );

        setSpringPropertyFromDotEnvIfNoRealEnv(
                dotenv,
                "SPRING_JPA_HIBERNATE_DDL_AUTO",
                "spring.jpa.hibernate.ddl-auto"
        );

        setSpringPropertyFromDotEnvIfNoRealEnv(
                dotenv,
                "SPRING_JPA_SHOW_SQL",
                "spring.jpa.show-sql"
        );
    }

    private static void setSpringPropertyFromDotEnvIfNoRealEnv(
            Dotenv dotenv,
            String envName,
            String springPropertyName
    ) {
        String realEnvironmentValue = System.getenv(envName);

        if (realEnvironmentValue != null && !realEnvironmentValue.isBlank()) {
            System.setProperty(springPropertyName, realEnvironmentValue);
            return;
        }

        String dotenvValue = dotenv.get(envName);

        if (dotenvValue != null && !dotenvValue.isBlank()) {
            System.setProperty(springPropertyName, dotenvValue);
        }
    }
}