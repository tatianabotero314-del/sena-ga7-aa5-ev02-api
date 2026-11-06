package co.sena.servicios;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
    private final JdbcTemplate db;
    private final PasswordEncoder encoder;
    private final String hashFicticio;

    public AuthService(JdbcTemplate db, PasswordEncoder encoder) {
        this.db = db;
        this.encoder = encoder;
        // También se calcula BCrypt si el usuario no existe; no se omite la comparación.
        this.hashFicticio = encoder.encode(UUID.randomUUID().toString());
    }

    private String validar(Credenciales datos) {
        String usuario = datos.usuario().strip().toLowerCase(Locale.ROOT);
        if (!usuario.matches("[a-z0-9._-]{3,30}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "El usuario debe tener de 3 a 30 letras sin tildes, números, puntos, guiones o guiones bajos.");
        }
        // BCrypt admite como máximo 72 bytes. Se valida antes de codificar, sin truncar.
        if (datos.contrasena().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "La contraseña supera el límite de 72 bytes en UTF-8.");
        }
        return usuario;
    }

    @Transactional
    public Respuesta registrar(Credenciales datos) {
        String usuario = validar(datos);
        String hash = encoder.encode(datos.contrasena());
        // UNIQUE resuelve también dos registros simultáneos del mismo nombre.
        db.update("INSERT INTO usuario(nombre_usuario, password_hash) VALUES (?, ?)", usuario, hash);
        return new Respuesta("registro_exitoso", "Usuario registrado correctamente.", usuario);
    }

    public Respuesta autenticar(Credenciales datos) {
        String usuario = validar(datos);
        var hashes = db.query("SELECT password_hash FROM usuario WHERE nombre_usuario = ?",
            (rs, fila) -> rs.getString("password_hash"), usuario);
        String hash = hashes.isEmpty() ? hashFicticio : hashes.get(0);
        boolean coincide = encoder.matches(datos.contrasena(), hash);
        if (hashes.isEmpty() || !coincide) {
            // El mensaje es el mismo para usuario inexistente y contraseña incorrecta.
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Error en la autenticación.");
        }
        return new Respuesta("autenticacion_satisfactoria", "Autenticación satisfactoria.", usuario);
    }
}
