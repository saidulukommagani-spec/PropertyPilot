import { Alert, Box, Button, Stack } from '@mui/material';
import { Link, Outlet } from 'react-router-dom';
import { getUserRoles } from '../../api/auth';
import { useAuth } from '../../features/auth/AuthContext';

export default function RoleGuard({ allowedRoles }: { allowedRoles: string[] }) {
  const { user } = useAuth();
  const roles = getUserRoles(user);
  if (roles.some((role) => allowedRoles.includes(role))) return <Outlet />;
  const workspace = roles.includes('ADMIN') || roles.includes('OPERATIONS')
    ? { path: '/staff', label: 'Operations workspace' }
    : roles.includes('AGENT')
      ? { path: '/agent/dashboard', label: 'Agent workspace' }
      : roles.includes('CUSTOMER')
        ? { path: '/customer/dashboard', label: 'Customer portal' }
        : null;
  return <Box sx={{ maxWidth: 680, mx: 'auto', mt: 8, px: 3 }}>
    <Stack spacing={2}>
      <Alert severity="warning">Your account does not have an authorized role for this page. Contact an administrator.{!roles.length && ' No roles are assigned to this account.'}</Alert>
      {workspace && <Button component={Link} to={workspace.path} variant="contained" sx={{ alignSelf: 'flex-start' }}>Go to {workspace.label}</Button>}
    </Stack>
  </Box>;
}
