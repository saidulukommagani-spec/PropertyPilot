import { useState } from 'react';
import { Alert, Button, Card, CardContent, CircularProgress, Dialog, DialogActions, DialogContent, DialogTitle, FormControl, InputLabel, MenuItem, Select, Stack, TextField, Typography } from '@mui/material';
import { useQuery } from '@tanstack/react-query';
import { dealApi } from '../services/dealApi';
import { useAddDealParticipant, useDealParticipants, useSelectDealBuyer } from '../hooks/useDeals';
import type { AddDealParticipantRequest } from '../types/deal';

const roles: AddDealParticipantRequest['participantRole'][] = ['BUYER', 'TENANT', 'LANDLORD', 'SELLER'];
const visibilityOptions: AddDealParticipantRequest['contactVisibility'][] = ['HIDDEN', 'PROPERTY_PILOT_ONLY', 'SHARED'];

export default function DealParticipantsPanel({ dealId, sellerCustomerId }: { dealId: string; sellerCustomerId: string }) {
  const participants = useDealParticipants(dealId);
  const customers = useQuery({ queryKey: ['deals', 'customers'], queryFn: dealApi.customers });
  const add = useAddDealParticipant(dealId);
  const selectBuyer = useSelectDealBuyer(dealId);
  const [open, setOpen] = useState(false);
  const [customerId, setCustomerId] = useState('');
  const [role, setRole] = useState<AddDealParticipantRequest['participantRole']>('BUYER');
  const [contactVisibility, setContactVisibility] = useState<AddDealParticipantRequest['contactVisibility']>('PROPERTY_PILOT_ONLY');
  const [offeredAmount, setOfferedAmount] = useState('');
  const [remarks, setRemarks] = useState('');
  const [success, setSuccess] = useState('');
  const activeCustomers = (customers.data ?? []).filter((customer) => customer.customerId !== sellerCustomerId);

  const submit = () => add.mutate({
    customerId,
    participantRole: role,
    contactVisibility,
    ...(offeredAmount ? { offeredAmount: Number(offeredAmount) } : {}),
    ...(remarks.trim() ? { remarks: remarks.trim() } : {}),
  }, { onSuccess: () => { setOpen(false); setSuccess('Participant added.'); setCustomerId(''); setOfferedAmount(''); setRemarks(''); } });

  return <Card variant="outlined"><CardContent><Stack spacing={2}>
    <Stack direction={{ xs: 'column', sm: 'row' }} justifyContent="space-between" gap={1}>
      <BoxTitle />
      <Button variant="outlined" onClick={() => setOpen(true)}>Add participant</Button>
    </Stack>
    {success && <Alert severity="success" onClose={() => setSuccess('')}>{success}</Alert>}
    {add.isError && <Alert severity="error">{add.error instanceof Error ? add.error.message : 'Participant could not be added.'}</Alert>}
    {selectBuyer.isError && <Alert severity="error">{selectBuyer.error instanceof Error ? selectBuyer.error.message : 'Buyer could not be selected.'}</Alert>}
    {participants.isLoading && <CircularProgress size={24} aria-label="Loading deal participants" />}
    {participants.isError && <Alert severity="error" action={<Button color="inherit" size="small" onClick={() => void participants.refetch()}>Retry</Button>}>Participants could not be loaded.</Alert>}
    {!participants.isLoading && !participants.isError && (participants.data?.length ?? 0) === 0 && <Typography color="text.secondary">No additional participants are linked to this deal.</Typography>}
    {participants.data?.map((participant) => <Stack key={participant.participantId} direction={{ xs: 'column', sm: 'row' }} spacing={1} justifyContent="space-between" alignItems={{ sm: 'center' }}>
      <Stack><Typography fontWeight={650}>{participant.customerName} · {participant.participantRole.replaceAll('_', ' ')}</Typography><Typography variant="body2" color="text.secondary">{participant.participantStatus.replaceAll('_', ' ')} · Contact visibility: {participant.contactVisibility.replaceAll('_', ' ')}{participant.offeredAmount != null ? ` · Offer ${money(participant.offeredAmount)}` : ''}</Typography>{participant.remarks && <Typography variant="caption" color="text.secondary">{participant.remarks}</Typography>}</Stack>
      {participant.participantRole === 'BUYER' && !participant.selectedFlag && <Button size="small" disabled={selectBuyer.isPending} onClick={() => selectBuyer.mutate(participant.participantId, { onSuccess: () => setSuccess('Buyer selected for this deal.') })}>Select buyer</Button>}
      {participant.selectedFlag && <Typography color="success.main" fontWeight={650}>Selected buyer</Typography>}
    </Stack>)}

    <Dialog open={open} onClose={() => !add.isPending && setOpen(false)} fullWidth maxWidth="sm">
      <DialogTitle>Add deal participant</DialogTitle>
      <DialogContent><Stack spacing={2} sx={{ pt: 1 }}>
        {customers.isError && <Alert severity="error" action={<Button color="inherit" size="small" onClick={() => void customers.refetch()}>Retry</Button>}>Customer options could not be loaded.</Alert>}
        <FormControl fullWidth required><InputLabel id="participant-customer-label">Customer</InputLabel><Select labelId="participant-customer-label" label="Customer" value={customerId} onChange={(event) => setCustomerId(event.target.value)} disabled={customers.isLoading}>{activeCustomers.map((customer) => <MenuItem key={customer.customerId} value={customer.customerId}>{customer.fullName}{customer.email ? ` · ${customer.email}` : ''}</MenuItem>)}</Select></FormControl>
        <FormControl fullWidth><InputLabel id="participant-role-label">Role</InputLabel><Select labelId="participant-role-label" label="Role" value={role} onChange={(event) => setRole(event.target.value as AddDealParticipantRequest['participantRole'])}>{roles.map((value) => <MenuItem key={value} value={value}>{value.replaceAll('_', ' ')}</MenuItem>)}</Select></FormControl>
        <FormControl fullWidth><InputLabel id="participant-visibility-label">Contact visibility</InputLabel><Select labelId="participant-visibility-label" label="Contact visibility" value={contactVisibility} onChange={(event) => setContactVisibility(event.target.value as AddDealParticipantRequest['contactVisibility'])}>{visibilityOptions.map((value) => <MenuItem key={value} value={value}>{value.replaceAll('_', ' ')}</MenuItem>)}</Select></FormControl>
        <TextField type="number" label="Offer amount" value={offeredAmount} onChange={(event) => setOfferedAmount(event.target.value)} inputProps={{ min: 0, step: 0.01 }} />
        <TextField label="Remarks" value={remarks} onChange={(event) => setRemarks(event.target.value)} multiline minRows={2} />
      </Stack></DialogContent>
      <DialogActions><Button onClick={() => setOpen(false)} disabled={add.isPending}>Cancel</Button><Button variant="contained" onClick={submit} disabled={!customerId || customers.isLoading || add.isPending}>{add.isPending ? 'Adding…' : 'Add participant'}</Button></DialogActions>
    </Dialog>
  </Stack></CardContent></Card>;
}

function BoxTitle() { return <Stack spacing={0.5}><Typography variant="h6" fontWeight={700}>Participants & buyer selection</Typography><Typography variant="body2" color="text.secondary">Maintain deal parties and select the buyer before completion.</Typography></Stack>; }
function money(value: number) { return new Intl.NumberFormat(undefined, { style: 'currency', currency: 'INR', maximumFractionDigits: 2 }).format(value); }
