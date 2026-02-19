package com.aurareviews.proyecto.service;

import com.aurareviews.proyecto.model.Cliente;
import com.aurareviews.proyecto.repository.ClienteRepository;
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
 * Test unitario para ClienteService.
 * Aísla el servicio usando mocks del repositorio.
 */
@ExtendWith(MockitoExtension.class)
class ClienteServiceUnitTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    private Cliente clienteTest;

    @BeforeEach
    void setUp() {
        clienteTest = new Cliente("Juan Perez", 30, "Masculino", false, null);
        clienteTest.setId(1L);
    }

    @Test
    void testObtenerTodos() {
        // Arrange - preparar datos de prueba
        Cliente cliente2 = new Cliente("Maria Lopez", 25, "Femenino", true, "Lactosa");
        cliente2.setId(2L);
        List<Cliente> clientesEsperados = Arrays.asList(clienteTest, cliente2);
        
        when(clienteRepository.findAll()).thenReturn(clientesEsperados);

        // Act - ejecutar el método a probar
        List<Cliente> resultado = clienteService.obtenerTodos();

        // Assert - verificar resultados
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Juan Perez", resultado.get(0).getNombre());
        assertEquals("Maria Lopez", resultado.get(1).getNombre());
        
        // Verificar que se llamó al repositorio
        verify(clienteRepository, times(1)).findAll();
    }

    @Test
    void testObtenerPorId_Existe() {
        // Arrange
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteTest));

        // Act
        Optional<Cliente> resultado = clienteService.obtenerPorId(1L);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("Juan Perez", resultado.get().getNombre());
        assertEquals(30, resultado.get().getEdad());
        verify(clienteRepository, times(1)).findById(1L);
    }

    @Test
    void testObtenerPorId_NoExiste() {
        // Arrange
        when(clienteRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Cliente> resultado = clienteService.obtenerPorId(999L);

        // Assert
        assertFalse(resultado.isPresent());
        verify(clienteRepository, times(1)).findById(999L);
    }

    @Test
    void testGuardar_ClienteSinIntolerancia() {
        // Arrange
        Cliente clienteConIntoleranciaErronea = new Cliente("Pedro Garcia", 35, "Masculino", false, "Gluten");
        Cliente clienteGuardado = new Cliente("Pedro Garcia", 35, "Masculino", false, null);
        clienteGuardado.setId(3L);
        
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteGuardado);

        // Act
        Cliente resultado = clienteService.guardar(clienteConIntoleranciaErronea);

        // Assert
        assertNotNull(resultado);
        assertEquals("Pedro Garcia", resultado.getNombre());
        assertNull(resultado.getDetalleIntolerancia()); // Debe limpiar el detalle si no hay intolerancia
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    void testGuardar_ClienteConIntolerancia() {
        // Arrange
        Cliente clienteConIntolerancia = new Cliente("Ana Martinez", 28, "Femenino", true, "Frutos secos");
        Cliente clienteGuardado = new Cliente("Ana Martinez", 28, "Femenino", true, "Frutos secos");
        clienteGuardado.setId(4L);
        
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteGuardado);

        // Act
        Cliente resultado = clienteService.guardar(clienteConIntolerancia);

        // Assert
        assertNotNull(resultado);
        assertEquals("Ana Martinez", resultado.getNombre());
        assertEquals("Frutos secos", resultado.getDetalleIntolerancia());
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    void testEliminar() {
        // Arrange
        doNothing().when(clienteRepository).deleteById(1L);

        // Act
        clienteService.eliminar(1L);

        // Assert
        verify(clienteRepository, times(1)).deleteById(1L);
    }

    @Test
    void testExiste_Verdadero() {
        // Arrange
        when(clienteRepository.existsById(1L)).thenReturn(true);

        // Act
        boolean resultado = clienteService.existe(1L);

        // Assert
        assertTrue(resultado);
        verify(clienteRepository, times(1)).existsById(1L);
    }

    @Test
    void testExiste_Falso() {
        // Arrange
        when(clienteRepository.existsById(999L)).thenReturn(false);

        // Act
        boolean resultado = clienteService.existe(999L);

        // Assert
        assertFalse(resultado);
        verify(clienteRepository, times(1)).existsById(999L);
    }

    @Test
    void testContarTotal() {
        // Arrange
        when(clienteRepository.count()).thenReturn(5L);

        // Act
        long resultado = clienteService.contarTotal();

        // Assert
        assertEquals(5L, resultado);
        verify(clienteRepository, times(1)).count();
    }
}
