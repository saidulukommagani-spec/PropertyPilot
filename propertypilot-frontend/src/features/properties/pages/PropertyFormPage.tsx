import { useEffect } from 'react';
import { Alert, Button, Card, CardContent, MenuItem, Skeleton, Stack, TextField, Typography } from '@mui/material';
import { zodResolver } from '@hookform/resolvers/zod';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { Controller, useForm } from 'react-hook-form';
import { z } from 'zod';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { propertiesApi, type PropertyMutation } from '../../../api/properties';
import { customerKeys, useCustomerProfile, useCustomerProperty } from '../../customer-portal/hooks/useCustomerPortal';
import { CustomerPageHeading, CustomerQueryError } from '../../customer-portal/components/CustomerStates';

const schema = z.object({
  title: z.string().trim().min(2, 'Enter a property name.').max(160),
  propertyType: z.string().trim().min(2, 'Choose a property type.').max(80),
  addressLine1: z.string().trim().min(5, 'Enter the property address.').max(255),
  addressLine2: z.string().trim().max(255),
  localityName: z.string().trim().min(2, 'Enter the locality or neighborhood.').max(120),
  city: z.string().trim().min(2, 'Enter the city or town.').max(120),
  district: z.string().trim().min(2, 'Enter the district.').max(120),
  state: z.string().trim().min(2, 'Enter the state.').max(120),
  postalCode: z.string().trim().regex(/^\d{6}$/, 'Enter a valid 6-digit Indian PIN code.'),
  status: z.enum(['AVAILABLE', 'OCCUPIED', 'UNDER_MAINTENANCE', 'INACTIVE']),
  price: z.string().refine((value) => !value || (Number.isFinite(Number(value)) && Number(value) >= 0), 'Enter a valid non-negative price.'),
});
type Values = z.infer<typeof schema>;

const propertyTypes = [
  ['HOUSE', 'House'], ['APARTMENT', 'Apartment'], ['VILLA', 'Villa'], ['TOWNHOUSE', 'Townhouse'],
  ['COMMERCIAL', 'Commercial property'], ['INDUSTRIAL', 'Industrial property'], ['LAND', 'Land'], ['OTHER', 'Other'],
] as const;
const propertyStatuses = [
  ['AVAILABLE', 'Available for services'], ['OCCUPIED', 'Occupied'],
  ['UNDER_MAINTENANCE', 'Under maintenance'], ['INACTIVE', 'Inactive'],
] as const;
function supportedPropertyStatus(status?: string): Values['status'] {
  return propertyStatuses.some(([value]) => value === status) ? status as Values['status'] : 'AVAILABLE';
}

export default function PropertyFormPage() {
  const { id = '' } = useParams<{ id: string }>();
  const editing = Boolean(id);
  const navigate = useNavigate();
  const client = useQueryClient();
  const profile = useCustomerProfile();
  const property = useCustomerProperty(id);
  const form = useForm<Values>({ resolver: zodResolver(schema), defaultValues: { title: '', propertyType: '', addressLine1: '', addressLine2: '', localityName: '', city: '', district: '', state: '', postalCode: '', status: 'AVAILABLE', price: '' } });
  const mutation = useMutation({
    mutationFn: async (values: Values) => {
      const request: PropertyMutation = { title: values.title.trim(), propertyType: values.propertyType, addressLine1: values.addressLine1.trim(), addressLine2: values.addressLine2.trim() || undefined, localityName: values.localityName.trim(), city: values.city.trim(), district: values.district.trim(), state: values.state.trim(), postalCode: values.postalCode.trim(), countryCode: 'IN', listingStatus: property.data?.listingStatus ?? 'DRAFT', price: values.price ? Number(values.price) : undefined, status: values.status };
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
    if (property.data) form.reset({ title: property.data.title, propertyType: property.data.propertyType ?? '', addressLine1: property.data.addressLine1 ?? '', addressLine2: property.data.addressLine2 ?? '', localityName: property.data.localityName ?? '', city: property.data.city ?? '', district: property.data.district ?? '', state: property.data.state ?? '', postalCode: property.data.postalCode ?? '', status: supportedPropertyStatus(property.data.status), price: property.data.price == null ? '' : String(property.data.price) });
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
      <Controller name="propertyType" control={form.control} render={({ field }) => <TextField select {...field} label="Property type" required error={Boolean(form.formState.errors.propertyType)} helperText={form.formState.errors.propertyType?.message || 'Choose the type that best describes this property.'}>{propertyTypes.map(([value, label]) => <MenuItem key={value} value={value}>{label}</MenuItem>)}</TextField>} />
      <Typography variant="subtitle2" fontWeight={700} sx={{ pt: 1 }}>Property address</Typography>
      <TextField label="Address line 1" placeholder="House or flat number and street" {...form.register('addressLine1')} error={Boolean(form.formState.errors.addressLine1)} helperText={form.formState.errors.addressLine1?.message || 'Helps us identify the property and coordinate services when needed.'} required />
      <TextField label="Apartment, suite or additional details" {...form.register('addressLine2')} error={Boolean(form.formState.errors.addressLine2)} helperText={form.formState.errors.addressLine2?.message} />
      <TextField label="Locality or neighborhood" {...form.register('localityName')} error={Boolean(form.formState.errors.localityName)} helperText={form.formState.errors.localityName?.message} required />
      <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
        <TextField fullWidth label="City or town" {...form.register('city')} error={Boolean(form.formState.errors.city)} helperText={form.formState.errors.city?.message} required />
        <TextField fullWidth label="District" {...form.register('district')} error={Boolean(form.formState.errors.district)} helperText={form.formState.errors.district?.message} required />
      </Stack>
      <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
        <TextField fullWidth label="State" {...form.register('state')} error={Boolean(form.formState.errors.state)} helperText={form.formState.errors.state?.message} required />
        <TextField fullWidth label="PIN code" inputProps={{ inputMode: 'numeric', maxLength: 6 }} {...form.register('postalCode')} error={Boolean(form.formState.errors.postalCode)} helperText={form.formState.errors.postalCode?.message || '6-digit PIN code'} required />
      </Stack>
      <TextField select label="Country" value="IN" disabled helperText="PropertyPilot currently supports service addresses in India."><MenuItem value="IN">India</MenuItem></TextField>
      <Controller name="status" control={form.control} render={({ field }) => <TextField select {...field} label="Property status" required error={Boolean(form.formState.errors.status)} helperText={form.formState.errors.status?.message}>{propertyStatuses.map(([value, label]) => <MenuItem key={value} value={value}>{label}</MenuItem>)}</TextField>} />
      <TextField label="Property price" type="number" inputProps={{ min: 0, step: 0.01 }} {...form.register('price')} error={Boolean(form.formState.errors.price)} helperText={form.formState.errors.price?.message} />
      <Button type="submit" variant="contained" disabled={mutation.isPending || (editing && !property.data) || (!editing && !profile.data)}>{mutation.isPending ? 'Saving…' : editing ? 'Save property' : 'Add property'}</Button>
    </Stack></CardContent></Card>
  </Stack>;
}
