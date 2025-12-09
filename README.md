# 🏥 Sistema de Gestión de Laboratorio Clínico (Backend - Spring Boot)

Este proyecto es el *backend* del sistema de gestión para un laboratorio clínico, desarrollado usando Spring Boot 3 y PostgreSQL. Implementa una arquitectura RESTful API para manejar la autenticación, administración de usuarios y la gestión de catálogos internos.

## 🚀 Tecnologías Principales

* **Framework:** Spring Boot 3.5.8
* **Lenguaje:** Java 21
* **Base de Datos:** PostgreSQL 17
* **ORM:** Spring Data JPA / Hibernate
* **Seguridad:** Spring Security (JWT Authentication)
* **Construcción:** Maven

## 📌 Requisitos Funcionales Implementados (RF)

Hasta ahora, se han completado los siguientes módulos principales del rol Administrador:

### ✅ RF-ADM-01: Gestión de Usuarios
* Registro y edición de usuarios (Admin, Médico, Biólogo, etc.).
* Funcionalidad de **Bloqueo/Activación** de cuentas de usuario.
* Mensaje de error claro y personalizado para usuarios bloqueados (Status 400).

### ✅ RF-ADM-03: Gestión de Catálogos
Implementación completa del CRUD (Crear, Leer, Actualizar, Eliminar) para los catálogos fundamentales del laboratorio:
* Áreas de Laboratorio (`/api/catalogs/areas`)
* Unidades de Medida (`/api/catalogs/units`)
* Tipos de Examen (`/api/catalogs/exam-types`)
* Exámenes/Pruebas (`/api/catalogs/exams`) - Incluye manejo de relaciones.

### ✅ RF-ADM-04 (Inicio): Gestión de Datos de Personas
* Gestión CRUD básica de registros de **Pacientes** (`/api/admin/patients`).

---

## ⚙️ Configuración del Entorno

### 1. Requisitos Previos

Asegúrate de tener instalado:
* JDK 17 o superior.
* Maven 3.x.
* Una instancia de **PostgreSQL** corriendo localmente.

### 2. Configuración de la Base de Datos

Modifica el archivo `src/main/resources/application.properties` con tus credenciales de PostgreSQL:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/nombre_de_tu_base_de_datos
spring.datasource.username=tu_usuario_postgres
spring.datasource.password=tu_contraseña_postgres

# Configuración de Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
