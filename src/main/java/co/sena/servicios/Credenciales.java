package co.sena.servicios;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Contrato JSON compartido por registro y autenticación. */
public record Credenciales(
    @NotBlank(message = "El usuario es obligatorio.")
    @Size(max = 80, message = "El usuario enviado es demasiado largo.")
    String usuario,

    @NotBlank(message = "La contraseña es obligatoria.")
    @Size(min = 8, max = 64, message = "La contraseña debe tener entre 8 y 64 caracteres.")
    String contrasena
) {}
