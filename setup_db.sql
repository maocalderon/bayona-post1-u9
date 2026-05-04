-- ============================================================
--  Script de configuración de base de datos
--  Unidad 9: Seguridad Web — UDES 2026
--  Ejecutar ANTES de arrancar la aplicación por primera vez
-- ============================================================

-- 1. Crear base de datos
CREATE DATABASE IF NOT EXISTS estudiantes_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- 2. Usar la base de datos
USE estudiantes_db;

-- 3. Crear usuario de aplicación (si no existe)
--    NOTA: Ejecutar como root de MySQL
CREATE USER IF NOT EXISTS 'appuser'@'localhost' IDENTIFIED BY 'apppassword';
GRANT ALL PRIVILEGES ON estudiantes_db.* TO 'appuser'@'localhost';
FLUSH PRIVILEGES;

-- ============================================================
--  NOTA IMPORTANTE:
--  La tabla "usuarios" se crea automáticamente por Hibernate
--  (spring.jpa.hibernate.ddl-auto=update) al arrancar la app.
--  Solo después de arrancar, ejecutar el INSERT del admin:
-- ============================================================

-- 4. Insertar usuario ADMIN (ejecutar DESPUÉS de arrancar la app
--    y de generar el hash con el test GenerarHash.java)
--
--  REEMPLAZAR $2a$12$HASH_GENERADO con el hash real del test.
--
-- INSERT INTO usuarios (nombre, email, contrasenia, rol, activo)
-- VALUES (
--   'Administrador',
--   'admin@universidad.edu',
--   '$2a$12$REEMPLAZAR_CON_HASH_GENERADO_POR_GenerarHash_java',
--   'ROLE_ADMIN',
--   1
-- );

-- ============================================================
--  Verificación: ver contraseñas hasheadas (checkpoint 2)
-- ============================================================
-- SELECT id, nombre, email, LEFT(contrasenia, 10) AS hash_inicio,
--        rol, activo FROM usuarios;
--
--  El campo contrasenia debe comenzar siempre con: $2a$12$
-- ============================================================
