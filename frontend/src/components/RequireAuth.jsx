import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { hasAnyRole } from '../config/api';

/**
 * Chặn portal khi chưa đăng nhập hoặc sai role (JWT trong localStorage).
 */
export default function RequireAuth({ children, roles }) {
  const location = useLocation();
  const token = localStorage.getItem('jwt_token');

  if (!token) {
    return <Navigate to="/login" replace state={{ from: location.pathname }} />;
  }

  if (roles?.length && !hasAnyRole(roles)) {
    return <Navigate to="/login" replace state={{ from: location.pathname, forbidden: true }} />;
  }

  return children;
}
