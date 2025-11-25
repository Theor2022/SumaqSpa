// reserva.js - Gestión de reservas con validación de horarios

const API_URL = 'http://localhost:8080/api';

let horariosOcupados = [];

/**
 * Cargar horarios ocupados para una fecha
 */
async function cargarHorariosOcupados(fecha) {
    try {
        const response = await fetch(`${API_URL}/reservas/horarios?fecha=${fecha}`);

        if (response.ok) {
            horariosOcupados = await response.json();
            actualizarSelectHorarios();
        }
    } catch (error) {
        console.error('Error al cargar horarios:', error);
    }
}

/**
 * Actualizar el select de horarios deshabilitando los ocupados
 */
function actualizarSelectHorarios() {
    const selectHora = document.getElementById('hora');

    // Resetear todas las opciones
    Array.from(selectHora.options).forEach(option => {
        if (option.value) {
            option.disabled = false;
            option.style.color = '#000';
        }
    });

    // Deshabilitar horarios ocupados
    horariosOcupados.forEach(hora => {
        Array.from(selectHora.options).forEach(option => {
            if (option.value === hora) {
                option.disabled = true;
                option.style.color = '#ccc';
                option.textContent = option.textContent.replace(' (Ocupado)', '') + ' (Ocupado)';
            }
        });
    });
}

/**
 * Crear nueva reserva
 */
async function crearReserva(reservaData) {
    try {
        const response = await fetchWithAuth(`${API_URL}/reservas`, {
            method: 'POST',
            body: JSON.stringify(reservaData)
        });

        if (!response) {
            return { success: false, error: 'No autenticado' };
        }

        const data = await response.json();

        if (response.ok) {
            return { success: true, data };
        } else if (response.status === 409) {
            // Conflicto - horario ocupado
            return {
                success: false,
                error: data.error || 'El horario seleccionado ya está ocupado',
                conflicto: true
            };
        } else {
            return { success: false, error: data.error || 'Error al crear la reserva' };
        }
    } catch (error) {
        console.error('Error al crear reserva:', error);
        return { success: false, error: 'Error de conexión' };
    }
}

/**
 * Obtener todas las reservas (solo admin)
 */
async function obtenerReservas() {
    try {
        const response = await fetchWithAuth(`${API_URL}/reservas/reservas`);

        if (!response) {
            return { success: false, error: 'No autenticado' };
        }

        if (response.ok) {
            const reservas = await response.json();
            return { success: true, data: reservas };
        } else {
            return { success: false, error: 'Error al obtener reservas' };
        }
    } catch (error) {
        console.error('Error al obtener reservas:', error);
        return { success: false, error: 'Error de conexión' };
    }
}

/**
 * Eliminar reserva (solo admin)
 */
async function eliminarReserva(id) {
    try {
        const response = await fetchWithAuth(`${API_URL}/reservas/delete/${id}`, {
            method: 'DELETE'
        });

        if (!response) {
            return { success: false, error: 'No autenticado' };
        }

        const data = await response.json();

        if (response.ok) {
            return { success: true, data };
        } else {
            return { success: false, error: data.error || 'Error al eliminar reserva' };
        }
    } catch (error) {
        console.error('Error al eliminar reserva:', error);
        return { success: false, error: 'Error de conexión' };
    }
}

/**
 * Actualizar reserva (solo admin)
 */
async function actualizarReserva(id, reservaData) {
    try {
        const response = await fetchWithAuth(`${API_URL}/reservas/update/${id}`, {
            method: 'PUT',
            body: JSON.stringify(reservaData)
        });

        if (!response) {
            return { success: false, error: 'No autenticado' };
        }

        const data = await response.json();

        if (response.ok) {
            return { success: true, data };
        } else if (response.status === 409) {
            return {
                success: false,
                error: data.error || 'El horario seleccionado ya está ocupado',
                conflicto: true
            };
        } else {
            return { success: false, error: data.error || 'Error al actualizar reserva' };
        }
    } catch (error) {
        console.error('Error al actualizar reserva:', error);
        return { success: false, error: 'Error de conexión' };
    }
}

// Event listener para cambio de fecha
document.getElementById('fecha')?.addEventListener('change', async (e) => {
    const fecha = e.target.value;

    if (fecha) {
        // Validar que la fecha no sea en el pasado
        const fechaSeleccionada = new Date(fecha);
        const hoy = new Date();
        hoy.setHours(0, 0, 0, 0);

        if (fechaSeleccionada < hoy) {
            alert('No se pueden hacer reservas en fechas pasadas');
            e.target.value = '';
            return;
        }

        await cargarHorariosOcupados(fecha);
    }
});

// Event listener para el formulario de reserva
document.getElementById('reservaForm')?.addEventListener('submit', async (e) => {
    e.preventDefault();

    const reservaData = {
        nombre: document.getElementById('nombre').value,
        correo: document.getElementById('correo').value,
        telefono: document.getElementById('telefono').value,
        tratamiento: document.getElementById('tratamiento').value,
        total: parseFloat(document.getElementById('total').value),
        fecha: document.getElementById('fecha').value,
        hora: document.getElementById('hora').value
    };

    const errorDiv = document.getElementById('error');
    const submitBtn = document.getElementById('submitBtn');

    // Deshabilitar botón mientras se procesa
    submitBtn.disabled = true;
    submitBtn.textContent = 'Procesando...';

    const result = await crearReserva(reservaData);

    if (result.success) {
        alert('Reserva creada exitosamente');
        window.location.href = '/confirmacion.html';
    } else {
        errorDiv.textContent = result.error;
        errorDiv.style.display = 'block';

        // Si es conflicto de horario, recargar horarios ocupados
        if (result.conflicto) {
            await cargarHorariosOcupados(reservaData.fecha);
        }

        submitBtn.disabled = false;
        submitBtn.textContent = 'Confirmar Reserva';
    }
});

// Cargar reservas en la página de administración
if (window.location.pathname.includes('reservaslist.html')) {
    cargarListaReservas();
}

async function cargarListaReservas() {
    const result = await obtenerReservas();

    if (result.success) {
        mostrarReservas(result.data);
    } else {
        console.error('Error al cargar reservas:', result.error);
    }
}

function mostrarReservas(reservas) {
    const tbody = document.getElementById('reservasTableBody');
    tbody.innerHTML = '';

    reservas.forEach(reserva => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${reserva.id}</td>
            <td>${reserva.nombre}</td>
            <td>${reserva.correo}</td>
            <td>${reserva.telefono}</td>
            <td>${reserva.tratamiento}</td>
            <td>$${reserva.total}</td>
            <td>${reserva.fecha}</td>
            <td>${reserva.hora}</td>
            <td>
                <button onclick="editarReserva(${reserva.id})" class="btn-edit">Editar</button>
                <button onclick="confirmarEliminar(${reserva.id})" class="btn-delete">Eliminar</button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

async function confirmarEliminar(id) {
    if (confirm('¿Está seguro de eliminar esta reserva?')) {
        const result = await eliminarReserva(id);

        if (result.success) {
            alert('Reserva eliminada exitosamente');
            cargarListaReservas();
        } else {
            alert('Error: ' + result.error);
        }
    }
}

function editarReserva(id) {
    // Implementar modal o página de edición
    window.location.href = `/editar-reserva.html?id=${id}`;
}