package com.aurareviews.proyecto.controller;

import com.aurareviews.proyecto.model.Cliente;
import com.aurareviews.proyecto.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

/**
 * Controlador para el CRUD de clientes.
 */
@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    @Autowired
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    // Listar todos los clientes
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("clientes", clienteService.obtenerTodos());
        model.addAttribute("totalClientes", clienteService.contarTotal());
        return "clientes/lista";
    }

    // Mostrar formulario para nuevo cliente
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("cliente", new Cliente());
        model.addAttribute("titulo", "Nuevo Cliente");
        return "clientes/formulario";
    }

    // Guardar cliente
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Cliente cliente, RedirectAttributes redirect) {
        try {
            clienteService.guardar(cliente);
            redirect.addFlashAttribute("mensaje", "Cliente guardado correctamente");
            redirect.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirect.addFlashAttribute("mensaje", "Error al guardar el cliente");
            redirect.addFlashAttribute("tipoMensaje", "danger");
        }
        return "redirect:/clientes";
    }

    // Mostrar formulario para editar
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        Optional<Cliente> cliente = clienteService.obtenerPorId(id);
        
        if (cliente.isPresent()) {
            model.addAttribute("cliente", cliente.get());
            model.addAttribute("titulo", "Editar Cliente");
            return "clientes/formulario";
        } else {
            redirect.addFlashAttribute("mensaje", "Cliente no encontrado");
            redirect.addFlashAttribute("tipoMensaje", "warning");
            return "redirect:/clientes";
        }
    }

    // Eliminar cliente
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            if (clienteService.existe(id)) {
                clienteService.eliminar(id);
                redirect.addFlashAttribute("mensaje", "Cliente eliminado correctamente");
                redirect.addFlashAttribute("tipoMensaje", "success");
            } else {
                redirect.addFlashAttribute("mensaje", "El cliente no existe");
                redirect.addFlashAttribute("tipoMensaje", "warning");
            }
        } catch (Exception e) {
            redirect.addFlashAttribute("mensaje", "Error al eliminar");
            redirect.addFlashAttribute("tipoMensaje", "danger");
        }
        return "redirect:/clientes";
    }

    // Ver detalle de un cliente
    @GetMapping("/detalle/{id}")
    public String verDetalle(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        Optional<Cliente> cliente = clienteService.obtenerPorId(id);
        
        if (cliente.isPresent()) {
            model.addAttribute("cliente", cliente.get());
            return "clientes/detalle";
        } else {
            redirect.addFlashAttribute("mensaje", "Cliente no encontrado");
            redirect.addFlashAttribute("tipoMensaje", "warning");
            return "redirect:/clientes";
        }
    }
}