/**
 * Base URL backend Java (VITE_API_BASE_URL trong .env).
 * Dev không gán env: dùng '' + proxy Vite (/api → localhost:8080) để tránh gọi nhầm cổng 5173.
 */
export const API_BASE_URL = (() => {
  const v = import.meta.env.VITE_API_BASE_URL;
  if (v != null && String(v).trim() !== '') {
    return String(v).trim().replace(/\/$/, '');
  }
  if (import.meta.env.DEV) {
    return '';
  }
  return 'http://localhost:8080';
})();

export function authHeaders() {
  const token = localStorage.getItem('jwt_token');
  const headers = { 'Content-Type': 'application/json' };
  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }
  return headers;
}

export function clearSession() {
  localStorage.removeItem('jwt_token');
  localStorage.removeItem('user_roles');
  localStorage.removeItem('username');
}

export function hasAnyRole(roleNames) {
  let roles = [];
  try {
    roles = JSON.parse(localStorage.getItem('user_roles') || '[]');
  } catch {
    roles = [];
  }
  return roleNames.some((r) => roles.includes(r));
}
