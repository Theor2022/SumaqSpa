document.addEventListener("DOMContentLoaded", () => {
    // NO eliminar el carrito al inicio - esta línea causaba el problema
    // sessionStorage.removeItem("carrito"); // ❌ COMENTADO

    const carrito = JSON.parse(sessionStorage.getItem('carrito')) || [];
    const botonesAgregar = document.querySelectorAll(".add-to-cart");
    const listaCarrito = document.getElementById("cart-items");
    const precioTotal = document.getElementById("total-price");
    const botonReserva = document.getElementById("go-to-reserve");

    function actualizarCarrito() {
        if (!listaCarrito || !precioTotal) return;

        listaCarrito.innerHTML = "";
        let total = 0;

        carrito.forEach((item, index) => {
            const li = document.createElement("li");
            li.className = "d-flex justify-content-between align-items-center mb-1";

            const span = document.createElement("span");
            span.textContent = `${item.nombre} - $${item.precio.toFixed(2)}`;

            const btnEliminar = document.createElement("button");
            btnEliminar.textContent = "✖";
            btnEliminar.className = "btn btn-sm btn-danger ms-2";
            btnEliminar.addEventListener("click", () => {
                carrito.splice(index, 1);
                sessionStorage.setItem("carrito", JSON.stringify(carrito));
                actualizarCarrito();
            });

            li.appendChild(span);
            li.appendChild(btnEliminar);
            listaCarrito.appendChild(li);

            total += item.precio;
        });

        precioTotal.textContent = `Total: $${total.toFixed(2)}`;
        sessionStorage.setItem("carrito", JSON.stringify(carrito));
    }

    if (botonesAgregar) {
        botonesAgregar.forEach(boton => {
            boton.addEventListener("click", () => {
                const nombre = boton.getAttribute("data-name");
                const precio = parseFloat(boton.getAttribute("data-price"));

                carrito.push({ nombre, precio });
                actualizarCarrito();
                alert(`${nombre} fue agregado al carrito.`);
            });
        });
    }

    if (botonReserva) {
        botonReserva.addEventListener("click", () => {
            if (carrito.length === 0) {
                alert("Debe agregar al menos un tratamiento.");
                return;
            }

            // Verificar autenticación antes de ir a reservar
            const token = localStorage.getItem('jwt_token');
            if (!token) {
                alert("Debe iniciar sesión para realizar una reserva.");
                window.location.href = "login.html";
                return;
            }

            window.location.href = "reservar.html";
        });
    }

    // Actualizar carrito al cargar
    actualizarCarrito();
});