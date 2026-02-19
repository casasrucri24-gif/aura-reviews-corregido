package com.aurareviews.proyecto.service;

import com.aurareviews.proyecto.model.Review;
import com.aurareviews.proyecto.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Servicio con la logica de negocio para reviews.
 */
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public List<Review> obtenerTodas() {
        return reviewRepository.findAll();
    }

    public Optional<Review> obtenerPorId(Long id) {
        return reviewRepository.findById(id);
    }

    public Review guardar(Review review) {
        // Validar valoracion entre 1 y 5
        if (review.getValoracion() < 1) {
            review.setValoracion(1);
        } else if (review.getValoracion() > 5) {
            review.setValoracion(5);
        }
        return reviewRepository.save(review);
    }

    public void eliminar(Long id) {
        reviewRepository.deleteById(id);
    }

    public boolean existe(Long id) {
        return reviewRepository.existsById(id);
    }

    public Optional<Review> obtenerPorClienteId(Long clienteId) {
        return reviewRepository.findByClienteId(clienteId);
    }

    public Optional<Review> obtenerPorCliente(Long clienteId) {
        return reviewRepository.findByClienteId(clienteId);
    }

    public List<Review> obtenerPorValoracion(int valoracion) {
        return reviewRepository.findByValoracion(valoracion);
    }

    public boolean clienteTieneReview(Long clienteId) {
        return reviewRepository.findByClienteId(clienteId).isPresent();
    }

    public List<Object[]> obtenerEstadisticasPorValoracion() {
        return reviewRepository.contarPorValoracion();
    }

    public Double calcularPromedioValoracion() {
        return reviewRepository.calcularPromedioValoracion();
    }

    public Double obtenerMediaValoracion() {
        Double media = reviewRepository.calcularMediaValoracion();
        return media != null ? media : 0.0;
    }

    public long contarTotal() {
        return reviewRepository.count();
    }
}