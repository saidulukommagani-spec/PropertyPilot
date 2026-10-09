import { Alert, Box } from '@mui/material';
import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { getUserRoles } from './authApi';
import { useAuth } from './AuthContext';

export function ProtectedRoute({ allowedRoles }: { allowedRoles?: string[] }) {
  const { isAuthenticated, user } = useAuth();
  const location = useLocation();
  if (!isAuthenticated) return <Navigate to="/login" replace state={{ from: location.pathname }} />;
  if (allowedRoles && !getUserRoles(user).some((role) => allowedRoles.includes(role))) {
    const hasRoles = getUserRoles(user).length > 0;
    return <Box sx={{ maxWidth: 680, mx: 'auto', mt: 8, px: 3 }}><Alert severity="warning">Your account does not have an authorized staff role for this portal. Contact an administrator.{!hasRoles && ' The current profile response does not include role information.'}</Alert></Box>;
  }
  return <Outlet />;
}
