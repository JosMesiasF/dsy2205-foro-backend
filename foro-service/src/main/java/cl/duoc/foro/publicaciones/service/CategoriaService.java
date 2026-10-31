
package cl.duoc.foro.publicaciones.service;

import cl.duoc.foro.publicaciones.model.Categoria;
import cl.duoc.foro.publicaciones.repository.CategoriaRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    // Listar todas las categorias
    public List<Categoria> listarCategorias() {
        return categoriaRepository.findAll();
    }

    // Buscar categoria por ID
    public Optional<Categoria> buscarPorId(Long id) {
        return categoriaRepository.findById(id);
    }
}
