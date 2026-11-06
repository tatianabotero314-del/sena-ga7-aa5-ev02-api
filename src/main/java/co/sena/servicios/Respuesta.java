package co.sena.servicios;

/** La respuesta pública excluye tanto la contraseña como su hash. */
public record Respuesta(String estado, String mensaje, String usuario) {}
