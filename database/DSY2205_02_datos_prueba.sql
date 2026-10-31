
-- DSY2205 - Semana 2
-- Datos de demostracion del Sistema de Foros
--
-- Ejecutar primero DSY2205_01_estructura_bd.sql
-- en una base Oracle nueva con el esquema FORO_APP.
--
-- Cuentas exclusivas para pruebas academicas.
-- PASSWORD_HASH utiliza PBKDF2-HMAC-SHA256
-- con 210000 iteraciones.
--
-- Este script incluye:
-- 3 usuarios, 3 temas y 3 comentarios.
-- Las 5 categorias se crean en el script 01.

SET DEFINE OFF;

-- ============================================
-- 1. USUARIOS DE PRUEBA
-- ============================================

-- ADMIN
-- Correo: admin.foro@example.com
-- Contrasena: AdminDemo2026!

INSERT INTO USUARIOS (
    NOMBRE, CORREO, PASSWORD_HASH, ROL
)
SELECT
    'Administrador Foro',
    'admin.foro@example.com',
    'pbkdf2$210000$L+rw843JybbwhkFEdrDTGA==$jvukPfCIpEnTJps/9oSrz0e9PXexusU/R1RJsXdYF4k=',
    'ADMIN'
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM USUARIOS
    WHERE CORREO = 'admin.foro@example.com'
);

-- USER 1
-- Correo: usuario.actualizado@example.com
-- Contrasena: PruebaForo2026!

INSERT INTO USUARIOS (
    NOMBRE, CORREO, PASSWORD_HASH, ROL
)
SELECT
    'Usuario Actualizado',
    'usuario.actualizado@example.com',
    'pbkdf2$210000$9GmmOIjWawYMhCiENxyGXw==$el2ZPiH6M57C0bxiQ2qGFyjsnnzlmUAUu/1kWFtYu1I=',
    'USER'
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM USUARIOS
    WHERE CORREO = 'usuario.actualizado@example.com'
);

-- USER 2
-- Correo: usuario2@example.com
-- Contrasena: UsuarioDemo2026!

INSERT INTO USUARIOS (
    NOMBRE, CORREO, PASSWORD_HASH, ROL
)
SELECT
    'Usuario Dos',
    'usuario2@example.com',
    'pbkdf2$210000$zwzdUYd+b0O2Z0+5e2Bu6Q==$HZaPcEt7BViLvCKWQ16gChwQ2pSJei0OfWsHPUxP/AU=',
    'USER'
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM USUARIOS
    WHERE CORREO = 'usuario2@example.com'
);


-- ============================================
-- 2. PUBLICACIONES DE PRUEBA
-- ============================================

-- Tema 1 - Tecnologia

INSERT INTO TEMAS (
    TITULO, CONTENIDO, USUARIO_ID, CATEGORIA_ID
)
SELECT
    'DSY2205 - Bienvenida al foro',
    'Este tema presenta nuestro sistema de foros en linea.',
    u.ID,
    c.ID
FROM USUARIOS u
CROSS JOIN CATEGORIAS c
WHERE u.CORREO = 'usuario.actualizado@example.com'
  AND c.NOMBRE = 'Tecnologia'
  AND NOT EXISTS (
      SELECT 1 FROM TEMAS
      WHERE TITULO = 'DSY2205 - Bienvenida al foro'
  );


-- Tema 2 - Videojuegos

INSERT INTO TEMAS (
    TITULO, CONTENIDO, USUARIO_ID, CATEGORIA_ID
)
SELECT
    'DSY2205 - Videojuegos favoritos',
    'Comparte tus videojuegos favoritos y recomendaciones.',
    u.ID,
    c.ID
FROM USUARIOS u
CROSS JOIN CATEGORIAS c
WHERE u.CORREO = 'usuario2@example.com'
  AND c.NOMBRE = 'Videojuegos'
  AND NOT EXISTS (
      SELECT 1 FROM TEMAS
      WHERE TITULO = 'DSY2205 - Videojuegos favoritos'
  );


-- Tema 3 - Educacion

INSERT INTO TEMAS (
    TITULO, CONTENIDO, USUARIO_ID, CATEGORIA_ID
)
SELECT
    'DSY2205 - Aprendiendo Java',
    'Espacio para conversar sobre Java y Spring Boot.',
    u.ID,
    c.ID
FROM USUARIOS u
CROSS JOIN CATEGORIAS c
WHERE u.CORREO = 'admin.foro@example.com'
  AND c.NOMBRE = 'Educacion'
  AND NOT EXISTS (
      SELECT 1 FROM TEMAS
      WHERE TITULO = 'DSY2205 - Aprendiendo Java'
  );


-- ============================================
-- 3. COMENTARIOS DE PRUEBA
-- ============================================

-- Comentario del usuario 2 en el tema 1

INSERT INTO COMENTARIOS (
    CONTENIDO, USUARIO_ID, TEMA_ID
)
SELECT
    'Excelente tema de bienvenida para comenzar a participar.',
    u.ID,
    t.ID
FROM USUARIOS u
CROSS JOIN TEMAS t
WHERE u.CORREO = 'usuario2@example.com'
  AND t.TITULO = 'DSY2205 - Bienvenida al foro'
  AND NOT EXISTS (
      SELECT 1 FROM COMENTARIOS
      WHERE CONTENIDO =
          'Excelente tema de bienvenida para comenzar a participar.'
        AND TEMA_ID = t.ID
  );


-- Comentario del usuario 1 en el tema 2

INSERT INTO COMENTARIOS (
    CONTENIDO, USUARIO_ID, TEMA_ID
)
SELECT
    'Me gustan los juegos de estrategia y aventuras.',
    u.ID,
    t.ID
FROM USUARIOS u
CROSS JOIN TEMAS t
WHERE u.CORREO = 'usuario.actualizado@example.com'
  AND t.TITULO = 'DSY2205 - Videojuegos favoritos'
  AND NOT EXISTS (
      SELECT 1 FROM COMENTARIOS
      WHERE CONTENIDO =
          'Me gustan los juegos de estrategia y aventuras.'
        AND TEMA_ID = t.ID
  );


-- Comentario del administrador en el tema 3

INSERT INTO COMENTARIOS (
    CONTENIDO, USUARIO_ID, TEMA_ID
)
SELECT
    'Spring Boot facilita mucho el desarrollo de APIs REST.',
    u.ID,
    t.ID
FROM USUARIOS u
CROSS JOIN TEMAS t
WHERE u.CORREO = 'admin.foro@example.com'
  AND t.TITULO = 'DSY2205 - Aprendiendo Java'
  AND NOT EXISTS (
      SELECT 1 FROM COMENTARIOS
      WHERE CONTENIDO =
          'Spring Boot facilita mucho el desarrollo de APIs REST.'
        AND TEMA_ID = t.ID
  );


-- ============================================
-- 4. GUARDAR CAMBIOS
-- ============================================

COMMIT;


-- ============================================
-- 5. VERIFICACION DE REGISTROS
-- ============================================

SELECT
    'USUARIOS' AS TABLA,
    COUNT(*) AS TOTAL
FROM USUARIOS

UNION ALL

SELECT
    'CATEGORIAS',
    COUNT(*)
FROM CATEGORIAS

UNION ALL

SELECT
    'TEMAS',
    COUNT(*)
FROM TEMAS

UNION ALL

SELECT
    'COMENTARIOS',
    COUNT(*)
FROM COMENTARIOS;
