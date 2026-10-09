import { useState } from 'react';
import { Alert, Box, Button, Container, Paper, Stack, TextField, Typography } from '@mui/material';
import { zodResolver } from '@hookform/resolvers/zod';
import { useMutation } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import { Link, useNavigate } from 'react-router-dom';
import { z } from 'zod';
import { authApi } from '../../../api/auth';

const schema = z.object({
  fullName: z.string().trim().min(2, 'Enter your full name.').max(150),
  email: z.string().trim().email('Enter a valid email address.').max(255),
  mobileNumber: z.string().trim().min(1, 'Enter your phone number.').max(20),
  password: z.string().min(8, 'Use at least 8 characters.').max(100),
  confirmPassword: z.string(),
}).refine((values) => values.password === values.confirmPassword, { path: ['confirmPassword'], message: 'Passwords do not match.' });
type Values = z.infer<typeof schema>;

export default function CustomerRegistrationPage() {
  const navigate = useNavigate();
  const [error, setError] = useState('');
  const form = useForm<Values>({ resolver: zodResolver(schema), defaultValues: { fullName: '', email: '', mobileNumber: '', password: '', confirmPassword: '' } });
  const registration = useMutation({
    mutationFn: (values: Values) => authApi.registerCustomer({ fullName: values.fullName.trim(), email: values.email.trim().toLowerCase(), mobileNumber: values.mobileNumber.trim(), password: values.password }),
    onSuccess: () => navigate('/login', { replace: true, state: { registrationComplete: true } }),
    onError: (caught) => setError(caught instanceof Error ? caught.message : 'Your account could not be created.'),
  });
  return <Box className="login-background"><Container maxWidth="sm" sx={{ py: { xs: 4, md: 8 } }}>
    <Paper elevation={0} sx={{ p: { xs: 3, sm: 5 }, border: '1px solid', borderColor: 'divider', borderRadius: 4 }}>
      <Stack spacing={3}>
        <Box><Typography variant="overline" color="primary.main" fontWeight={800} letterSpacing={1.4}>PROPERTY PILOT</Typography><Typography variant="h4" fontWeight={750} sx={{ mt: 1 }}>Create your customer account</Typography><Typography color="text.secondary" sx={{ mt: 1 }}>Register to manage your properties and service requests.</Typography></Box>
        {error && <Alert severity="error">{error}</Alert>}
        <Stack component="form" spacing={2} noValidate onSubmit={(event) => { setError(''); void form.handleSubmit((values) => registration.mutateAsync(values))(event); }}>
          <TextField label="Full name" autoComplete="name" required {...form.register('fullName')} error={Boolean(form.formState.errors.fullName)} helperText={form.formState.errors.fullName?.message} />
          <TextField label="Email address" type="email" autoComplete="email" required {...form.register('email')} error={Boolean(form.formState.errors.email)} helperText={form.formState.errors.email?.message} />
          <TextField label="Phone number" type="tel" autoComplete="tel" required {...form.register('mobileNumber')} error={Boolean(form.formState.errors.mobileNumber)} helperText={form.formState.errors.mobileNumber?.message} />
          <TextField label="Password" type="password" autoComplete="new-password" required {...form.register('password')} error={Boolean(form.formState.errors.password)} helperText={form.formState.errors.password?.message || 'At least 8 characters.'} />
          <TextField label="Confirm password" type="password" autoComplete="new-password" required {...form.register('confirmPassword')} error={Boolean(form.formState.errors.confirmPassword)} helperText={form.formState.errors.confirmPassword?.message} />
          <Button type="submit" variant="contained" size="large" disabled={registration.isPending}>{registration.isPending ? 'Creating account…' : 'Create account'}</Button>
        </Stack>
        <Typography variant="body2" color="text.secondary">Already registered? <Button component={Link} to="/login" size="small">Sign in</Button></Typography>
      </Stack>
    </Paper>
  </Container></Box>;
}
