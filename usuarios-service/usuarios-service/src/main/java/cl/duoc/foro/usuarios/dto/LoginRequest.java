
package cl.duoc.foro.usuarios.dto;

public record LoginRequest(
    String correo,
    String contrasena
) {}
