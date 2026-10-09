import { Alert, Button, Dialog, DialogActions, DialogContent, DialogTitle, FormControl, InputLabel, MenuItem, Select, Stack, TextField } from '@mui/material';
import { Controller, useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useRecordInvoicePayment } from '../hooks/useInvoices';

const schema = z.object({
  amount: z.coerce.number().positive('Enter a payment amount.'),
  paymentMethod: z.enum(['CASH', 'UPI', 'BANK_TRANSFER', 'CARD', 'CHEQUE']),
  provider: z.string().trim().min(1, 'Enter the payment provider.').max(100),
  providerPaymentId: z.string().trim().min(1, 'Enter the provider transaction reference.').max(100),
  remarks: z.string().max(500),
});
type Values = z.input<typeof schema>;

interface InvoicePaymentDialogProps {
  open: boolean;
  invoiceId: string;
  amount: number;
  onClose: () => void;
  onSuccess: () => void;
}

export default function InvoicePaymentDialog({ open, invoiceId, amount, onClose, onSuccess }: InvoicePaymentDialogProps) {
  const mutation = useRecordInvoicePayment(invoiceId);
  const { control, register, handleSubmit, formState: { errors } } = useForm<Values>({
    resolver: zodResolver(schema),
    defaultValues: { amount, paymentMethod: 'UPI', provider: '', providerPaymentId: '', remarks: '' },
  });

  const submit = handleSubmit(async (value) => {
    try {
      await mutation.mutateAsync({
        amount: Number(value.amount),
        paymentMethod: value.paymentMethod,
        provider: value.provider.trim(),
        providerPaymentId: value.providerPaymentId.trim(),
        remarks: value.remarks || undefined,
      });
      onSuccess();
      onClose();
    } catch {
      // The API error is shown in the dialog.
    }
  });

  return <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
    <DialogTitle>Register payment for settlement</DialogTitle>
    <DialogContent>
      <Stack spacing={2} sx={{ pt: 1 }}>
        <Alert severity="info">This registers a pending payment. The invoice changes to paid only after provider settlement is verified. Outstanding balance: {amount.toLocaleString()}.</Alert>
        {mutation.error && <Alert severity="error">{mutation.error instanceof Error ? mutation.error.message : 'Payment could not be registered.'}</Alert>}
        <TextField type="number" label="Amount" {...register('amount')} error={Boolean(errors.amount)} helperText={errors.amount?.message} inputProps={{ min: 0.01, step: 0.01 }} />
        <Controller name="paymentMethod" control={control} render={({ field }) => <FormControl fullWidth>
          <InputLabel id="payment-method-label">Payment method</InputLabel>
          <Select {...field} labelId="payment-method-label" label="Payment method">
            {['CASH', 'UPI', 'BANK_TRANSFER', 'CARD', 'CHEQUE'].map((method) => <MenuItem key={method} value={method}>{method.replaceAll('_', ' ')}</MenuItem>)}
          </Select>
        </FormControl>} />
        <TextField label="Payment provider" required {...register('provider')} error={Boolean(errors.provider)} helperText={errors.provider?.message} />
        <TextField label="Provider transaction reference" required {...register('providerPaymentId')} error={Boolean(errors.providerPaymentId)} helperText={errors.providerPaymentId?.message} />
        <TextField label="Remarks" multiline minRows={2} {...register('remarks')} />
      </Stack>
    </DialogContent>
    <DialogActions>
      <Button onClick={onClose}>Cancel</Button>
      <Button variant="contained" onClick={() => void submit()} disabled={mutation.isPending}>Register pending payment</Button>
    </DialogActions>
  </Dialog>;
}
