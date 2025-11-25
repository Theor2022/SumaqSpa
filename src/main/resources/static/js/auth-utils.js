// Utilidades para manejo de JWT y autenticación
class AuthUtils {
    static TOKEN_KEY = 'jwt_token';
    static USER_KEY = 'user_info';

    // Guardar token y información del usuario
    static saveAuthData(token, userInfo) {
        localStorage.setItem(this.TOKEN_KEY, token);
        localStorage.setItem(this.USER_KEY, JSON.stringify(userInfo));
    }

    // Obtener token
    static getToken() {
        return localStorage.getItem(this.TOKEN_KEY);
    }

    // Obtener información del usuario
    static getUserInfo() {
        const userInfo = localStorage.getItem(this.USER_KEY);
        return userInfo ? JSON.parse(userInfo) : null;
    }

    // Verificar si el usuario está logueado
    static isLoggedIn() {
        const token = this.getToken();
        const userInfo = this.getUserInfo();
        return token && userInfo;
    }

    // Verificar si el usuario es admin
    static isAdmin() {
        const userInfo = this.getUserInfo();
        return userInfo && userInfo.roles && userInfo.roles.includes('ADMIN');
    }

    // Cerrar sesión
    static logout() {
        localStorage.removeItem(this.TOKEN_KEY);
        localStorage.removeItem(this.USER_KEY);
        window.location.href = '/login.html';
    }

    // Hacer petición autenticada
    static async authenticatedFetch(url, options = {}) {
        const token = this.getToken();
        
        if (!token) {
            throw new Error('No token found. Please login.');
        }

        const defaultOptions = {
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        };

        // Combinar opciones
        const finalOptions = {
            ...defaultOptions,
            ...options,
            headers: {
                ...defaultOptions.headers,
                ...options.headers
            }
        };

        const response = await fetch(url, finalOptions);

        // Si recibimos 401, el token expiró
        if (response.status === 401) {
            this.logout();
            throw new Error('Session expired. Please login again.');
        }

        return response;
    }

    // Redirigir a login si no está autenticado
    static requireAuth() {
        if (!this.isLoggedIn()) {
            window.location.href = '/login.html';
            return false;
        }
        return true;
    }

    // Redirigir si no es admin
    static requireAdmin() {
        if (!this.isLoggedIn()) {
            window.location.href = '/login.html';
            return false;
        }
        if (!this.isAdmin()) {
            alert('Acceso denegado. Se requieren permisos de administrador.');
            window.location.href = '/index.html';
            return false;
        }
        return true;
    }

    // Actualizar UI según el estado de autenticación
    static updateNavigation() {
        const userInfo = this.getUserInfo();
        const isLoggedIn = this.isLoggedIn();
        
        // Buscar elementos de navegación (si existen)
        const loginLink = document.querySelector('a[href="login.html"]');
        const logoutBtn = document.getElementById('logoutBtn');
        const userNameSpan = document.getElementById('userName');
        const adminLinks = document.querySelectorAll('.admin-only');

        if (isLoggedIn && userInfo) {
            // Usuario logueado
            if (loginLink) loginLink.style.display = 'none';
            if (userNameSpan) userNameSpan.textContent = userInfo.username;
            
            // Mostrar/ocultar enlaces de admin
            adminLinks.forEach(link => {
                link.style.display = this.isAdmin() ? 'block' : 'none';
            });

        } else {
            // Usuario no logueado
            if (loginLink) loginLink.style.display = 'block';
            if (userNameSpan) userNameSpan.textContent = '';
            
            // Ocultar enlaces de admin
            adminLinks.forEach(link => {
                link.style.display = 'none';
            });
        }

        // Configurar botón de logout
        if (logoutBtn) {
            logoutBtn.addEventListener('click', () => {
                this.logout();
            });
        }
    }
}

// Ejecutar al cargar la página
document.addEventListener('DOMContentLoaded', () => {
    AuthUtils.updateNavigation();
});