package com.universidad.seguridad;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Clase utilitaria para generar el hash BCrypt del administrador.
 *
 * INSTRUCCIONES:
 *  1. Ejecutar este test UNA SOLA VEZ: mvn test -Dtest=GenerarHash
 *  2. Copiar el hash que aparece en la consola (empieza con $2a$12$...)
 *  3. Usarlo en el INSERT de MySQL del Paso 7.
 *  4. Después de insertar el admin, este test ya no es necesario.
 */
@SpringBootTest
class GenerarHash {

    @Autowired
    PasswordEncoder encoder;

    @Test
    void generarHashAdmin() {
        String hashAdmin = encoder.encode("admin123");
        System.out.println("============================================");
        System.out.println("HASH BCrypt para 'admin123':");
        System.out.println(hashAdmin);
        System.out.println("============================================");
        System.out.println("SQL para insertar el admin:");
        System.out.println("INSERT INTO usuarios (nombre, email, contrasenia, rol, activo)");
        System.out.println("VALUES ('Administrador', 'admin@universidad.edu',");
        System.out.println("        '" + hashAdmin + "', 'ROLE_ADMIN', 1);");
        System.out.println("============================================");
    }

    @Test
    void generarHashUsuarioPrueba() {
        String hashUser = encoder.encode("user123");
        System.out.println("============================================");
        System.out.println("HASH BCrypt para 'user123':");
        System.out.println(hashUser);
        System.out.println("============================================");
    }
}
