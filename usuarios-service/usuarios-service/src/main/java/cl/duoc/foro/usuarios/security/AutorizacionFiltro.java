
package cl.duoc.foro.usuarios.security;

import cl.duoc.foro.usuarios.model.Usuario;
import cl.duoc.foro.usuarios.repository.UsuarioRepository;
import cl.duoc.foro.usuarios.util.PasswordUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Locale;
import java.util.Optional;

@Component
public class AutorizacionFiltro extends OncePerRequestFilter {

    private final UsuarioRepository usuarioRepository;

    public AutorizacionFiltro(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain)
            throws ServletException, IOException {

        String ruta = request.getServletPath();
        String metodo = request.getMethod();

        // Solo proteger las rutas de usuarios
        if (!ruta.equals("/api/usuarios")
                && !ruta.startsWith("/api/usuarios/")) {
            chain.doFilter(request, response);
            return;
        }

        // Permitir registro e inicio de sesion
        if ("POST".equals(metodo)
                && (ruta.equals("/api/usuarios")
                || ruta.equals("/api/usuarios/login"))) {

            chain.doFilter(request, response);
            return;
        }

        // Obtener credenciales HTTP Basic
        String authorization = request.getHeader("Authorization");

        if (authorization == null
                || !authorization.startsWith("Basic ")) {
            noAutorizado(response);
            return;
        }

        try {
            String credenciales = new String(
                    Base64.getDecoder().decode(
                            authorization.substring(6).trim()),
                    StandardCharsets.UTF_8);

            int separador = credenciales.indexOf(':');

            if (separador <= 0) {
                noAutorizado(response);
                return;
            }

            String correo = credenciales.substring(0, separador)
                    .trim().toLowerCase(Locale.ROOT);

            String contrasena = credenciales.substring(separador + 1);

            Optional<Usuario> resultado =
                    usuarioRepository.findByCorreo(correo);

            if (resultado.isEmpty()
                    || !PasswordUtil.verificarContrasena(
                            contrasena,
                            resultado.get().getPasswordHash())) {

                noAutorizado(response);
                return;
            }

            Usuario usuario = resultado.get();

            // Administrador: acceso a todas las operaciones
            if ("ADMIN".equals(usuario.getRol())) {
                chain.doFilter(request, response);
                return;
            }

            // Usuario normal: solo su propio perfil
            if ("USER".equals(usuario.getRol())
                    && ("GET".equals(metodo)
                    || "PUT".equals(metodo))
                    && ruta.matches("/api/usuarios/[0-9]+")) {

                Long idSolicitado = Long.valueOf(
                        ruta.substring("/api/usuarios/".length()));

                if (usuario.getId().equals(idSolicitado)) {
                    chain.doFilter(request, response);
                    return;
                }
            }

            // Acceso denegado por permisos insuficientes
            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "No tienes permisos para esta operacion");

        } catch (IllegalArgumentException e) {
            noAutorizado(response);
        }
    }

    private void noAutorizado(HttpServletResponse response)
            throws IOException {

        response.setHeader(
                "WWW-Authenticate",
                "Basic realm=\"Foro\"");

        response.sendError(
                HttpServletResponse.SC_UNAUTHORIZED,
                "Credenciales incorrectas");
    }
}
