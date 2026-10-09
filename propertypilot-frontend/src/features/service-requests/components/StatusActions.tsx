import { useState } from 'react';
import { Alert, Button, CircularProgress, Dialog, DialogActions, DialogContent, DialogTitle, Stack, TextField } from '@mui/material';
interface Props { status: string; isPending: boolean; error?: string | null; canManageRequest?: boolean; onAssign(): void; onAction(action: 'accept' | 'start' | 'complete'): Promise<void>; onCancel(reason: string): Promise<void> }
export default function StatusActions({ status, isPending, error, canManageRequest = true, onAssign, onAction, onCancel }: Props) {
  const [actionError, setActionError] = useState<string | null>(null);
  const [cancelOpen, setCancelOpen] = useState(false);
  const [reason, setReason] = useState('');
  const actions: Record<string, { label: string; action: 'accept' | 'start' | 'complete' } | null> = {
    ASSIGNED: { label: 'Mark accepted', action: 'accept' }, ACCEPTED: { label: 'Start work', action: 'start' }, IN_PROGRESS: { label: 'Complete request', action: 'complete' },
  };
  if (status === 'COMPLETED') return null;
  const current = actions[status];
  const run = async (action: 'accept' | 'start' | 'complete') => { setActionError(null); try { await onAction(action); } catch (cause) { setActionError(cause instanceof Error ? cause.message : 'Unable to update request status.'); } };
  const cancel = async () => { setActionError(null); try { await onCancel(reason.trim()); setCancelOpen(false); setReason(''); } catch (cause) { setActionError(cause instanceof Error ? cause.message : 'Unable to cancel this request.'); } };
  return <Stack spacing={1} alignItems="flex-end">
    {canManageRequest && status === 'NEW' && <Button variant="contained" onClick={onAssign}>Assign Agent</Button>}
    {current && <Button variant="contained" disabled={isPending} onClick={() => void run(current.action)}>{isPending ? <CircularProgress size={19} /> : current.label}</Button>}
    {canManageRequest && ['NEW', 'PENDING_ASSIGNMENT', 'ASSIGNED', 'ACCEPTED', 'IN_PROGRESS'].includes(status) && <Button color="error" size="small" onClick={() => setCancelOpen(true)}>Cancel request</Button>}
    {(actionError || error) && <Alert severity="error">{actionError || error}</Alert>}
    <Dialog open={cancelOpen} onClose={() => !isPending && setCancelOpen(false)} fullWidth maxWidth="sm"><DialogTitle>Cancel service request</DialogTitle><DialogContent><TextField autoFocus fullWidth multiline minRows={3} label="Cancellation reason" value={reason} onChange={(event) => setReason(event.target.value)} required sx={{ mt: 1 }} inputProps={{ maxLength: 500 }} /></DialogContent><DialogActions><Button onClick={() => setCancelOpen(false)} disabled={isPending}>Keep request</Button><Button color="error" variant="contained" onClick={() => void cancel()} disabled={isPending || reason.trim().length < 3}>{isPending ? <CircularProgress size={18} /> : 'Cancel request'}</Button></DialogActions></Dialog>
  </Stack>;
}
