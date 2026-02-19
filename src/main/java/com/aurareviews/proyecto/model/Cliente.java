package com.aurareviews.proyecto.model;

import jakarta.persistence.*;

/**
 * Entidad que representa a un cliente del sistema.
 */
@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false)
    private int edad;

    @Column(nullable = false, length = 50)
    private String genero;

    @Column(nullable = false)
    private boolean intolerancia;

    @Column(length = 255)
    private String detalleIntolerancia;

    // Relacion con Review (un cliente puede tener una review o ninguna)
    @OneToOne(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private Review review;

    // Constructor vacio requerido por JPA
    public Cliente() {
    }

    // Constructor con parametros
    public Cliente(String nombre, int edad, String genero, boolean intolerancia, String detalleIntolerancia) {
        this.nombre = nombre;
        this.edad = edad;
        this.genero = genero;
        this.intolerancia = intolerancia;
        this.detalleIntolerancia = detalleIntolerancia;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public boolean isIntolerancia() {
        return intolerancia;
    }

    public void setIntolerancia(boolean intolerancia) {
        this.intolerancia = intolerancia;
    }

    public String getDetalleIntolerancia() {
        return detalleIntolerancia;
    }

    public void setDetalleIntolerancia(String detalleIntolerancia) {
        this.detalleIntolerancia = detalleIntolerancia;
    }

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", edad=" + edad +
                ", genero='" + genero + '\'' +
                ", intolerancia=" + intolerancia +
                '}';
    }
}