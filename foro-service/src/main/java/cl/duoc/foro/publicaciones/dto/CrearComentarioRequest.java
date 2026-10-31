
package cl.duoc.foro.publicaciones.dto;

public record CrearComentarioRequest(
    String contenido,
    Long temaId
) {}
