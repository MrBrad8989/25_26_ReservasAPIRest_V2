// Frontend en vanilla JS: autenticación y CRUD básico para recursos

const API_BASE = '';

const loginForm = document.getElementById('loginForm');
const authInfo = document.getElementById('authInfo');
const btnClearToken = document.getElementById('btnClearToken');
const navButtons = document.querySelectorAll('.nav-btn');

// Vistas
const views = {
  aulas: document.getElementById('aulasView'),
  horarios: document.getElementById('horariosView'),
  reservas: document.getElementById('reservasView'),
  usuarios: document.getElementById('usuariosView')
};

// Elementos específicos
const aulasList = document.getElementById('aulasList');
const aulaForm = document.getElementById('aulaForm');
const aulaId = document.getElementById('aulaId');
const aulaNombre = document.getElementById('aulaNombre');
const aulaCapacidad = document.getElementById('aulaCapacidad');
const aulaOrdenadores = document.getElementById('aulaOrdenadores');
const aulaNumeroOrdenadores = document.getElementById('aulaNumeroOrdenadores');

const horariosList = document.getElementById('horariosList');
const horarioForm = document.getElementById('horarioForm');
const horarioId = document.getElementById('horarioId');
const horarioDia = document.getElementById('horarioDia');
const horarioSesion = document.getElementById('horarioSesion');
const horarioInicio = document.getElementById('horarioInicio');
const horarioFin = document.getElementById('horarioFin');

const reservasList = document.getElementById('reservasList');
const reservaForm = document.getElementById('reservaForm');
const reservaFecha = document.getElementById('reservaFecha');
const reservaMotivo = document.getElementById('reservaMotivo');
const reservaAsistentes = document.getElementById('reservaAsistentes');
const reservaAula = document.getElementById('reservaAula');
const reservaHorario = document.getElementById('reservaHorario');

const usuariosList = document.getElementById('usuariosList');
const usuarioForm = document.getElementById('usuarioForm');
const usuarioId = document.getElementById('usuarioId');
const usuarioNombre = document.getElementById('usuarioNombre');
const usuarioEmail = document.getElementById('usuarioEmail');
const usuarioPassword = document.getElementById('usuarioPassword');
const usuarioRole = document.getElementById('usuarioRole');

function setToken(token) {
  localStorage.setItem('jwt_token', token);
  authInfo.textContent = 'Autenticado — token almacenado en localStorage';
}

function clearToken() {
  localStorage.removeItem('jwt_token');
  authInfo.textContent = 'No autenticado';
}

async function apiFetch(path, opts = {}) {
  const token = localStorage.getItem('jwt_token');
  const headers = opts.headers || {};
  if (token) headers['Authorization'] = 'Bearer ' + token;
  if (opts.body && typeof opts.body === 'object' && !(opts.body instanceof FormData)) {
    headers['Content-Type'] = 'application/json';
    opts.body = JSON.stringify(opts.body);
  }
  const res = await fetch(API_BASE + path, {...opts, headers});
  if (res.status === 401) throw new Error('No autorizado (401)');
  return res;
}

async function login(username, password) {
  authInfo.textContent = 'Iniciando sesión...';
  try {
    const res = await fetch(`${API_BASE}/api/auth/login`, {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify({username, password})
    });
    if (!res.ok) throw new Error(await res.text());
    const data = await res.json();
    const token = data.token || data.jwt || data.accessToken || data.access_token;
    if (!token) throw new Error('Respuesta de login no contiene token');
    setToken(token);
    loadCurrentView();
  } catch (err) {
    authInfo.textContent = 'Error login: ' + err.message;
  }
}

// Navegación entre vistas
// Navegación entre vistas + manejo de clase activa
function setActiveNav(btn) {
  navButtons.forEach(b => b.classList.remove('active'));
  if (btn) btn.classList.add('active');
}

navButtons.forEach(btn => btn.addEventListener('click', (e) => {
  const view = btn.dataset.view;
  showView(view);
  setActiveNav(btn);
}));

function showView(name) {
  Object.values(views).forEach(v => v.style.display = 'none');
  const v = views[name];
  if (v) v.style.display = '';
  if (name === 'aulas') loadAulas();
  if (name === 'horarios') loadHorarios();
  if (name === 'reservas') { loadAulas(); loadHorarios(); loadReservas(); }
  if (name === 'usuarios') loadUsuarios();
}

function loadCurrentView() {
  // Keep visible view
  const visible = Object.keys(views).find(k => views[k].style.display !== 'none') || 'aulas';
  showView(visible);
}

// --- AULAS ---
async function loadAulas() {
  try {
    const res = await apiFetch('/api/aulas');
    const data = await res.json();
    renderAulas(data);
    // también poblar selects para reservas
    populateAulaSelect(data);
  } catch (err) {
    aulasList.innerHTML = `<pre>${err.message}</pre>`;
  }
}

function renderAulas(aulas) {
  if (!Array.isArray(aulas)) {
    aulasList.innerHTML = `<pre>${JSON.stringify(aulas,null,2)}</pre>`; return;
  }
  aulasList.innerHTML = '';
  const ul = document.createElement('ul');
  aulas.forEach(a => {
    const li = document.createElement('li');
    li.innerHTML = `<strong>${a.nombre}</strong> — cap: ${a.capacidad || '-'} — ordenadores: ${a.esAulaOrdenadores ? 'sí' : 'no'} `;
    const btnEdit = document.createElement('button'); btnEdit.textContent = 'Editar';
    btnEdit.addEventListener('click', () => fillAulaForm(a));
    const btnDel = document.createElement('button'); btnDel.textContent = 'Borrar';
    btnDel.addEventListener('click', () => deleteAula(a.id));
    li.appendChild(btnEdit); li.appendChild(btnDel);
    ul.appendChild(li);
  });
  aulasList.appendChild(ul);
}

function fillAulaForm(a) {
  aulaId.value = a.id || '';
  aulaNombre.value = a.nombre || '';
  aulaCapacidad.value = a.capacidad || '';
  aulaOrdenadores.checked = !!a.esAulaOrdenadores;
  aulaNumeroOrdenadores.value = a.numeroOrdenadores || '';
}

async function submitAula(e) {
  e.preventDefault();
  const payload = {
    nombre: aulaNombre.value,
    capacidad: aulaCapacidad.value ? Number(aulaCapacidad.value) : null,
    esAulaOrdenadores: aulaOrdenadores.checked,
    numeroOrdenadores: aulaNumeroOrdenadores.value ? Number(aulaNumeroOrdenadores.value) : null
  };
  try {
    if (aulaId.value) {
      const id = aulaId.value;
      const res = await apiFetch(`/api/aulas/${id}`, {method: 'PUT', body: payload});
      if (!res.ok) throw new Error(await res.text());
    } else {
      const res = await apiFetch('/api/aulas', {method: 'POST', body: payload});
      if (!res.ok) throw new Error(await res.text());
    }
    aulaForm.reset(); aulaId.value='';
    loadAulas();
  } catch (err) { aulasList.innerHTML = `<pre>${err.message}</pre>`; }
}

async function deleteAula(id) {
  if (!confirm('Borrar aula ' + id + '?')) return;
  try {
    const res = await apiFetch(`/api/aulas/${id}`, {method: 'DELETE'});
    if (!res.ok) throw new Error(await res.text());
    loadAulas();
  } catch (err) { aulasList.innerHTML = `<pre>${err.message}</pre>`; }
}

// --- HORARIOS ---
async function loadHorarios() {
  try {
    const res = await apiFetch('/api/horarios');
    const data = await res.json();
    renderHorarios(data);
    populateHorarioSelect(data);
  } catch (err) { horariosList.innerHTML = `<pre>${err.message}</pre>`; }
}

function renderHorarios(hs) {
  horariosList.innerHTML = '';
  const ul = document.createElement('ul');
  hs.forEach(h => {
    const li = document.createElement('li');
    li.innerHTML = `${h.diaSemana} - ${h.horaInicio || ''} ➜ ${h.horaFin || ''}`;
    const btnDel = document.createElement('button'); btnDel.textContent = 'Borrar';
    btnDel.addEventListener('click', () => deleteHorario(h.id));
    ul.appendChild(li); li.appendChild(btnDel);
  });
  horariosList.appendChild(ul);
}

async function submitHorario(e) {
  e.preventDefault();
  const payload = {
    diaSemana: horarioDia.value,
    sesionDiaria: horarioSesion.value ? Number(horarioSesion.value) : null,
    horaInicio: horarioInicio.value ? horarioInicio.value : null,
    horaFin: horarioFin.value ? horarioFin.value : null
  };
  try {
    if (horarioId.value) {
      // No endpoint PUT definido; TODO: skip edit
    } else {
      const res = await apiFetch('/api/horarios', {method: 'POST', body: payload});
      if (!res.ok) throw new Error(await res.text());
    }
    horarioForm.reset(); loadHorarios();
  } catch (err) { horariosList.innerHTML = `<pre>${err.message}</pre>`; }
}

async function deleteHorario(id) {
  if (!confirm('Borrar horario ' + id + '?')) return;
  try {
    const res = await apiFetch(`/api/horarios/${id}`, {method: 'DELETE'});
    if (!res.ok) throw new Error(await res.text());
    loadHorarios();
  } catch (err) { horariosList.innerHTML = `<pre>${err.message}</pre>`; }
}

// --- RESERVAS ---
async function loadReservas() {
  try {
    const res = await apiFetch('/api/reservas');
    const data = await res.json();
    renderReservas(data);
  } catch (err) { reservasList.innerHTML = `<pre>${err.message}</pre>`; }
}

function renderReservas(list) {
  reservasList.innerHTML = '';
  const ul = document.createElement('ul');
  list.forEach(r => {
    const li = document.createElement('li');
    li.innerHTML = `${r.fecha} - ${r.motivo || ''} - aula: ${r.aula?.nombre || '-'} - horario: ${r.horario?.diaSemana || ''}`;
    const btnDel = document.createElement('button'); btnDel.textContent = 'Borrar';
    btnDel.addEventListener('click', () => deleteReserva(r.id));
    li.appendChild(btnDel);
    ul.appendChild(li);
  });
  reservasList.appendChild(ul);
}

async function submitReserva(e) {
  e.preventDefault();
  const payload = {
    fecha: reservaFecha.value,
    motivo: reservaMotivo.value,
    numeroAsistentes: reservaAsistentes.value ? Number(reservaAsistentes.value) : null,
    aulaId: reservaAula.value ? Number(reservaAula.value) : null,
    horarioId: reservaHorario.value ? Number(reservaHorario.value) : null
  };
  try {
    const res = await apiFetch('/api/reservas', {method: 'POST', body: payload});
    if (!res.ok) throw new Error(await res.text());
    reservaForm.reset(); loadReservas();
  } catch (err) { reservasList.innerHTML = `<pre>${err.message}</pre>`; }
}

async function deleteReserva(id) {
  if (!confirm('Borrar reserva ' + id + '?')) return;
  try {
    const res = await apiFetch(`/api/reservas/${id}`, {method: 'DELETE'});
    if (!res.ok) throw new Error(await res.text());
    loadReservas();
  } catch (err) { reservasList.innerHTML = `<pre>${err.message}</pre>`; }
}

// --- USUARIOS ---
async function loadUsuarios() {
  try {
    const res = await apiFetch('/api/usuarios');
    const data = await res.json();
    renderUsuarios(data);
  } catch (err) { usuariosList.innerHTML = `<pre>${err.message}</pre>`; }
}

function renderUsuarios(list) {
  usuariosList.innerHTML = '';
  const ul = document.createElement('ul');
  list.forEach(u => {
    const li = document.createElement('li');
    li.innerHTML = `${u.nombre} — ${u.email} — ${u.role || ''}`;
    const btnDel = document.createElement('button'); btnDel.textContent = 'Borrar';
    btnDel.addEventListener('click', () => deleteUsuario(u.id));
    ul.appendChild(li); li.appendChild(btnDel);
  });
  usuariosList.appendChild(ul);
}

async function submitUsuario(e) {
  e.preventDefault();
  const payload = {
    nombre: usuarioNombre.value,
    email: usuarioEmail.value,
    password: usuarioPassword.value,
    role: usuarioRole.value
  };
  try {
    const res = await apiFetch('/api/auth/register', {method: 'POST', body: payload});
    if (!res.ok) throw new Error(await res.text());
    usuarioForm.reset(); loadUsuarios();
  } catch (err) { usuariosList.innerHTML = `<pre>${err.message}</pre>`; }
}

async function deleteUsuario(id) {
  if (!confirm('Borrar usuario ' + id + '?')) return;
  try {
    const res = await apiFetch(`/api/usuarios/${id}`, {method: 'DELETE'});
    if (!res.ok) throw new Error(await res.text());
    loadUsuarios();
  } catch (err) { usuariosList.innerHTML = `<pre>${err.message}</pre>`; }
}

// Helper para poblar selects
function populateAulaSelect(aulas) {
  if (!reservaAula) return;
  reservaAula.innerHTML = '';
  aulas.forEach(a => { const o = document.createElement('option'); o.value = a.id; o.textContent = a.nombre; reservaAula.appendChild(o); });
}

function populateHorarioSelect(hs) {
  if (!reservaHorario) return;
  reservaHorario.innerHTML = '';
  hs.forEach(h => { const o = document.createElement('option'); o.value = h.id; o.textContent = `${h.diaSemana} ${h.horaInicio || ''}`; reservaHorario.appendChild(o); });
}

// Event listeners
loginForm.addEventListener('submit', (e) => { e.preventDefault(); login(document.getElementById('username').value.trim(), document.getElementById('password').value); });
btnClearToken.addEventListener('click', () => { clearToken(); });
aulaForm.addEventListener('submit', submitAula);
horarioForm.addEventListener('submit', submitHorario);
reservaForm.addEventListener('submit', submitReserva);
usuarioForm.addEventListener('submit', submitUsuario);

// Reset buttons (limpiar formularios)
document.getElementById('aulaReset')?.addEventListener('click', () => { aulaForm.reset(); aulaId.value=''; });
document.getElementById('horarioReset')?.addEventListener('click', () => { horarioForm.reset(); horarioId.value=''; });
document.getElementById('reservaReset')?.addEventListener('click', () => { reservaForm.reset(); });
document.getElementById('usuarioReset')?.addEventListener('click', () => { usuarioForm.reset(); usuarioId.value=''; });
document.getElementById('btnGuest')?.addEventListener('click', () => { authInfo.textContent = 'Modo demo: token simulado'; localStorage.setItem('jwt_token','DEMO'); });

// Estado inicial
if (localStorage.getItem('jwt_token')) { authInfo.textContent = 'Autenticado — token en localStorage'; } else { authInfo.textContent = 'No autenticado'; }

// Mostrar vista inicial
showView('aulas');
