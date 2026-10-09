import { useState } from 'react';
import { zodResolver } from '@hookform/resolvers/zod';
import { Alert, Button, Card, CardContent, Chip, Dialog, DialogActions, DialogContent, DialogTitle, Divider, FormControl, FormHelperText, Grid, InputLabel, LinearProgress, MenuItem, Select, Skeleton, Stack, TextField, Typography } from '@mui/material';
import { useQueries, useQuery } from '@tanstack/react-query';
import { Controller, useForm } from 'react-hook-form';
import { Link } from 'react-router-dom';
import { z } from 'zod';
import { serviceRequestApi } from '../../../api/serviceRequestApi';
import { CustomerPortalApi } from '../../customer-portal/services/CustomerPortalApi';
import type { CustomerQuote } from '../../customer-portal/types/customerPortal';
import { serviceRequestDetailKeys } from '../../service-requests/hooks/useServiceRequestDetails';
import type { RequestQuoteOption } from '../../service-requests/types/serviceRequest';
import { useCreateLeadServiceRequest, useLeadServiceRequests } from '../hooks/useLeads';

const schema = z.object({
  quoteId: z.string().uuid('Select an accepted quote.'),
  priority: z.enum(['LOW', 'MEDIUM', 'HIGH', 'URGENT']),
  description: z.string().trim().min(10, 'Describe the service needed in at least 10 characters.').max(4000),
  preferredDate: z.string().optional(),
}).superRefine((values, context) => {
  if (values.preferredDate && new Date(`${values.preferredDate}T23:59:59`).getTime() <= Date.now()) {
    context.addIssue({ code: 'custom', path: ['preferredDate'], message: 'Select a future date.' });
  }
});
type FormValues = z.infer<typeof schema>;
type QuoteOption = CustomerQuote | RequestQuoteOption;

export default function LeadServiceRequestsPanel({ leadId, operations, agent = false }: { leadId: string; operations: boolean; agent?: boolean }) {
  const query = useLeadServiceRequests(leadId);
  const quotes = useQuery<QuoteOption[]>({ queryKey: ['lead-service-quotes', operations], queryFn: async (): Promise<QuoteOption[]> => operations ? serviceRequestApi.listQuotes() : CustomerPortalApi.quotes(), enabled: false });
  const mutation = useCreateLeadServiceRequest(leadId);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [notice, setNotice] = useState('');
  const form = useForm<FormValues>({ resolver: zodResolver(schema), defaultValues: { quoteId: '', priority: 'MEDIUM', description: '', preferredDate: '' } });
  const requests = query.data ?? [];
  const visits = useQueries({ queries: requests.map((item) => ({ queryKey: serviceRequestDetailKeys.visits(item.serviceRequestId), queryFn: () => serviceRequestApi.getVisits(item.serviceRequestId) })) });
  const evidence = useQueries({ queries: requests.map((item) => ({ queryKey: serviceRequestDetailKeys.evidence(item.serviceRequestId), queryFn: () => serviceRequestApi.getEvidence(item.serviceRequestId) })) });
  const quotesAvailable = (quotes.data ?? []).filter((quote) => quote.status === 'ACCEPTED') as QuoteOption[];
  const currencies = [...new Set(requests.flatMap((request) => request.revenueByCurrency.map((row) => row.currencyCode)))];
  const submit = form.handleSubmit(async (values) => {
    try {
      await mutation.mutateAsync({ operations, request: { ...values, preferredDate: values.preferredDate ? new Date(`${values.preferredDate}T12:00:00`).toISOString() : undefined } });
      setDialogOpen(false);
      form.reset();
      setNotice('Service request created and linked to this lead.');
    } catch { /* The mutation error is rendered in the dialog. */ }
  });

  return <Stack spacing={2.5}>
    <Stack direction={{ xs: 'column', sm: 'row' }} justifyContent="space-between" alignItems={{ sm: 'center' }} gap={1}>
      <Stack><Typography variant="h6" fontWeight={700}>Linked service requests</Typography><Typography variant="body2" color="text.secondary">Requests retain their own service, SLA, assignment, visit, evidence, and completion records.</Typography></Stack>
      {!agent && <Button variant="contained" onClick={() => { setNotice(''); setDialogOpen(true); void quotes.refetch(); }}>Create service request</Button>}
    </Stack>
    {agent && <Alert severity="info">Only linked service requests assigned to you are shown.</Alert>}
    {notice && <Alert severity="success">{notice}</Alert>}
    {query.isError && <Alert severity="error" action={<Button onClick={() => void query.refetch()}>Retry</Button>}>{query.error.message || 'Linked requests could not be loaded.'}</Alert>}
    {query.isLoading ? <Skeleton variant="rounded" height={140} /> : requests.length ? <>
      {!agent && currencies.map((currency) => <Grid key={currency} container spacing={2}>
        {(['invoicedRevenue', 'collectedRevenue'] as const).map((field) => <Grid item xs={12} sm={6} key={field}><Card variant="outlined"><CardContent><Typography variant="caption" color="text.secondary">{field === 'invoicedRevenue' ? 'Invoiced service value (excl. tax)' : 'Payments received (incl. tax)'} - {currency}</Typography><Typography variant="h6" fontWeight={750}>{money(requests.reduce((sum, item) => sum + (item.revenueByCurrency.find((revenue) => revenue.currencyCode === currency)?.[field] ?? 0), 0), currency)}</Typography></CardContent></Card></Grid>)}
      </Grid>)}
      {requests.map((request, index) => <Card key={request.serviceRequestId} variant="outlined"><CardContent><Stack spacing={1.5}>
        <Stack direction={{ xs: 'column', sm: 'row' }} justifyContent="space-between" alignItems={{ sm: 'center' }} gap={1}><Stack><Typography variant="subtitle1" fontWeight={750}>{request.serviceName}</Typography><Typography variant="caption" color="text.secondary">Requested {new Date(request.requestedAt).toLocaleString()}</Typography></Stack><Chip size="small" label={request.status.replaceAll('_', ' ')} /></Stack>
        <Grid container spacing={1.5}>
          <Grid item xs={12} sm={6} md={3}><Field label="SLA" value={request.slaStatus === 'NO_POLICY' ? 'No active SLA policy' : `${request.slaStatus} - due ${request.slaDueAt ? new Date(request.slaDueAt).toLocaleString() : '-'}`} /></Grid>
          <Grid item xs={12} sm={6} md={3}><Field label="Assigned agent" value={request.assignedAgent || 'Unassigned'} /></Grid>
          <Grid item xs={12} sm={6} md={3}><Field label="Visits" value={String(request.visitCount)} /></Grid>
          <Grid item xs={12} sm={6} md={3}><Field label="Evidence" value={String(request.evidenceCount)} /></Grid>
        </Grid>
        <Divider /><Typography variant="subtitle2" fontWeight={700}>Visits</Typography>
        {visits[index]?.isLoading ? <LinearProgress /> : visits[index]?.isError ? <Alert severity="error" action={<Button onClick={() => void visits[index]?.refetch()}>Retry</Button>}>Visit information could not be loaded.</Alert> : visits[index]?.data?.length ? visits[index].data.map((visit) => <Typography key={visit.visitId} variant="body2"><Button component={Link} to={operations ? `/staff/visits/${visit.visitId}` : agent ? `/agent/visits/${visit.visitId}` : `/customer/visits/${visit.visitId}`} size="small">Open visit</Button> - {visit.status.replaceAll('_', ' ')} - {new Date(visit.scheduledAt).toLocaleString()} - {visit.agentName || 'Agent not assigned'}</Typography>) : <Typography variant="body2" color="text.secondary">No visits recorded.</Typography>}
        <Typography variant="subtitle2" fontWeight={700}>Evidence</Typography>
        {evidence[index]?.isLoading ? <LinearProgress /> : evidence[index]?.isError ? <Alert severity="error" action={<Button onClick={() => void evidence[index]?.refetch()}>Retry</Button>}>Evidence information could not be loaded.</Alert> : evidence[index]?.data?.length ? evidence[index].data.map((item) => <Stack key={item.evidenceId} spacing={0.25}><Typography variant="body2"><Button component={Link} to={operations ? `/evidence/${item.evidenceId}` : '/customer/evidence'} size="small">{item.evidenceTypeName}</Button> - {item.fileName || 'Evidence item'} - {item.validationStatus || 'UPLOADED'}{item.capturedAt ? ` - ${new Date(item.capturedAt).toLocaleString()}` : ''}</Typography>{item.validationHistory?.map((event) => <Typography key={`${event.occurredAt}-${event.status}`} variant="caption" color="text.secondary">{new Date(event.occurredAt).toLocaleString()} - {event.status}{event.userName ? ` by ${event.userName}` : ''}{event.comment ? ` - ${event.comment}` : ''}</Typography>)}</Stack>) : <Typography variant="body2" color="text.secondary">No evidence uploaded.</Typography>}
        <Divider /><Typography variant="subtitle2" fontWeight={700}>Service request audit</Typography>
        {request.statusHistory.map((entry, historyIndex) => <Typography key={`${entry.changedAt}-${historyIndex}`} variant="body2">{new Date(entry.changedAt).toLocaleString()} - {entry.previousStatus ? `${entry.previousStatus}  to  ` : ''}{entry.newStatus}{entry.changeReason ? ` - ${entry.changeReason}` : ''}</Typography>)}
        {request.assignmentHistory.map((entry) => <Typography key={entry.assignmentId} variant="body2">{new Date(entry.assignedAt).toLocaleString()} - {entry.agentName || entry.agentCode || 'Agent'} - {entry.assignmentStatus}{entry.notes ? ` - ${entry.notes}` : ''}{entry.assignedByUserId ? ` - assigned by ${entry.assignedByName || 'Operations team'}` : ''}</Typography>)}
        <Divider /><Stack direction={{ xs: 'column', sm: 'row' }} justifyContent="space-between" gap={1}>
          {!agent && (<><Field label="Request value" value={money(request.requestValue, request.currencyCode)} />{request.revenueByCurrency.map((row) => <Stack key={row.currencyCode} spacing={0.25}><Field label={`Invoiced (excl. tax) - ${row.currencyCode}`} value={money(row.invoicedRevenue, row.currencyCode)} /><Field label={`Received (incl. tax) - ${row.currencyCode}`} value={money(row.collectedRevenue, row.currencyCode)} /></Stack>)}
          </>)}<Button component={Link} to={operations ? `/staff/service-requests/${request.serviceRequestId}` : agent ? `/agent/service-requests/${request.serviceRequestId}` : `/customer/service-requests/${request.serviceRequestId}`} size="small">Open request</Button>
        </Stack>
      </Stack></CardContent></Card>)}
    </> : !query.isError && <Alert severity="info">No service requests are linked to this lead yet.</Alert>}
    <Dialog open={dialogOpen} onClose={() => !mutation.isPending && setDialogOpen(false)} fullWidth maxWidth="sm">
      <DialogTitle>Create service request from lead</DialogTitle><DialogContent><Stack id="lead-service-request-form" component="form" spacing={2} onSubmit={(event) => void submit(event)} sx={{ pt: 1 }} noValidate>
        {quotes.isLoading ? <Skeleton variant="rounded" height={56} /> : quotes.isError ? <Alert severity="error" action={<Button onClick={() => void quotes.refetch()}>Retry</Button>}>Accepted quotes could not be loaded.</Alert> : <Controller name="quoteId" control={form.control} render={({ field }) => <FormControl fullWidth error={Boolean(form.formState.errors.quoteId)}><InputLabel id="lead-service-quote">Accepted quote</InputLabel><Select {...field} labelId="lead-service-quote" label="Accepted quote">{quotesAvailable.map((quote) => <MenuItem key={quote.quoteId} value={quote.quoteId}>{quote.serviceName} - {money(quote.totalAmount, quote.currencyCode)}</MenuItem>)}</Select><FormHelperText>{form.formState.errors.quoteId?.message || (quotesAvailable.length ? 'Only accepted quotes can create a service request.' : 'No accepted quotes are available.')}</FormHelperText></FormControl>} />}
        <TextField select label="Priority" {...form.register('priority')}>{['LOW', 'MEDIUM', 'HIGH', 'URGENT'].map((priority) => <MenuItem key={priority} value={priority}>{priority}</MenuItem>)}</TextField>
        <TextField label="Preferred date" type="date" InputLabelProps={{ shrink: true }} {...form.register('preferredDate')} error={Boolean(form.formState.errors.preferredDate)} helperText={form.formState.errors.preferredDate?.message} />
        <TextField label="Description" multiline minRows={3} {...form.register('description')} error={Boolean(form.formState.errors.description)} helperText={form.formState.errors.description?.message} />
        {mutation.isError && <Alert severity="error">{mutation.error.message || 'Service request could not be created.'}</Alert>}
      </Stack></DialogContent><DialogActions><Button onClick={() => setDialogOpen(false)} disabled={mutation.isPending}>Cancel</Button><Button type="submit" form="lead-service-request-form" variant="contained" disabled={mutation.isPending || quotes.isLoading || quotesAvailable.length === 0}>Create service request</Button></DialogActions>
    </Dialog>
  </Stack>;
}

function Field({ label, value }: { label: string; value: string }) { return <Stack spacing={0.25}><Typography variant="caption" color="text.secondary">{label}</Typography><Typography variant="body2">{value}</Typography></Stack>; }
function money(value: number | null | undefined, currency: string | null | undefined) { return value == null ? '-' : new Intl.NumberFormat(undefined, { style: 'currency', currency: currency || 'INR' }).format(value); }

