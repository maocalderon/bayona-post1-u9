package com.universidad.seguridad.service;

import com.universidad.seguridad.model.Usuario;
import com.universidad.seguridad.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio de negocio para la gestión de usuarios.
 * Aplica BCrypt al registrar para que la contraseña NUNCA se guarde en texto claro.
 */
@Service
public class UsuarioService {

    private final UsuarioRepository repo;
    private final PasswordEncoder encoder;

    // Inyección por constructor (buena práctica: permite mockear en tests)
    public UsuarioService(UsuarioRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    /**
     * Registra un nuevo usuario en la base de datos.
     * - Verifica que el correo no exista previamente.
     * - Hashea la contraseña con BCrypt (factor de coste 12).
     * - Asigna el rol ROLE_USER por defecto.
     *
     * @param usuario objeto con los datos del formulario (contraseña en texto claro)
     * @throws RuntimeException si el correo ya está registrado
     */
    @Transactional
    public void registrar(Usuario usuario) {
        if (repo.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("El correo ya está registrado");
        }
        // Hash BCrypt — a partir de aquí la contraseña en texto claro ya no existe
        usuario.setContrasenia(encoder.encode(usuario.getContrasenia()));
        usuario.setRol("ROLE_USER"); // rol por defecto para nuevos registros
        usuario.setActivo(true);
        repo.save(usuario);
    }

    /**
     * Retorna la lista completa de usuarios (uso exclusivo del panel ADMIN).
     */
    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return repo.findAll();
    }

    /**
     * Busca un usuario por su ID.
     */
    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + id));
    }

    /**
     * Activa o desactiva un usuario (útil para el panel ADMIN).
     */
    @Transactional
    public void toggleActivo(Long id) {
        Usuario u = buscarPorId(id);
        u.setActivo(!u.isActivo());
        repo.save(u);
    }
}
