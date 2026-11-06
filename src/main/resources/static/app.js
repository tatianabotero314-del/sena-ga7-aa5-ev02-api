// Una función compartida reutiliza el mismo manejo de envío, carga y respuesta.
function prepararFormulario(nombre) {
  const formulario = document.getElementById(nombre);
  const salida = document.getElementById(`${nombre}-respuesta`);
  formulario.addEventListener('submit', async (evento) => {
    evento.preventDefault();
    const boton = formulario.querySelector('button');
    boton.disabled = true;
    salida.className = 'respuesta';
    salida.textContent = 'Procesando…';
    const datos = Object.fromEntries(new FormData(formulario));
    try {
      const respuesta = await fetch(`/api/${nombre}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', Accept: 'application/json' },
        body: JSON.stringify(datos),
      });
      const contenido = await respuesta.json();
      salida.className = `respuesta ${respuesta.ok ? 'exito' : 'error'}`;
      const mensaje = document.createElement('p');
      mensaje.textContent = `HTTP ${respuesta.status} · ${contenido.mensaje || 'Respuesta recibida.'}`;
      const detalle = document.createElement('pre');
      // textContent presenta JSON como texto; no ejecuta contenido recibido como HTML.
      detalle.textContent = JSON.stringify(contenido, null, 2);
      salida.replaceChildren(mensaje, detalle);
      if (respuesta.ok) formulario.elements.contrasena.value = '';
    } catch {
      salida.className = 'respuesta error';
      salida.textContent = 'No fue posible conectar con el servicio. Reintenta cuando esté disponible.';
    } finally {
      boton.disabled = false;
    }
  });
}
prepararFormulario('registro');
prepararFormulario('login');
