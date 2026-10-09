import { useEffect, useMemo, useState } from 'react';
import { zodResolver } from '@hookform/resolvers/zod';
import { Alert, Box, Button, Card, CardContent, Chip, MenuItem, Skeleton, Stack, TextField, Typography } from '@mui/material';
import { Controller, useForm } from 'react-hook-form';
import { z } from 'zod';
import { Link, useNavigate, useSearchParams } from 'react-router-dom';
import { CustomerPageHeading, CustomerQueryError } from '../components/CustomerStates';
import { useCustomerProperties, useCustomerQuotes, useCustomerServices, useCreateCustomerRequest } from '../hooks/useCustomerPortal';
import type { CustomerRequestCreated, CustomerService } from '../types/customerPortal';

const schema = z.object({
  propertyId: z.string().uuid('Choose one of your properties.'),
  serviceId: z.string().uuid('Choose an available service.'),
  quoteId: z.string().uuid('Choose an accepted quote for this property and service.'),
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
  const quotes = useCustomerQuotes();
  const services = useCustomerServices();
  const mutation = useCreateCustomerRequest();
  const [created, setCreated] = useState<CustomerRequestCreated | null>(null);
  const form = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: { propertyId: '', serviceId: '', quoteId: '', priority: 'MEDIUM', description: '', preferredDate: '' },
  });
  const propertyId = form.watch('propertyId');
  const serviceId = form.watch('serviceId');
  const acceptedQuotes = useMemo(() => (quotes.data ?? []).filter((quote) => quote.status === 'ACCEPTED' && quote.propertyId === propertyId && quote.serviceId === serviceId), [quotes.data, propertyId, serviceId]);
  const serviceQueryError = services.isError;
  const quotedServices = useMemo<CustomerService[]>(() => {
    const unique = new Map((quotes.data ?? []).filter((quote) => quote.status === 'ACCEPTED' && quote.propertyId === propertyId).map((quote) => [quote.serviceId, { serviceId: quote.serviceId, serviceName: quote.serviceName }]));
    return [...unique.values()];
  }, [propertyId, quotes.data]);
  const availableServices = useMemo(() => (services.isSuccess ? services.data ?? [] : quotedServices), [quotedServices, services.data, services.isSuccess]);
  const hasProperties = (properties.data ?? []).some((property) => property.status !== 'ARCHIVED');
  useEffect(() => {
    if (properties.isSuccess && preselectedPropertyId && form.getValues('propertyId') !== preselectedPropertyId && properties.data.some((property) => property.propertyId === preselectedPropertyId && property.status !== 'ARCHIVED')) {
      form.setValue('propertyId', preselectedPropertyId, { shouldValidate: true });
    }
  }, [form, preselectedPropertyId, properties.data, properties.isSuccess]);

  const submit = form.handleSubmit(async (values) => {
    const selectedQuote = acceptedQuotes.find((quote) => quote.quoteId === values.quoteId);
    if (!selectedQuote) {
      form.setError('quoteId', { message: 'Choose an accepted quote for the selected property and service.' });
      return;
    }
    try {
      const result = await mutation.mutateAsync({
        quoteId: values.quoteId,
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
  const quoteQueryError = quotes.isError;
  const chosenProperty = (properties.data ?? []).find((property) => property.propertyId === (created?.propertyId || propertyId));
  const chosenService = availableServices.find((service) => service.serviceId === (created?.serviceId || serviceId));
  const chosenQuote = (quotes.data ?? []).find((quote) => quote.quoteId === created?.quoteId) ?? acceptedQuotes.find((quote) => quote.quoteId === form.watch('quoteId'));

  if (created) return <Stack spacing={3}>
    <CustomerPageHeading title="Service request submitted" subtitle="Your request has been received by PropertyPilot." />
    <Alert severity="success" role="status">
      {`Your ${created.serviceName || chosenService?.serviceName || chosenQuote?.serviceName || 'service'} request for ${created.propertyTitle || chosenProperty?.title || 'your property'} was submitted.`}
    </Alert>
    <Stack direction={{ xs: 'column', sm: 'row' }} spacing={1}>
      {created.serviceRequestId && <Button component={Link} to={`/customer/service-requests/${created.serviceRequestId}`} variant="contained">View service request</Button>}
      <Button component={Link} to="/customer/service-requests">Back to service requests</Button>
      <Button onClick={() => navigate('/customer/properties')}>My properties</Button>
    </Stack>
  </Stack>;

  return <Stack spacing={3}>
    <CustomerPageHeading title="Request a service" subtitle="Choose a property and service, then confirm its accepted quote." action={<Button component={Link} to="/customer/service-requests">Back to service requests</Button>} />
    <Alert severity="info">A subscription is not required to request a service. The current request workflow does require an accepted quote for the chosen property and service.</Alert>

    {propertyQueryError && <CustomerQueryError error={properties.error} retry={() => void properties.refetch()} />}
    {quoteQueryError && <CustomerQueryError error={quotes.error} retry={() => void quotes.refetch()} />}
    {serviceQueryError && <Alert severity="warning" action={<Button color="inherit" size="small" onClick={() => void services.refetch()}>Retry</Button>}>The service catalog could not be loaded. Accepted quote details may still be used if available.</Alert>}

    {!properties.isLoading && !propertyQueryError && !hasProperties && <Alert severity="info" action={<Button component={Link} to="/customer/properties/new">Add property</Button>}>Add an active property before requesting a service.</Alert>}

    <Card variant="outlined"><CardContent>
      <Stack component="form" spacing={2.5} onSubmit={(event) => void submit(event)} noValidate>
        <Typography variant="h6" fontWeight={700}>Service request details</Typography>

        {properties.isLoading ? <Skeleton variant="rounded" height={56} /> : <Controller name="propertyId" control={form.control} render={({ field }) => <TextField {...field} select label="Property" error={Boolean(form.formState.errors.propertyId)} helperText={form.formState.errors.propertyId?.message || 'Choose the property that needs this service.'} disabled={propertyQueryError || !hasProperties} onChange={(event) => { field.onChange(event); form.setValue('serviceId', '', { shouldValidate: true }); form.setValue('quoteId', '', { shouldValidate: true }); }}>
          {(properties.data ?? []).filter((property) => property.status !== 'ARCHIVED').map((property) => <MenuItem key={property.propertyId} value={property.propertyId}>{property.title}{property.propertyType ? ` · ${property.propertyType}` : ''}</MenuItem>)}
        </TextField>} />}

        {services.isLoading ? <Skeleton variant="rounded" height={56} /> : <Controller name="serviceId" control={form.control} render={({ field }) => <TextField {...field} select label="Service" error={Boolean(form.formState.errors.serviceId)} helperText={form.formState.errors.serviceId?.message || (propertyId ? 'Choose the service you need.' : 'Choose a property first.')} disabled={!propertyId || (serviceQueryError && availableServices.length === 0)} onChange={(event) => { field.onChange(event); form.setValue('quoteId', '', { shouldValidate: true }); }}>
          {availableServices.map((service) => <MenuItem key={service.serviceId} value={service.serviceId}>
            <Box><Typography>{service.serviceName}</Typography><Typography variant="caption" color="text.secondary">{[service.category, visitDescription(service.requiresVisit, service.visitMandatory, service.remote)].filter(Boolean).join(' · ')}</Typography></Box>
          </MenuItem>)}
        </TextField>} />}

        <Controller name="quoteId" control={form.control} render={({ field }) => <TextField {...field} select label="Accepted quote" error={Boolean(form.formState.errors.quoteId)} helperText={form.formState.errors.quoteId?.message || (propertyId && serviceId ? acceptedQuotes.length ? 'Choose the approved pricing for this request.' : 'No accepted quote is available for this property and service.' : 'Select a property and service first.')} disabled={!propertyId || !serviceId || quoteQueryError}>
          {acceptedQuotes.map((quote) => <MenuItem key={quote.quoteId} value={quote.quoteId}>{quote.serviceName} · {new Intl.NumberFormat(undefined, { style: 'currency', currency: quote.currencyCode || 'INR' }).format(quote.totalAmount)}</MenuItem>)}
        </TextField>} />

        {propertyId && serviceId && acceptedQuotes.length === 0 && !quotes.isLoading && !quoteQueryError && <Alert severity="info" action={<Button component={Link} to="/quotes">View quotes</Button>}>
          No accepted quote matches this selection. Quote calculation is currently handled by PropertyPilot Operations; you can review any existing quotes in your account.
        </Alert>}

        {chosenQuote && <Box sx={{ p: 2, borderRadius: 1.5, bgcolor: 'action.hover' }}>
          <Stack direction="row" justifyContent="space-between" alignItems="center" gap={1}><Typography fontWeight={650}>Confirmed quote amount</Typography><Chip size="small" color="success" label="Accepted" /></Stack>
          <Typography variant="h6" fontWeight={750} sx={{ mt: .5 }}>{new Intl.NumberFormat(undefined, { style: 'currency', currency: chosenQuote.currencyCode || 'INR' }).format(chosenQuote.totalAmount)}</Typography>
          <Typography variant="body2" color="text.secondary">This amount comes from your accepted quote.</Typography>
        </Box>}

        <Controller name="priority" control={form.control} render={({ field }) => <TextField {...field} select label="Priority" error={Boolean(form.formState.errors.priority)} helperText={form.formState.errors.priority?.message}>
          {(['LOW', 'MEDIUM', 'HIGH', 'URGENT'] as const).map((value) => <MenuItem key={value} value={value}>{value.charAt(0) + value.slice(1).toLowerCase()}</MenuItem>)}
        </TextField>} />
        <TextField label="Preferred service date (optional)" type="date" InputLabelProps={{ shrink: true }} {...form.register('preferredDate')} error={Boolean(form.formState.errors.preferredDate)} helperText={form.formState.errors.preferredDate?.message || 'A preferred date is a request, not a confirmed appointment.'} />
        <TextField label="Service details" multiline minRows={4} inputProps={{ maxLength: 2000 }} {...form.register('description')} error={Boolean(form.formState.errors.description)} helperText={form.formState.errors.description?.message || 'Tell us what you need so our team can prepare.'} />

        {mutation.isError && <Alert severity="error">{mutation.error instanceof Error ? mutation.error.message : 'Your request could not be submitted. Your entries are still here; review and try again.'}</Alert>}
        <Button type="submit" variant="contained" disabled={mutation.isPending || form.formState.isSubmitting || properties.isLoading || quotes.isLoading || services.isLoading || !hasProperties || !acceptedQuotes.some((quote) => quote.quoteId === form.watch('quoteId'))}>
          {mutation.isPending || form.formState.isSubmitting ? 'Submitting request…' : 'Submit service request'}
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
