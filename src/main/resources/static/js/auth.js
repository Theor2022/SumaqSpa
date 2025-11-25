// auth.js - Funciones de autenticación para el frontend

const API_URL = 'http://localhost:8080/api';

/**
 * Login de usuario
 */
async function login(username, password) {
    try {
        const response = await fetch(`${API_URL}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, password })
        });

        const data = await response.json();

        if (response.ok) {
            // Guardar token y datos de usuario
            localStorage.setItem('token', data.token);
            localStorage.setItem('username', data.username);
            localStorage.setItem('role', data.role);

            return { success: true, data };
        } else {
            return { success: false, error: data.error || 'Error al iniciar sesión' };
        }
    } catch (error) {
        console.error('Error en login:', error);
        return { success: false, error: 'Error de conexión' };
    }
}

/**
 * Registro de nuevo usuario
 */
async function register(username, password) {
    try {
        const response = await fetch(`${API_URL}/auth/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, password })
        });

        const data = await response.json();

        if (response.ok) {
            return { success: true, data };
        } else {
            return { success: false, error: data.error || 'Error al registrarse' };
        }
    } catch (error) {
        console.error('Error en registro:', error);
        return { success: false, error: 'Error de conexión' };
    }
}

/**
 * Logout
 */
function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    localStorage.removeItem('role');
    window.location.href = '/login.html';
}

/**
 * Verificar si el usuario está autenticado
 */
function isAuthenticated() {
    return localStorage.getItem('token') !== null;
}

/**
 * Obtener el token actual
 */
function getToken() {
    return localStorage.getItem('token');
}

/**
 * Verificar si el usuario es admin
 */
function isAdmin() {
    return localStorage.getItem('role') === 'ADMIN';
}

/**
 * Hacer petición autenticada
 */
async function fetchWithAuth(url, options = {}) {
    const token = getToken();

    if (!token) {
        logout();
        return;
    }

    const headers = {
        ...options.headers,
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
    };

    try {
        const response = await fetch(url, { ...options, headers });

        // Si el token expiró, hacer logout
        if (response.status === 401) {
            logout();
            return null;
        }

        return response;
    } catch (error) {
        console.error('Error en petición:', error);
        throw error;
    }
}

// Ejemplo de uso en login.html
document.getElementById('loginForm')?.addEventListener('submit', async (e) => {
    e.preventDefault();

    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    const errorDiv = document.getElementById('error');

    const result = await login(username, password);

    if (result.success) {
        // Redirigir según el rol
        if (result.data.role === 'ADMIN') {
            window.location.href = '/reservaslist.html';
        } else {
            window.location.href = '/reservar.html';
        }
    } else {
        errorDiv.textContent = result.error;
        errorDiv.style.display = 'block';
    }
});

// Ejemplo de uso en register.html
document.getElementById('registerForm')?.addEventListener('submit', async (e) => {
    e.preventDefault();

    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    const errorDiv = document.getElementById('error');

    if (password !== confirmPassword) {
        errorDiv.textContent = 'Las contraseñas no coinciden';
        errorDiv.style.display = 'block';
        return;
    }

    const result = await register(username, password);

    if (result.success) {
        alert('Registro exitoso. Ahora puedes iniciar sesión.');
        window.location.href = '/login.html';
    } else {
        errorDiv.textContent = result.error;
        errorDiv.style.display = 'block';
    }
});

// Proteger páginas que requieren autenticación
if (window.location.pathname.includes('reservar.html') ||
    window.location.pathname.includes('confirmacion.html')) {
    if (!isAuthenticated()) {
        window.location.href = '/login.html';
    }
}

// Proteger páginas de admin
if (window.location.pathname.includes('reservaslist.html') ||
    window.location.pathname.includes('admin')) {
    if (!isAuthenticated() || !isAdmin()) {
        window.location.href = '/login.html';
    }
}