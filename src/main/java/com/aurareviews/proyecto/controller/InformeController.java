package com.aurareviews.proyecto.controller;

import com.aurareviews.proyecto.service.ClienteService;
import com.aurareviews.proyecto.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * Controlador para generar los informes con Chart.js.
 */
@Controller
@RequestMapping("/informes")
public class InformeController {

    private final ClienteService clienteService;
    private final ReviewService reviewService;

    @Autowired
    public InformeController(ClienteService clienteService, ReviewService reviewService) {
        this.clienteService = clienteService;
        this.reviewService = reviewService;
    }

    @GetMapping
    public String mostrarInformes(Model model) {
        // Datos para el grafico de clientes por genero
        List<Object[]> datosPorGenero = clienteService.obtenerEstadisticasPorGenero();
        List<String> labelsGenero = new ArrayList<>();
        List<Long> valoresGenero = new ArrayList<>();
        
        for (Object[] dato : datosPorGenero) {
            labelsGenero.add((String) dato[0]);
            valoresGenero.add((Long) dato[1]);
        }
        model.addAttribute("labelsGenero", labelsGenero);
        model.addAttribute("valoresGenero", valoresGenero);

        // Datos para el grafico de reviews por valoracion
        List<Object[]> datosPorValoracion = reviewService.obtenerEstadisticasPorValoracion();
        Long[] conteoEstrellas = new Long[]{0L, 0L, 0L, 0L, 0L};
        for (Object[] dato : datosPorValoracion) {
            int valoracion = (Integer) dato[0];
            Long cantidad = (Long) dato[1];
            if (valoracion >= 1 && valoracion <= 5) {
                conteoEstrellas[valoracion - 1] = cantidad;
            }
        }
        
        List<String> labelsValoracion = List.of("1 Estrella", "2 Estrellas", "3 Estrellas", "4 Estrellas", "5 Estrellas");
        List<Long> valoresValoracion = List.of(conteoEstrellas[0], conteoEstrellas[1], 
                                               conteoEstrellas[2], conteoEstrellas[3], conteoEstrellas[4]);
        model.addAttribute("labelsValoracion", labelsValoracion);
        model.addAttribute("valoresValoracion", valoresValoracion);

        // Datos para el grafico de clientes por franja de edad
        List<Object[]> datosPorEdad = clienteService.obtenerEstadisticasPorEdad();
        List<String> labelsEdad = new ArrayList<>();
        List<Long> valoresEdad = new ArrayList<>();
        
        for (Object[] dato : datosPorEdad) {
            labelsEdad.add((String) dato[0]);
            valoresEdad.add((Long) dato[1]);
        }
        model.addAttribute("labelsEdad", labelsEdad);
        model.addAttribute("valoresEdad", valoresEdad);

        // Datos para el grafico de intolerancias (grafico libre)
        List<Object[]> datosPorIntolerancia = clienteService.obtenerEstadisticasIntolerancias();
        List<String> labelsIntolerancia = new ArrayList<>();
        List<Long> valoresIntolerancia = new ArrayList<>();
        
        for (Object[] dato : datosPorIntolerancia) {
            Boolean tieneIntolerancia = (Boolean) dato[0];
            String label = tieneIntolerancia ? "Con Intolerancia" : "Sin Intolerancia";
            labelsIntolerancia.add(label);
            valoresIntolerancia.add((Long) dato[1]);
        }
        model.addAttribute("labelsIntolerancia", labelsIntolerancia);
        model.addAttribute("valoresIntolerancia", valoresIntolerancia);

        // KPIs generales
        model.addAttribute("totalClientes", clienteService.contarTotal());
        model.addAttribute("totalReviews", reviewService.contarTotal());
        model.addAttribute("mediaValoracion", String.format("%.2f", reviewService.obtenerMediaValoracion()));

        return "reviews/informes/dashboard";
    }
}