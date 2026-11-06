package co.sena.servicios;

import java.util.Map;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ErroresApi {
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<?> duplicado(DuplicateKeyException error) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
            Map.of("estado", "error", "mensaje", "El nombre de usuario ya está registrado."));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> validacion(MethodArgumentNotValidException error) {
        // Se devuelven instrucciones de corrección, nunca los valores rechazados.
        var errores = error.getBindingResult().getFieldErrors().stream()
            .map(e -> e.getField() + ": " + e.getDefaultMessage()).distinct().sorted().toList();
        return ResponseEntity.badRequest().body(Map.of(
            "estado", "error", "mensaje", "Datos inválidos.", "errores", errores));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> jsonInvalido(HttpMessageNotReadableException error) {
        return ResponseEntity.badRequest().body(Map.of(
            "estado", "error", "mensaje", "JSON inválido o campos no permitidos."));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> negocio(ResponseStatusException error) {
        return ResponseEntity.status(error.getStatusCode()).body(Map.of(
            "estado", "error", "mensaje", error.getReason()));
    }
}
