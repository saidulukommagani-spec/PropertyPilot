import { useState } from 'react';
import { Alert, Box, Button, Card, CardContent, Chip, Skeleton, Stack, Typography } from '@mui/material';
import { useCustomerBilling } from '../hooks/useCustomerPortal';
import { CustomerPageHeading, CustomerQueryError } from '../components/CustomerStates';
import { useInvoicePayments } from '../../invoices/hooks/useInvoices';
import InvoicePaymentDialog from '../../invoices/components/InvoicePaymentDialog';
import type { CustomerInvoice } from '../types/customerPortal';
import { isVerifiedSettlement } from '../../payments/utils/settlement';

const money = (amount: number, currencyCode?: string | null) => new Intl.NumberFormat(undefined, { style: 'currency', currency: currencyCode || 'INR' }).format(amount);

export default function CustomerInvoicesPage() {
  const billing = useCustomerBilling();
  const invoices = billing.invoices.data ?? [];

  return <Stack spacing={3}>
    <CustomerPageHeading title="Invoices" subtitle="Review invoice totals, payment status, and due dates." />
    {billing.profile.isError && <CustomerQueryError error={billing.profile.error} retry={() => void billing.profile.refetch()} />}
    {billing.invoices.isError && <CustomerQueryError error={billing.invoices.error} retry={() => void billing.invoices.refetch()} />}
    {billing.invoices.isLoading
      ? Array.from({ length: 3 }, (_, index) => <Skeleton key={index} variant="rounded" height={125} />)
      : invoices.length
        ? invoices.map((invoice) => <CustomerInvoiceCard key={invoice.invoiceId} invoice={invoice} onPaymentReported={() => void billing.invoices.refetch()} />)
        : !billing.invoices.isError && <Alert severity="info">No invoices are available for your account.</Alert>}
  </Stack>;
}

function CustomerInvoiceCard({ invoice, onPaymentReported }: { invoice: CustomerInvoice; onPaymentReported: () => void }) {
  const [paymentOpen, setPaymentOpen] = useState(false);
  const canAcceptPayment = ['SENT', 'PARTIALLY_PAID', 'OVERDUE'].includes(invoice.status);
  const payments = useInvoicePayments(invoice.invoiceId);
  const invoiceCurrency = (invoice.currencyCode || 'INR').toUpperCase();
  const currencyMatchesInvoice = (payment: NonNullable<typeof payments.data>[number]) =>
    (payment.currencyCode || '').toUpperCase() === invoiceCurrency;
  const settledPayments = payments.data?.filter((payment) => isVerifiedSettlement(payment) && currencyMatchesInvoice(payment)) ?? [];
  const settled = settledPayments.reduce((sum, payment) => sum + payment.amount, 0);
  const unverifiedSuccess = payments.data?.some((payment) => payment.paymentStatus === 'SUCCESS' && !isVerifiedSettlement(payment)) ?? false;
  const currencyMismatch = payments.data?.some((payment) =>
    (isVerifiedSettlement(payment) || payment.paymentStatus === 'PENDING') && !currencyMatchesInvoice(payment)) ?? false;
  const pending = payments.data?.filter((payment) => payment.paymentStatus === 'PENDING' && currencyMatchesInvoice(payment)).reduce((sum, payment) => sum + payment.amount, 0) ?? 0;
  const total = invoice.invoiceAmount + invoice.taxAmount;
  const balance = Math.max(0, total - settled);
  const availableToReport = Math.max(0, total - settled - pending);

  return <Card variant="outlined"><CardContent>
    <Stack direction={{ xs: 'column', sm: 'row' }} justifyContent="space-between" gap={2}>
      <Box>
        <Typography variant="h6" fontWeight={700}>{invoice.invoiceNumber}</Typography>
        <Typography variant="body2" color="text.secondary">{invoice.description || 'PropertyPilot invoice'}</Typography>
        <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
          Issued {invoice.issuedAt ? new Date(invoice.issuedAt).toLocaleDateString() : 'Not issued'}
          {' · '}Due {invoice.dueAt ? new Date(invoice.dueAt).toLocaleDateString() : 'No due date'}
        </Typography>
      </Box>
      <Stack alignItems={{ sm: 'flex-end' }} spacing={0.5}>
        <Chip label={invoice.status === 'PAID' && settled < total ? 'PAID · RECONCILIATION REQUIRED' : invoice.status.replaceAll('_', ' ')} color={invoice.status === 'PAID' && settled >= total ? 'success' : invoice.status === 'OVERDUE' ? 'error' : 'warning'} />
        <Typography variant="body2">Subtotal {money(invoice.invoiceAmount, invoice.currencyCode)}</Typography>
        <Typography variant="body2">Tax {money(invoice.taxAmount, invoice.currencyCode)}</Typography>
        <Typography fontWeight={750}>Total {money(total, invoice.currencyCode)}</Typography>
        {balance > 0 && <Typography variant="body2">Balance due {money(balance, invoice.currencyCode)}</Typography>}
      </Stack>
    </Stack>
    {payments.isLoading && <Skeleton height={36} />}
    {payments.isError && <Alert severity="error" sx={{ mt: 2 }} action={<Button color="inherit" onClick={() => void payments.refetch()}>Retry</Button>}>Payment status could not be loaded, so payment reporting is disabled.</Alert>}
    {!payments.isLoading && !payments.isError && invoice.status === 'PAID' && settled < total && <Alert severity="warning" sx={{ mt: 2 }}>The invoice is marked paid, but verified settlement records do not cover the invoice total. Contact PropertyPilot to reconcile this payment before relying on the paid status.</Alert>}
    {!payments.isLoading && !payments.isError && unverifiedSuccess && <Alert severity="warning" sx={{ mt: 2 }}>A legacy payment is marked successful without verified settlement evidence and is excluded from the settled balance.</Alert>}
    {!payments.isLoading && !payments.isError && currencyMismatch && <Alert severity="warning" sx={{ mt: 2 }}>A payment uses a different currency from this invoice. It is excluded from the balance and pending amount; contact PropertyPilot to reconcile the payment record.</Alert>}
    {canAcceptPayment && !payments.isLoading && !payments.isError && pending > 0 && <Alert severity="info" sx={{ mt: 2 }}>A payment of {money(pending, invoice.currencyCode)} is awaiting provider verification. The invoice will update after settlement is confirmed.</Alert>}
    {canAcceptPayment && !payments.isLoading && !payments.isError && availableToReport > 0 && <Button variant="outlined" sx={{ mt: 2 }} onClick={() => setPaymentOpen(true)}>Report external payment</Button>}
    <InvoicePaymentDialog open={paymentOpen} invoiceId={invoice.invoiceId} amount={availableToReport} onClose={() => setPaymentOpen(false)} onSuccess={() => { setPaymentOpen(false); onPaymentReported(); void payments.refetch(); }} />
  </CardContent></Card>;
}
