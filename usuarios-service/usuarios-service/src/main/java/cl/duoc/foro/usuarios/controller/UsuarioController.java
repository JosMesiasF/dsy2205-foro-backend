
package cl.duoc.foro.usuarios.controller;

import cl.duoc.foro.usuarios.dto.ActualizarUsuarioRequest;
import cl.duoc.foro.usuarios.dto.LoginRequest;
import cl.duoc.foro.usuarios.dto.RegistroUsuarioRequest;
import cl.duoc.foro.usuarios.dto.UsuarioResponse;
import cl.duoc.foro.usuarios.service.UsuarioService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // GET: Obtener todos los usuarios
    @GetMapping
    public List<UsuarioResponse> listarUsuarios() {
        return usuarioService.listarUsuarios()
                .stream()
                .map(UsuarioResponse::desdeEntidad)
                .toList();
    }

    // GET: Obtener un usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> obtenerUsuario(
            @PathVariable Long id) {

        return usuarioService.buscarPorId(id)
                .map(usuario -> ResponseEntity.ok(
                        UsuarioResponse.desdeEntidad(usuario)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST: Registrar un nuevo usuario
    @PostMapping
    public ResponseEntity<UsuarioResponse> registrarUsuario(
            @RequestBody RegistroUsuarioRequest datos) {

        var usuario = usuarioService.registrarUsuario(datos);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(UsuarioResponse.desdeEntidad(usuario));
    }

    
    // PUT: Actualizar un usuario por ID
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> actualizarUsuario(
            @PathVariable Long id,
            @RequestBody ActualizarUsuarioRequest datos) {

        var usuario = usuarioService.actualizarUsuario(id, datos);

        return ResponseEntity.ok(
                UsuarioResponse.desdeEntidad(usuario));
    }

    
    // DELETE: Eliminar usuario por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(
            @PathVariable Long id) {

        usuarioService.eliminarUsuario(id);

        return ResponseEntity.noContent().build();
    }



    // POST: Validar inicio de sesión
    @PostMapping("/login")
    public ResponseEntity<UsuarioResponse> iniciarSesion(
            @RequestBody LoginRequest datos) {

        var usuario = usuarioService.iniciarSesion(datos);

        return ResponseEntity.ok(
                UsuarioResponse.desdeEntidad(usuario));
    }


}
