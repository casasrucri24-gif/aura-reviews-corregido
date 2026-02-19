package com.aurareviews.proyecto.service;

import com.aurareviews.proyecto.model.Cliente;
import com.aurareviews.proyecto.model.Review;
import com.aurareviews.proyecto.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test unitario para ReviewService.
 * Aísla el servicio usando mocks del repositorio.
 */
@ExtendWith(MockitoExtension.class)
class ReviewServiceUnitTest {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewService reviewService;

    private Review reviewTest;
    private Cliente clienteTest;

    @BeforeEach
    void setUp() {
        clienteTest = new Cliente("Juan Perez", 30, "Masculino", false, null);
        clienteTest.setId(1L);
        
        reviewTest = new Review("Excelente servicio", 5, clienteTest);
        reviewTest.setId(1L);
    }

    @Test
    void testObtenerTodas() {
        // Arrange
        Cliente cliente2 = new Cliente("Maria Lopez", 25, "Femenino", true, "Lactosa");
        cliente2.setId(2L);
        Review review2 = new Review("Buena experiencia", 4, cliente2);
        review2.setId(2L);
        
        List<Review> reviewsEsperadas = Arrays.asList(reviewTest, review2);
        when(reviewRepository.findAll()).thenReturn(reviewsEsperadas);

        // Act
        List<Review> resultado = reviewService.obtenerTodas();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Excelente servicio", resultado.get(0).getDescripcion());
        assertEquals(5, resultado.get(0).getValoracion());
        verify(reviewRepository, times(1)).findAll();
    }

    @Test
    void testObtenerPorId_Existe() {
        // Arrange
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(reviewTest));

        // Act
        Optional<Review> resultado = reviewService.obtenerPorId(1L);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("Excelente servicio", resultado.get().getDescripcion());
        assertEquals(5, resultado.get().getValoracion());
        verify(reviewRepository, times(1)).findById(1L);
    }

    @Test
    void testObtenerPorId_NoExiste() {
        // Arrange
        when(reviewRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Review> resultado = reviewService.obtenerPorId(999L);

        // Assert
        assertFalse(resultado.isPresent());
        verify(reviewRepository, times(1)).findById(999L);
    }

    @Test
    void testGuardar() {
        // Arrange
        Review nuevaReview = new Review("Servicio correcto", 3, clienteTest);
        Review reviewGuardada = new Review("Servicio correcto", 3, clienteTest);
        reviewGuardada.setId(3L);
        
        when(reviewRepository.save(any(Review.class))).thenReturn(reviewGuardada);

        // Act
        Review resultado = reviewService.guardar(nuevaReview);

        // Assert
        assertNotNull(resultado);
        assertEquals(3L, resultado.getId());
        assertEquals("Servicio correcto", resultado.getDescripcion());
        assertEquals(3, resultado.getValoracion());
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void testEliminar() {
        // Arrange
        doNothing().when(reviewRepository).deleteById(1L);

        // Act
        reviewService.eliminar(1L);

        // Assert
        verify(reviewRepository, times(1)).deleteById(1L);
    }

    @Test
    void testExiste_Verdadero() {
        // Arrange
        when(reviewRepository.existsById(1L)).thenReturn(true);

        // Act
        boolean resultado = reviewService.existe(1L);

        // Assert
        assertTrue(resultado);
        verify(reviewRepository, times(1)).existsById(1L);
    }

    @Test
    void testExiste_Falso() {
        // Arrange
        when(reviewRepository.existsById(999L)).thenReturn(false);

        // Act
        boolean resultado = reviewService.existe(999L);

        // Assert
        assertFalse(resultado);
        verify(reviewRepository, times(1)).existsById(999L);
    }

    @Test
    void testObtenerPorValoracion() {
        // Arrange
        List<Review> reviewsCincoEstrellas = Arrays.asList(reviewTest);
        when(reviewRepository.findByValoracion(5)).thenReturn(reviewsCincoEstrellas);

        // Act
        List<Review> resultado = reviewService.obtenerPorValoracion(5);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(5, resultado.get(0).getValoracion());
        verify(reviewRepository, times(1)).findByValoracion(5);
    }

    @Test
    void testContarTotal() {
        // Arrange
        when(reviewRepository.count()).thenReturn(10L);

        // Act
        long resultado = reviewService.contarTotal();

        // Assert
        assertEquals(10L, resultado);
        verify(reviewRepository, times(1)).count();
    }

    @Test
    void testCalcularPromedioValoracion_ConReviews() {
        // Arrange
        when(reviewRepository.calcularPromedioValoracion()).thenReturn(4.2);

        // Act
        Double resultado = reviewService.calcularPromedioValoracion();

        // Assert
        assertNotNull(resultado);
        assertEquals(4.2, resultado);
        verify(reviewRepository, times(1)).calcularPromedioValoracion();
    }

    @Test
    void testCalcularPromedioValoracion_SinReviews() {
        // Arrange
        when(reviewRepository.calcularPromedioValoracion()).thenReturn(null);

        // Act
        Double resultado = reviewService.calcularPromedioValoracion();

        // Assert
        assertNull(resultado);
        verify(reviewRepository, times(1)).calcularPromedioValoracion();
    }
}
