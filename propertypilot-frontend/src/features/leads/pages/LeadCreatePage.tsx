import { zodResolver } from '@hookform/resolvers/zod';
import { Alert, Button, Card, CardContent, Stack, TextField, Typography } from '@mui/material';
import { useForm } from 'react-hook-form';
import { z } from 'zod';
import { Link, useNavigate } from 'react-router-dom';
import { useCreateLead } from '../hooks/useLeads';

const schema = z.object({ source: z.string().trim().min(1, 'Enter how you heard about PropertyPilot.').max(80), budgetMin: z.string().optional(), budgetMax: z.string().optional(), notes: z.string().max(2000).optional() }).superRefine((data, context) => {
  const min = data.budgetMin ? Number(data.budgetMin) : undefined;
  const max = data.budgetMax ? Number(data.budgetMax) : undefined;
  if (min !== undefined && (!Number.isFinite(min) || min < 0)) context.addIssue({ code: 'custom', path: ['budgetMin'], message: 'Enter a non-negative amount.' });
  if (max !== undefined && (!Number.isFinite(max) || max < 0)) context.addIssue({ code: 'custom', path: ['budgetMax'], message: 'Enter a non-negative amount.' });
  if (min !== undefined && max !== undefined && max < min) context.addIssue({ code: 'custom', path: ['budgetMax'], message: 'Maximum budget must be at least the minimum.' });
});
type FormValues = z.infer<typeof schema>;

export default function LeadCreatePage() {
  const mutation = useCreateLead();
  const navigate = useNavigate();
  const form = useForm<FormValues>({ resolver: zodResolver(schema), defaultValues: { source: 'CUSTOMER', budgetMin: '', budgetMax: '', notes: '' } });
  const submit = form.handleSubmit(async (values) => {
    try {
      const lead = await mutation.mutateAsync({ source: values.source, ...(values.budgetMin ? { budgetMin: Number(values.budgetMin) } : {}), ...(values.budgetMax ? { budgetMax: Number(values.budgetMax) } : {}), ...(values.notes?.trim() ? { notes: values.notes.trim() } : {}) });
      navigate(`/customer/leads/${lead.leadId}`, { replace: true });
    } catch { /* Mutation error is rendered in the form. */ }
  });
  return <Stack spacing={3}><Stack direction="row" justifyContent="space-between" alignItems="center"><Stack><Typography variant="h4" fontWeight={750}>Create a lead</Typography><Typography color="text.secondary">Share your enquiry with the PropertyPilot team.</Typography></Stack><Button component={Link} to="/customer/leads">Back to leads</Button></Stack>
    <Card variant="outlined"><CardContent><Stack component="form" spacing={2.5} onSubmit={(event) => void submit(event)} noValidate>
      <Typography variant="h6" fontWeight={700}>Enquiry details</Typography>
      <TextField label="Lead source" {...form.register('source')} error={Boolean(form.formState.errors.source)} helperText={form.formState.errors.source?.message || 'For example: customer portal, referral, or phone.'} />
      <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}><TextField label="Minimum budget" type="number" inputProps={{ min: 0, step: '0.01' }} fullWidth {...form.register('budgetMin')} error={Boolean(form.formState.errors.budgetMin)} helperText={form.formState.errors.budgetMin?.message} /><TextField label="Maximum budget" type="number" inputProps={{ min: 0, step: '0.01' }} fullWidth {...form.register('budgetMax')} error={Boolean(form.formState.errors.budgetMax)} helperText={form.formState.errors.budgetMax?.message} /></Stack>
      <TextField label="Notes" multiline minRows={4} {...form.register('notes')} error={Boolean(form.formState.errors.notes)} helperText={form.formState.errors.notes?.message || 'Add details about what you are looking for.'} />
      {mutation.isError && <Alert severity="error">{mutation.error.message || 'Lead could not be created. Please try again.'}</Alert>}
      <Button type="submit" variant="contained" disabled={mutation.isPending}>{mutation.isPending ? 'Submitting…' : 'Submit lead'}</Button>
    </Stack></CardContent></Card>
  </Stack>;
}
