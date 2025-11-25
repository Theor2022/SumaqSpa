document.addEventListener("DOMContentLoaded", () => {
    // Si ya está logueado, redirigir
    const token = localStorage.getItem('jwt_token');
    if (token) {
        const userInfo = JSON.parse(localStorage.getItem('user_info') || '{}');
        if (userInfo.role === 'ADMIN') {
            window.location.href = 'reservaslist.html';
        } else {
            window.location.href = 'index.html';
        }
        return;
    }

    const loginForm = document.getElementById('loginForm');
    const registerForm = document.getElementById('registerForm');

    // LOGIN
    if (loginForm) {
        loginForm.addEventListener('submit', async (e) => {
            e.preventDefault();

            const username = document.getElementById('username').value;
            const password = document.getElementById('password').value;
            const errorDiv = document.getElementById('error');
            const submitBtn = loginForm.querySelector('button[type="submit"]');

            submitBtn.disabled = true;
            submitBtn.textContent = 'Iniciando sesión...';

            try {
                const response = await fetch('http://localhost:8080/api/auth/login', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ username, password })
                });

                const data = await response.json();

                if (response.ok && data.token) {
                    // Guardar token y datos del usuario
                    localStorage.setItem('jwt_token', data.token);
                    localStorage.setItem('user_info', JSON.stringify({
                        username: data.username,
                        role: data.role
                    }));

                    // Redirigir según el rol
                    if (data.role === 'ADMIN') {
                        window.location.href = 'reservaslist.html';
                    } else {
                        window.location.href = 'index.html';
                    }
                } else {
                    errorDiv.textContent = data.error || 'Credenciales incorrectas';
                    errorDiv.style.display = 'block';
                }
            } catch (error) {
                console.error('Error en login:', error);
                errorDiv.textContent = 'Error de conexión con el servidor';
                errorDiv.style.display = 'block';
            } finally {
                submitBtn.disabled = false;
                submitBtn.textContent = 'Iniciar Sesión';
            }
        });
    }

    // REGISTRO
    if (registerForm) {
        registerForm.addEventListener('submit', async (e) => {
            e.preventDefault();

            const username = document.getElementById('reg_username').value;
            const password = document.getElementById('reg_password').value;
            const confirmPassword = document.getElementById('reg_confirm_password').value;
            const errorDiv = document.getElementById('register_error');
            const submitBtn = registerForm.querySelector('button[type="submit"]');

            // Validar que las contraseñas coincidan
            if (password !== confirmPassword) {
                errorDiv.textContent = 'Las contraseñas no coinciden';
                errorDiv.style.display = 'block';
                return;
            }

            submitBtn.disabled = true;
            submitBtn.textContent = 'Registrando...';

            try {
                const response = await fetch('http://localhost:8080/api/auth/register', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ username, password })
                });

                const data = await response.json();

                if (response.ok) {
                    alert('Registro exitoso. Ahora puedes iniciar sesión.');
                    // Cambiar a pestaña de login o redirigir
                    window.location.href = 'login.html';
                } else {
                    errorDiv.textContent = data.error || 'Error al registrarse';
                    errorDiv.style.display = 'block';
                }
            } catch (error) {
                console.error('Error en registro:', error);
                errorDiv.textContent = 'Error de conexión con el servidor';
                errorDiv.style.display = 'block';
            } finally {
                submitBtn.disabled = false;
                submitBtn.textContent = 'Registrarse';
            }
        });
    }
});

// Función de logout global
function logout() {
    localStorage.removeItem('jwt_token');
    localStorage.removeItem('user_info');
    sessionStorage.removeItem('carrito');
    window.location.href = 'login.html';
}