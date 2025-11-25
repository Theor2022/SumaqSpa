const selectHora = document.getElementById("hora");
const inputFecha = document.getElementById("fecha");
let horariosOcupados = [];

// Genera horarios de 9:00 a 21:00 con intervalo de 30 minutos
function generarHorarios() {
    if (!selectHora) return;
    
    selectHora.innerHTML = '<option value="">Seleccione un horario</option>';

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
    if (!fecha || !selectHora) return;

    try {
        const res = await fetch(`http://localhost:8080/api/reservas/horarios?fecha=${fecha}`);
        if (!res.ok) throw new Error('Error al obtener horarios');
        
        horariosOcupados = await res.json();

        generarHorarios();

        // Deshabilitar horarios ocupados
        horariosOcupados.forEach(hora => {
            const option = selectHora.querySelector(`option[value="${hora}"]`);
            if (option) {
                option.disabled = true;
                option.textContent = option.textContent + ' ❌ (Ocupado)';
                option.style.color = '#dc3545';
            }
        });

        // Mostrar mensaje si no hay horarios disponibles
        const disponibles = Array.from(selectHora.options).filter(opt => !opt.disabled && opt.value);
        if (disponibles.length === 0) {
            alert("⚠️ No hay horarios disponibles para esta fecha. Por favor, seleccione otra fecha.");
            inputFecha.value = '';
            generarHorarios();
        } else {
            // Mostrar cuántos horarios están disponibles
            const mensaje = document.getElementById('horarios-info');
            if (mensaje) {
                mensaje.textContent = `✅ ${disponibles.length} horarios disponibles`;
                mensaje.className = 'text-success small mt-1';
            }
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
    const fechaStr = inputFecha ? inputFecha.value : '';
    const hora = selectHora ? selectHora.value : '';

    if (!nombre || !correo || !telefono || !fechaStr || !hora) {
        alert("⚠️ Por favor, completa todos los campos.");
        return false;
    }

    const regexCorreo = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!regexCorreo.test(correo)) {
        alert("⚠️ Ingresa un correo electrónico válido.");
        return false;
    }

    const regexTelefono = /^\+?\d{7,15}$/;
    if (!regexTelefono.test(telefono)) {
        alert("⚠️ Ingresa un número de teléfono válido (solo números, mínimo 7 dígitos).");
        return false;
    }

    const fechaHoraSeleccionada = new Date(`${fechaStr}T${hora}`);
    if (isNaN(fechaHoraSeleccionada.getTime())) {
        alert("⚠️ La fecha u hora ingresada no es válida.");
        return false;
    }

    // Validar que la fecha esté entre jueves y lunes
    const fecha = new Date(fechaStr + 'T00:00:00');
    const dia = fecha.getDay();
    const diasPermitidos = [0, 1, 4, 5, 6]; // Dom, Lun, Jue, Vie, Sáb
    if (!diasPermitidos.includes(dia)) {
        alert('⚠️ Solo puedes reservar de jueves a lunes.');
        return false;
    }

    // Validar que la fecha y hora no sean pasadas
    const ahora = new Date();
    if (fechaHoraSeleccionada < ahora) {
        alert("⚠️ La fecha y hora seleccionadas no pueden estar en el pasado.");
        return false;
    }

    // Validar que el horario seleccionado no esté ocupado
    if (horariosOcupados.includes(hora)) {
        alert("⚠️ El horario seleccionado ya está reservado. Por favor, seleccione otro horario.");
        return false;
    }

    return true;
}

// Ejecutar al cargar la página
document.addEventListener('DOMContentLoaded', () => {
    generarHorarios();

    // Agregar contenedor para mensajes de horarios
    if (selectHora && !document.getElementById('horarios-info')) {
        const info = document.createElement('div');
        info.id = 'horarios-info';
        info.className = 'small text-muted mt-1';
        selectHora.parentElement.appendChild(info);
    }

    if (inputFecha) {
        // Establecer fecha mínima como hoy
        const hoy = new Date().toISOString().split('T')[0];
        inputFecha.setAttribute('min', hoy);

        inputFecha.addEventListener('change', (e) => {
            const fecha = e.target.value;
            if (!fecha) {
                if (selectHora) selectHora.innerHTML = '<option value="">Primero seleccione una fecha</option>';
                return;
            }

            // Validar día de la semana
            const fechaObj = new Date(fecha + 'T00:00:00');
            const dia = fechaObj.getDay();
            const diasPermitidos = [0, 1, 4, 5, 6];
            
            if (!diasPermitidos.includes(dia)) {
                alert('⚠️ Solo puedes reservar de jueves a lunes.');
                e.target.value = '';
                generarHorarios();
                return;
            }

            // Validar que no sea una fecha pasada
            const hoy = new Date();
            hoy.setHours(0, 0, 0, 0);
            if (fechaObj < hoy) {
                alert('⚠️ No se pueden hacer reservas en fechas pasadas.');
                e.target.value = '';
                generarHorarios();
                return;
            }

            actualizarHorariosDisponibles(fecha);
        });
    }

    // Validar horario al seleccionar
    if (selectHora) {
        selectHora.addEventListener('change', (e) => {
            const horaSeleccionada = e.target.value;
            if (horaSeleccionada && horariosOcupados.includes(horaSeleccionada)) {
                alert('⚠️ Este horario ya está ocupado. Por favor, seleccione otro.');
                e.target.value = '';
            }
        });
    }
});