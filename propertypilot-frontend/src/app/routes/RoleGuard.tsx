import { Alert, Box } from '@mui/material';
import { Outlet } from 'react-router-dom';
import { getUserRoles } from '../../api/auth';
import { useAuth } from '../../features/auth/AuthContext';

export default function RoleGuard({ allowedRoles }: { allowedRoles: string[] }) {
  const { user } = useAuth();
  const roles = getUserRoles(user);
  if (roles.some((role) => allowedRoles.includes(role))) return <Outlet />;
  return <Box sx={{ maxWidth: 680, mx: 'auto', mt: 8, px: 3 }}>
    <Alert severity="warning">Your account does not have an authorized role for this page. Contact an administrator.{!roles.length && ' No roles are assigned to this account.'}</Alert>
  </Box>;
}
