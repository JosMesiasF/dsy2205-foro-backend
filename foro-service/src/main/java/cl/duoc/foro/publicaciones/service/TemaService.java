
package cl.duoc.foro.publicaciones.service;

import cl.duoc.foro.publicaciones.dto.ActualizarTemaRequest;
import cl.duoc.foro.publicaciones.dto.CrearTemaRequest;
import cl.duoc.foro.publicaciones.model.Tema;
import cl.duoc.foro.publicaciones.repository.CategoriaRepository;
import cl.duoc.foro.publicaciones.repository.TemaRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class TemaService {

    private final TemaRepository temaRepository;
    private final CategoriaRepository categoriaRepository;
    private final UsuarioAutenticacionService autenticacionService;

    public TemaService(
            TemaRepository temaRepository,
            CategoriaRepository categoriaRepository,
            UsuarioAutenticacionService autenticacionService) {

        this.temaRepository = temaRepository;
        this.categoriaRepository = categoriaRepository;
        this.autenticacionService = autenticacionService;
    }

    // Listar y buscar temas
    public List<Tema> listarTemas(
            String buscar, Long categoriaId) {

        boolean tieneBusqueda =
                buscar != null && !buscar.isBlank();

        if (categoriaId != null && tieneBusqueda) {
            return temaRepository
                    .findByCategoriaIdAndTituloContainingIgnoreCase(
                            categoriaId, buscar.trim());
        }

        if (categoriaId != null) {
            return temaRepository.findByCategoriaId(categoriaId);
        }

        if (tieneBusqueda) {
            return temaRepository
                    .findByTituloContainingIgnoreCase(buscar.trim());
        }

        return temaRepository.findAll();
    }

    // Buscar un tema por ID
    public Optional<Tema> buscarPorId(Long id) {
        return temaRepository.findById(id);
    }

    // Publicar un nuevo tema
    public Tema crearTema(
            CrearTemaRequest datos, String authorization) {

        // Comprobar quien esta publicando
        Long usuarioId =
                autenticacionService.obtenerUsuarioId(authorization);

        // Validar titulo
        if (datos == null
                || datos.titulo() == null
                || datos.titulo().isBlank()
                || datos.titulo().trim().length() > 200) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Titulo no valido");
        }

        // Validar contenido
        if (datos.contenido() == null
                || datos.contenido().isBlank()
                || datos.contenido().length() > 2000) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Contenido no valido");
        }

        // Validar categoria
        if (datos.categoriaId() == null
                || !categoriaRepository.existsById(
                        datos.categoriaId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Categoria no valida");
        }

        // Crear publicacion
        Tema tema = new Tema();
        tema.setTitulo(datos.titulo().trim());
        tema.setContenido(datos.contenido().trim());
        tema.setCategoriaId(datos.categoriaId());
        tema.setUsuarioId(usuarioId);

        return temaRepository.save(tema);
    }

    // PUT: Actualizar una publicacion
    public Tema actualizarTema(
            Long id,
            ActualizarTemaRequest datos,
            String authorization) {

        // Identificar al usuario autenticado
        Long usuarioId =
                autenticacionService.obtenerUsuarioId(authorization);

        // Buscar la publicacion
        Tema tema = temaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Tema no encontrado"));

        // Verificar que sea el autor
        if (!tema.getUsuarioId().equals(usuarioId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No puedes modificar temas de otro usuario");
        }

        // Validar titulo
        if (datos == null
                || datos.titulo() == null
                || datos.titulo().isBlank()
                || datos.titulo().trim().length() > 200) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Titulo no valido");
        }

        // Validar contenido
        if (datos.contenido() == null
                || datos.contenido().isBlank()
                || datos.contenido().trim().length() > 2000) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Contenido no valido");
        }

        // Validar categoria
        if (datos.categoriaId() == null
                || !categoriaRepository.existsById(
                        datos.categoriaId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Categoria no valida");
        }

        // Actualizar los datos
        tema.setTitulo(datos.titulo().trim());
        tema.setContenido(datos.contenido().trim());
        tema.setCategoriaId(datos.categoriaId());

        return temaRepository.save(tema);
    }

    
    // DELETE: Eliminar una publicacion
    public void eliminarTema(
            Long id,
            String authorization) {

        // Identificar al usuario autenticado
        Long usuarioId =
                autenticacionService.obtenerUsuarioId(authorization);

        // Buscar el tema
        Tema tema = temaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Tema no encontrado"));

        // Verificar que sea el autor
        if (!tema.getUsuarioId().equals(usuarioId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No puedes eliminar temas de otro usuario");
        }

        // Eliminar de Oracle
        temaRepository.delete(tema);
    }


}
