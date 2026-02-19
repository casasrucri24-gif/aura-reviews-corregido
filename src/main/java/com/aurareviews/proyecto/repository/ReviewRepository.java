package com.aurareviews.proyecto.repository;

import com.aurareviews.proyecto.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para operaciones CRUD sobre Review.
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Buscar review por id de cliente
    Optional<Review> findByClienteId(Long clienteId);

    // Buscar reviews por valoración
    List<Review> findByValoracion(int valoracion);

    // Contar reviews por valoracion
    @Query("SELECT r.valoracion, COUNT(r) FROM Review r GROUP BY r.valoracion ORDER BY r.valoracion")
    List<Object[]> contarPorValoracion();

    // Calcular promedio de valoraciones
    @Query("SELECT AVG(r.valoracion) FROM Review r")
    Double calcularPromedioValoracion();

    // Calcular media de valoraciones (alias para compatibilidad)
    @Query("SELECT AVG(r.valoracion) FROM Review r")
    Double calcularMediaValoracion();

    // Contar total de reviews
    @Query("SELECT COUNT(r) FROM Review r")
    Long contarTotalReviews();
}