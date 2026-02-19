package com.aurareviews.proyecto.controller.rest;

import com.aurareviews.proyecto.model.Review;
import com.aurareviews.proyecto.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * RestController para la API REST de Reviews.
 * Proporciona endpoints JSON para operaciones CRUD.
 */
@RestController
@RequestMapping("/api/reviews")
public class ReviewRestController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewRestController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    /**
     * GET /api/reviews - Obtener todas las reviews
     */
    @GetMapping
    public ResponseEntity<List<Review>> obtenerTodas() {
        List<Review> reviews = reviewService.obtenerTodas();
        return ResponseEntity.ok(reviews);
    }

    /**
     * GET /api/reviews/{id} - Obtener una review por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Review> obtenerPorId(@PathVariable Long id) {
        Optional<Review> review = reviewService.obtenerPorId(id);
        return review.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/reviews - Crear una nueva review
     */
    @PostMapping
    public ResponseEntity<Review> crear(@RequestBody Review review) {
        Review nuevaReview = reviewService.guardar(review);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaReview);
    }

    /**
     * PUT /api/reviews/{id} - Actualizar una review existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<Review> actualizar(@PathVariable Long id, @RequestBody Review review) {
        if (!reviewService.existe(id)) {
            return ResponseEntity.notFound().build();
        }
        review.setId(id);
        Review reviewActualizada = reviewService.guardar(review);
        return ResponseEntity.ok(reviewActualizada);
    }

    /**
     * DELETE /api/reviews/{id} - Eliminar una review
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!reviewService.existe(id)) {
            return ResponseEntity.notFound().build();
        }
        reviewService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/reviews/cliente/{idCliente} - Obtener review de un cliente
     */
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<Review> obtenerPorCliente(@PathVariable Long idCliente) {
        Optional<Review> review = reviewService.obtenerPorClienteId(idCliente);
        return review.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/reviews/valoracion/{valoracion} - Obtener reviews por valoración
     */
    @GetMapping("/valoracion/{valoracion}")
    public ResponseEntity<List<Review>> obtenerPorValoracion(@PathVariable int valoracion) {
        List<Review> reviews = reviewService.obtenerPorValoracion(valoracion);
        return ResponseEntity.ok(reviews);
    }

    /**
     * GET /api/reviews/count - Contar total de reviews
     */
    @GetMapping("/count")
    public ResponseEntity<Long> contarTotal() {
        long total = reviewService.contarTotal();
        return ResponseEntity.ok(total);
    }

    /**
     * GET /api/reviews/promedio-valoracion - Obtener promedio de valoraciones
     */
    @GetMapping("/promedio-valoracion")
    public ResponseEntity<Double> obtenerPromedioValoracion() {
        Double promedio = reviewService.calcularPromedioValoracion();
        return ResponseEntity.ok(promedio != null ? promedio : 0.0);
    }
}
