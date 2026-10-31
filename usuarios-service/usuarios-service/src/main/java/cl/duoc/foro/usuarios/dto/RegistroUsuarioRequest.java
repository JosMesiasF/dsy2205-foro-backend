package cl.duoc.foro.usuarios.dto;

public record RegistroUsuarioRequest(
    String nombre,
    String correo,
    String contrasena
) {}