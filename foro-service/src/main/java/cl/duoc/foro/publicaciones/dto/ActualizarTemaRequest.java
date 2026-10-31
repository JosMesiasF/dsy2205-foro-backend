
package cl.duoc.foro.publicaciones.dto;

public record ActualizarTemaRequest(
    String titulo,
    String contenido,
    Long categoriaId
) {}
