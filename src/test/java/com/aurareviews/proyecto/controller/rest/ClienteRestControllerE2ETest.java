package com.aurareviews.proyecto.controller.rest;

import com.aurareviews.proyecto.model.Cliente;
import com.aurareviews.proyecto.repository.ClienteRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test E2E (End-to-End) para ClienteRestController.
 * Simula peticiones HTTP reales y verifica el flujo completo desde el controller
 * hasta la base de datos.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ClienteRestControllerE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Cliente clienteTest;

    @BeforeEach
    void setUp() {
        // Limpiar la base de datos antes de cada test
        clienteRepository.deleteAll();
        
        // Crear cliente de prueba
        clienteTest = new Cliente("Juan Perez", 30, "Masculino", false, null);
        clienteRepository.save(clienteTest);
    }

    @Test
    void testObtenerTodos_DebeRetornarListaDeClientes() throws Exception {
        // Arrange - añadir otro cliente
        Cliente cliente2 = new Cliente("Maria Lopez", 25, "Femenino", true, "Lactosa");
        clienteRepository.save(cliente2);

        // Act & Assert
        mockMvc.perform(get("/api/clientes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nombre", is("Juan Perez")))
                .andExpect(jsonPath("$[0].edad", is(30)))
                .andExpect(jsonPath("$[1].nombre", is("Maria Lopez")))
                .andExpect(jsonPath("$[1].edad", is(25)));
    }

    @Test
    void testObtenerPorId_ClienteExiste_DebeRetornar200() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/clientes/{id}", clienteTest.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(clienteTest.getId().intValue())))
                .andExpect(jsonPath("$.nombre", is("Juan Perez")))
                .andExpect(jsonPath("$.edad", is(30)))
                .andExpect(jsonPath("$.genero", is("Masculino")))
                .andExpect(jsonPath("$.intolerancia", is(false)));
    }

    @Test
    void testObtenerPorId_ClienteNoExiste_DebeRetornar404() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/clientes/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCrear_ClienteValido_DebeRetornar201YCrearCliente() throws Exception {
        // Arrange
        Cliente nuevoCliente = new Cliente("Pedro Garcia", 35, "Masculino", true, "Gluten");
        String clienteJson = objectMapper.writeValueAsString(nuevoCliente);

        // Act & Assert
        mockMvc.perform(post("/api/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(clienteJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.nombre", is("Pedro Garcia")))
                .andExpect(jsonPath("$.edad", is(35)))
                .andExpect(jsonPath("$.genero", is("Masculino")))
                .andExpect(jsonPath("$.intolerancia", is(true)))
                .andExpect(jsonPath("$.detalleIntolerancia", is("Gluten")));
        
        // Verificar que se guardó en la base de datos
        assert clienteRepository.count() == 2;
    }

    @Test
    void testActualizar_ClienteExiste_DebeRetornar200YActualizar() throws Exception {
        // Arrange
        clienteTest.setNombre("Juan Perez Actualizado");
        clienteTest.setEdad(31);
        String clienteJson = objectMapper.writeValueAsString(clienteTest);

        // Act & Assert
        mockMvc.perform(put("/api/clientes/{id}", clienteTest.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(clienteJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Juan Perez Actualizado")))
                .andExpect(jsonPath("$.edad", is(31)));
        
        // Verificar que se actualizó en la base de datos
        Cliente actualizado = clienteRepository.findById(clienteTest.getId()).orElseThrow();
        assert actualizado.getNombre().equals("Juan Perez Actualizado");
        assert actualizado.getEdad() == 31;
    }

    @Test
    void testActualizar_ClienteNoExiste_DebeRetornar404() throws Exception {
        // Arrange
        Cliente clienteInexistente = new Cliente("Inexistente", 20, "Masculino", false, null);
        String clienteJson = objectMapper.writeValueAsString(clienteInexistente);

        // Act & Assert
        mockMvc.perform(put("/api/clientes/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(clienteJson))
                .andExpect(status().isNotFound());
    }

    @Test
    void testEliminar_ClienteExiste_DebeRetornar204YEliminar() throws Exception {
        // Arrange
        Long idAEliminar = clienteTest.getId();

        // Act & Assert
        mockMvc.perform(delete("/api/clientes/{id}", idAEliminar)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        
        // Verificar que se eliminó de la base de datos
        assert !clienteRepository.existsById(idAEliminar);
    }

    @Test
    void testEliminar_ClienteNoExiste_DebeRetornar404() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/clientes/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testObtenerSinReview_DebeRetornarClientesSinReview() throws Exception {
        // Arrange - todos los clientes de prueba están sin review por defecto
        Cliente cliente2 = new Cliente("Ana Martinez", 28, "Femenino", false, null);
        clienteRepository.save(cliente2);

        // Act & Assert
        mockMvc.perform(get("/api/clientes/sin-review")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nombre", is("Juan Perez")))
                .andExpect(jsonPath("$[1].nombre", is("Ana Martinez")));
    }

    @Test
    void testContarTotal_DebeRetornarCantidadCorrecta() throws Exception {
        // Arrange - ya tenemos 1 cliente, añadimos 2 más
        clienteRepository.save(new Cliente("Maria Lopez", 25, "Femenino", false, null));
        clienteRepository.save(new Cliente("Pedro Garcia", 35, "Masculino", true, "Gluten"));

        // Act & Assert
        mockMvc.perform(get("/api/clientes/count")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }

    @Test
    void testCrear_ClienteSinIntolerancia_DebeLimpiarDetalle() throws Exception {
        // Arrange - cliente sin intolerancia pero con detalle erróneo
        Cliente cliente = new Cliente("Ana Martinez", 28, "Femenino", false, "Lactosa");
        String clienteJson = objectMapper.writeValueAsString(cliente);

        // Act & Assert
        mockMvc.perform(post("/api/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(clienteJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.intolerancia", is(false)))
                .andExpect(jsonPath("$.detalleIntolerancia", nullValue()));
    }
}
