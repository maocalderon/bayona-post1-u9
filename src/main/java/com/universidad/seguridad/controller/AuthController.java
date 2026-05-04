package com.universidad.seguridad.controller;

import com.universidad.seguridad.model.Usuario;
import com.universidad.seguridad.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Controlador principal de autenticación y vistas protegidas.
 * Gestiona: login, registro, dashboard y redirección raíz.
 */
@Controller
public class AuthController {

    private final UsuarioService service;

    public AuthController(UsuarioService service) {
        this.service = service;
    }

    // ── Raíz ────────────────────────────────────────────────────────────────
    @GetMapping("/")
    public String index() {
        return "redirect:/dashboard";
    }

    // ── Login ────────────────────────────────────────────────────────────────
    /**
     * Muestra el formulario de login personalizado.
     * Spring Security procesa el POST a /login automáticamente.
     */
    @GetMapping("/login")
    public String mostrarLogin() {
        return "auth/login";
    }

    // ── Registro ─────────────────────────────────────────────────────────────
    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "auth/registro";
    }

    @PostMapping("/registro")
    public String registrar(@Valid @ModelAttribute("usuario") Usuario usuario,
                            BindingResult result) {
        if (result.hasErrors()) {
            return "auth/registro";
        }
        try {
            service.registrar(usuario);
            return "redirect:/login?registrado";
        } catch (RuntimeException e) {
            result.rejectValue("email", "error.email", e.getMessage());
            return "auth/registro";
        }
    }

    // ── Dashboard ────────────────────────────────────────────────────────────
    /**
     * Vista principal post-login.
     * Spring Security garantiza que solo llegan usuarios autenticados.
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {
        model.addAttribute("usuario", auth.getName());
        model.addAttribute("roles", auth.getAuthorities());
        return "dashboard";
    }
}
