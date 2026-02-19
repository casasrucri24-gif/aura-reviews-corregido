package com.aurareviews.proyecto.controller.rest;

import com.aurareviews.proyecto.model.Cliente;
import com.aurareviews.proyecto.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * RestController para la API REST de Clientes.
 * Proporciona endpoints JSON para operaciones CRUD.
 */
@RestController
@RequestMapping("/api/clientes")
public class ClienteRestController {

    private final ClienteService clienteService;

    @Autowired
    public ClienteRestController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    /**
     * GET /api/clientes - Obtener todos los clientes
     */
    @GetMapping
    public ResponseEntity<List<Cliente>> obtenerTodos() {
        List<Cliente> clientes = clienteService.obtenerTodos();
        return ResponseEntity.ok(clientes);
    }

    /**
     * GET /api/clientes/{id} - Obtener un cliente por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> obtenerPorId(@PathVariable Long id) {
        Optional<Cliente> cliente = clienteService.obtenerPorId(id);
        return cliente.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/clientes - Crear un nuevo cliente
     */
    @PostMapping
    public ResponseEntity<Cliente> crear(@RequestBody Cliente cliente) {
        Cliente nuevoCliente = clienteService.guardar(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoCliente);
    }

    /**
     * PUT /api/clientes/{id} - Actualizar un cliente existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<Cliente> actualizar(@PathVariable Long id, @RequestBody Cliente cliente) {
        if (!clienteService.existe(id)) {
            return ResponseEntity.notFound().build();
        }
        cliente.setId(id);
        Cliente clienteActualizado = clienteService.guardar(cliente);
        return ResponseEntity.ok(clienteActualizado);
    }

    /**
     * DELETE /api/clientes/{id} - Eliminar un cliente
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!clienteService.existe(id)) {
            return ResponseEntity.notFound().build();
        }
        clienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/clientes/sin-review - Obtener clientes sin review
     */
    @GetMapping("/sin-review")
    public ResponseEntity<List<Cliente>> obtenerSinReview() {
        List<Cliente> clientes = clienteService.obtenerClientesSinReview();
        return ResponseEntity.ok(clientes);
    }

    /**
     * GET /api/clientes/count - Contar total de clientes
     */
    @GetMapping("/count")
    public ResponseEntity<Long> contarTotal() {
        long total = clienteService.contarTotal();
        return ResponseEntity.ok(total);
    }
}
