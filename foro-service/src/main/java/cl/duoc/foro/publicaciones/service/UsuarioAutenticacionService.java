
package cl.duoc.foro.publicaciones.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Service
public class UsuarioAutenticacionService {

    private final RestClient cliente = RestClient.create(
            "http://localhost:8081");

    public record UsuarioRemoto(
            Long id,
            String nombre,
            String correo,
            String rol
    ) {}

    public Long obtenerUsuarioId(String authorization) {

        if (authorization == null
                || !authorization.startsWith("Basic ")) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Debes iniciar sesion");
        }

        String credenciales;

        try {
            credenciales = new String(
                    Base64.getDecoder().decode(
                            authorization.substring(6).trim()),
                    StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Autenticacion invalida");
        }

        String[] partes = credenciales.split(":", 2);

        if (partes.length != 2
                || partes[0].isBlank()
                || partes[1].isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Credenciales incompletas");
        }

        try {
            UsuarioRemoto usuario = cliente.post()
                    .uri("/api/usuarios/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "correo", partes[0],
                            "contrasena", partes[1]))
                    .retrieve()
                    .body(UsuarioRemoto.class);

            if (usuario == null || usuario.id() == null) {
                throw new ResponseStatusException(
                        HttpStatus.SERVICE_UNAVAILABLE,
                        "Respuesta invalida de usuarios-service");
            }

            return usuario.id();

        } catch (HttpClientErrorException e) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Credenciales incorrectas");

        } catch (RestClientException e) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo conectar con usuarios-service");
        }
    }
}
