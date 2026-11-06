package co.sena.servicios;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class ServiciosApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiciosApplication.class, args);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        // BCrypt incorpora una sal aleatoria; nunca se guarda la contraseña original.
        return new BCryptPasswordEncoder(12);
    }
}
