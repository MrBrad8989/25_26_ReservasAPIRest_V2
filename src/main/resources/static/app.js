// Pequeño frontend en vanilla JS para autenticar y listar aulas

const loginForm = document.getElementById('loginForm');
const authInfo = document.getElementById('authInfo');
const result = document.getElementById('result');
const btnGetAulas = document.getElementById('btnGetAulas');
const btnClearToken = document.getElementById('btnClearToken');

const API_BASE = '';

function setToken(token) {
  localStorage.setItem('jwt_token', token);
  authInfo.textContent = 'Autenticado — token almacenado en localStorage';
}

function clearToken() {
  localStorage.removeItem('jwt_token');
  authInfo.textContent = 'No autenticado';
  result.innerHTML = '';
}

async function login(username, password) {
  try {
    const res = await fetch(`${API_BASE}/api/auth/login`, {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify({username, password})
    });

    if (!res.ok) {
      const txt = await res.text();
      throw new Error(txt || res.statusText);
    }

    const data = await res.json();
    // Suponemos que la respuesta contiene el token en un campo llamado 'token' o 'jwt'
    const token = data.token || data.jwt || data.accessToken || data.access_token;
    if (!token) throw new Error('Respuesta de login no contiene token');
    setToken(token);
  } catch (err) {
    authInfo.textContent = 'Error login: ' + err.message;
  }
}

async function fetchAulas() {
  const token = localStorage.getItem('jwt_token');
  if (!token) {
    authInfo.textContent = 'Necesitas iniciar sesión primero';
    return;
  }

  try {
    const res = await fetch(`${API_BASE}/api/aulas`, {
      headers: { 'Authorization': 'Bearer ' + token }
    });

    if (res.status === 401) {
      authInfo.textContent = 'Token inválido o expirado';
      return;
    }

    if (!res.ok) {
      const txt = await res.text();
      throw new Error(txt || res.statusText);
    }

    const data = await res.json();
    showAulas(data);
  } catch (err) {
    result.innerHTML = `<pre>${err.message}</pre>`;
  }
}

function showAulas(aulas) {
  if (!Array.isArray(aulas)) {
    result.innerHTML = `<pre>${JSON.stringify(aulas, null, 2)}</pre>`;
    return;
  }

  const list = document.createElement('ul');
  aulas.forEach(a => {
    const li = document.createElement('li');
    li.textContent = `${a.nombre || a.name || 'Aula'} — ${a.descripcion || a.capacity || ''}`;
    list.appendChild(li);
  });
  result.innerHTML = '';
  result.appendChild(list);
}

loginForm.addEventListener('submit', (e) => {
  e.preventDefault();
  const user = document.getElementById('username').value.trim();
  const pass = document.getElementById('password').value;
  authInfo.textContent = 'Iniciando sesión...';
  login(user, pass);
});

btnGetAulas.addEventListener('click', () => fetchAulas());
btnClearToken.addEventListener('click', () => clearToken());

// Estado inicial
if (localStorage.getItem('jwt_token')) {
  authInfo.textContent = 'Autenticado — token en localStorage';
} else {
  authInfo.textContent = 'No autenticado';
}
