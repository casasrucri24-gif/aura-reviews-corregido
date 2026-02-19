package com.aurareviews.proyecto.service;

import com.aurareviews.proyecto.model.Cliente;
import com.aurareviews.proyecto.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Servicio con la logica de negocio para clientes.
 */
@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    @Autowired
    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<Cliente> obtenerTodos() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> obtenerPorId(Long id) {
        return clienteRepository.findById(id);
    }

    public Cliente guardar(Cliente cliente) {
        // Si no tiene intolerancia, limpiamos el detalle
        if (!cliente.isIntolerancia()) {
            cliente.setDetalleIntolerancia(null);
        }
        return clienteRepository.save(cliente);
    }

    public void eliminar(Long id) {
        clienteRepository.deleteById(id);
    }

    public boolean existe(Long id) {
        return clienteRepository.existsById(id);
    }

    public List<Object[]> obtenerEstadisticasPorGenero() {
        return clienteRepository.contarPorGenero();
    }

    public List<Object[]> obtenerEstadisticasPorEdad() {
        return clienteRepository.contarPorFranjaEdad();
    }

    public List<Object[]> obtenerEstadisticasIntolerancias() {
        return clienteRepository.contarPorIntolerancia();
    }

    public List<Cliente> obtenerClientesSinReview() {
        return clienteRepository.findClientesSinReview();
    }

    public long contarTotal() {
        return clienteRepository.count();
    }
}