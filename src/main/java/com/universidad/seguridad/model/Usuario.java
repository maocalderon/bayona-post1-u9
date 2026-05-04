package com.universidad.seguridad.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

/**
 * Entidad que representa a un usuario del sistema.
 * La contraseña NUNCA se almacena en texto claro;
 * siempre se guarda el hash BCrypt generado por BCryptPasswordEncoder.
 */
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;

    @Email(message = "Debe ser un correo electrónico válido")
    @NotBlank(message = "El correo es obligatorio")
    @Column(unique = true, nullable = false, length = 150)
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    @Column(nullable = false)
    private String contrasenia; // Almacena el hash BCrypt — nunca texto claro

    /**
     * Rol del usuario en el sistema.
     * Valores posibles: "ROLE_ADMIN" o "ROLE_USER"
     */
    @Column(nullable = false, length = 20)
    private String rol;

    @Column(nullable = false)
    private boolean activo = true;

    // ── Constructor vacío requerido por JPA ─────────────────────────────────
    public Usuario() {}

    // ── Getters y Setters ────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getContrasenia() { return contrasenia; }
    public void setContrasenia(String contrasenia) { this.contrasenia = contrasenia; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    @Override
    public String toString() {
        return "Usuario{id=" + id + ", nombre='" + nombre + "', email='" + email +
               "', rol='" + rol + "', activo=" + activo + "}";
    }
}
