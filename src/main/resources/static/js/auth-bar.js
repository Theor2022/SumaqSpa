// ========================================
// UTILIDAD DE AUTENTICACIÓN
// ========================================
// Este script maneja el estado de autenticación del usuario
// y proporciona funciones para mostrar/ocultar info de sesión

function obtenerUsuarioLogueado() {
    const userInfo = localStorage.getItem('user_info');
    if (!userInfo) return null;

    try {
        return JSON.parse(userInfo);
    } catch {
        return null;
    }
}

function estaLogueado() {
    const token = localStorage.getItem('jwt_token');
    const user = obtenerUsuarioLogueado();
    return !!(token && user);
}

function cerrarSesion() {
    if (confirm('¿Estás seguro que deseas cerrar sesión?')) {
        localStorage.removeItem('jwt_token');
        localStorage.removeItem('user_info');
        sessionStorage.removeItem('carrito'); // Limpiar también el carrito
        alert('Sesión cerrada exitosamente');
        window.location.href = 'index.html';
    }
}

function crearBarraUsuario() {
    const user = obtenerUsuarioLogueado();

    if (!user) {
        return `
            <div class="login-bar">
                <a href="login.html" class="btn-login">
                    🔐 Iniciar Sesión / Registrarse
                </a>
            </div>
        `;
    }

    const roleBadge = user.role === 'ADMIN'
        ? '<span class="badge-admin">ADMIN</span>'
        : '<span class="badge-user">USUARIO</span>';

    return `
        <div class="user-bar">
            <div class="user-info">
                <span class="user-name">👤 ${user.nombre || user.username}</span>
                ${roleBadge}
                ${user.email ? `<span class="user-email">📧 ${user.email}</span>` : ''}
            </div>
            <div class="user-actions">
                ${user.role === 'ADMIN'
                    ? '<a href="reservaslist.html" class="btn-admin">📋 Panel Admin</a>'
                    : ''
                }
                <button onclick="cerrarSesion()" class="btn-logout">
                    🚪 Cerrar Sesión
                </button>
            </div>
        </div>
    `;
}

function insertarBarraUsuario() {
    const barraHTML = crearBarraUsuario();

    const contenedor = document.createElement('div');
    contenedor.innerHTML = barraHTML;

    const barra = contenedor.firstElementChild;

    document.body.insertBefore(barra, document.body.firstChild);

    // Controlar visibilidad del botón Login en el menú
    controlarBotonLoginMenu();
}

/**
 * Controlar la visibilidad del botón Login en el menú de navegación
 */
function controlarBotonLoginMenu() {
    const loginNavItem = document.getElementById('login-nav-item');

    if (!loginNavItem) return; // Si no existe el elemento, salir

    const user = obtenerUsuarioLogueado();

    if (user && estaLogueado()) {
        // Si está logueado, ocultar el botón de login
        loginNavItem.style.display = 'none';
    } else {
        // Si NO está logueado, mostrar el botón de login
        loginNavItem.style.display = 'block';
    }
}

/**
 * Redirigir a login si no está autenticado
 */
function requiereAutenticacion() {
    if (!estaLogueado()) {
        alert('Debes iniciar sesión para acceder a esta página');
        window.location.href = 'login.html';
        return false;
    }
    return true;
}

/**
 * Redirigir a admin panel si no es administrador
 */
function requiereAdmin() {
    const user = obtenerUsuarioLogueado();

    if (!user) {
        alert('Debes iniciar sesión para acceder a esta página');
        window.location.href = 'login.html';
        return false;
    }

    if (user.role !== 'ADMIN') {
        alert('No tienes permisos para acceder a esta página');
        window.location.href = 'index.html';
        return false;
    }

    return true;
}

// Inicializar cuando carga la página
document.addEventListener('DOMContentLoaded', () => {
    insertarBarraUsuario();
    controlarBotonLoginMenu();
});
