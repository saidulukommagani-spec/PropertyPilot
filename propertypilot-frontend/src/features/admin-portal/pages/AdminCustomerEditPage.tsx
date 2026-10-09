import { useEffect } from 'react';
import { Alert, Button, Card, CardContent, Skeleton, Stack, TextField, Typography } from '@mui/material';
import ArrowBackRounded from '@mui/icons-material/ArrowBackRounded';
import { zodResolver } from '@hookform/resolvers/zod';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { z } from 'zod';
import { apiClient } from '../../../api/client';
import { adminPortalKeys } from '../hooks/useAdminPortal';

interface CustomerRecord { customerId: string; userId: string; fullName: string; email: string; mobileNumber: string; status: string; customerType: string }
const schema = z.object({
  fullName: z.string().trim().min(2, 'Enter the customer name.').max(150),
  email: z.string().trim().email('Enter a valid email.').max(255),
  mobileNumber: z.string().trim().min(1, 'Enter a phone number.').max(20),
});
type Values = z.infer<typeof schema>;

export default function AdminCustomerEditPage() {
  const { id = '' } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const client = useQueryClient();
  const customer = useQuery({ queryKey: ['admin-customer-edit', id], queryFn: async () => (await apiClient.get<CustomerRecord>(`/api/v1/admin/customers/${id}`)).data, enabled: Boolean(id) });
  const form = useForm<Values>({ resolver: zodResolver(schema), defaultValues: { fullName: '', email: '', mobileNumber: '' } });
  useEffect(() => { if (customer.data) form.reset({ fullName: customer.data.fullName, email: customer.data.email, mobileNumber: customer.data.mobileNumber }); }, [customer.data, form]);
  const mutation = useMutation({
    mutationFn: async (values: Values) => {
      if (!customer.data) throw new Error('Customer details are not available.');
      return apiClient.put(`/api/v1/users/${customer.data.userId}`, { fullName: values.fullName.trim(), email: values.email.trim().toLowerCase(), mobileNumber: values.mobileNumber.trim() });
    },
    onSuccess: async () => {
      await Promise.all([client.invalidateQueries({ queryKey: adminPortalKeys.list('customers') }), client.invalidateQueries({ queryKey: adminPortalKeys.detail('customers', id) }), client.invalidateQueries({ queryKey: ['admin-customer-edit', id] })]);
      navigate(`/staff/customers/${id}`, { replace: true });
    },
  });
  if (customer.isLoading) return <Stack spacing={2}><Skeleton height={44} /><Skeleton variant="rounded" height={300} /></Stack>;
  if (customer.isError || !customer.data) return <Stack spacing={2}><Button component={Link} to="/staff/customers" startIcon={<ArrowBackRounded />}>Back to customers</Button><Alert severity="error" action={<Button color="inherit" size="small" onClick={() => void customer.refetch()}>Retry</Button>}>{customer.error instanceof Error ? customer.error.message : 'Customer could not be loaded.'}</Alert></Stack>;
  return <Stack spacing={3}>
    <Button component={Link} to={`/staff/customers/${id}`} startIcon={<ArrowBackRounded />} sx={{ alignSelf: 'flex-start', ml: -1 }}>Back to customer</Button>
    <Stack><Typography variant="h4" fontWeight={750}>Edit customer contact details</Typography><Typography color="text.secondary">Update {customer.data.fullName}’s name, email, and phone.</Typography></Stack>
    {mutation.isError && <Alert severity="error">{mutation.error instanceof Error ? mutation.error.message : 'Customer details could not be saved.'}</Alert>}
    <Card variant="outlined"><CardContent><Stack component="form" spacing={2.5} noValidate onSubmit={(event) => { void form.handleSubmit((values) => mutation.mutateAsync(values))(event); }}>
      <TextField label="Full name" required {...form.register('fullName')} error={Boolean(form.formState.errors.fullName)} helperText={form.formState.errors.fullName?.message} />
      <TextField label="Email address" type="email" required {...form.register('email')} error={Boolean(form.formState.errors.email)} helperText={form.formState.errors.email?.message} />
      <TextField label="Phone number" type="tel" required {...form.register('mobileNumber')} error={Boolean(form.formState.errors.mobileNumber)} helperText={form.formState.errors.mobileNumber?.message} />
      <Stack direction="row" spacing={1}><Button type="submit" variant="contained" disabled={mutation.isPending}>{mutation.isPending ? 'Saving…' : 'Save changes'}</Button><Button component={Link} to={`/staff/customers/${id}`} disabled={mutation.isPending}>Cancel</Button></Stack>
    </Stack></CardContent></Card>
  </Stack>;
}
