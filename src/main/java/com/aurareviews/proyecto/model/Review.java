package com.aurareviews.proyecto.model;

import jakarta.persistence.*;

/**
 * Entidad que representa una review de un cliente.
 */
@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String descripcion;

    // Valoracion del 1 al 5 (estilo estrellas de Google)
    @Column(nullable = false)
    private int valoracion;

    // Relacion con Cliente
    @OneToOne
    @JoinColumn(name = "id_cliente", nullable = false, unique = true)
    private Cliente cliente;

    public Review() {
    }

    public Review(String descripcion, int valoracion, Cliente cliente) {
        this.descripcion = descripcion;
        this.valoracion = valoracion;
        this.cliente = cliente;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getValoracion() {
        return valoracion;
    }

    public void setValoracion(int valoracion) {
        this.valoracion = valoracion;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Long getIdCliente() {
        return cliente != null ? cliente.getId() : null;
    }

    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", descripcion='" + descripcion + '\'' +
                ", valoracion=" + valoracion +
                '}';
    }
}