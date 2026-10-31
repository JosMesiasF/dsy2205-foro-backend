
package cl.duoc.foro.publicaciones.repository;

import cl.duoc.foro.publicaciones.model.Tema;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TemaRepository extends JpaRepository<Tema, Long> {

    // Buscar temas por titulo
    List<Tema> findByTituloContainingIgnoreCase(String titulo);

    // Buscar temas por categoria
    List<Tema> findByCategoriaId(Long categoriaId);

    // Buscar por categoria y titulo
    List<Tema> findByCategoriaIdAndTituloContainingIgnoreCase(
            Long categoriaId, String titulo);
}
