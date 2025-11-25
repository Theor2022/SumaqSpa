document.addEventListener("DOMContentLoaded", () => {
    // Verificar que sea admin
    const token = localStorage.getItem('jwt_token');
    const userInfo = JSON.parse(localStorage.getItem('user_info') || '{}');

    if (!token || userInfo.role !== 'ADMIN') {
        alert('Acceso denegado. Se requieren permisos de administrador.');
        window.location.href = 'login.html';
        return;
    }

    const reservasContainer = document.getElementById('reservas-container');
    const loadingMessage = document.getElementById('loading-message');

    // Función para hacer peticiones autenticadas
    async function fetchWithAuth(url, options = {}) {
        const defaultOptions = {
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        };

        const finalOptions = {
            ...defaultOptions,
            ...options,
            headers: {
                ...defaultOptions.headers,
                ...options.headers
            }
        };

        const response = await fetch(url, finalOptions);

        if (response.status === 401) {
            alert('Sesión expirada. Por favor, inicie sesión nuevamente.');
            logout();
            return null;
        }

        return response;
    }

    // Función para cargar y mostrar reservas
    async function cargarReservas() {
        try {
            if (loadingMessage) loadingMessage.style.display = 'block';

            const response = await fetchWithAuth('http://localhost:8080/api/reservas/reservas');

            if (!response) return;

            if (response.ok) {
                const reservas = await response.json();
                mostrarReservas(reservas);
            } else {
                throw new Error('Error al cargar las reservas');
            }
        } catch (error) {
            console.error('Error:', error);
            alert('Error al cargar las reservas: ' + error.message);
        } finally {
            if (loadingMessage) loadingMessage.style.display = 'none';
        }
    }

    // Función para mostrar las reservas en la tabla
    function mostrarReservas(reservas) {
        if (!reservas || reservas.length === 0) {
            reservasContainer.innerHTML = '<p class="text-center">No hay reservas registradas.</p>';
            return;
        }

        const tabla = `
            <div class="table-responsive">
                <table class="table table-striped table-hover">
                    <thead class="table-dark">
                        <tr>
                            <th>ID</th>
                            <th>Cliente</th>
                            <th>Email</th>
                            <th>Teléfono</th>
                            <th>Fecha</th>
                            <th>Hora</th>
                            <th>Tratamiento</th>
                            <th>Total</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${reservas.map(reserva => `
                            <tr>
                                <td>${reserva.id}</td>
                                <td>${reserva.nombre || ''}</td>
                                <td>${reserva.correo || ''}</td>
                                <td>${reserva.telefono || ''}</td>
                                <td>${formatearFecha(reserva.fecha)}</td>
                                <td>${reserva.hora || ''}</td>
                                <td>${reserva.tratamiento || ''}</td>
                                <td>$${reserva.total ? reserva.total.toFixed(2) : '0.00'}</td>
                                <td>
                                    <button class="btn btn-sm btn-primary me-1" onclick="editarReserva(${reserva.id})">
                                        ✏️ Editar
                                    </button>
                                    <button class="btn btn-sm btn-danger" onclick="eliminarReserva(${reserva.id})">
                                        🗑️ Eliminar
                                    </button>
                                </td>
                            </tr>
                        `).join('')}
                    </tbody>
                </table>
            </div>
        `;

        reservasContainer.innerHTML = tabla;
    }

    // Función para formatear fecha
    function formatearFecha(fecha) {
        if (!fecha) return '';
        const date = new Date(fecha + 'T00:00:00');
        return date.toLocaleDateString('es-ES', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit'
        });
    }

    // Función para eliminar reserva
    window.eliminarReserva = async function(id) {
        if (!confirm('¿Está seguro de que desea eliminar esta reserva?')) {
            return;
        }

        try {
            const response = await fetchWithAuth(`http://localhost:8080/api/reservas/delete/${id}`, {
                method: 'DELETE'
            });

            if (!response) return;

            if (response.ok) {
                alert('Reserva eliminada exitosamente');
                cargarReservas();
            } else {
                const error = await response.json();
                alert('Error al eliminar la reserva: ' + (error.error || error.message));
            }
        } catch (error) {
            console.error('Error:', error);
            alert('Error de conexión al eliminar la reserva');
        }
    };

    // Función para editar reserva
    window.editarReserva = async function(id) {
        try {
            const response = await fetchWithAuth('http://localhost:8080/api/reservas/reservas');
            if (!response) return;

            const reservas = await response.json();
            const reserva = reservas.find(r => r.id === id);

            if (!reserva) {
                alert('Reserva no encontrada');
                return;
            }

            mostrarModalEdicion(reserva);
        } catch (error) {
            console.error('Error:', error);
            alert('Error al cargar los datos de la reserva');
        }
    };

    function mostrarModalEdicion(reserva) {
        const modalHTML = `
            <div class="modal fade" id="editModal" tabindex="-1">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title">Editar Reserva #${reserva.id}</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <form id="editForm">
                                <div class="mb-3">
                                    <label class="form-label">Nombre</label>
                                    <input type="text" class="form-control" id="edit_nombre" value="${reserva.nombre || ''}" required>
                                </div>
                                <div class="mb-3">
                                    <label class="form-label">Email</label>
                                    <input type="email" class="form-control" id="edit_correo" value="${reserva.correo || ''}" required>
                                </div>
                                <div class="mb-3">
                                    <label class="form-label">Teléfono</label>
                                    <input type="tel" class="form-control" id="edit_telefono" value="${reserva.telefono || ''}" required>
                                </div>
                                <div class="mb-3">
                                    <label class="form-label">Fecha</label>
                                    <input type="date" class="form-control" id="edit_fecha" value="${reserva.fecha || ''}" required>
                                </div>
                                <div class="mb-3">
                                    <label class="form-label">Hora</label>
                                    <input type="time" class="form-control" id="edit_hora" value="${reserva.hora || ''}" required>
                                </div>
                                <div class="mb-3">
                                    <label class="form-label">Tratamiento</label>
                                    <input type="text" class="form-control" id="edit_tratamiento" value="${reserva.tratamiento || ''}" required>
                                </div>
                                <div class="mb-3">
                                    <label class="form-label">Total</label>
                                    <input type="number" step="0.01" class="form-control" id="edit_total" value="${reserva.total || 0}" required>
                                </div>
                            </form>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                            <button type="button" class="btn btn-primary" onclick="guardarEdicion(${reserva.id})">Guardar Cambios</button>
                        </div>
                    </div>
                </div>
            </div>
        `;

        document.body.insertAdjacentHTML('beforeend', modalHTML);
        const modal = new bootstrap.Modal(document.getElementById('editModal'));
        modal.show();

        document.getElementById('editModal').addEventListener('hidden.bs.modal', function () {
            this.remove();
        });
    }

    // Función para guardar la edición
    window.guardarEdicion = async function(id) {
        const reservaActualizada = {
            nombre: document.getElementById('edit_nombre').value,
            correo: document.getElementById('edit_correo').value,
            telefono: document.getElementById('edit_telefono').value,
            fecha: document.getElementById('edit_fecha').value,
            hora: document.getElementById('edit_hora').value,
            tratamiento: document.getElementById('edit_tratamiento').value,
            total: parseFloat(document.getElementById('edit_total').value)
        };

        try {
            const response = await fetchWithAuth(`http://localhost:8080/api/reservas/update/${id}`, {
                method: 'PUT',
                body: JSON.stringify(reservaActualizada)
            });

            if (!response) return;

            if (response.ok) {
                alert('Reserva actualizada exitosamente');
                bootstrap.Modal.getInstance(document.getElementById('editModal')).hide();
                cargarReservas();
            } else {
                const error = await response.json();
                alert('Error al actualizar: ' + (error.error || error.message));
            }
        } catch (error) {
            console.error('Error:', error);
            alert('Error de conexión al actualizar la reserva');
        }
    };

    // Botón de refrescar
    const refreshBtn = document.getElementById('refresh-btn');
    if (refreshBtn) {
        refreshBtn.addEventListener('click', cargarReservas);
    }

    // Botón de logout
    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', logout);
    }

    // Cargar reservas al inicio
    cargarReservas();
});