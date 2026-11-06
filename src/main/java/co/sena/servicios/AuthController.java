package co.sena.servicios;

import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthController {
    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping(value = "/registro", consumes = "application/json")
    public ResponseEntity<Respuesta> registrar(@Valid @RequestBody Credenciales datos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(datos));
    }

    @PostMapping(value = "/login", consumes = "application/json")
    public Respuesta iniciarSesion(@Valid @RequestBody Credenciales datos) {
        // El alcance de este caso es comprobar credenciales y devolver el resultado.
        // No se emiten tokens ni se concede acceso a los módulos de Recursos Humanos.
        return service.autenticar(datos);
    }

    @GetMapping("/salud")
    public Map<String, String> salud() {
        return Map.of("estado", "disponible");
    }
}
