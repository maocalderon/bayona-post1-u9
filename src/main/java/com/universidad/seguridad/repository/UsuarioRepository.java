package com.universidad.seguridad.repository;

import com.universidad.seguridad.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio JPA para la entidad Usuario.
 * Spring Data JPA genera la implementación automáticamente en tiempo de ejecución.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca un usuario por su dirección de correo electrónico.
     * Usado por UserDetailsService para autenticar al usuario.
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Verifica si ya existe un usuario con el correo indicado.
     * Usado en el registro para evitar duplicados.
     */
    boolean existsByEmail(String email);
}
