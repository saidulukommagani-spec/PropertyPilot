import { useEffect, useRef, useState } from 'react';
import { zodResolver } from '@hookform/resolvers/zod';
import { Alert, Box, Button, Card, CardContent, MenuItem, Skeleton, Stack, TextField, Typography } from '@mui/material';
import { Controller, useForm } from 'react-hook-form';
import { z } from 'zod';
import { Link, useNavigate, useSearchParams } from 'react-router-dom';
import { CustomerPageHeading, CustomerQueryError } from '../components/CustomerStates';
import { useCustomerProperties, useCustomerEligibleServiceTypes, useCustomerEligibleServices, useCreateCustomerEstimate, useCreateCustomerRequest } from '../hooks/useCustomerPortal';
import type { CustomerEstimate, CustomerRequestCreated } from '../types/customerPortal';

const schema = z.object({
  propertyId: z.string().uuid('Choose one of your properties.'),
  serviceCategory: z.string().min(1, 'Choose a service type.'),
  serviceId: z.string().uuid('Choose an available service.'),
  quoteId: z.string().uuid('Generate an estimate for this property and service.'),
  priority: z.enum(['LOW', 'MEDIUM', 'HIGH', 'URGENT']),
  description: z.string().trim().min(10, 'Add at least 10 characters describing the service needed.').max(2000, 'Description must be 2,000 characters or fewer.'),
  preferredDate: z.string().optional(),
}).refine((values) => !values.preferredDate || new Date(`${values.preferredDate}T23:59:59`).getTime() > Date.now(), { message: 'Select a future preferred date.', path: ['preferredDate'] });

type FormValues = z.infer<typeof schema>;

export default function CustomerRequestCreatePage() {
  const [searchParams] = useSearchParams();
  const preselectedPropertyId = searchParams.get('propertyId') ?? '';
  const navigate = useNavigate();
  const properties = useCustomerProperties();
  const estimateMutation = useCreateCustomerEstimate();
  const mutation = useCreateCustomerRequest();
  const [created, setCreated] = useState<CustomerRequestCreated | null>(null);
  const [estimate, setEstimate] = useState<CustomerEstimate | null>(null);
  const estimateKey = useRef('');
  const form = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: { propertyId: '', serviceCategory: '', serviceId: '', quoteId: '', priority: 'MEDIUM', description: '', preferredDate: '' },
  });
  const propertyId = form.watch('propertyId');
  const serviceCategory = form.watch('serviceCategory');
  const serviceId = form.watch('serviceId');
  const serviceTypes = useCustomerEligibleServiceTypes(propertyId);
  const services = useCustomerEligibleServices(propertyId, serviceCategory);
  const serviceCategories = serviceTypes.data ?? [];
  const categoryServices = services.data ?? [];
  const hasProperties = (properties.data ?? []).some((property) => property.status !== 'ARCHIVED');
  useEffect(() => {
    if (properties.isSuccess && preselectedPropertyId && form.getValues('propertyId') !== preselectedPropertyId && properties.data.some((property) => property.propertyId === preselectedPropertyId && property.status !== 'ARCHIVED')) {
      form.setValue('propertyId', preselectedPropertyId, { shouldValidate: true });
    }
  }, [form, preselectedPropertyId, properties.data, properties.isSuccess]);

  const submit = form.handleSubmit(async (values) => {
    if (!estimate || estimate.pricingEstimateId !== values.quoteId || estimate.propertyId !== values.propertyId || estimate.serviceId !== values.serviceId) {
      form.setError('quoteId', { message: 'Generate a current estimate for the selected property and service.' });
      return;
    }
    try {
      const result = await mutation.mutateAsync({
        quoteId: values.quoteId,
        propertyId: values.propertyId,
        serviceId: values.serviceId,
        serviceType: values.serviceCategory,
        priority: values.priority,
        description: values.description.trim(),
        preferredDate: values.preferredDate ? new Date(`${values.preferredDate}T12:00:00`).toISOString() : undefined,
      });
      setCreated(result);
    } catch {
      // React Hook Form retains the entered values; the API error is rendered below.
    }
  });

  const propertyQueryError = properties.isError;
  const chosenProperty = (properties.data ?? []).find((property) => property.propertyId === (created?.propertyId || propertyId));
  const chosenService = categoryServices.find((service) => service.serviceId === (created?.serviceId || serviceId));

  if (created) return <Stack spacing={3}>
    <CustomerPageHeading title="Service request submitted" subtitle="Your request has been received by PropertyPilot." />
    <Alert severity="success" role="status">
      {`Your ${created.serviceName || chosenService?.serviceName || 'service'} request for ${created.propertyTitle || chosenProperty?.title || 'your property'} was submitted.`}
    </Alert>
    <Stack direction={{ xs: 'column', sm: 'row' }} spacing={1}>
      {created.serviceRequestId && <Button component={Link} to={`/customer/service-requests/${created.serviceRequestId}`} variant="contained">View service request</Button>}
      <Button component={Link} to="/customer/service-requests">Back to service requests</Button>
      <Button onClick={() => navigate('/customer/properties')}>My properties</Button>
    </Stack>
  </Stack>;

  return <Stack spacing={3}>
    <CustomerPageHeading title="Request a service" subtitle="Choose a property and service to receive a live estimate before booking." action={<Button component={Link} to="/customer/service-requests">Back to service requests</Button>} />
    <Alert severity="info">Your estimate uses current PropertyPilot pricing rules and shows the standard price, your price and savings. Accepting it will create the service request.</Alert>

    {propertyQueryError && <CustomerQueryError error={properties.error} retry={() => void properties.refetch()} />}
    {serviceTypes.isError && <CustomerQueryError error={serviceTypes.error} retry={() => void serviceTypes.refetch()} />}
    {services.isError && <CustomerQueryError error={services.error} retry={() => void services.refetch()} />}

    {!properties.isLoading && !propertyQueryError && !hasProperties && <Alert severity="info" action={<Button component={Link} to="/customer/properties/new">Add property</Button>}>Add an active property before requesting a service.</Alert>}

    <Card variant="outlined"><CardContent>
      <Stack component="form" spacing={2.5} onSubmit={(event) => void submit(event)} noValidate>
        <Typography variant="h6" fontWeight={700}>Service request details</Typography>

        {properties.isLoading ? <Skeleton variant="rounded" height={56} /> : <Controller name="propertyId" control={form.control} render={({ field }) => <TextField {...field} select label="Property" error={Boolean(form.formState.errors.propertyId)} helperText={form.formState.errors.propertyId?.message || 'Choose the property that needs this service.'} disabled={propertyQueryError || !hasProperties} onChange={(event) => { field.onChange(event); estimateKey.current = ''; setEstimate(null); estimateMutation.reset(); form.setValue('serviceCategory', ''); form.setValue('serviceId', ''); form.setValue('quoteId', ''); form.clearErrors(['serviceCategory', 'serviceId', 'quoteId']); }}>
          {(properties.data ?? []).filter((property) => property.status !== 'ARCHIVED').map((property) => <MenuItem key={property.propertyId} value={property.propertyId}>{property.title}{property.propertyType ? ` · ${property.propertyType}` : ''}</MenuItem>)}
        </TextField>} />}

        {serviceTypes.isLoading && propertyId ? <Skeleton variant="rounded" height={56} /> : <Controller name="serviceCategory" control={form.control} render={({ field }) => <TextField {...field} select label="Service type" error={Boolean(form.formState.errors.serviceCategory) && serviceCategories.length > 0} helperText={!propertyId ? 'Choose a property first.' : serviceTypes.isError ? 'Service types could not be loaded. Retry above.' : serviceCategories.length === 0 && serviceTypes.isSuccess ? 'No active service types are configured for this property yet.' : form.formState.errors.serviceCategory?.message || 'Choose a service type to see matching services.'} disabled={!propertyId || serviceTypes.isError || serviceCategories.length === 0} onChange={(event) => { field.onChange(event); estimateKey.current = ''; setEstimate(null); estimateMutation.reset(); form.clearErrors('serviceCategory'); form.setValue('serviceId', ''); form.setValue('quoteId', ''); form.clearErrors(['serviceId', 'quoteId']); }}>
          {serviceCategories.map((category) => <MenuItem key={category.code} value={category.code}>{category.label}</MenuItem>)}
        </TextField>} />}
        {propertyId && serviceTypes.isSuccess && serviceCategories.length === 0 && <Alert severity="info">No services are currently configured for this property. An administrator can add an active service type and service in the Service Catalog.</Alert>}

        {services.isLoading && serviceCategory ? <Skeleton variant="rounded" height={56} /> : <Controller name="serviceId" control={form.control} render={({ field }) => <TextField {...field} select label="Service" error={Boolean(form.formState.errors.serviceId) && categoryServices.length > 0} helperText={!serviceCategory ? 'Choose a service type first.' : services.isError ? 'Services could not be loaded. Retry above.' : categoryServices.length === 0 && services.isSuccess ? 'No active services match this property and service type.' : form.formState.errors.serviceId?.message || 'Choose the specific service you need.'} disabled={!propertyId || !serviceCategory || services.isError || categoryServices.length === 0} onChange={(event) => { const selectedServiceId = event.target.value; field.onChange(selectedServiceId); const requestKey = `${propertyId}:${selectedServiceId}`; estimateKey.current = requestKey; setEstimate(null); estimateMutation.reset(); form.setValue('quoteId', ''); form.clearErrors(['serviceId', 'quoteId']); if (selectedServiceId && propertyId) { void estimateMutation.mutateAsync({ propertyId, serviceId: selectedServiceId }).then((result) => { if (estimateKey.current === requestKey) { setEstimate(result); form.setValue('quoteId', result.pricingEstimateId, { shouldValidate: true }); } }).catch(() => { /* The estimate error is shown below. */ }); } }}>
          {categoryServices.map((service) => <MenuItem key={service.serviceId} value={service.serviceId}>
            <Box><Typography>{service.serviceName}</Typography><Typography variant="caption" color="text.secondary">{visitDescription(service.requiresVisit, service.visitMandatory, service.remote)}</Typography></Box>
          </MenuItem>)}
        </TextField>} />}

        {estimateMutation.isPending && <Skeleton variant="rounded" height={112} />}
        {estimateMutation.isError && <Alert severity="error">{getEstimateErrorMessage(estimateMutation.error)}</Alert>}
        {estimate && estimate.propertyId === propertyId && estimate.serviceId === serviceId && <Box sx={{ p: 2.5, borderRadius: 2, border: '1px solid', borderColor: 'divider', bgcolor: 'background.default' }}>
          <Typography variant="subtitle1" fontWeight={700}>Your price estimate</Typography>
          <Stack direction={{ xs: 'column', sm: 'row' }} spacing={3} sx={{ mt: 1.5 }}>
            <Box><Typography variant="body2" color="text.secondary">Standard price</Typography><Typography sx={{ textDecoration: 'line-through' }}>{formatCurrency(estimate.standardPrice, estimate.currencyCode)}</Typography></Box>
            <Box><Typography variant="body2" color="text.secondary">Your PropertyPilot price</Typography><Typography variant="h6" fontWeight={750}>{formatCurrency(estimate.customerPrice, estimate.currencyCode)}</Typography></Box>
            <Box><Typography variant="body2" color="text.secondary">Your savings</Typography><Typography fontWeight={700} color="success.main">{formatCurrency(estimate.savings, estimate.currencyCode)}</Typography></Box>
          </Stack>
          <Typography variant="caption" color="text.secondary">Valid until {new Date(estimate.expiresAt).toLocaleString()}. The final amount is frozen when you accept this estimate.</Typography>
        </Box>}
        {form.formState.errors.quoteId && <Typography color="error" variant="caption">{form.formState.errors.quoteId.message}</Typography>}

        <Controller name="priority" control={form.control} render={({ field }) => <TextField {...field} select label="Priority" error={Boolean(form.formState.errors.priority)} helperText={form.formState.errors.priority?.message}>
          {(['LOW', 'MEDIUM', 'HIGH', 'URGENT'] as const).map((value) => <MenuItem key={value} value={value}>{value.charAt(0) + value.slice(1).toLowerCase()}</MenuItem>)}
        </TextField>} />
        <TextField label="Preferred service date (optional)" type="date" InputLabelProps={{ shrink: true }} {...form.register('preferredDate')} error={Boolean(form.formState.errors.preferredDate)} helperText={form.formState.errors.preferredDate?.message || 'A preferred date is a request, not a confirmed appointment.'} />
        <TextField label="Service details" multiline minRows={4} inputProps={{ maxLength: 2000 }} {...form.register('description')} error={Boolean(form.formState.errors.description)} helperText={form.formState.errors.description?.message || 'Tell us what you need so our team can prepare.'} />

        {mutation.isError && <Alert severity="error">{mutation.error instanceof Error ? mutation.error.message : 'Your request could not be submitted. Your entries are still here; review and try again.'}</Alert>}
        <Button type="submit" variant="contained" disabled={mutation.isPending || estimateMutation.isPending || form.formState.isSubmitting || properties.isLoading || serviceTypes.isLoading || services.isLoading || !hasProperties || !estimate || estimate.pricingEstimateId !== form.watch('quoteId')}>
          {mutation.isPending || form.formState.isSubmitting ? 'Accepting estimate and submitting…' : 'Accept estimate and submit request'}
        </Button>
      </Stack>
    </CardContent></Card>
    <Typography variant="body2" color="text.secondary">Submitting sends your request to PropertyPilot for service delivery. A visit is scheduled only when the service requires one.</Typography>
  </Stack>;
}

function visitDescription(requiresVisit?: boolean | null, visitMandatory?: boolean | null, remote?: boolean | null): string {
  if (visitMandatory === true) return 'On-site visit required';
  if (remote === true) return 'Remote service';
  if (requiresVisit === false) return 'No on-site visit required';
  if (requiresVisit === true) return 'May include a site visit';
  return '';
}

function formatCurrency(amount: number, currency = 'INR'): string {
  return new Intl.NumberFormat(undefined, { style: 'currency', currency }).format(amount);
}

function getEstimateErrorMessage(error: unknown): string {
  const fallback = 'We could not calculate a price. For travel-based services, PropertyPilot Operations will prepare the quote.';
  if (!error || typeof error !== 'object') return fallback;
  const response = 'response' in error ? error.response : undefined;
  const data = response && typeof response === 'object' && 'data' in response ? response.data : undefined;
  if (data && typeof data === 'object') {
    if ('message' in data && typeof data.message === 'string' && data.message.trim()) {
      const message = data.message.trim();
      if (message === 'A positive service price must be configured before estimating') {
        return 'This service does not have an active price yet, so an estimate cannot be generated. Please contact PropertyPilot Operations to configure its pricing.';
      }
      return message;
    }
    if ('errors' in data && data.errors && typeof data.errors === 'object') {
      const messages = Object.values(data.errors).filter((value): value is string => typeof value === 'string' && value.trim().length > 0);
      if (messages.length > 0) return messages.join(' ');
    }
  }
  return error instanceof Error && error.message !== 'Request failed with status code 400' ? error.message : fallback;
}
