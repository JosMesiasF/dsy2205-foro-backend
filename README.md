
# Sistema de Foros en Línea

## Desarrollo Full Stack III - DSY2205
### Evaluación Sumativa - Semana 2

Proyecto backend desarrollado mediante una arquitectura de
microservicios REST con Spring Boot y Oracle Database.

## 1. Descripción

El Sistema de Foros en Línea permite gestionar usuarios,
publicaciones y comentarios, organizados en distintas
categorías.

El sistema contempla dos roles de usuario: ADMIN y USER.

## 2. Tecnologías utilizadas

- Java 21
- Spring Boot
- Spring Web MVC
- Spring Data JPA
- Maven
- Oracle Database
- Docker Desktop
- Postman
- DBeaver
- Git y GitHub

## 3. Arquitectura

El sistema está dividido en dos microservicios:

### usuarios-service

Puerto: 8081

Funcionalidades:
- Registro de usuarios.
- Inicio de sesión.
- Consulta de usuarios.
- Actualización de usuarios.
- Eliminación de usuarios.
- Control de acceso según rol.

### foro-service

Puerto: 8083

Funcionalidades:
- Consulta de categorías.
- Publicación de temas.
- Búsqueda y consulta de temas.
- Actualización y eliminación de temas.
- Registro y consulta de comentarios.
- Actualización y eliminación de comentarios.

El microservicio foro-service se comunica con
usuarios-service para validar la identidad de los autores.

## 4. Base de datos

Motor: Oracle Database

Servicio: FREEPDB1

Usuario de aplicación: FORO_APP

Tablas principales:
- USUARIOS
- CATEGORIAS
- TEMAS
- COMENTARIOS

El archivo database/DSY2205_01_estructura_bd.sql
contiene la estructura de las tablas y las cinco
categorías iniciales.

El script debe ejecutarse una vez sobre un esquema
FORO_APP previamente creado y con los permisos necesarios.


### Instalación de la base de datos

Para preparar la base Oracle desde cero:

1. Crear el usuario/esquema FORO_APP con los permisos necesarios.
2. Conectarse a Oracle utilizando FORO_APP.
3. Ejecutar database/DSY2205_01_estructura_bd.sql
   para crear las cuatro tablas y las cinco categorías.
4. Ejecutar database/DSY2205_02_datos_prueba.sql
   para insertar tres usuarios, tres temas y tres comentarios.
5. Configurar la variable de entorno FORO_DB_PASSWORD
   antes de iniciar cada microservicio.

IMPORTANTE:
Los scripts 01 y 02 están destinados a una instalación nueva.
No se debe volver a ejecutar el script 01 sobre una base
que ya contiene las tablas.


## 5. Configuración

Requisitos:
- JDK compatible con Java 21.
- Oracle Database disponible en localhost:1521.
- Esquema FORO_APP creado.
- Contraseña de Oracle configurada como variable de entorno.

Los microservicios utilizan la variable:

FORO_DB_PASSWORD

No se almacenan contraseñas de conexión en el repositorio.

## 6. Ejecución

Abrir dos terminales independientes.

### Terminal 1 - Usuarios

Ubicarse en:

usuarios-service/usuarios-service

Configurar FORO_DB_PASSWORD y ejecutar:

.\mvnw.cmd spring-boot:run

Disponible en:

http://localhost:8081

### Terminal 2 - Foro

Ubicarse en:

foro-service

Configurar FORO_DB_PASSWORD y ejecutar:

.\mvnw.cmd spring-boot:run

Disponible en:

http://localhost:8083

Ambos microservicios deben estar activos para
realizar publicaciones y comentarios autenticados.

## 7. Endpoints REST

### Usuarios - puerto 8081

GET    /api/usuarios
GET    /api/usuarios/{id}
POST   /api/usuarios
PUT    /api/usuarios/{id}
DELETE /api/usuarios/{id}
POST   /api/usuarios/login

### Categorías - puerto 8083

GET    /api/categorias
GET    /api/categorias/{id}

### Temas - puerto 8083

GET    /api/temas
GET    /api/temas/{id}
GET    /api/temas?buscar=Java
GET    /api/temas?categoriaId=1
POST   /api/temas
PUT    /api/temas/{id}
DELETE /api/temas/{id}

### Comentarios - puerto 8083

GET    /api/comentarios
GET    /api/comentarios/{id}
GET    /api/comentarios?temaId=1
POST   /api/comentarios
PUT    /api/comentarios/{id}
DELETE /api/comentarios/{id}

## 8. Autenticación y permisos

El sistema utiliza autenticación HTTP Basic
para operaciones protegidas.

Las contraseñas de usuarios se almacenan mediante
hash PBKDF2 con salt.

Los usuarios normales pueden administrar sus
propias publicaciones y comentarios.

El servicio de usuarios distingue los roles
ADMIN y USER para controlar el acceso.

La implementación está destinada a una demostración
académica en un entorno local.

## 9. Pruebas

Las pruebas funcionales fueron realizadas con Postman.

Se comprobaron:
- Respuestas GET, POST, PUT y DELETE.
- Registro e inicio de sesión.
- Búsquedas de publicaciones.
- Validación de categorías.
- Autorización por usuario.
- Respuestas HTTP 200, 201, 204, 400, 401, 403 y 404.

La persistencia se verificó mediante consultas SQL
en DBeaver sobre Oracle Database.

## 10. Repositorio

https://github.com/JosMesiasF/dsy2205-foro-backend
