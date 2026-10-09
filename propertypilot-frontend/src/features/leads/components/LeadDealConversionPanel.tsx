import { useMemo, useState } from 'react';
import { zodResolver } from '@hookform/resolvers/zod';
import { useQuery } from '@tanstack/react-query';
import { Alert, Button, Card, CardContent, Dialog, DialogActions, DialogContent, DialogTitle, FormControl, FormHelperText, InputLabel, LinearProgress, Link as MuiLink, MenuItem, Select, Skeleton, Stack, TextField, Typography } from '@mui/material';
import { Controller, useForm, useWatch } from 'react-hook-form';
import { z } from 'zod';
import { Link } from 'react-router-dom';
import { dealApi } from '../../deals/services/dealApi';
import { useConvertLeadToDeal, useLeadDeals } from '../hooks/useLeads';
import type { Lead } from '../types/lead';

const dealTypes = ['PROPERTY_SALE', 'PROPERTY_PURCHASE', 'PROPERTY_RENTAL', 'PROPERTY_LEASE'] as const;
const dealTypeLabels: Record<(typeof dealTypes)[number], string> = {
  PROPERTY_SALE: 'Property sale',
  PROPERTY_PURCHASE: 'Property purchase',
  PROPERTY_RENTAL: 'Property rental',
  PROPERTY_LEASE: 'Property lease',
};
const terminalDealStatuses = new Set(['COMPLETED', 'CANCELLED']);
const conversionStatuses = new Set(['QUALIFIED', 'READY_FOR_DEAL', 'DEAL_CONVERTED', 'CONVERTED']);

const schema = z.object({
  dealType: z.enum(dealTypes),
  propertyId: z.string(),
  expectedAmount: z.coerce.number().positive('Enter an expected transaction amount.'),
  sellerCommissionPercent: z.coerce.number().min(0).max(100),
  buyerCommissionPercent: z.coerce.number().min(0).max(100),
  remarks: z.string().max(2000, 'Deal notes cannot exceed 2,000 characters.'),
  duplicateOpportunityReason: z.string().max(1000, 'Reason cannot exceed 1,000 characters.'),
});
type FormInput = z.input<typeof schema>;
type FormValues = z.output<typeof schema>;

export default function LeadDealConversionPanel({ lead }: { lead: Lead }) {
  const linkedDeals = useLeadDeals(lead.leadId);
  const convert = useConvertLeadToDeal(lead.leadId);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [notice, setNotice] = useState('');
  const propertyOptions = useQuery({
    queryKey: ['lead-deal-properties', lead.customerId],
    queryFn: dealApi.properties,
    enabled: dialogOpen && Boolean(lead.customerId),
  });
  const form = useForm<FormInput, unknown, FormValues>({
    resolver: zodResolver(schema),
    defaultValues: {
      dealType: 'PROPERTY_SALE', propertyId: '', expectedAmount: '',
      sellerCommissionPercent: 0, buyerCommissionPercent: 0, remarks: '', duplicateOpportunityReason: '',
    },
  });
  const selectedDealType = useWatch({ control: form.control, name: 'dealType' });
  const customerProperties = useMemo(() => (propertyOptions.data ?? [])
    .filter((property) => property.customerId === lead.customerId)
    .sort((a, b) => a.title.localeCompare(b.title)), [lead.customerId, propertyOptions.data]);
  const activeSameTypeExists = (linkedDeals.data ?? []).some((deal) =>
    deal.dealType === selectedDealType && !terminalDealStatuses.has(deal.dealStatus));
  const duplicateReasonRequired = ['DEAL_CONVERTED', 'CONVERTED'].includes(lead.status) || activeSameTypeExists;
  const hasProspectivePropertyContext = Boolean(lead.propertyType?.trim() && lead.location?.trim());
  const canConvert = conversionStatuses.has(lead.status) && Boolean(lead.customerId);

  const submit = form.handleSubmit(async (values) => {
    if (!values.propertyId && !hasProspectivePropertyContext) {
      form.setError('propertyId', { type: 'validate', message: 'Select one of this customer’s properties. This lead has no prospective property details.' });
      return;
    }
    if (duplicateReasonRequired && !values.duplicateOpportunityReason.trim()) {
      form.setError('duplicateOpportunityReason', { type: 'validate', message: 'Explain why another deal is authorized.' });
      return;
    }
    try {
      await convert.mutateAsync({
        dealType: values.dealType,
        ...(values.propertyId ? { propertyId: values.propertyId } : {}),
        expectedAmount: values.expectedAmount,
        sellerCommissionPercent: values.sellerCommissionPercent,
        buyerCommissionPercent: values.buyerCommissionPercent,
        ...(values.remarks.trim() ? { remarks: values.remarks.trim() } : {}),
        ...(values.duplicateOpportunityReason.trim() ? { duplicateOpportunityReason: values.duplicateOpportunityReason.trim() } : {}),
      });
      setDialogOpen(false);
      form.reset();
      setNotice('Deal created and linked to this lead. Lead conversion history was recorded.');
    } catch { /* The API error is rendered in the conversion form. */ }
  });

  return <>
    <Card variant="outlined"><CardContent><Stack spacing={2}>
      <Stack direction={{ xs: 'column', sm: 'row' }} justifyContent="space-between" gap={1}>
        <Stack spacing={0.5}><Typography variant="h6" fontWeight={700}>Linked deals</Typography><Typography variant="body2" color="text.secondary">Property transaction opportunities linked to this lead.</Typography></Stack>
        {canConvert && <Button variant="contained" onClick={() => { setNotice(''); setDialogOpen(true); }}>Convert to deal</Button>}
      </Stack>
      <Alert severity="info">Deal conversion creates a property transaction. Service requests and their revenue remain linked and tracked separately.</Alert>
      {notice && <Alert severity="success">{notice}</Alert>}
      {linkedDeals.isLoading && <Skeleton variant="rounded" height={72} />}
      {linkedDeals.isError && <Alert severity="error" action={<Button color="inherit" size="small" onClick={() => void linkedDeals.refetch()}>Retry</Button>}>Linked deals could not be loaded. {linkedDeals.error instanceof Error ? linkedDeals.error.message : ''}</Alert>}
      {!linkedDeals.isLoading && !linkedDeals.isError && linkedDeals.data?.length === 0 && <Typography color="text.secondary">No deals are linked to this lead.</Typography>}
      {linkedDeals.data?.map((deal) => <Stack key={deal.dealId} direction={{ xs: 'column', sm: 'row' }} spacing={1} justifyContent="space-between" alignItems={{ sm: 'center' }}>
        <Stack><MuiLink component={Link} to={`/deals/${deal.dealId}`} underline="hover" fontWeight={650}>View linked deal</MuiLink><Typography variant="body2" color="text.secondary">{dealTypeLabels[deal.dealType as (typeof dealTypes)[number]] ?? 'Property transaction'} · {deal.dealStatus.replaceAll('_', ' ')} · {new Date(deal.createdAt).toLocaleDateString()}</Typography></Stack>
        <Typography fontWeight={650}>{amount(deal.expectedAmount)}</Typography>
      </Stack>)}
      {!canConvert && <Typography variant="caption" color="text.secondary">A deal can be created after this lead is qualified and marked ready. Link a customer account before conversion.</Typography>}
    </Stack></CardContent></Card>

    <Dialog open={dialogOpen} onClose={() => { if (!convert.isPending) setDialogOpen(false); }} fullWidth maxWidth="sm">
      <DialogTitle>Convert lead to property deal</DialogTitle>
      <DialogContent><Stack component="form" id="lead-deal-conversion-form" spacing={2.5} sx={{ pt: 1 }} onSubmit={(event) => void submit(event)} noValidate>
        <Typography variant="body2" color="text.secondary">The deal will retain this lead as its source. Existing service request history and revenue stay on their own records. Deal currency is not exposed by the current API.</Typography>
        {propertyOptions.isError && <Alert severity="error" action={<Button color="inherit" size="small" onClick={() => void propertyOptions.refetch()}>Retry</Button>}>Customer properties could not be loaded.</Alert>}
        <Controller name="dealType" control={form.control} render={({ field }) => <FormControl fullWidth error={Boolean(form.formState.errors.dealType)}><InputLabel id="lead-conversion-type">Transaction type</InputLabel><Select {...field} labelId="lead-conversion-type" label="Transaction type">{dealTypes.map((type) => <MenuItem key={type} value={type}>{dealTypeLabels[type]}</MenuItem>)}</Select>{form.formState.errors.dealType && <FormHelperText>{form.formState.errors.dealType.message}</FormHelperText>}</FormControl>} />
        <Controller name="propertyId" control={form.control} render={({ field }) => <FormControl fullWidth error={Boolean(form.formState.errors.propertyId)} disabled={propertyOptions.isLoading || propertyOptions.isError}><InputLabel id="lead-conversion-property">Property</InputLabel><Select {...field} labelId="lead-conversion-property" label="Property"><MenuItem value="" disabled={!hasProspectivePropertyContext}>{hasProspectivePropertyContext ? 'Use prospective property details from lead' : 'Select customer property'}</MenuItem>{customerProperties.map((property) => <MenuItem key={property.propertyId} value={property.propertyId}>{property.title}</MenuItem>)}</Select><FormHelperText>{form.formState.errors.propertyId?.message || (hasProspectivePropertyContext ? `Prospective property: ${lead.propertyType} in ${lead.location}.` : 'Select a property owned by the lead customer.')}</FormHelperText></FormControl>} />
        <TextField {...form.register('expectedAmount')} type="number" label="Expected transaction amount (currency unavailable)" inputProps={{ min: 0.01, step: 0.01 }} error={Boolean(form.formState.errors.expectedAmount)} helperText={form.formState.errors.expectedAmount?.message} />
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}><TextField {...form.register('sellerCommissionPercent')} type="number" label="Seller commission (%)" inputProps={{ min: 0, max: 100, step: 0.01 }} error={Boolean(form.formState.errors.sellerCommissionPercent)} helperText={form.formState.errors.sellerCommissionPercent?.message} fullWidth /><TextField {...form.register('buyerCommissionPercent')} type="number" label="Buyer commission (%)" inputProps={{ min: 0, max: 100, step: 0.01 }} error={Boolean(form.formState.errors.buyerCommissionPercent)} helperText={form.formState.errors.buyerCommissionPercent?.message} fullWidth /></Stack>
        <TextField {...form.register('remarks')} label="Deal notes" multiline minRows={2} error={Boolean(form.formState.errors.remarks)} helperText={form.formState.errors.remarks?.message} />
        {duplicateReasonRequired && <TextField {...form.register('duplicateOpportunityReason')} required label="Reason for additional deal" multiline minRows={2} error={Boolean(form.formState.errors.duplicateOpportunityReason)} helperText={form.formState.errors.duplicateOpportunityReason?.message || 'The backend requires an authorized reason for another deal of this type.'} />}
        {convert.isError && <Alert severity="error">{convert.error instanceof Error ? convert.error.message : 'Lead conversion failed.'}</Alert>}
        {convert.isPending && <LinearProgress />}
      </Stack></DialogContent>
      <DialogActions><Button onClick={() => setDialogOpen(false)} disabled={convert.isPending}>Cancel</Button><Button type="submit" form="lead-deal-conversion-form" variant="contained" disabled={convert.isPending || propertyOptions.isLoading || propertyOptions.isError || linkedDeals.isLoading || linkedDeals.isError}>{convert.isPending ? 'Creating deal…' : 'Create deal'}</Button></DialogActions>
    </Dialog>
  </>;
}

function amount(value: number) {
  return new Intl.NumberFormat(undefined, { maximumFractionDigits: 2 }).format(value);
}
