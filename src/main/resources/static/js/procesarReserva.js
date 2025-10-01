document.addEventListener("DOMContentLoaded", () => {
    let carrito = JSON.parse(sessionStorage.getItem('carrito')) || [];
    const contenedorTratamientos = document.getElementById("cart-items"); // tu ul
    const inputTratamiento = document.getElementById("tratamiento");
    const inputTotal = document.getElementById("total");

    const renderCarrito = () => {
        contenedorTratamientos.innerHTML = "";
        let total = 0;
        let nombres = [];

        carrito.forEach((item, index) => {
            const li = document.createElement("li");
            li.className = "d-flex justify-content-between align-items-center mb-1";

            const span = document.createElement("span");
            span.textContent = `${item.nombre} - $${item.precio.toFixed(2)}`;

            const btn = document.createElement("button");
            btn.textContent = "✖";
            btn.className = "btn btn-sm btn-danger ms-2";
            btn.addEventListener("click", () => {
                carrito.splice(index, 1); // elimina del carrito
                sessionStorage.setItem('carrito', JSON.stringify(carrito));
                renderCarrito(); // vuelve a renderizar
            });

            li.appendChild(span);
            li.appendChild(btn);
            contenedorTratamientos.appendChild(li);

            total += item.precio;
            nombres.push(item.nombre);
        });

        if (inputTratamiento) inputTratamiento.value = nombres.join(', ');
        if (inputTotal) inputTotal.value = total.toFixed(2);

        document.getElementById("total-price").textContent = `Total: $${total.toFixed(2)}`;
    };

    renderCarrito();

    const formulario = document.getElementById("formulario");
    if (!formulario) return;

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
