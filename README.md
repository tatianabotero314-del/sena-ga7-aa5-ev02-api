# API - GA7-220501096-AA5-EV02
Laura Tatiana Ciro Botero · SENA · Ficha 3235898 · 2026.

Pruebas de los servicios de registro e inicio de sesión construidos en AA5-EV01. El código Java conserva sus reglas y comentarios.

## Ejecutar
Java 17+ y Maven 3.9+: `mvn spring-boot:run`. Abrir http://127.0.0.1:8080.
Para compilar: `mvn clean verify`; iniciar con `java -jar target/servicios-registro-login-1.0.0.jar`.
La base H2 de práctica persiste en data. Las pruebas automáticas usan una base en memoria independiente.

## Postman
Instalar Postman, importar `postman/AA5_EV02.postman_collection.json` y ejecutar las diez peticiones en orden. La variable baseUrl apunta a http://127.0.0.1:8080. El registro genera un usuario nuevo en cada ejecución. Scripts de Post-response verifican estado HTTP, mensaje y ausencia de claves/hash. El resultado esperado es 30 verificaciones aprobadas.

## Verificación adicional
GitHub Actions compila con Maven, inicia Spring Boot y ejecuta la misma colección mediante Newman, el ejecutor de colecciones Postman. El reporte automático complementa las capturas y el video de Postman de escritorio.

## Endpoints
POST /api/registro; POST /api/login; GET /api/salud. JSON con usuario y contrasena. Registro: 201,400,409. Login: 200,400,401.
El caso confirma las credenciales, no crea sesiones ni tokens. No usar cuentas reales para las demostraciones.

Repositorio: https://github.com/tatianabotero314-del/sena-ga7-aa5-ev02-api
