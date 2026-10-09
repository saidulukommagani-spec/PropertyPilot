import { useEffect } from 'react';
import { Alert, Box, Button, Card, CardContent, MenuItem, Skeleton, Stack, TextField, Typography } from '@mui/material';
import ArrowBackRounded from '@mui/icons-material/ArrowBackRounded';
import { zodResolver } from '@hookform/resolvers/zod';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { Controller, useForm } from 'react-hook-form';
import { z } from 'zod';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { propertiesApi } from '../../../api/properties';
import { adminPortalKeys } from '../hooks/useAdminPortal';

const schema = z.object({
  title: z.string().trim().min(2, 'Enter a property name.').max(160),
  propertyType: z.string().trim().min(2, 'Enter a property type.').max(80),
  listingStatus: z.string().max(80),
  price: z.string().refine((value) => !value || (Number.isFinite(Number(value)) && Number(value) >= 0), 'Enter a valid non-negative price.'),
  status: z.string().trim().min(1, 'Select a property status.').max(40),
});
type Values = z.infer<typeof schema>;

export default function AdminPropertyEditPage() {
  const { id = '' } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const client = useQueryClient();
  const property = useQuery({ queryKey: ['admin-property-edit', id], queryFn: () => propertiesApi.get(id), enabled: Boolean(id) });
  const form = useForm<Values>({ resolver: zodResolver(schema), defaultValues: { title: '', propertyType: '', listingStatus: '', price: '', status: 'ACTIVE' } });
  useEffect(() => {
    if (property.data) form.reset({ title: property.data.title, propertyType: property.data.propertyType, listingStatus: property.data.listingStatus ?? '', price: property.data.price == null ? '' : String(property.data.price), status: property.data.status });
  }, [property.data, form]);
  const mutation = useMutation({
    mutationFn: (values: Values) => {
      if (!property.data) throw new Error('Property details are not available.');
      return propertiesApi.update(id, property.data.customerId, { title: values.title.trim(), propertyType: values.propertyType.trim(), listingStatus: values.listingStatus.trim() || undefined, price: values.price ? Number(values.price) : undefined, status: values.status });
    },
    onSuccess: async () => {
      await Promise.all([client.invalidateQueries({ queryKey: adminPortalKeys.list('properties') }), client.invalidateQueries({ queryKey: ['admin-portal', 'properties', id] })]);
      navigate(`/staff/properties/${id}`, { replace: true });
    },
  });
  if (property.isLoading) return <Stack spacing={2}><Skeleton height={44} /><Skeleton variant="rounded" height={320} /></Stack>;
  if (property.isError || !property.data) return <Stack spacing={2}><Button component={Link} to="/staff/properties" startIcon={<ArrowBackRounded />}>Back to properties</Button><Alert severity="error" action={<Button color="inherit" size="small" onClick={() => void property.refetch()}>Retry</Button>}>{property.error instanceof Error ? property.error.message : 'Property could not be loaded.'}</Alert></Stack>;
  if (property.data.status === 'ARCHIVED') return <Stack spacing={2}><Button component={Link} to="/staff/properties" startIcon={<ArrowBackRounded />}>Back to properties</Button><Alert severity="info">Restore this property before editing it.</Alert></Stack>;
  return <Stack spacing={3}>
    <Button component={Link} to={`/staff/properties/${id}`} startIcon={<ArrowBackRounded />} sx={{ alignSelf: 'flex-start', ml: -1 }}>Back to property</Button>
    <Box><Typography variant="h4" fontWeight={750}>Edit property</Typography><Typography color="text.secondary" sx={{ mt: .5 }}>Update {property.data.title}. Customer ownership stays unchanged.</Typography></Box>
    {mutation.isError && <Alert severity="error">{mutation.error instanceof Error ? mutation.error.message : 'Property could not be saved.'}</Alert>}
    <Card variant="outlined"><CardContent><Stack component="form" spacing={2.5} noValidate onSubmit={(event) => { void form.handleSubmit((values) => mutation.mutateAsync(values))(event); }}>
      <TextField label="Property name" required {...form.register('title')} error={Boolean(form.formState.errors.title)} helperText={form.formState.errors.title?.message} />
      <TextField label="Property type" required {...form.register('propertyType')} error={Boolean(form.formState.errors.propertyType)} helperText={form.formState.errors.propertyType?.message} />
      <TextField label="Listing status" {...form.register('listingStatus')} error={Boolean(form.formState.errors.listingStatus)} helperText={form.formState.errors.listingStatus?.message} />
      <TextField label="Property price" type="number" inputProps={{ min: 0, step: 0.01 }} {...form.register('price')} error={Boolean(form.formState.errors.price)} helperText={form.formState.errors.price?.message} />
      <Controller name="status" control={form.control} render={({ field }) => <TextField select {...field} label="Property status">{[...new Set(['ACTIVE', 'AVAILABLE', 'INACTIVE', property.data.status])].map((status) => <MenuItem key={status} value={status}>{status.replaceAll('_', ' ')}</MenuItem>)}</TextField>} />
      <Stack direction="row" spacing={1}><Button type="submit" variant="contained" disabled={mutation.isPending}>{mutation.isPending ? 'Saving…' : 'Save changes'}</Button><Button component={Link} to={`/staff/properties/${id}`} disabled={mutation.isPending}>Cancel</Button></Stack>
    </Stack></CardContent></Card>
  </Stack>;
}
