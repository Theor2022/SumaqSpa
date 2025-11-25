document.addEventListener("DOMContentLoaded", () => {
    // Verificar autenticación
    const token = localStorage.getItem('jwt_token');
    if (!token) {
        alert("Debe iniciar sesión para realizar una reserva.");
        window.location.href = "login.html";
        return;
    }

    let carrito = JSON.parse(sessionStorage.getItem('carrito')) || [];
    const contenedorTratamientos = document.getElementById("cart-items");
    const inputTratamiento = document.getElementById("tratamiento");
    const inputTotal = document.getElementById("total");

    const renderCarrito = () => {
        if (!contenedorTratamientos) return;

        contenedorTratamientos.innerHTML = "";
        let total = 0;
        let nombres = [];

        if (carrito.length === 0) {
            contenedorTratamientos.innerHTML = '<li class="text-muted">No hay tratamientos seleccionados</li>';
            if (inputTratamiento) inputTratamiento.value = '';
            if (inputTotal) inputTotal.value = '0';
            document.getElementById("total-price").textContent = 'Total: $0.00';
            return;
        }

        carrito.forEach((item, index) => {
            const li = document.createElement("li");
            li.className = "d-flex justify-content-between align-items-center mb-1";

            const span = document.createElement("span");
            span.textContent = `${item.nombre} - $${item.precio.toFixed(2)}`;

            const btn = document.createElement("button");
            btn.textContent = "✖";
            btn.className = "btn btn-sm btn-danger ms-2";
            btn.addEventListener("click", () => {
                carrito.splice(index, 1);
                sessionStorage.setItem('carrito', JSON.stringify(carrito));
                renderCarrito();
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

        if (carrito.length === 0) {
            alert("Debe agregar al menos un tratamiento al carrito.");
            return;
        }

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

        // Buscar el botón de submit (puede ser input o button)
        const submitBtn = formulario.querySelector('input[type="submit"]') ||
                         formulario.querySelector('button[type="submit"]');

        if (submitBtn) {
            submitBtn.disabled = true;
            submitBtn.value = submitBtn.value ? 'Procesando...' : submitBtn.textContent;
            if (submitBtn.tagName === 'BUTTON') {
                submitBtn.textContent = 'Procesando...';
            }
        }

        try {
            const response = await fetch("http://localhost:8080/api/reservas", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify(data)
            });

            const result = await response.json();

            if (response.ok) {
                alert("¡Reserva realizada con éxito!");
                sessionStorage.removeItem('carrito');
                window.location.href = "confirmacion.html";
            } else if (response.status === 409) {
                // Conflicto de horario
                alert("El horario seleccionado ya está reservado. Por favor, seleccione otro horario.");
                // Recargar horarios disponibles
                const fecha = e.target.fecha.value;
                if (fecha) {
                    await actualizarHorariosDisponibles(fecha);
                }
            } else if (response.status === 401) {
                alert("Sesión expirada. Por favor, inicie sesión nuevamente.");
                localStorage.removeItem('jwt_token');
                localStorage.removeItem('user_info');
                window.location.href = "login.html";
            } else {
                alert("Error en la reserva: " + (result.error || result.message || 'Error desconocido'));
            }
        } catch (error) {
            console.error("Error de red:", error);
            alert("Error al conectar con el servidor.");
        } finally {
            // Re-habilitar el botón
            if (submitBtn) {
                submitBtn.disabled = false;
                if (submitBtn.tagName === 'BUTTON') {
                    submitBtn.textContent = 'Confirmar Reserva';
                } else if (submitBtn.tagName === 'INPUT') {
                    submitBtn.value = 'Reservar';
                }
            }
        }
    });
});