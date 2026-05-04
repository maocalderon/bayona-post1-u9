# bayona-post1-u9 — Seguridad en Aplicaciones Web

**Programación Web — Unidad 9 | Ingeniería de Sistemas — UDES 2026**

Sistema de autenticación completo con Spring Security 6, BCryptPasswordEncoder,
UserDetailsService + MySQL y autorización diferenciada por roles ADMIN / USER.

---

##  Requisitos previos

| Herramienta | Versión mínima |
|-------------|---------------|
| Java (JDK)  | 17            |
| Maven       | 3.8+          |
| MySQL       | 8.0+          |
| IntelliJ IDEA / VS Code | Cualquier versión reciente |

---

##  Configuración de MySQL

### 1. Crear base de datos y usuario

```sql
CREATE DATABASE IF NOT EXISTS estudiantes_db
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'appuser'@'localhost' IDENTIFIED BY 'apppassword';
GRANT ALL PRIVILEGES ON estudiantes_db.* TO 'appuser'@'localhost';
FLUSH PRIVILEGES;
```

### 2. La tabla `usuarios` se crea automáticamente

Spring Boot con `spring.jpa.hibernate.ddl-auto=update` crea la tabla
`usuarios` al arrancar la aplicación por primera vez. No es necesario
ejecutar ningún DDL manualmente.

### 3. Insertar el usuario ADMIN

Primero genere el hash BCrypt ejecutando el test:

```bash
mvn test -Dtest=GenerarHash -Dspring.datasource.url=jdbc:mysql://localhost:3306/estudiantes_db
```

Copie el hash que aparece en la consola (empieza con `$2a$12$...`) y ejecute:

```sql
USE estudiantes_db;

INSERT INTO usuarios (nombre, email, contrasenia, rol, activo)
VALUES (
  'Administrador',
  'admin@universidad.edu',
  '$2a$12$[PEGAR_AQUI_EL_HASH_GENERADO]',
  'ROLE_ADMIN',
  1
);
```

> **Nota de seguridad:** La contraseña `admin123` NUNCA se guarda en texto
> claro. MySQL solo almacena el hash BCrypt que empieza con `$2a$12$`.

---

##  Configuración de la aplicación

Edite `src/main/resources/application.properties` si sus credenciales
de MySQL son diferentes:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/estudiantes_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=appuser
spring.datasource.password=apppassword
```

---

##  Ejecutar la aplicación

```bash
# Clonar / descomprimir el proyecto
cd apellido-post1-u9

# Compilar y ejecutar
mvn spring-boot:run

# O compilar primero y luego ejecutar el JAR
mvn clean package -DskipTests
java -jar target/seguridad-0.0.1-SNAPSHOT.jar
```

La aplicación estará disponible en: **http://localhost:8080**

---

## 👤 Usuarios de prueba

| Correo | Contraseña (texto claro) | Rol |
|--------|--------------------------|-----|
| `admin@universidad.edu` | `admin123` | ROLE_ADMIN |
| *(registrar desde /registro)* | *(la que elija)* | ROLE_USER |

> Estos datos son **solo para ambiente de desarrollo/testing**.
> En producción las contraseñas deben ser más robustas y nunca compartidas.

---

##  Rutas de la aplicación

| URL | Acceso | Descripción |
|-----|--------|-------------|
| `/` | Público | Redirige a `/dashboard` |
| `/login` | Público | Formulario de inicio de sesión |
| `/registro` | Público | Registro de nuevo usuario |
| `/dashboard` | Autenticado | Panel principal del usuario |
| `/admin` | Solo ADMIN | Lista y gestión de usuarios |
| `/admin/toggle/{id}` | Solo ADMIN | Activar/desactivar usuario |
| `/logout` | Autenticado | Cierra sesión e invalida JSESSIONID |

---

##  Arquitectura del proyecto

```
src/main/java/com/universidad/seguridad/
├── SeguridadApplication.java          ← Punto de entrada Spring Boot
├── config/
│   └── SecurityConfig.java            ← SecurityFilterChain, BCrypt, DaoAuthProvider
├── controller/
│   ├── AuthController.java            ← login, registro, dashboard
│   └── AdminController.java           ← panel admin (ROLE_ADMIN)
├── model/
│   └── Usuario.java                   ← Entidad JPA (@Entity)
├── repository/
│   └── UsuarioRepository.java         ← JpaRepository + findByEmail
└── service/
    ├── UsuarioService.java            ← Lógica de negocio + BCrypt al registrar
    └── UsuarioDetailsService.java     ← UserDetailsService → consulta MySQL
```

---

## Mecanismos de seguridad implementados

### BCryptPasswordEncoder (factor 12)
```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);
}
```
Las contraseñas se hashean antes de persistir. MySQL solo almacena el hash.

### UserDetailsService personalizado
`UsuarioDetailsService` busca al usuario por email en MySQL y construye
el objeto `UserDetails` con el hash BCrypt y el rol correspondiente.

### Autorización por roles
```
/admin/** → hasRole("ADMIN")   // 403 Forbidden para ROLE_USER
/*        → authenticated()    // 302 redirect a /login si no autenticado
```

### Protección CSRF
Spring Security 6 habilita CSRF por defecto. Thymeleaf inyecta el token
automáticamente en todos los formularios con `th:action`.

### Logout seguro
Al hacer logout: se invalida la `HttpSession`, se elimina la cookie
`JSESSIONID` y se redirige a `/login?logout`.

---

##  Checkpoints de verificación

**Checkpoint 1:** Abrir http://localhost:8080/dashboard sin autenticarse →
Spring Security redirige automáticamente a `/login`.

**Checkpoint 2:** Registrar usuario en `/registro` → verificar en MySQL
que `contrasenia` empieza con `$2a$12$`. Iniciar sesión y acceder a `/admin`
con ese usuario → recibir error **403 Forbidden**.

**Checkpoint 3:** Iniciar sesión como `admin@universidad.edu` → `/admin`
muestra la tabla de usuarios. Cerrar sesión → redirige a `/login?logout`.
Intentar `/dashboard` → redirige a `/login`.

---

## 📸 Capturas de pantalla

*(Incluidas en la carpeta `/capturas` del repositorio)*

1. `01_login.png` — Formulario de login personalizado
![alt text](image.png)
2. `02_registro.png` — Formulario de registro
![alt text](image.png)
3. `03_dashboard_user.png` — Dashboard con rol USER
![alt text](image-1.png)
4. `04_dashboard_admin.png` — Dashboard con rol ADMIN
![alt text](image.png)

---

##  Entregables

-  Repositorio GitHub: `bayona-post1-u9`
-  Mínimo 3 commits descriptivos
-  README con instrucciones completas
-  Capturas de todas las vistas requeridas
-  Código fuente sin contraseñas hardcodeadas

---

##  Tecnologías utilizadas

- Spring Boot 3.2.5
- Spring Security 6
- Spring Data JPA
- Thymeleaf + thymeleaf-extras-springsecurity6
- BCryptPasswordEncoder
- MySQL 8
- Jakarta Validation
- Maven
