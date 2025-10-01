document.addEventListener("DOMContentLoaded", () => {

    sessionStorage.removeItem("carrito");
    const carrito = JSON.parse(sessionStorage.getItem('carrito')) || [];
    const botonesAgregar = document.querySelectorAll(".add-to-cart");
    const listaCarrito = document.getElementById("cart-items");
    const precioTotal = document.getElementById("total-price");
    const botonReserva = document.getElementById("go-to-reserve");

    function actualizarCarrito() {
        listaCarrito.innerHTML = "";
        let total = 0;

        carrito.forEach((item, index) => {
            const li = document.createElement("li");
            li.className = "d-flex justify-content-between align-items-center mb-1";

            const span = document.createElement("span");
            span.textContent = `${item.nombre} - $${item.precio}`;

            const btnEliminar = document.createElement("button");
            btnEliminar.textContent = "✖";
            btnEliminar.className = "btn btn-sm btn-danger ms-2";
            btnEliminar.addEventListener("click", () => {
                carrito.splice(index, 1); // eliminar item
                sessionStorage.setItem("carrito", JSON.stringify(carrito));
                actualizarCarrito(); // re-renderizar
            });

            li.appendChild(span);
            li.appendChild(btnEliminar);
            listaCarrito.appendChild(li);

            total += item.precio;
        });

        precioTotal.textContent = `Total: $${total}`;
        sessionStorage.setItem("carrito", JSON.stringify(carrito));
    }

    botonesAgregar.forEach(boton => {
        boton.addEventListener("click", () => {
            const nombre = boton.getAttribute("data-name");
            const precio = parseFloat(boton.getAttribute("data-price"));

            carrito.push({ nombre, precio });
            actualizarCarrito();
            alert(`${nombre} fue agregado al carrito.`);
        });
    });

    if (botonReserva) {
        botonReserva.addEventListener("click", () => {
            if (carrito.length === 0) {
                alert("Debe agregar al menos un tratamiento.");
                return;
            }
            window.location.href = "Reservar.html";
        });
    }

    actualizarCarrito();
});
