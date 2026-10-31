
package cl.duoc.foro.usuarios.service;

import cl.duoc.foro.usuarios.dto.ActualizarUsuarioRequest;
import cl.duoc.foro.usuarios.dto.LoginRequest;
import cl.duoc.foro.usuarios.dto.RegistroUsuarioRequest;
import cl.duoc.foro.usuarios.model.Usuario;
import cl.duoc.foro.usuarios.repository.UsuarioRepository;
import cl.duoc.foro.usuarios.util.PasswordUtil;


import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // Consultar todos los usuarios
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    // Buscar un usuario por ID
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    // Buscar un usuario por correo
    public Optional<Usuario> buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    // Verificar si el correo ya existe
    public boolean existeCorreo(String correo) {
        return usuarioRepository.existsByCorreo(correo);
    }

    // Registrar nuevo usuario
    public Usuario registrarUsuario(RegistroUsuarioRequest datos) {

        // Validar nombre
        if (datos.nombre() == null
                || datos.nombre().isBlank()
                || datos.nombre().trim().length() > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Nombre no valido");
        }

        // Validar correo
        if (datos.correo() == null
                || datos.correo().isBlank()
                || datos.correo().trim().length() > 150) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Correo no valido");
        }

        // Validar contraseña
        if (datos.contrasena() == null
                || datos.contrasena().length() < 8) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La contraseña requiere al menos 8 caracteres");
        }

        // Normalizar correo
        String correo = datos.correo()
                .trim()
                .toLowerCase(Locale.ROOT);

        // Verificar correo duplicado
        if (usuarioRepository.existsByCorreo(correo)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El correo ya esta registrado");
        }

        // Crear usuario
        Usuario usuario = new Usuario();
        usuario.setNombre(datos.nombre().trim());
        usuario.setCorreo(correo);
        usuario.setPasswordHash(
                PasswordUtil.generarHash(datos.contrasena()));
        usuario.setRol("USER");

        // Guardar en Oracle
        return usuarioRepository.save(usuario);
    }

    
    // Actualizar usuario existente
    public Usuario actualizarUsuario(
            Long id, ActualizarUsuarioRequest datos) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario no encontrado"));

        if (datos == null
                || datos.nombre() == null
                || datos.nombre().isBlank()
                || datos.nombre().trim().length() > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Nombre no valido");
        }

        if (datos.correo() == null
                || datos.correo().isBlank()
                || datos.correo().trim().length() > 150) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Correo no valido");
        }

        String correo = datos.correo()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (!usuario.getCorreo().equals(correo)
                && usuarioRepository.existsByCorreo(correo)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El correo ya esta registrado");
        }

        usuario.setNombre(datos.nombre().trim());
        usuario.setCorreo(correo);

        return usuarioRepository.save(usuario);
    }


    // Eliminar un usuario por ID
    public void eliminarUsuario(Long id) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario no encontrado"));

        usuarioRepository.delete(usuario);
    }

    
    // Validar credenciales de inicio de sesión
    public Usuario iniciarSesion(LoginRequest datos) {

        if (datos == null
                || datos.correo() == null
                || datos.correo().isBlank()
                || datos.contrasena() == null
                || datos.contrasena().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Correo y contraseña son obligatorios");
        }

        String correo = datos.correo()
                .trim()
                .toLowerCase(Locale.ROOT);

        Usuario usuario = usuarioRepository
                .findByCorreo(correo)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Credenciales incorrectas"));

        boolean valida = PasswordUtil.verificarContrasena(
                datos.contrasena(),
                usuario.getPasswordHash());

        if (!valida) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Credenciales incorrectas");
        }

        return usuario;
    }


}
