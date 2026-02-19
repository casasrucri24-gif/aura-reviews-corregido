package com.aurareviews.proyecto.controller.rest;

import com.aurareviews.proyecto.model.Cliente;
import com.aurareviews.proyecto.model.Review;
import com.aurareviews.proyecto.repository.ClienteRepository;
import com.aurareviews.proyecto.repository.ReviewRepository;
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
 * Test E2E (End-to-End) para ReviewRestController.
 * Simula peticiones HTTP reales y verifica el flujo completo desde el controller
 * hasta la base de datos.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ReviewRestControllerE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Cliente cliente1;
    private Cliente cliente2;
    private Review reviewTest;

    @BeforeEach
    void setUp() {
        // Limpiar la base de datos
        reviewRepository.deleteAll();
        clienteRepository.deleteAll();
        
        // Crear clientes
        cliente1 = new Cliente("Juan Perez", 30, "Masculino", false, null);
        cliente2 = new Cliente("Maria Lopez", 25, "Femenino", true, "Lactosa");
        clienteRepository.save(cliente1);
        clienteRepository.save(cliente2);
        
        // Crear review de prueba
        reviewTest = new Review("Excelente servicio", 5, cliente1);
        reviewRepository.save(reviewTest);
    }

    @Test
    void testObtenerTodas_DebeRetornarListaDeReviews() throws Exception {
        // Arrange - añadir otra review
        Review review2 = new Review("Buena experiencia", 4, cliente2);
        reviewRepository.save(review2);

        // Act & Assert
        mockMvc.perform(get("/api/reviews")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].descripcion", is("Excelente servicio")))
                .andExpect(jsonPath("$[0].valoracion", is(5)))
                .andExpect(jsonPath("$[1].descripcion", is("Buena experiencia")))
                .andExpect(jsonPath("$[1].valoracion", is(4)));
    }

    @Test
    void testObtenerPorId_ReviewExiste_DebeRetornar200() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/reviews/{id}", reviewTest.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(reviewTest.getId().intValue())))
                .andExpect(jsonPath("$.descripcion", is("Excelente servicio")))
                .andExpect(jsonPath("$.valoracion", is(5)));
    }

    @Test
    void testObtenerPorId_ReviewNoExiste_DebeRetornar404() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/reviews/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCrear_ReviewValida_DebeRetornar201YCrearReview() throws Exception {
        // Arrange - crear cliente sin review
        Cliente cliente3 = new Cliente("Pedro Garcia", 35, "Masculino", false, null);
        clienteRepository.save(cliente3);
        
        Review nuevaReview = new Review("Servicio correcto", 3, cliente3);
        String reviewJson = objectMapper.writeValueAsString(nuevaReview);

        // Act & Assert
        mockMvc.perform(post("/api/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reviewJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.descripcion", is("Servicio correcto")))
                .andExpect(jsonPath("$.valoracion", is(3)));
        
        // Verificar que se guardó en la base de datos
        assert reviewRepository.count() == 2;
    }

    @Test
    void testActualizar_ReviewExiste_DebeRetornar200YActualizar() throws Exception {
        // Arrange
        reviewTest.setDescripcion("Servicio actualizado");
        reviewTest.setValoracion(4);
        String reviewJson = objectMapper.writeValueAsString(reviewTest);

        // Act & Assert
        mockMvc.perform(put("/api/reviews/{id}", reviewTest.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(reviewJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descripcion", is("Servicio actualizado")))
                .andExpect(jsonPath("$.valoracion", is(4)));
        
        // Verificar que se actualizó en la base de datos
        Review actualizada = reviewRepository.findById(reviewTest.getId()).orElseThrow();
        assert actualizada.getDescripcion().equals("Servicio actualizado");
        assert actualizada.getValoracion() == 4;
    }

    @Test
    void testActualizar_ReviewNoExiste_DebeRetornar404() throws Exception {
        // Arrange
        Review reviewInexistente = new Review("Inexistente", 1, cliente1);
        String reviewJson = objectMapper.writeValueAsString(reviewInexistente);

        // Act & Assert
        mockMvc.perform(put("/api/reviews/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(reviewJson))
                .andExpect(status().isNotFound());
    }

    @Test
    void testEliminar_ReviewExiste_DebeRetornar204YEliminar() throws Exception {
        // Arrange
        Long idAEliminar = reviewTest.getId();

        // Act & Assert
        mockMvc.perform(delete("/api/reviews/{id}", idAEliminar)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        
        // Verificar que se eliminó de la base de datos
        assert !reviewRepository.existsById(idAEliminar);
    }

    @Test
    void testEliminar_ReviewNoExiste_DebeRetornar404() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/reviews/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testObtenerPorCliente_ClienteConReview_DebeRetornarReview() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/reviews/cliente/{idCliente}", cliente1.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descripcion", is("Excelente servicio")))
                .andExpect(jsonPath("$.valoracion", is(5)));
    }

    @Test
    void testObtenerPorCliente_ClienteSinReview_DebeRetornar404() throws Exception {
        // Arrange - cliente2 no tiene review
        
        // Act & Assert
        mockMvc.perform(get("/api/reviews/cliente/{idCliente}", cliente2.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testObtenerPorValoracion_DebeRetornarReviewsConEsaValoracion() throws Exception {
        // Arrange - añadir más reviews
        Review review2 = new Review("Otra review excelente", 5, cliente2);
        reviewRepository.save(review2);

        // Act & Assert
        mockMvc.perform(get("/api/reviews/valoracion/{valoracion}", 5)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].valoracion", is(5)))
                .andExpect(jsonPath("$[1].valoracion", is(5)));
    }

    @Test
    void testContarTotal_DebeRetornarCantidadCorrecta() throws Exception {
        // Arrange - añadir otra review
        Review review2 = new Review("Segunda review", 3, cliente2);
        reviewRepository.save(review2);

        // Act & Assert
        mockMvc.perform(get("/api/reviews/count")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("2"));
    }

    @Test
    void testObtenerPromedioValoracion_DebeRetornarPromedioCorrecto() throws Exception {
        // Arrange - añadir reviews con diferentes valoraciones
        Review review2 = new Review("Review 4 estrellas", 4, cliente2);
        reviewRepository.save(review2);
        // Ahora tenemos: 5 y 4, promedio = 4.5

        // Act & Assert
        mockMvc.perform(get("/api/reviews/promedio-valoracion")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(4.5)));
    }

    @Test
    void testObtenerPromedioValoracion_SinReviews_DebeRetornarCero() throws Exception {
        // Arrange - eliminar todas las reviews
        reviewRepository.deleteAll();

        // Act & Assert
        mockMvc.perform(get("/api/reviews/promedio-valoracion")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(0.0)));
    }
}
