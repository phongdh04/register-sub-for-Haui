/** Base URL backend Java (override bằng VITE_API_BASE_URL trong .env). */
export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

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
