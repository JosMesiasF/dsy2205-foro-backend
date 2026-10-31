
package cl.duoc.foro.publicaciones.service;

import cl.duoc.foro.publicaciones.dto.ActualizarComentarioRequest;
import cl.duoc.foro.publicaciones.dto.CrearComentarioRequest;
import cl.duoc.foro.publicaciones.model.Comentario;
import cl.duoc.foro.publicaciones.repository.ComentarioRepository;
import cl.duoc.foro.publicaciones.repository.TemaRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class ComentarioService {

    private final ComentarioRepository comentarioRepository;
    private final TemaRepository temaRepository;
    private final UsuarioAutenticacionService autenticacionService;

    public ComentarioService(
            ComentarioRepository comentarioRepository,
            TemaRepository temaRepository,
            UsuarioAutenticacionService autenticacionService) {

        this.comentarioRepository = comentarioRepository;
        this.temaRepository = temaRepository;
        this.autenticacionService = autenticacionService;
    }

    // GET: Listar todos los comentarios o filtrar por tema
    public List<Comentario> listarComentarios(Long temaId) {

        if (temaId != null) {
            return comentarioRepository.findByTemaId(temaId);
        }

        return comentarioRepository.findAll();
    }

    // GET: Buscar comentario por ID
    public Optional<Comentario> buscarPorId(Long id) {
        return comentarioRepository.findById(id);
    }

    // POST: Crear comentario
    public Comentario crearComentario(
            CrearComentarioRequest datos,
            String authorization) {

        // Identificar usuario autenticado
        Long usuarioId =
                autenticacionService.obtenerUsuarioId(authorization);

        // Validar datos recibidos
        if (datos == null
                || datos.contenido() == null
                || datos.contenido().isBlank()
                || datos.contenido().trim().length() > 2000) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Contenido del comentario no valido");
        }

        // Validar que exista el tema
        if (datos.temaId() == null
                || !temaRepository.existsById(datos.temaId())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El tema indicado no existe");
        }

        // Crear comentario
        Comentario comentario = new Comentario();
        comentario.setContenido(datos.contenido().trim());
        comentario.setTemaId(datos.temaId());
        comentario.setUsuarioId(usuarioId);

        // Guardar en Oracle
        return comentarioRepository.save(comentario);
    }

    
    // PUT: Actualizar un comentario
    public Comentario actualizarComentario(
            Long id,
            ActualizarComentarioRequest datos,
            String authorization) {

        // Identificar al usuario autenticado
        Long usuarioId =
                autenticacionService.obtenerUsuarioId(authorization);

        // Buscar el comentario
        Comentario comentario = comentarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Comentario no encontrado"));

        // Verificar que el usuario sea el autor
        if (!comentario.getUsuarioId().equals(usuarioId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No puedes modificar comentarios de otro usuario");
        }

        // Validar el contenido
        if (datos == null
                || datos.contenido() == null
                || datos.contenido().isBlank()
                || datos.contenido().trim().length() > 2000) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Contenido no valido");
        }

        // Actualizar comentario
        comentario.setContenido(datos.contenido().trim());

        return comentarioRepository.save(comentario);
    }

    
    // DELETE: Eliminar comentario por ID
    public void eliminarComentario(
            Long id,
            String authorization) {

        // Identificar usuario autenticado
        Long usuarioId =
                autenticacionService.obtenerUsuarioId(authorization);

        // Buscar comentario
        Comentario comentario = comentarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Comentario no encontrado"));

        // Validar que sea el autor
        if (!comentario.getUsuarioId().equals(usuarioId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No puedes eliminar comentarios de otro usuario");
        }

        // Eliminar comentario de Oracle
        comentarioRepository.delete(comentario);
    }

}
