
package cl.duoc.foro.usuarios.util;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordUtil {

    private static final int ITERACIONES = 210000;
    private static final int LONGITUD = 256;
    private static final SecureRandom RANDOM = new SecureRandom();

    private static byte[] derivarClave(
            String contrasena, byte[] salt, int iteraciones)
            throws GeneralSecurityException {

        PBEKeySpec spec = new PBEKeySpec(
                contrasena.toCharArray(),
                salt,
                iteraciones,
                LONGITUD);

        try {
            return SecretKeyFactory
                    .getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec)
                    .getEncoded();
        } finally {
            spec.clearPassword();
        }
    }

    // Generar hash para guardar en Oracle
    public static String generarHash(String contrasena) {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);

        try {
            byte[] hash = derivarClave(
                    contrasena, salt, ITERACIONES);

            return "pbkdf2$" + ITERACIONES + "$"
                    + Base64.getEncoder().encodeToString(salt)
                    + "$"
                    + Base64.getEncoder().encodeToString(hash);

        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(
                    "Error al generar hash", e);
        }
    }

    // Verificar contraseña durante el login
    public static boolean verificarContrasena(
            String contrasena, String hashAlmacenado) {

        if (contrasena == null || hashAlmacenado == null) {
            return false;
        }

        try {
            String[] partes = hashAlmacenado.split("\\$");

            if (partes.length != 4
                    || !partes[0].equals("pbkdf2")) {
                return false;
            }

            int iteraciones = Integer.parseInt(partes[1]);

            if (iteraciones < 1 || iteraciones > 1000000) {
                return false;
            }

            byte[] salt = Base64.getDecoder().decode(partes[2]);
            byte[] hashOriginal = Base64.getDecoder().decode(partes[3]);

            byte[] hashCalculado = derivarClave(
                    contrasena, salt, iteraciones);

            return MessageDigest.isEqual(
                    hashOriginal, hashCalculado);

        } catch (GeneralSecurityException
                | IllegalArgumentException e) {
            return false;
        }
    }
}
