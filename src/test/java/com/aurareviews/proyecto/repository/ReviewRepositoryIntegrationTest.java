package com.aurareviews.proyecto.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import com.aurareviews.proyecto.model.Cliente;
import com.aurareviews.proyecto.model.Review;

/**
 * Test de integración para ReviewRepository.
 * Verifica que la capa de datos se comunique correctamente con la base de datos.
 * Usa H2 en memoria para los tests.
 */
@DataJpaTest
@ActiveProfiles("test")
class ReviewRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ReviewRepository reviewRepository;

    private Cliente cliente1;
    private Cliente cliente2;
    private Cliente cliente3;
    private Review review1;
    private Review review2;

    @BeforeEach
    void setUp() {
        // Limpiar la base de datos
        reviewRepository.deleteAll();
        
        // Crear clientes
        cliente1 = new Cliente("Juan Perez", 30, "Masculino", false, null);
        cliente2 = new Cliente("Maria Lopez", 25, "Femenino", true, "Lactosa");
        cliente3 = new Cliente("Pedro Garcia", 45, "Masculino", true, "Gluten");
        
        entityManager.persist(cliente1);
        entityManager.persist(cliente2);
        entityManager.persist(cliente3);
        
        // Crear reviews
        review1 = new Review("Excelente servicio, muy recomendable", 5, cliente1);
        review2 = new Review("Buena experiencia en general", 4, cliente2);
        
        entityManager.persist(review1);
        entityManager.persist(review2);
        entityManager.flush();
    }

    @Test
    void testFindAll() {
        // Act
        List<Review> reviews = reviewRepository.findAll();

        // Assert
        assertNotNull(reviews);
        assertEquals(2, reviews.size());
    }

    @Test
    void testFindById_Existe() {
        // Act
        Optional<Review> resultado = reviewRepository.findById(review1.getId());

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("Excelente servicio, muy recomendable", resultado.get().getDescripcion());
        assertEquals(5, resultado.get().getValoracion());
    }

    @Test
    void testFindById_NoExiste() {
        // Act
        Optional<Review> resultado = reviewRepository.findById(999L);

        // Assert
        assertFalse(resultado.isPresent());
    }

    @Test
    void testSave_NuevaReview() {
        // Arrange
        Review nuevaReview = new Review("Servicio correcto", 3, cliente3);

        // Act
        Review guardada = reviewRepository.save(nuevaReview);

        // Assert
        assertNotNull(guardada.getId());
        assertEquals("Servicio correcto", guardada.getDescripcion());
        assertEquals(3, guardada.getValoracion());
        
        // Verificar en la base de datos
        Review encontrada = entityManager.find(Review.class, guardada.getId());
        assertNotNull(encontrada);
        assertEquals("Servicio correcto", encontrada.getDescripcion());
    }

    @Test
    void testSave_ActualizarReview() {
        // Arrange
        review1.setDescripcion("Servicio actualizado");
        review1.setValoracion(4);

        // Act
        Review actualizada = reviewRepository.save(review1);

        // Assert
        assertEquals("Servicio actualizado", actualizada.getDescripcion());
        assertEquals(4, actualizada.getValoracion());
        
        // Verificar en la base de datos
        Review encontrada = entityManager.find(Review.class, review1.getId());
        assertEquals("Servicio actualizado", encontrada.getDescripcion());
        assertEquals(4, encontrada.getValoracion());
    }

    @Test
    void testDeleteById() {
        // Arrange
        Long idAEliminar = review2.getId();

        // Act
        reviewRepository.deleteById(idAEliminar);
        entityManager.flush();

        // Assert
        Optional<Review> resultado = reviewRepository.findById(idAEliminar);
        assertFalse(resultado.isPresent());
        
        // Verificar que la otra review sigue existiendo
        assertEquals(1, reviewRepository.count());
    }

    @Test
    void testExistsById() {
        // Act & Assert
        assertTrue(reviewRepository.existsById(review1.getId()));
        assertFalse(reviewRepository.existsById(999L));
    }

    @Test
    void testCount() {
        // Act
        long total = reviewRepository.count();

        // Assert
        assertEquals(2, total);
    }

    @Test
    void testFindByValoracion() {
        // Act
        List<Review> reviewsCincoEstrellas = reviewRepository.findByValoracion(5);
        List<Review> reviewsCuatroEstrellas = reviewRepository.findByValoracion(4);

        // Assert
        assertEquals(1, reviewsCincoEstrellas.size());
        assertEquals("Excelente servicio, muy recomendable", reviewsCincoEstrellas.get(0).getDescripcion());
        
        assertEquals(1, reviewsCuatroEstrellas.size());
        assertEquals("Buena experiencia en general", reviewsCuatroEstrellas.get(0).getDescripcion());
    }

    @Test
    void testFindByClienteId() {
        // Act
        Optional<Review> reviewCliente1 = reviewRepository.findByClienteId(cliente1.getId());
        Optional<Review> reviewCliente3 = reviewRepository.findByClienteId(cliente3.getId());

        // Assert
        assertTrue(reviewCliente1.isPresent());
        assertEquals("Excelente servicio, muy recomendable", reviewCliente1.get().getDescripcion());
        
        assertFalse(reviewCliente3.isPresent()); // cliente3 no tiene review
    }

    @Test
    void testCalcularPromedioValoracion() {
        // Act
        Double promedio = reviewRepository.calcularPromedioValoracion();

        // Assert
        assertNotNull(promedio);
        assertEquals(4.5, promedio, 0.01); // (5 + 4) / 2 = 4.5
    }

    @Test
    void testCalcularPromedioValoracion_SinReviews() {
        // Arrange - eliminar todas las reviews
        reviewRepository.deleteAll();
        entityManager.flush();

        // Act
        Double promedio = reviewRepository.calcularPromedioValoracion();

        // Assert
        assertNull(promedio); // No hay reviews, debe devolver null
    }

    @Test
    void testRelacionConCliente() {
        // Act
        Review review = reviewRepository.findById(review1.getId()).orElseThrow();

        // Assert - verificar que la relación con cliente funciona
        assertNotNull(review.getCliente());
        assertEquals("Juan Perez", review.getCliente().getNombre());
        assertEquals(30, review.getCliente().getEdad());
    }

    @Test
    void testUnicidadClienteEnReview() {
        // Arrange - intentar crear una segunda review para el mismo cliente
        Review reviewDuplicada = new Review("Segunda review del mismo cliente", 3, cliente1);

        // Act & Assert - debe lanzar una excepción por violar la restricción UNIQUE
        assertThrows(Exception.class, () -> {
            entityManager.persist(reviewDuplicada);
            entityManager.flush();
        });
    }
}
