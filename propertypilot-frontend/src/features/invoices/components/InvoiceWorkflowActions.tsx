import { useState } from 'react';
import { Alert, Button, Snackbar, Stack } from '@mui/material';
import { useCancelInvoice, useSendInvoice } from '../hooks/useInvoices';
import type { InvoiceStatus } from '../types/invoice';

export default function InvoiceWorkflowActions({ invoiceId, status }: { invoiceId: string; status: InvoiceStatus }) {
  const send = useSendInvoice(invoiceId);
  const cancel = useCancelInvoice(invoiceId);
  const [message, setMessage] = useState('');
  const canCancel = ['DRAFT', 'SENT', 'PARTIALLY_PAID', 'OVERDUE'].includes(status);
  const isPending = send.isPending || cancel.isPending;

  return <Stack direction="row" spacing={1} flexWrap="wrap">
    {status === 'DRAFT' && <Button variant="contained" disabled={isPending} onClick={() => send.mutate(undefined, { onSuccess: () => setMessage('Invoice sent to the customer.'), onError: () => setMessage('Invoice could not be sent.') })}>Send invoice</Button>}
    {canCancel && <Button color="error" variant="outlined" disabled={isPending} onClick={() => {
      if (window.confirm('Cancel this invoice? An invoice with settled payments cannot be cancelled.')) {
        cancel.mutate(undefined, { onSuccess: () => setMessage('Invoice cancelled.'), onError: () => setMessage('Invoice could not be cancelled.') });
      }
    }}>Cancel invoice</Button>}
    {(send.isError || cancel.isError) && <Alert severity="error" role="alert">{send.error instanceof Error ? send.error.message : cancel.error instanceof Error ? cancel.error.message : 'Invoice action failed.'}</Alert>}
    <Snackbar open={Boolean(message)} autoHideDuration={4500} onClose={() => setMessage('')} message={message} />
  </Stack>;
}
