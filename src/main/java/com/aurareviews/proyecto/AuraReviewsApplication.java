package com.aurareviews.proyecto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal que arranca la aplicación Spring Boot.
 * El escaneo de componentes parte desde este paquete (com.aurareviews.proyecto),
 * que es donde residen todos los controladores, servicios, repositorios y modelos.
 */
@SpringBootApplication
public class AuraReviewsApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuraReviewsApplication.class, args);
    }
}
