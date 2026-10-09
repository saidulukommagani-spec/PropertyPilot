import { useEffect } from 'react';
import { Alert, Box, Button, Card, CardContent, FormControl, FormHelperText, InputLabel, MenuItem, Select, Skeleton, Stack, TextField, Typography } from '@mui/material';
import ArrowBackRounded from '@mui/icons-material/ArrowBackRounded';
import { zodResolver } from '@hookform/resolvers/zod';
import { Controller, useForm } from 'react-hook-form';
import { z } from 'zod';
import { Link, useLocation, useNavigate, useParams } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { serviceRequestApi } from '../../../api/serviceRequestApi';
import { useCreateServiceRequest, useEditServiceRequest } from '../hooks/useServiceRequests';
import type { ServiceRequestCreateRequest, ServiceRequestUpdateRequest } from '../types/serviceRequest';

const schema = z.object({
  quoteId: z.string().min(1, 'Select a valid accepted quote.'),
  priority: z.enum(['LOW', 'MEDIUM', 'HIGH', 'URGENT']),
  description: z.string().trim().min(5, 'Enter at least 5 characters.').max(4000, 'Description cannot exceed 4,000 characters.'),
  preferredDate: z.string().optional().refine((value) => !value || new Date(value).getTime() > Date.now(), 'Preferred date must be in the future.'),
});
type FormValues = z.infer<typeof schema>;
const toLocalDateTime = (value?: string) => {
  if (!value) return '';
  const date = new Date(value);
  if (Number.isNaN(date.valueOf())) return '';
  return new Date(date.getTime() - date.getTimezoneOffset() * 60_000).toISOString().slice(0, 16);
};

export default function ServiceRequestFormPage() {
  const { id = '' } = useParams<{ id: string }>(); const isEdit = Boolean(id); const navigate = useNavigate(); const location = useLocation();
  const requestPath = location.pathname.startsWith('/staff/') ? '/staff/service-requests' : '/service-requests';
  const create = useCreateServiceRequest(); const update = useEditServiceRequest();
  const detail = useQuery({ queryKey: ['service-request-detail', id], queryFn: () => serviceRequestApi.getDetails(id), enabled: isEdit });
  const quotes = useQuery({ queryKey: ['request-form-quotes'], queryFn: serviceRequestApi.listQuotes, enabled: !isEdit });
  const { control, register, handleSubmit, reset, formState: { errors } } = useForm<FormValues>({ resolver: zodResolver(schema), defaultValues: { priority: 'MEDIUM', description: '', quoteId: '', preferredDate: '' } });
  const acceptedQuotes = (quotes.data ?? []).filter((quote) => quote.status === 'ACCEPTED');
  const editable = !isEdit || Boolean(detail.data && ['NEW', 'PENDING_ASSIGNMENT'].includes(detail.data.status));

  useEffect(() => {
    if (!isEdit || !detail.data) return;
    reset({ quoteId: '', priority: detail.data.priority as FormValues['priority'], description: detail.data.description || '', preferredDate: toLocalDateTime(detail.data.scheduledAt) });
  }, [isEdit, detail.data, reset]);

  const submit = handleSubmit(async (values) => {
    try {
      if (isEdit) {
        const request: ServiceRequestUpdateRequest = { priority: values.priority, description: values.description.trim(), scheduledAt: values.preferredDate ? new Date(values.preferredDate).toISOString() : undefined };
        await update.mutateAsync({ id, request });
        navigate(`${requestPath}/${id}`, { replace: true });
      } else {
        const quote = acceptedQuotes.find((entry) => entry.quoteId === values.quoteId);
        if (!quote) return;
        const request: ServiceRequestCreateRequest = { quoteId: quote.quoteId, priority: values.priority, description: values.description.trim(), preferredDate: values.preferredDate ? new Date(values.preferredDate).toISOString() : undefined };
        const created = await create.mutateAsync(request);
        navigate(`${requestPath}/${created.serviceRequestId}`, { replace: true });
      }
    } catch { /* mutation errors are presented below */ }
  });
  const mutationError = create.error || update.error;
  const isPending = create.isPending || update.isPending;
  if (isEdit && detail.isLoading) return <Stack spacing={2}><Skeleton height={40}/><Skeleton variant="rounded" height={360}/></Stack>;
  if (isEdit && (detail.isError || !detail.data)) return <Stack spacing={2}><Button component={Link} to={requestPath} startIcon={<ArrowBackRounded/>}>Back to Requests</Button><Alert severity="error" action={<Button color="inherit" size="small" onClick={() => void detail.refetch()}>Retry</Button>}>{detail.error instanceof Error ? detail.error.message : 'Could not load this request.'}</Alert></Stack>;
  return <Stack spacing={2.5}>
    <Button component={Link} to={isEdit ? `${requestPath}/${id}` : requestPath} startIcon={<ArrowBackRounded/>} sx={{ alignSelf: 'flex-start', ml: -1 }}>{isEdit ? 'Back to Request' : 'Back to Requests'}</Button>
    <Box><Typography variant="h4" fontWeight={750}>{isEdit ? 'Edit service request' : 'Create service request'}</Typography><Typography color="text.secondary" sx={{ mt: 0.5 }}>{isEdit ? 'Update the request details before assignment.' : 'Create a request from a valid accepted quote so customer, property, and service pricing remain linked.'}</Typography></Box>
    {!isEdit && quotes.isError && <Alert severity="error" action={<Button color="inherit" size="small" onClick={() => void quotes.refetch()}>Retry</Button>}>Accepted quotes could not be loaded.</Alert>}
    {isEdit && detail.data && !editable && <Alert severity="info">Request fields are locked after assignment. View the request workflow for available actions.</Alert>}
    {mutationError && <Alert severity="error">{mutationError instanceof Error ? mutationError.message : 'Could not save the service request.'}</Alert>}
    <Card component="form" onSubmit={(event) => { void submit(event); }} noValidate><CardContent sx={{ p: { xs: 2.5, md: 3 } }}><Stack spacing={2.5}>
      {isEdit ? <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}><TextField label="Customer" value={detail.data?.customerName || ''} fullWidth disabled/><TextField label="Property" value={detail.data?.propertyTitle || ''} fullWidth disabled/><TextField label="Service" value={detail.data?.serviceName || detail.data?.requestType || ''} fullWidth disabled/></Stack> : <Controller name="quoteId" control={control} render={({ field }) => <FormControl fullWidth error={Boolean(errors.quoteId)}><InputLabel id="request-quote-label">Accepted quote</InputLabel><Select {...field} labelId="request-quote-label" label="Accepted quote" disabled={quotes.isLoading || !acceptedQuotes.length}>{acceptedQuotes.map((quote) => { const labels = [quote.serviceName, quote.customerName, quote.propertyName].filter((label): label is string => Boolean(label)); return <MenuItem key={quote.quoteId} value={quote.quoteId}>{labels.join(' · ')} · {quote.currencyCode} {quote.totalAmount.toLocaleString()}</MenuItem>; })}</Select>{errors.quoteId && <FormHelperText>{errors.quoteId.message}</FormHelperText>}{!quotes.isLoading && !acceptedQuotes.length && <FormHelperText>No accepted quotes are available. Accept a customer quote before creating a request.</FormHelperText>}</FormControl>}/>}
      <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
        <Controller name="priority" control={control} render={({ field }) => <FormControl fullWidth error={Boolean(errors.priority)} disabled={!editable}><InputLabel id="request-priority-label">Priority</InputLabel><Select {...field} labelId="request-priority-label" label="Priority">{['LOW','MEDIUM','HIGH','URGENT'].map((priority) => <MenuItem key={priority} value={priority}>{priority}</MenuItem>)}</Select>{errors.priority && <FormHelperText>{errors.priority.message}</FormHelperText>}</FormControl>}/>
        <TextField fullWidth type="datetime-local" label="Preferred date" disabled={!editable} InputLabelProps={{ shrink: true }} inputProps={{ min: toLocalDateTime(new Date().toISOString()) }} error={Boolean(errors.preferredDate)} helperText={errors.preferredDate?.message} {...register('preferredDate')}/>
      </Stack>
      <TextField fullWidth multiline minRows={4} label="Description" disabled={!editable} error={Boolean(errors.description)} helperText={errors.description?.message || 'Describe the customer’s service need.'} {...register('description')}/>
      <Stack direction="row" justifyContent="flex-end" spacing={1}><Button component={Link} to={isEdit ? `${requestPath}/${id}` : requestPath} disabled={isPending}>Cancel</Button><Button type="submit" variant="contained" disabled={!editable || isPending || (!isEdit && (quotes.isLoading || !acceptedQuotes.length))}>{isPending ? 'Saving…' : isEdit ? 'Save changes' : 'Create request'}</Button></Stack>
    </Stack></CardContent></Card>
  </Stack>;
}
