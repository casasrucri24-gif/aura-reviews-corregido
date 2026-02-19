package com.aurareviews.proyecto;

import com.aurareviews.proyecto.model.Cliente;
import com.aurareviews.proyecto.model.Review;
import com.aurareviews.proyecto.repository.ClienteRepository;
import com.aurareviews.proyecto.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Carga datos de ejemplo al iniciar la aplicacion.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Override
    public void run(String... args) throws Exception {
        cargarDatosEjemplo();
        System.out.println("Datos de ejemplo cargados correctamente.");
    }

    private void cargarDatosEjemplo() {
        // Crear clientes
        Cliente c1 = new Cliente("Laura García", 28, "Femenino", false, null);
        Cliente c2 = new Cliente("Carlos Martínez", 35, "Masculino", true, "Intolerancia a la lactosa");
        Cliente c3 = new Cliente("Ana López", 22, "Femenino", false, null);
        Cliente c4 = new Cliente("Miguel Torres", 45, "Masculino", false, null);
        Cliente c5 = new Cliente("Elena Ruiz", 31, "Femenino", true, "Celiaquía - sin gluten");
        Cliente c6 = new Cliente("David Fernández", 19, "Masculino", false, null);
        Cliente c7 = new Cliente("Sofía Moreno", 52, "Femenino", true, "Alergia a frutos secos");
        Cliente c8 = new Cliente("Pablo Jiménez", 67, "Masculino", false, null);
        Cliente c9 = new Cliente("María Sánchez", 41, "Femenino", false, null);
        Cliente c10 = new Cliente("Javier Romero", 29, "Masculino", true, "Intolerancia a la fructosa");
        Cliente c11 = new Cliente("Carmen Díaz", 38, "Femenino", false, null);
        Cliente c12 = new Cliente("Alejandro Pérez", 24, "Masculino", false, null);
        Cliente c13 = new Cliente("Isabel Navarro", 55, "Femenino", true, "Alergia al marisco");
        Cliente c14 = new Cliente("Andrés Castillo", 33, "Otro", false, null);
        Cliente c15 = new Cliente("Lucía Ortega", 16, "Femenino", false, null);

        clienteRepository.save(c1);
        clienteRepository.save(c2);
        clienteRepository.save(c3);
        clienteRepository.save(c4);
        clienteRepository.save(c5);
        clienteRepository.save(c6);
        clienteRepository.save(c7);
        clienteRepository.save(c8);
        clienteRepository.save(c9);
        clienteRepository.save(c10);
        clienteRepository.save(c11);
        clienteRepository.save(c12);
        clienteRepository.save(c13);
        clienteRepository.save(c14);
        clienteRepository.save(c15);

        // Crear reviews
        reviewRepository.save(new Review("Excelente servicio, comida deliciosa.", 5, c1));
        reviewRepository.save(new Review("Buena experiencia, aunque algo de espera.", 4, c2));
        reviewRepository.save(new Review("Me encantó la variedad del menú.", 5, c3));
        reviewRepository.save(new Review("Ambiente agradable pero comida mejorable.", 3, c4));
        reviewRepository.save(new Review("Muy bien las opciones para celíacos.", 5, c5));
        reviewRepository.save(new Review("Primera vez, estuvo bien.", 3, c6));
        reviewRepository.save(new Review("Precios elevados para las raciones.", 2, c7));
        reviewRepository.save(new Review("Celebré mi cumpleaños, perfecto.", 5, c9));
        reviewRepository.save(new Review("La atención deja que desear.", 1, c10));
    }
}
