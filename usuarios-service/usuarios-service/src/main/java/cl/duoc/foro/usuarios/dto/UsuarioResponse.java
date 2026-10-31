
package cl.duoc.foro.usuarios.dto;

import cl.duoc.foro.usuarios.model.Usuario;

public record UsuarioResponse(
    Long id,
    String nombre,
    String correo,
    String rol
) {
    public static UsuarioResponse desdeEntidad(Usuario usuario) {
        return new UsuarioResponse(
            usuario.getId(),
            usuario.getNombre(),
            usuario.getCorreo(),
            usuario.getRol()
        );
    }
}
