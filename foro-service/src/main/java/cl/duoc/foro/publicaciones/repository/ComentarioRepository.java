
package cl.duoc.foro.publicaciones.repository;

import cl.duoc.foro.publicaciones.model.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComentarioRepository
        extends JpaRepository<Comentario, Long> {

    // Buscar comentarios de un tema
    List<Comentario> findByTemaId(Long temaId);

    // Buscar comentarios de un usuario
    List<Comentario> findByUsuarioId(Long usuarioId);
}
