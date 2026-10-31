
package cl.duoc.foro.publicaciones.controller;

import cl.duoc.foro.publicaciones.dto.ActualizarTemaRequest;
import cl.duoc.foro.publicaciones.dto.CrearTemaRequest;
import cl.duoc.foro.publicaciones.model.Tema;
import cl.duoc.foro.publicaciones.service.TemaService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/temas")
public class TemaController {

    private final TemaService temaService;

    public TemaController(TemaService temaService) {
        this.temaService = temaService;
    }

    // GET: Listar y buscar publicaciones
    @GetMapping
    public List<Tema> listarTemas(
            @RequestParam(required = false) String buscar,
            @RequestParam(required = false) Long categoriaId) {

        return temaService.listarTemas(buscar, categoriaId);
    }

    // GET: Obtener tema por ID
    @GetMapping("/{id}")
    public ResponseEntity<Tema> obtenerTema(
            @PathVariable Long id) {

        return temaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }



    // POST: Publicar un nuevo tema
    @PostMapping
    public ResponseEntity<Tema> crearTema(
            @RequestBody CrearTemaRequest datos,
            @RequestHeader(
                    value = "Authorization",
                    required = false) String authorization) {

        Tema tema = temaService.crearTema(
                datos, authorization);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(tema);
    }

    // PUT: Actualizar publicacion existente
    @PutMapping("/{id}")
    public ResponseEntity<Tema> actualizarTema(
            @PathVariable Long id,
            @RequestBody ActualizarTemaRequest datos,
            @RequestHeader(
                    value = "Authorization",
                    required = false) String authorization) {

        Tema tema = temaService.actualizarTema(
                id, datos, authorization);

        return ResponseEntity.ok(tema);
    }

    
    // DELETE: Eliminar una publicacion por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTema(
            @PathVariable Long id,
            @RequestHeader(
                    value = "Authorization",
                    required = false) String authorization) {

        temaService.eliminarTema(id, authorization);

        return ResponseEntity.noContent().build();
    }


}
