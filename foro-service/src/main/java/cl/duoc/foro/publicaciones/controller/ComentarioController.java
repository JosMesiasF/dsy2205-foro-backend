
package cl.duoc.foro.publicaciones.controller;

import cl.duoc.foro.publicaciones.dto.ActualizarComentarioRequest;
import cl.duoc.foro.publicaciones.dto.CrearComentarioRequest;
import cl.duoc.foro.publicaciones.model.Comentario;
import cl.duoc.foro.publicaciones.service.ComentarioService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comentarios")
public class ComentarioController {

    private final ComentarioService comentarioService;

    public ComentarioController(ComentarioService comentarioService) {
        this.comentarioService = comentarioService;
    }

    // GET: Listar comentarios
    @GetMapping
    public List<Comentario> listarComentarios(
            @RequestParam(required = false) Long temaId) {

        return comentarioService.listarComentarios(temaId);
    }

    // GET: Obtener comentario por ID
    @GetMapping("/{id}")
    public ResponseEntity<Comentario> obtenerComentario(
            @PathVariable Long id) {

        return comentarioService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST: Crear nuevo comentario
    @PostMapping
    public ResponseEntity<Comentario> crearComentario(
            @RequestBody CrearComentarioRequest datos,
            @RequestHeader(
                    value = "Authorization",
                    required = false) String authorization) {

        Comentario comentario =
                comentarioService.crearComentario(datos, authorization);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(comentario);
    }

    
    // PUT: Actualizar un comentario
    @PutMapping("/{id}")
    public ResponseEntity<Comentario> actualizarComentario(
            @PathVariable Long id,
            @RequestBody ActualizarComentarioRequest datos,
            @RequestHeader(
                    value = "Authorization",
                    required = false) String authorization) {

        Comentario comentario =
                comentarioService.actualizarComentario(
                        id, datos, authorization);

        return ResponseEntity.ok(comentario);
    }

    
    // DELETE: Eliminar comentario por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarComentario(
            @PathVariable Long id,
            @RequestHeader(
                    value = "Authorization",
                    required = false) String authorization) {

        comentarioService.eliminarComentario(
                id, authorization);

        return ResponseEntity.noContent().build();
    }

}
