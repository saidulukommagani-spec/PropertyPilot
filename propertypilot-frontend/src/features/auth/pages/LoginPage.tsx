import { useState, type FormEvent } from 'react';
import { Alert, Box, Button, Paper, Stack, TextField, Typography } from '@mui/material';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../AuthContext';
import { authApi, getUserRoles } from '../../../api/auth';
import BrandLogo from '../../../components/layouts/BrandLogo';

interface LoginLocationState { registrationComplete?: boolean }

function getWorkspaceDestination(roles: string[]): string {
  return roles.includes('CUSTOMER') && !roles.some((role) => ['ADMIN', 'OPERATIONS', 'AGENT'].includes(role))
    ? '/customer/dashboard'
    : roles.includes('AGENT') && !roles.some((role) => ['ADMIN', 'OPERATIONS'].includes(role))
      ? '/agent/dashboard'
      : '/staff';
}

export default function LoginPage() {
  const { signIn, isSigningIn } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError('');
    try {
      await signIn({ email: email.trim(), password });
      const currentUser = await authApi.currentUser();
      const roles = getUserRoles(currentUser);
      const destination = getWorkspaceDestination(roles);
      navigate(destination, { replace: true });
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : 'Sign in failed. Please try again.');
    }
  }

  return <Box className="login-background" sx={{ minHeight: '100vh', display: 'grid', gridTemplateColumns: { xs: '1fr', md: 'minmax(0, 1.05fr) minmax(420px, .95fr)' }, bgcolor: '#F4F5F4' }}>
    <Box component="section" aria-label="PropertyPilot brand" sx={{ minHeight: { xs: 180, md: '100vh' }, px: { xs: 3, sm: 6, md: 8 }, py: { xs: 3, md: 6 }, display: 'flex', flexDirection: 'column', justifyContent: { xs: 'center', md: 'space-between' }, color: 'white', overflow: 'hidden', position: 'relative', background: 'radial-gradient(ellipse at 80% 85%, rgba(22,127,131,.32), transparent 38%), linear-gradient(145deg, #071F31, #0B2D43 62%, #123B50)' }}>
      <Box aria-hidden="true" sx={{ position: 'absolute', width: 520, height: 520, border: '1px solid rgba(229,191,112,.13)', borderRadius: '50%', right: -240, bottom: -260 }} />
      <Box sx={{ position: 'relative', width: '100%', display: 'flex', justifyContent: 'center' }}><BrandLogo variant="stacked-dark" alt="PropertyPilot — Your property. On autopilot." sx={{ width: { xs: 235, sm: 285, md: 330 }, maxWidth: '100%', height: 'auto' }} /></Box>
      <Box sx={{ display: { xs: 'none', md: 'block' }, maxWidth: 520, position: 'relative', py: 8 }}>
        <Typography variant="overline" sx={{ color: '#E5BF70', fontWeight: 750, letterSpacing: 2 }}>PROPERTY CARE, REIMAGINED</Typography>
        <Typography variant="h2" sx={{ color: 'white', mt: 1, maxWidth: 500 }}>A considered approach to property operations.</Typography>
        <Typography sx={{ color: 'rgba(255,255,255,.74)', mt: 2, maxWidth: 430 }}>One trusted workspace for properties, service delivery and the people who keep them running.</Typography>
        <Box aria-hidden="true" sx={{ mt: 3, width: 72, height: 3, borderRadius: 2, background: 'linear-gradient(90deg, #E5BF70 0 42%, #38A6A1 42% 100%)' }} />
      </Box>
      <Typography variant="caption" sx={{ display: { xs: 'none', md: 'block' }, color: 'rgba(255,255,255,.56)', position: 'relative' }}>Your property. On autopilot.</Typography>
    </Box>
    <Box component="main" sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', p: { xs: 2.5, sm: 5, md: 7 } }}>
    <Paper elevation={0} sx={{ width: '100%', maxWidth: 440, p: { xs: 3, sm: 4 }, border: '1px solid', borderColor: 'divider', borderRadius: 3, boxShadow: '0 18px 48px rgba(7,31,49,.085)' }}>
      <Stack spacing={2.5}>
        <Box><Typography variant="overline" sx={{ color: '#A8792E', fontWeight: 750, letterSpacing: 1.5 }}>WELCOME BACK</Typography><Typography variant="h3" sx={{ mt: 0.5 }}>Sign in</Typography><Typography color="text.secondary" sx={{ mt: 1 }}>Access your PropertyPilot customer or staff workspace.</Typography></Box>
        {(location.state as LoginLocationState | null)?.registrationComplete && <Alert severity="success">Your customer account is ready. Sign in to continue.</Alert>}
        {error && <Alert severity="error">{error}</Alert>}
        <Box component="form" onSubmit={handleSubmit}><Stack spacing={1.75}>
          <TextField label="Email address" type="email" autoComplete="username" required fullWidth value={email} onChange={(event) => setEmail(event.target.value)} />
          <TextField label="Password" type="password" autoComplete="current-password" required fullWidth value={password} onChange={(event) => setPassword(event.target.value)} />
          <Button type="submit" variant="contained" size="large" disabled={isSigningIn}>{isSigningIn ? 'Signing in…' : 'Sign in'}</Button>
        </Stack></Box>
        <Typography variant="body2" color="text.secondary">Need a customer account? <Button component={Link} to="/register" size="small">Register</Button></Typography>
      </Stack>
    </Paper>
    </Box>
  </Box>;
}
