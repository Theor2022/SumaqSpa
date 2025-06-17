document.addEventListener("DOMContentLoaded", () => {
    const carrito = JSON.parse(sessionStorage.getItem('carrito')) || [];
    const contenedorTratamientos = document.getElementById("selected-treatments");
    const inputTratamiento = document.getElementById("tratamiento");
    const inputTotal = document.getElementById("total");

    let total = 0;
    let nombres = [];

    carrito.forEach(item => {
        const p = document.createElement('p');
        p.textContent = `${item.nombre} - $${item.precio.toFixed(2)}`;
        contenedorTratamientos?.appendChild(p);
        total += item.precio;
        nombres.push(item.nombre);
    });

    if (inputTratamiento) inputTratamiento.value = nombres.join(', ');
    if (inputTotal) inputTotal.value = total.toFixed(2);

    const formulario = document.getElementById("formulario");
    if (!formulario) {
        console.error("No se encontró el formulario con id 'formulario'.");
        return;
    }

    formulario.addEventListener("submit", async (e) => {
        e.preventDefault();

        if (!validarFormulario()) return;

        const data = {
            nombre: e.target.nombre.value,
            correo: e.target.correo.value,
            telefono: e.target.telefono.value,
            fecha: e.target.fecha.value,
            hora: e.target.hora.value,
            tratamiento: e.target.tratamiento.value,
            total: parseFloat(e.target.total.value)
        };

        try {
            const response = await fetch("http://localhost:8080/api/reservas", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(data)
            });

            if (response.ok) {
                alert("¡Reserva realizada con éxito!");
                sessionStorage.removeItem('carrito');
                window.location.href = "confirmacion.html";
            } else {
                const errores = await response.json();
                alert("Error en la reserva:\n" + JSON.stringify(errores, null, 2));
            }
        } catch (error) {
            console.error("Error de red:", error);
            alert("Error al conectar con el servidor.");
        }
    });
});
