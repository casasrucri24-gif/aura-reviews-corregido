package com.aurareviews.proyecto.repository;

import com.aurareviews.proyecto.model.Cliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test de integracion para ClienteRepository.
 * Verifica que la capa de datos se comunique correctamente con la base de datos.
 * Usa H2 en memoria para los tests.
 */
@DataJpaTest
@ActiveProfiles("test")
class ClienteRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ClienteRepository clienteRepository;

    private Cliente cliente1;
    private Cliente cliente2;
    private Cliente cliente3;

    @BeforeEach
    void setUp() {
        clienteRepository.deleteAll();
        
        cliente1 = new Cliente("Juan Perez", 30, "Masculino", false, null);
        cliente2 = new Cliente("Maria Lopez", 25, "Femenino", true, "Lactosa");
        cliente3 = new Cliente("Pedro Garcia", 45, "Masculino", true, "Gluten");
        
        entityManager.persist(cliente1);
        entityManager.persist(cliente2);
        entityManager.persist(cliente3);
        entityManager.flush();
    }

    @Test
    void testFindAll() {
        List<Cliente> clientes = clienteRepository.findAll();
        assertNotNull(clientes);
        assertEquals(3, clientes.size());
    }

    @Test
    void testFindById_Existe() {
        Optional<Cliente> resultado = clienteRepository.findById(cliente1.getId());
        assertTrue(resultado.isPresent());
        assertEquals("Juan Perez", resultado.get().getNombre());
        assertEquals(30, resultado.get().getEdad());
    }

    @Test
    void testFindById_NoExiste() {
        Optional<Cliente> resultado = clienteRepository.findById(999L);
        assertFalse(resultado.isPresent());
    }

    @Test
    void testSave_NuevoCliente() {
        Cliente nuevoCliente = new Cliente("Ana Martinez", 28, "Femenino", false, null);
        Cliente guardado = clienteRepository.save(nuevoCliente);
        assertNotNull(guardado.getId());
        assertEquals("Ana Martinez", guardado.getNombre());
        Cliente encontrado = entityManager.find(Cliente.class, guardado.getId());
        assertNotNull(encontrado);
        assertEquals("Ana Martinez", encontrado.getNombre());
    }

    @Test
    void testSave_ActualizarCliente() {
        cliente1.setNombre("Juan Perez Actualizado");
        cliente1.setEdad(31);
        Cliente actualizado = clienteRepository.save(cliente1);
        assertEquals("Juan Perez Actualizado", actualizado.getNombre());
        assertEquals(31, actualizado.getEdad());
        Cliente encontrado = entityManager.find(Cliente.class, cliente1.getId());
        assertEquals("Juan Perez Actualizado", encontrado.getNombre());
        assertEquals(31, encontrado.getEdad());
    }

    @Test
    void testDeleteById() {
        Long idAEliminar = cliente2.getId();
        clienteRepository.deleteById(idAEliminar);
        entityManager.flush();
        Optional<Cliente> resultado = clienteRepository.findById(idAEliminar);
        assertFalse(resultado.isPresent());
        assertEquals(2, clienteRepository.count());
    }

    @Test
    void testExistsById() {
        assertTrue(clienteRepository.existsById(cliente1.getId()));
        assertFalse(clienteRepository.existsById(999L));
    }

    @Test
    void testCount() {
        long total = clienteRepository.count();
        assertEquals(3, total);
    }

    @Test
    void testFindClientesSinReview() {
        List<Cliente> clientesSinReview = clienteRepository.findClientesSinReview();
        assertNotNull(clientesSinReview);
        assertEquals(3, clientesSinReview.size());
    }

    @Test
    void testContarPorGenero() {
        List<Object[]> estadisticas = clienteRepository.contarPorGenero();
        assertNotNull(estadisticas);
        assertTrue(estadisticas.size() > 0);
        boolean tienesMasculino = false;
        boolean tienesFemenino = false;
        for (Object[] stat : estadisticas) {
            String genero = (String) stat[0];
            Long cantidad = (Long) stat[1];
            if ("Masculino".equals(genero)) {
                assertEquals(2L, cantidad);
                tienesMasculino = true;
            } else if ("Femenino".equals(genero)) {
                assertEquals(1L, cantidad);
                tienesFemenino = true;
            }
        }
        assertTrue(tienesMasculino);
        assertTrue(tienesFemenino);
    }

    @Test
    void testContarPorIntolerancia() {
        List<Object[]> estadisticas = clienteRepository.contarPorIntolerancia();
        assertNotNull(estadisticas);
        assertTrue(estadisticas.size() > 0);
        for (Object[] stat : estadisticas) {
            Boolean tieneIntolerancia = (Boolean) stat[0];
            Long cantidad = (Long) stat[1];
            if (tieneIntolerancia) {
                assertEquals(2L, cantidad);
            } else {
                assertEquals(1L, cantidad);
            }
        }
    }
}
