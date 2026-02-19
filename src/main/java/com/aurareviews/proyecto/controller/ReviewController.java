package com.aurareviews.proyecto.controller;

import com.aurareviews.proyecto.model.Cliente;
import com.aurareviews.proyecto.model.Review;
import com.aurareviews.proyecto.service.ClienteService;
import com.aurareviews.proyecto.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

/**
 * Controlador para el CRUD de reviews.
 */
@Controller
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final ClienteService clienteService;

    @Autowired
    public ReviewController(ReviewService reviewService, ClienteService clienteService) {
        this.reviewService = reviewService;
        this.clienteService = clienteService;
    }

    // Listar todas las reviews
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("reviews", reviewService.obtenerTodas());
        model.addAttribute("totalReviews", reviewService.contarTotal());
        model.addAttribute("mediaValoracion", reviewService.obtenerMediaValoracion());
        return "reviews/lista";
    }

    // Mostrar formulario para nueva review
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model, RedirectAttributes redirect) {
        List<Cliente> clientesDisponibles = clienteService.obtenerClientesSinReview();
        
        if (clientesDisponibles.isEmpty()) {
            redirect.addFlashAttribute("mensaje", "Todos los clientes ya tienen una review");
            redirect.addFlashAttribute("tipoMensaje", "info");
            return "redirect:/reviews";
        }
        
        model.addAttribute("review", new Review());
        model.addAttribute("clientes", clientesDisponibles);
        model.addAttribute("titulo", "Nueva Review");
        return "reviews/formulario";
    }

    // Guardar nueva review
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Review review, 
                          @RequestParam Long clienteId,
                          RedirectAttributes redirect) {
        try {
            Optional<Cliente> cliente = clienteService.obtenerPorId(clienteId);
            
            if (cliente.isEmpty()) {
                redirect.addFlashAttribute("mensaje", "Cliente no encontrado");
                redirect.addFlashAttribute("tipoMensaje", "danger");
                return "redirect:/reviews";
            }
            
            // Verificar que el cliente no tenga ya una review
            if (review.getId() == null && reviewService.clienteTieneReview(clienteId)) {
                redirect.addFlashAttribute("mensaje", "Este cliente ya tiene una review");
                redirect.addFlashAttribute("tipoMensaje", "warning");
                return "redirect:/reviews";
            }
            
            review.setCliente(cliente.get());
            reviewService.guardar(review);
            
            redirect.addFlashAttribute("mensaje", "Review guardada correctamente");
            redirect.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirect.addFlashAttribute("mensaje", "Error al guardar");
            redirect.addFlashAttribute("tipoMensaje", "danger");
        }
        return "redirect:/reviews";
    }

    // Mostrar formulario para editar review
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        Optional<Review> review = reviewService.obtenerPorId(id);
        
        if (review.isPresent()) {
            model.addAttribute("review", review.get());
            model.addAttribute("titulo", "Editar Review");
            model.addAttribute("clienteActual", review.get().getCliente());
            return "reviews/formulario-editar";
        } else {
            redirect.addFlashAttribute("mensaje", "Review no encontrada");
            redirect.addFlashAttribute("tipoMensaje", "warning");
            return "redirect:/reviews";
        }
    }

    // Actualizar review
    @PostMapping("/actualizar")
    public String actualizar(@ModelAttribute Review review,
                             @RequestParam Long clienteId,
                             RedirectAttributes redirect) {
        try {
            Optional<Cliente> cliente = clienteService.obtenerPorId(clienteId);
            
            if (cliente.isEmpty()) {
                redirect.addFlashAttribute("mensaje", "Cliente no encontrado");
                redirect.addFlashAttribute("tipoMensaje", "danger");
                return "redirect:/reviews";
            }
            
            review.setCliente(cliente.get());
            reviewService.guardar(review);
            
            redirect.addFlashAttribute("mensaje", "Review actualizada correctamente");
            redirect.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirect.addFlashAttribute("mensaje", "Error al actualizar");
            redirect.addFlashAttribute("tipoMensaje", "danger");
        }
        return "redirect:/reviews";
    }

    // Eliminar review
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            Optional<Review> review = reviewService.obtenerPorId(id);
            if (review.isPresent()) {
                reviewService.eliminar(id);
                redirect.addFlashAttribute("mensaje", "Review eliminada correctamente");
                redirect.addFlashAttribute("tipoMensaje", "success");
            } else {
                redirect.addFlashAttribute("mensaje", "La review no existe");
                redirect.addFlashAttribute("tipoMensaje", "warning");
            }
        } catch (Exception e) {
            redirect.addFlashAttribute("mensaje", "Error al eliminar");
            redirect.addFlashAttribute("tipoMensaje", "danger");
        }
        return "redirect:/reviews";
    }
}