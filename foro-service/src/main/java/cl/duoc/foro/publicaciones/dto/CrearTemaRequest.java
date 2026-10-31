
package cl.duoc.foro.publicaciones.dto;

public record CrearTemaRequest(
    String titulo,
    String contenido,
    Long categoriaId
) {}
