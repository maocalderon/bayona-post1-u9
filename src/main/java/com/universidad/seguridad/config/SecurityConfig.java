package com.universidad.seguridad.config;

import com.universidad.seguridad.service.UsuarioDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración central de Spring Security.
 *
 * Responsabilidades:
 *  - Define el PasswordEncoder (BCrypt con factor de coste 12)
 *  - Configura DaoAuthenticationProvider con el UserDetailsService personalizado
 *  - Define las reglas de autorización por URL y por rol
 *  - Configura el formulario de login y logout personalizados
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity   // Habilita @PreAuthorize, @Secured, etc.
public class SecurityConfig {

    /**
     * Bean PasswordEncoder con BCrypt (factor 12).
     * Se inyecta en UsuarioService para hashear contraseñas al registrar,
     * y en DaoAuthenticationProvider para verificar en el login.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * Proveedor de autenticación que consulta la BD a través de UserDetailsService
     * y verifica la contraseña usando BCrypt.
     */
    @Bean
    public DaoAuthenticationProvider authProvider(
            UsuarioDetailsService uds, PasswordEncoder pe) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(uds);
        provider.setPasswordEncoder(pe);
        return provider;
    }

    /**
     * Cadena de filtros de seguridad (reemplaza el WebSecurityConfigurerAdapter
     * depreciado en Spring Security 6).
     *
     * Reglas de acceso:
     *  - "/" , "/login", "/registro", "/css/**", "/js/**" → públicas
     *  - "/admin/**" → solo rol ADMIN
     *  - Cualquier otra ruta → autenticado
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // ── Autorización por URL ────────────────────────────────────────
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/registro",
                                 "/css/**", "/js/**", "/favicon.ico").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )

            // ── Formulario de Login personalizado ───────────────────────────
            .formLogin(form -> form
                .loginPage("/login")                    // GET → muestra el formulario
                .loginProcessingUrl("/login")           // POST → Spring Security procesa
                .defaultSuccessUrl("/dashboard", true)  // éxito → dashboard
                .failureUrl("/login?error=true")        // fallo → login con parámetro error
                .permitAll()
            )

            // ── Logout ─────────────────────────────────────────────────────
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)            // destruye la sesión HTTP
                .deleteCookies("JSESSIONID")            // elimina cookie de sesión
                .permitAll()
            );

        return http.build();
    }
}
