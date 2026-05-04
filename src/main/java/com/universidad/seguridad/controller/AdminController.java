package com.universidad.seguridad.controller;

import com.universidad.seguridad.service.UsuarioService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador exclusivo del panel de administración.
 * Todas las rutas bajo /admin/** están protegidas en SecurityConfig.hasRole("ADMIN").
 * La anotación @PreAuthorize añade una segunda capa de seguridad a nivel de método.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UsuarioService service;

    public AdminController(UsuarioService service) {
        this.service = service;
    }

    /**
     * Panel principal: lista todos los usuarios del sistema.
     * Solo accesible con rol ADMIN.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String panel(Model model) {
        model.addAttribute("usuarios", service.listarTodos());
        return "admin/panel";
    }

    /**
     * Activa o desactiva la cuenta de un usuario.
     */
    @PostMapping("/toggle/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String toggleActivo(@PathVariable Long id) {
        service.toggleActivo(id);
        return "redirect:/admin";
    }
}
