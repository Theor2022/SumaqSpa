// Referencias a elementos del DOM
const selectHora = document.getElementById("hora");
const inputFecha = document.getElementById("fecha");

// Genera horarios de 9:00 a 21:00 con intervalo de 30 minutos
function generarHorarios() {
    selectHora.innerHTML = ""; // limpiar opciones

    for (let h = 9; h < 21.5; h += 0.5) {
        const hora = Math.floor(h).toString().padStart(2, "0");
        const minuto = (h % 1 === 0) ? "00" : "30";
        const option = document.createElement("option");
        option.value = `${hora}:${minuto}`;
        option.textContent = `${hora}:${minuto}`;
        selectHora.appendChild(option);
    }
}

// Llama a la API para obtener los horarios ocupados en la fecha seleccionada
async function actualizarHorariosDisponibles(fecha) {
    if (!fecha) return;

    try {
        const res = await fetch(`http://localhost:8080/api/reservas/horarios?fecha=${fecha}`);
        if (!res.ok) throw new Error('Error al obtener horarios');
        const ocupados = await res.json(); // ["09:00", "10:30", ...]

        generarHorarios(); // Genera todos los horarios

        ocupados.forEach(hora => {
            const option = selectHora.querySelector(`option[value="${hora}"]`);
            if (option) option.remove();
        });

        if (selectHora.options.length === 0) {
            alert("No hay horarios disponibles para esta fecha.");
        }
    } catch (e) {
        console.error(e);
        alert("No se pudieron cargar los horarios disponibles.");
    }
}

// Validación del formulario antes del envío
function validarFormulario() {
    const nombre = document.getElementById("nombre").value.trim();
    const correo = document.getElementById("correo").value.trim();
    const telefono = document.getElementById("telefono").value.trim();
    const fechaStr = inputFecha.value;
    const hora = selectHora.value;

    if (!nombre || !correo || !telefono || !fechaStr || !hora) {
        alert("Por favor, completa todos los campos.");
        return false;
    }

    const regexCorreo = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!regexCorreo.test(correo)) {
        alert("Ingresa un correo electrónico válido.");
        return false;
    }

    const regexTelefono = /^\+?\d{7,15}$/;
    if (!regexTelefono.test(telefono)) {
        alert("Ingresa un número de teléfono válido (solo números, mínimo 7 dígitos).");
        return false;
    }

    const fechaHoraSeleccionada = new Date(`${fechaStr}T${hora}`);
    if (isNaN(fechaHoraSeleccionada.getTime())) {
        alert("La fecha u hora ingresada no es válida.");
        return false;
    }

    // Validar que la fecha esté entre jueves y lunes
    const fecha = new Date(fechaStr);
    const dia = fecha.getDay(); // 0=Dom, 1=Lun, ..., 6=Sáb
    const diasPermitidos = [0, 1, 4, 5, 6];
    if (!diasPermitidos.includes(dia)) {
        alert('Solo puedes reservar de jueves a lunes.');
        return false;
    }

    // Validar que la fecha y hora no sean pasadas
    const ahora = new Date();
    if (fechaHoraSeleccionada < ahora) {
        alert("La fecha y hora seleccionadas no pueden estar en el pasado.");
        return false;
    }

    return true;
}

// Ejecutar al cargar la página
document.addEventListener('DOMContentLoaded', () => {
    generarHorarios();

    inputFecha.addEventListener('change', (e) => {
        const fecha = e.target.value;
        if (!fecha) {
            selectHora.innerHTML = '';
            return;
        }

        const dia = new Date(fecha).getDay();
        const diasPermitidos = [0, 3, 4, 5, 6];
        if (!diasPermitidos.includes(dia)) {
            alert('Solo puedes reservar de jueves a lunes.');
            e.target.value = '';
            selectHora.innerHTML = '';
            return;
        }

        actualizarHorariosDisponibles(fecha);
    });
});
