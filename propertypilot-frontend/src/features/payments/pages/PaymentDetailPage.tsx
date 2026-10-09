import { Alert, Button, Card, CardContent, Chip, Skeleton, Stack, Typography } from '@mui/material';
import { Link, useParams } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { paymentsApi } from '../../../api/payments';
import { invoiceApi } from '../../invoices/services/invoiceApi';
import { isVerifiedSettlement, paymentStatusLabel } from '../utils/settlement';

const money = (amount: number, currencyCode?: string | null) => new Intl.NumberFormat(undefined, { style: 'currency', currency: currencyCode || 'INR' }).format(amount);

export default function PaymentDetailPage() {
  const { paymentId = '' } = useParams<{ paymentId: string }>();
  const query = useQuery({ queryKey: ['payments', 'detail', paymentId], queryFn: () => paymentsApi.get(paymentId), enabled: Boolean(paymentId) });
  const invoice = useQuery({ queryKey: ['invoices', 'detail', query.data?.invoiceId], queryFn: () => invoiceApi.get(query.data!.invoiceId), enabled: Boolean(query.data?.invoiceId) });
  if (query.isLoading) return <Stack spacing={2}><Skeleton height={42} /><Skeleton variant="rounded" height={250} /></Stack>;
  if (query.isError || !query.data) return <Stack spacing={2}><Button component={Link} to="/staff/payments" sx={{ alignSelf: 'flex-start' }}>Back to Payments</Button><Alert severity="error" action={<Button color="inherit" onClick={() => void query.refetch()}>Retry</Button>}>{query.error instanceof Error ? query.error.message : 'Payment could not be loaded.'}</Alert></Stack>;
  const payment = query.data;
  return <Stack spacing={3}>
    <Button component={Link} to="/staff/payments" sx={{ alignSelf: 'flex-start', ml: -1 }}>Back to Payments</Button>
    <Stack direction={{ xs: 'column', sm: 'row' }} spacing={1} alignItems={{ sm: 'center' }}><Typography variant="h4" fontWeight={750}>Payment details</Typography><Chip label={paymentStatusLabel(payment)} color={isVerifiedSettlement(payment) ? 'success' : payment.paymentStatus === 'FAILED' ? 'error' : 'warning'} /></Stack>
    <Card variant="outlined"><CardContent><Stack spacing={2}>
      <Typography variant="h6" fontWeight={700}>Payment details</Typography>
      <Typography>Amount: <strong>{money(payment.amount,payment.currencyCode||invoice.data?.currencyCode)}</strong></Typography>
      <Typography>Method: {payment.paymentMethod.replaceAll('_', ' ')}</Typography>
      <Typography>Customer: {invoice.data?.customerName || 'Customer'}</Typography>
      <Typography>Provider: {payment.provider || '—'}</Typography>
      <Typography>Provider transaction: {payment.providerPaymentId || '—'}</Typography>
      <Typography>Settled at: {payment.settledAt ? new Date(payment.settledAt).toLocaleString() : payment.paymentDate ? new Date(payment.paymentDate).toLocaleString() : '—'}</Typography>
      {payment.paymentStatus === 'SUCCESS' && !isVerifiedSettlement(payment) && <Alert severity="warning">This payment is marked successful without a verified settlement timestamp. Do not count it as collected until it is reconciled.</Alert>}
      <Typography>Invoice: {invoice.isError ? <Button size="small" onClick={() => void invoice.refetch()}>Retry invoice details</Button> : <Button component={Link} to={`/invoices/${payment.invoiceId}`} size="small">View {invoice.data?.invoiceNumber || 'invoice'}</Button>}</Typography>
    </Stack></CardContent></Card>
  </Stack>;
}
