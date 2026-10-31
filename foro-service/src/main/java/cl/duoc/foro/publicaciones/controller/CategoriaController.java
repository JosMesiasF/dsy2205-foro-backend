
package cl.duoc.foro.publicaciones.controller;

import cl.duoc.foro.publicaciones.model.Categoria;
import cl.duoc.foro.publicaciones.service.CategoriaService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    // GET: Listar todas las categorias
    @GetMapping
    public List<Categoria> listarCategorias() {
        return categoriaService.listarCategorias();
    }

    // GET: Buscar categoria por ID
    @GetMapping("/{id}")
    public ResponseEntity<Categoria> obtenerCategoria(
            @PathVariable Long id) {

        return categoriaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    
}
