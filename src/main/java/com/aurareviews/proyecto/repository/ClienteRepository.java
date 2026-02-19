package com.aurareviews.proyecto.repository;

import com.aurareviews.proyecto.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para operaciones CRUD sobre Cliente.
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // Buscar clientes por nombre
    List<Cliente> findByNombreContainingIgnoreCase(String nombre);

    // Contar clientes por genero
    @Query("SELECT c.genero, COUNT(c) FROM Cliente c GROUP BY c.genero")
    List<Object[]> contarPorGenero();

    // Contar clientes por franjas de edad
    @Query("SELECT " +
           "CASE " +
           "  WHEN c.edad BETWEEN 0 AND 14 THEN '0-14' " +
           "  WHEN c.edad BETWEEN 15 AND 24 THEN '15-24' " +
           "  WHEN c.edad BETWEEN 25 AND 34 THEN '25-34' " +
           "  WHEN c.edad BETWEEN 35 AND 44 THEN '35-44' " +
           "  WHEN c.edad BETWEEN 45 AND 54 THEN '45-54' " +
           "  WHEN c.edad BETWEEN 55 AND 64 THEN '55-64' " +
           "  ELSE '65+' " +
           "END as franjaEdad, COUNT(c) " +
           "FROM Cliente c GROUP BY franjaEdad ORDER BY franjaEdad")
    List<Object[]> contarPorFranjaEdad();

    // Contar clientes con y sin intolerancia
    @Query("SELECT c.intolerancia, COUNT(c) FROM Cliente c GROUP BY c.intolerancia")
    List<Object[]> contarPorIntolerancia();

    // Obtener clientes que no tienen review
    @Query("SELECT c FROM Cliente c WHERE c.review IS NULL")
    List<Cliente> findClientesSinReview();
}