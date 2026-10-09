import { Alert, Box, Button, Card, CardContent, Chip, CircularProgress, Link as MuiLink, Stack, Table, TableBody, TableCell, TableHead, TableRow, Typography } from '@mui/material';
import { Link } from 'react-router-dom';
import { useQueries, useQuery } from '@tanstack/react-query';
import type { UseQueryResult } from '@tanstack/react-query';
import { invoiceApi } from '../../invoices/services/invoiceApi';
import type { InvoiceResponse, PaymentResponse } from '../../invoices/types/invoice';
import { isVerifiedSettlement, paymentStatusLabel } from '../../payments/utils/settlement';

const money = (amount: number, currencyCode?: string | null) => new Intl.NumberFormat(undefined, { style: 'currency', currency: currencyCode || 'INR', maximumFractionDigits: 2 }).format(amount);

export default function DealFinancialsPanel({ dealId, completed }: { dealId: string; completed: boolean }) {
  const invoices = useQuery({
    queryKey: ['invoices', 'entity', 'DEAL', dealId],
    queryFn: () => invoiceApi.listForEntity('DEAL', dealId),
  });
  const paymentQueries = useQueries({
    queries: (invoices.data ?? []).map((invoice) => ({
      queryKey: ['invoices', 'payments', invoice.invoiceId],
      queryFn: () => invoiceApi.payments(invoice.invoiceId),
    })),
  });

  const paymentByInvoice = new Map<string, PaymentResponse[]>(
    (invoices.data ?? []).map((invoice, index) => [invoice.invoiceId, paymentQueries[index]?.data ?? []]),
  );
  const billableInvoices = (invoices.data ?? []).filter((invoice) => invoice.status !== 'CANCELLED');
  const currencies = [...new Set(billableInvoices.map((invoice) => (invoice.currencyCode || 'INR').toUpperCase()))];
  const financialMetrics = (currencies.length ? currencies : ['INR']).flatMap((currency) => {
    const currencyInvoices = billableInvoices.filter((invoice) => (invoice.currencyCode || 'INR').toUpperCase() === currency);
    const total = currencyInvoices.reduce((sum, invoice) => sum + invoice.invoiceAmount + invoice.taxAmount, 0);
    const settled = currencyInvoices.flatMap((invoice) => paymentByInvoice.get(invoice.invoiceId) ?? [])
      .filter((payment) => isVerifiedSettlement(payment) && (payment.currencyCode || currency).toUpperCase() === currency)
      .reduce((sum, payment) => sum + payment.amount, 0);
    return [
      { label: `Invoiced for deal - ${currency}`, value: money(total, currency) },
      { label: `Settled payments - ${currency}`, value: money(settled, currency) },
      { label: `Outstanding balance - ${currency}`, value: money(Math.max(0, total - settled), currency) },
    ];
  });
  const paymentErrors = paymentQueries.some((query) => query.isError);
  const paymentLoading = paymentQueries.some((query) => query.isLoading);

  return <Card variant="outlined"><CardContent><Stack spacing={2}>
    <Box><Typography variant="h6" fontWeight={700}>Property deal commission invoices & payments</Typography><Typography variant="body2" color="text.secondary">Financial records are sourced from commission invoices issued against this property transaction and their recorded payment settlements. Service Request revenue is tracked separately.</Typography></Box>
    {invoices.isLoading && <CircularProgress size={24} aria-label="Loading deal invoices" />}
    {invoices.isError && <Alert severity="error" action={<Button color="inherit" size="small" onClick={() => void invoices.refetch()}>Retry</Button>}>Deal invoices could not be loaded.</Alert>}
    {invoices.data && <>
      <Box sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr', sm: 'repeat(auto-fit, minmax(190px, 1fr))' }, gap: 2 }}>
        {financialMetrics.map((metric) => <Metric key={metric.label} label={metric.label} value={paymentLoading && metric.label.startsWith('Settled') ? 'Loading...' : paymentErrors && metric.label.startsWith('Settled') ? 'Unavailable' : metric.value} />)}
      </Box>
      {paymentErrors && <Alert severity="warning">Some invoice payment records could not be loaded. Retry from the invoice page to review those records.</Alert>}
      {invoices.data.length === 0 ? <Alert severity="info">{completed ? 'No invoice is currently linked to this completed deal.' : 'Deal commission invoices are generated when the deal is completed.'}</Alert> :
        <Box sx={{ overflowX: 'auto' }}><Table size="small" aria-label="Deal invoices and payment records">
          <TableHead><TableRow><TableCell>Invoice</TableCell><TableCell>Status</TableCell><TableCell align="right">Amount</TableCell><TableCell>Payments</TableCell><TableCell>Due date</TableCell><TableCell /></TableRow></TableHead>
          <TableBody>{invoices.data.map((invoice, index) => <InvoiceRow key={invoice.invoiceId} invoice={invoice} payments={paymentByInvoice.get(invoice.invoiceId) ?? []} paymentQuery={paymentQueries[index]} />)}</TableBody>
        </Table></Box>}
    </>}
  </Stack></CardContent></Card>;
}

function InvoiceRow({ invoice, payments, paymentQuery }: { invoice: InvoiceResponse; payments: PaymentResponse[]; paymentQuery: UseQueryResult<PaymentResponse[], Error> }) {
  const currency = (invoice.currencyCode || 'INR').toUpperCase();
  const settled = payments.filter((payment) => isVerifiedSettlement(payment)
    && (payment.currencyCode || currency).toUpperCase() === currency).reduce((total, payment) => total + payment.amount, 0);
  return <TableBodyRow invoice={invoice} settled={settled} payments={payments} loading={paymentQuery.isLoading} error={paymentQuery.isError} retry={() => void paymentQuery.refetch()} />;
}

function TableBodyRow({ invoice, settled, payments, loading, error, retry }: { invoice: InvoiceResponse; settled: number; payments: PaymentResponse[]; loading: boolean; error: boolean; retry: () => void }) {
  const hasUnverifiedSuccess = payments.some((payment) => payment.paymentStatus === 'SUCCESS' && !isVerifiedSettlement(payment));
  return <TableRow>
    <TableCell><MuiLink component={Link} to={`/invoices/${invoice.invoiceId}`} underline="hover">{invoice.invoiceNumber}</MuiLink><Typography variant="caption" display="block" color="text.secondary">{invoice.invoiceType.replaceAll('_', ' ')}</Typography></TableCell>
    <TableCell><Chip size="small" label={invoice.status.replaceAll('_', ' ')} color={invoice.status === 'PAID' ? 'success' : invoice.status === 'CANCELLED' ? 'default' : 'warning'} /></TableCell>
    <TableCell align="right">{money(invoice.invoiceAmount + invoice.taxAmount, invoice.currencyCode)}</TableCell>
    <TableCell>{loading ? 'Loading...' : error ? <Button size="small" onClick={retry}>Retry</Button> : <Stack spacing={0.5}><Typography variant="body2">Verified settled: {money(settled, invoice.currencyCode)}</Typography>{hasUnverifiedSuccess && <Typography variant="caption" color="warning.main">Unverified success requires reconciliation</Typography>}{payments.length === 0 ? <Typography variant="caption" color="text.secondary">No payment records</Typography> : payments.map((payment) => <MuiLink key={payment.paymentId} component={Link} to={`/staff/payments/${payment.paymentId}`} variant="caption" underline="hover">{money(payment.amount, payment.currencyCode || invoice.currencyCode)} / {paymentStatusLabel(payment)}</MuiLink>)}</Stack>}</TableCell>
    <TableCell>{invoice.dueAt ? new Date(invoice.dueAt).toLocaleDateString() : '-'}</TableCell>
    <TableCell><Button component={Link} to={`/invoices/${invoice.invoiceId}`} size="small">View</Button></TableCell>
  </TableRow>;
}

function Metric({ label, value }: { label: string; value: string }) {
  return <Box><Typography variant="caption" color="text.secondary">{label}</Typography><Typography variant="h6" fontWeight={700}>{value}</Typography></Box>;
}
