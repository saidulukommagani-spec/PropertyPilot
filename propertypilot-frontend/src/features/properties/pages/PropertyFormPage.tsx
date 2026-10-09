import { useEffect } from 'react';
import { Alert, Button, Card, CardContent, Skeleton, Stack, TextField, Typography } from '@mui/material';
import { zodResolver } from '@hookform/resolvers/zod';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import { z } from 'zod';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { propertiesApi, type PropertyMutation } from '../../../api/properties';
import { customerKeys, useCustomerProfile, useCustomerProperty } from '../../customer-portal/hooks/useCustomerPortal';
import { CustomerPageHeading, CustomerQueryError } from '../../customer-portal/components/CustomerStates';

const schema = z.object({
  title: z.string().trim().min(2, 'Enter a property name.').max(160),
  propertyType: z.string().trim().min(2, 'Enter a property type.').max(80),
  listingStatus: z.string().trim().max(80),
  price: z.string().refine((value) => !value || (Number.isFinite(Number(value)) && Number(value) >= 0), 'Enter a valid non-negative price.'),
});
type Values = z.infer<typeof schema>;

export default function PropertyFormPage() {
  const { id = '' } = useParams<{ id: string }>();
  const editing = Boolean(id);
  const navigate = useNavigate();
  const client = useQueryClient();
  const profile = useCustomerProfile();
  const property = useCustomerProperty(id);
  const form = useForm<Values>({ resolver: zodResolver(schema), defaultValues: { title: '', propertyType: '', listingStatus: '', price: '' } });
  const mutation = useMutation({
    mutationFn: async (values: Values) => {
      const request: PropertyMutation = { title: values.title.trim(), propertyType: values.propertyType.trim(), listingStatus: values.listingStatus.trim() || undefined, price: values.price ? Number(values.price) : undefined, status: 'ACTIVE' };
      if (editing) {
        if (!property.data) throw new Error('Property details are not available.');
        return propertiesApi.update(id, property.data.customerId, request);
      }
      return propertiesApi.create(request);
    },
    onSuccess: async (saved) => {
      await Promise.all([client.invalidateQueries({ queryKey: customerKeys.properties }), client.invalidateQueries({ queryKey: customerKeys.property(saved.propertyId) })]);
      navigate(`/customer/properties/${saved.propertyId}`, { replace: true });
    },
  });

  useEffect(() => {
    if (property.data) form.reset({ title: property.data.title, propertyType: property.data.propertyType ?? '', listingStatus: property.data.listingStatus ?? '', price: property.data.price == null ? '' : String(property.data.price) });
  }, [property.data, form]);

  const loading = editing ? property.isLoading : profile.isLoading;
  if (loading) return <Stack spacing={2}><Skeleton height={44} /><Skeleton variant="rounded" height={310} /></Stack>;
  if (editing && (property.isError || !property.data)) return <Stack spacing={2}><Button component={Link} to="/customer/properties">Back to properties</Button><CustomerQueryError error={property.error} retry={() => void property.refetch()} /></Stack>;
  if (!editing && (profile.isError || !profile.data)) return <Stack spacing={2}><Button component={Link} to="/customer/properties">Back to properties</Button><CustomerQueryError error={profile.error} retry={() => void profile.refetch()} /></Stack>;

  return <Stack spacing={3}>
    <CustomerPageHeading title={editing ? 'Edit property' : 'Add property'} subtitle={editing ? 'Update your property details.' : 'Register a property to request and track services.'} action={<Button component={Link} to={editing ? `/customer/properties/${id}` : '/customer/properties'}>Cancel</Button>} />
    {mutation.isError && <Alert severity="error">{mutation.error instanceof Error ? mutation.error.message : 'Property could not be saved.'}</Alert>}
    <Card variant="outlined"><CardContent><Stack component="form" spacing={2.5} noValidate onSubmit={(event) => { void form.handleSubmit((values) => mutation.mutateAsync(values))(event); }}>
      <Typography variant="h6" fontWeight={700}>Property information</Typography>
      <TextField label="Property name" {...form.register('title')} error={Boolean(form.formState.errors.title)} helperText={form.formState.errors.title?.message} required />
      <TextField label="Property type" {...form.register('propertyType')} error={Boolean(form.formState.errors.propertyType)} helperText={form.formState.errors.propertyType?.message} required />
      <TextField label="Listing status" {...form.register('listingStatus')} error={Boolean(form.formState.errors.listingStatus)} helperText={form.formState.errors.listingStatus?.message} />
      <TextField label="Property price" type="number" inputProps={{ min: 0, step: 0.01 }} {...form.register('price')} error={Boolean(form.formState.errors.price)} helperText={form.formState.errors.price?.message} />
      <Button type="submit" variant="contained" disabled={mutation.isPending || (editing && !property.data) || (!editing && !profile.data)}>{mutation.isPending ? 'Saving…' : editing ? 'Save property' : 'Add property'}</Button>
    </Stack></CardContent></Card>
  </Stack>;
}
