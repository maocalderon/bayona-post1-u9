package com.universidad.seguridad.service;

import com.universidad.seguridad.model.Usuario;
import com.universidad.seguridad.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implementación personalizada de UserDetailsService.
 * Spring Security llama a loadUserByUsername() en cada intento de login
 * para cargar el usuario desde MySQL y verificar el hash BCrypt.
 */
@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository repo;

    public UsuarioDetailsService(UsuarioRepository repo) {
        this.repo = repo;
    }

    /**
     * Carga el usuario desde la base de datos usando el correo como identificador.
     * El campo "username" del formulario de login debe ser el email.
     *
     * @param email dirección de correo enviada en el formulario de login
     * @return UserDetails con email, hash BCrypt y rol
     * @throws UsernameNotFoundException si el correo no existe en la BD
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario u = repo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado con el correo: " + email));

        return User.builder()
                .username(u.getEmail())
                .password(u.getContrasenia())           // Hash BCrypt almacenado en BD
                .roles(u.getRol().replace("ROLE_", "")) // "ADMIN" o "USER"
                .disabled(!u.isActivo())                // cuenta desactivada = no puede logearse
                .build();
    }
}
