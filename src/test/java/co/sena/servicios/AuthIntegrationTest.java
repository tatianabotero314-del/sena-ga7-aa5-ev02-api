package co.sena.servicios;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:pruebas;DB_CLOSE_DELAY=-1")
@AutoConfigureMockMvc
class AuthIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Autowired JdbcTemplate db;
    @Autowired PasswordEncoder encoder;

    @BeforeEach
    void limpiarBaseAislada() {
        // Solo se limpia la base en memoria exclusiva de estas pruebas.
        db.update("DELETE FROM usuario");
    }

    private ResultActions enviar(String ruta, String usuario, String clave) throws Exception {
        return mvc.perform(post(ruta).contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(Map.of("usuario", usuario, "contrasena", clave))));
    }

    private void registrar() throws Exception {
        enviar("/api/registro", "tatiana.demo", "Practica2026!").andExpect(status().isCreated());
    }

    @Test void registroCorrecto() throws Exception {
        enviar("/api/registro", "tatiana.demo", "Practica2026!")
            .andExpect(status().isCreated()).andExpect(jsonPath("$.usuario").value("tatiana.demo"))
            .andExpect(jsonPath("$.mensaje").value("Usuario registrado correctamente."));
    }
    @Test void autenticacionCorrecta() throws Exception {
        registrar();
        enviar("/api/login", "tatiana.demo", "Practica2026!").andExpect(status().isOk())
            .andExpect(jsonPath("$.mensaje").value("Autenticación satisfactoria."));
    }
    @Test void claveIncorrecta() throws Exception {
        registrar();
        enviar("/api/login", "tatiana.demo", "Incorrecta2026!").andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.mensaje").value("Error en la autenticación."));
    }
    @Test void usuarioInexistenteMismoError() throws Exception {
        enviar("/api/login", "nadie.demo", "Practica2026!").andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.mensaje").value("Error en la autenticación."));
    }
    @Test void duplicadoNormalizado() throws Exception {
        registrar();
        enviar("/api/registro", " TATIANA.DEMO ", "Practica2026!").andExpect(status().isConflict());
    }
    @Test void usuarioVacio() throws Exception {
        enviar("/api/registro", "", "Practica2026!").andExpect(status().isBadRequest());
    }
    @Test void claveCorta() throws Exception {
        enviar("/api/registro", "tatiana.demo", "123").andExpect(status().isBadRequest());
    }
    @Test void usuarioConCaracteresInvalidos() throws Exception {
        enviar("/api/registro", "usuario' OR 1=1", "Practica2026!").andExpect(status().isBadRequest());
    }
    @Test void jsonMalFormado() throws Exception {
        mvc.perform(post("/api/registro").contentType(MediaType.APPLICATION_JSON).content("{mal"))
            .andExpect(status().isBadRequest());
    }
    @Test void noAceptaCamposAdicionales() throws Exception {
        mvc.perform(post("/api/registro").contentType(MediaType.APPLICATION_JSON)
            .content("{\"usuario\":\"tatiana.demo\",\"contrasena\":\"Practica2026!\",\"rol\":\"ADMIN\"}"))
            .andExpect(status().isBadRequest());
    }
    @Test void limiteUtf8SinTruncamiento() throws Exception {
        enviar("/api/registro", "tatiana.demo", "á".repeat(40)).andExpect(status().isBadRequest());
    }
    @Test void almacenamientoProtegido() throws Exception {
        registrar();
        String hash = db.queryForObject("SELECT password_hash FROM usuario", String.class);
        assertNotEquals("Practica2026!", hash);
        assertTrue(encoder.matches("Practica2026!", hash));
        enviar("/api/login", "tatiana.demo", "Practica2026!").andExpect(status().isOk())
            .andExpect(content().string(not(containsString("Practica2026!"))))
            .andExpect(jsonPath("$.password_hash").doesNotExist());
    }
    @Test void loginVacio() throws Exception {
        enviar("/api/login", "", "").andExpect(status().isBadRequest());
    }
    @Test void usuarioNoDistingueMayusculas() throws Exception {
        registrar();
        enviar("/api/login", " TATIANA.DEMO ", "Practica2026!").andExpect(status().isOk());
    }
    @Test void claveConEspaciosSeConserva() throws Exception {
        enviar("/api/registro", "espacios.demo", " Clave2026! ").andExpect(status().isCreated());
        enviar("/api/login", "espacios.demo", " Clave2026! ").andExpect(status().isOk());
        enviar("/api/login", "espacios.demo", "Clave2026!").andExpect(status().isUnauthorized());
    }
}
