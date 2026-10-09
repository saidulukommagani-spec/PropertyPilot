import { useMemo, useState } from 'react';
import { Alert, Box, Button, Card, CardContent, Chip, Skeleton, Stack, Typography } from '@mui/material';
import { Link } from 'react-router-dom';
import { useQueries, useQuery } from '@tanstack/react-query';
import { SubscriptionApi } from '../services/SubscriptionApi';
import { useCreateRenewalInvoice, useRenewSubscription, useSendRenewalInvoice, useSubscriptions, subscriptionKeys } from '../hooks/useSubscriptions';
import { SubscriptionError, SubscriptionHeading } from '../components/SubscriptionStates';
import type { CustomerSubscription, RenewalInvoice, RenewalPayment } from '../types/subscription';
import { isVerifiedSettlement } from '../../payments/utils/settlement';

const DAY_MS = 24 * 60 * 60 * 1000;

function renewalPeriod(subscription: CustomerSubscription): { periodStart: string; periodEnd: string } | undefined {
  if (!subscription.startDate || !subscription.endDate) return undefined;
  const originalStart = Date.parse(`${subscription.startDate}T00:00:00Z`);
  const originalEnd = Date.parse(`${subscription.endDate}T00:00:00Z`);
  const termDays = Math.round((originalEnd - originalStart) / DAY_MS) + 1;
  if (!Number.isFinite(termDays) || termDays < 1) return undefined;
  const nextStart = originalEnd + DAY_MS;
  return {
    periodStart: new Date(nextStart).toISOString().slice(0, 10),
    periodEnd: new Date(nextStart + (termDays - 1) * DAY_MS).toISOString().slice(0, 10),
  };
}

function formatMoney(value: number, currencyCode: string): string {
  try {
    return new Intl.NumberFormat(undefined, { style: 'currency', currency: currencyCode }).format(value);
  } catch {
    return `${currencyCode} ${value.toFixed(2)}`;
  }
}

function isVerifiedFullInvoicePayment(payment: RenewalPayment, invoice: RenewalInvoice): boolean {
  const invoiceCurrency = invoice.currencyCode.trim().toUpperCase();
  const paymentCurrency = payment.currencyCode.trim().toUpperCase();
  const invoiceTotal = invoice.invoiceAmount + invoice.taxAmount;
  return isVerifiedSettlement(payment)
    && paymentCurrency === invoiceCurrency
    && Math.abs(payment.amount - invoiceTotal) < 0.000001;
}

export default function RenewalCenterPage() {
  const data = useSubscriptions();
  const customerId = data.profile.data?.customerId ?? '';
  const createInvoice = useCreateRenewalInvoice();
  const sendInvoice = useSendRenewalInvoice();
  const renewSubscription = useRenewSubscription();
  const [notice, setNotice] = useState('');
  const [actionError, setActionError] = useState('');
  const subscriptions = data.subscriptions.data ?? [];
  const invoiceQuery = useQuery({
    queryKey: subscriptionKeys.renewalInvoices(customerId, data.role.isOperations),
    queryFn: () => SubscriptionApi.renewalInvoices(customerId, data.role.isOperations),
    enabled: data.role.isOperations || Boolean(customerId),
  });
  const histories = useQueries({
    queries: subscriptions.map((item) => ({
      queryKey: subscriptionKeys.renewals(item.customerSubscriptionId),
      queryFn: () => SubscriptionApi.renewals(item.customerSubscriptionId),
    })),
  });
  const renewalInvoices = invoiceQuery.data ?? [];
  const paymentQueries = useQueries({
    queries: renewalInvoices.map((invoice) => ({
      queryKey: ['subscriptions', 'renewal-payments', invoice.invoiceId],
      queryFn: () => SubscriptionApi.renewalPayments(invoice.invoiceId),
      enabled: invoice.status === 'PAID',
    })),
  });
  const paymentsByInvoice = useMemo(() => new Map(
    renewalInvoices.map((invoice, index) => [invoice.invoiceId, paymentQueries[index]?.data ?? []]),
  ), [renewalInvoices, paymentQueries]);
  const cutoffDate = new Date();
  cutoffDate.setUTCDate(cutoffDate.getUTCDate() + 90);
  const cutoff = cutoffDate.toISOString().slice(0, 10);
  const eligible = subscriptions
    .filter((item) => item.endDate && (item.status === 'EXPIRED'
      || (item.status === 'ACTIVE' && item.endDate <= cutoff)))
    .sort((a, b) => (a.endDate ?? '').localeCompare(b.endDate ?? ''));
  const history = histories.flatMap((query) => query.data ?? [])
    .sort((a, b) => b.renewalDate.localeCompare(a.renewalDate));

  const renewalInvoiceFor = (subscription: CustomerSubscription, period: { periodStart: string; periodEnd: string }) =>
    renewalInvoices.find((invoice) => invoice.entityId === subscription.customerSubscriptionId
      && invoice.renewalPeriodStart === period.periodStart
      && invoice.renewalPeriodEnd === period.periodEnd
      && invoice.status !== 'CANCELLED');

  const create = async (subscription: CustomerSubscription, period: { periodStart: string; periodEnd: string }) => {
    setActionError('');
    setNotice('');
    try {
      const invoice = await createInvoice.mutateAsync({ customerSubscriptionId: subscription.customerSubscriptionId, ...period });
      setNotice(`Renewal invoice ${invoice.invoiceNumber} is ready for operations review.`);
    } catch (error) {
      setActionError(error instanceof Error ? error.message : 'Renewal invoice could not be created.');
    }
  };

  const send = async (invoice: RenewalInvoice) => {
    setActionError('');
    setNotice('');
    try {
      await sendInvoice.mutateAsync(invoice.invoiceId);
      setNotice(`Renewal invoice ${invoice.invoiceNumber} was sent to the customer.`);
    } catch (error) {
      setActionError(error instanceof Error ? error.message : 'Renewal invoice could not be sent.');
    }
  };

  const finalize = async (subscription: CustomerSubscription, invoice: RenewalInvoice) => {
    const payment = (paymentsByInvoice.get(invoice.invoiceId) ?? []).find((candidate) => isVerifiedFullInvoicePayment(candidate, invoice));
    if (!payment || !invoice.renewalPeriodStart || !invoice.renewalPeriodEnd) return;
    setActionError('');
    setNotice('');
    try {
      const result = await renewSubscription.mutateAsync({
        customerSubscriptionId: subscription.customerSubscriptionId,
        paymentId: payment.paymentId,
        periodStart: invoice.renewalPeriodStart,
        periodEnd: invoice.renewalPeriodEnd,
        renewalType: 'MANUAL',
        renewalAmount: invoice.invoiceAmount + invoice.taxAmount,
        remarks: `Settled renewal invoice ${invoice.invoiceNumber}`,
      });
      setNotice(`Subscription renewal completed for ${result.periodStart} to ${result.periodEnd}.`);
    } catch (error) {
      setActionError(error instanceof Error ? error.message : 'Verified renewal could not be applied.');
    }
  };

  return <Stack spacing={3}>
    <SubscriptionHeading title="Renewal center" subtitle="Prepare a renewal invoice, collect verified payment, and continue the existing subscription term." />
    {data.profile.isError && <SubscriptionError error={data.profile.error} retry={() => void data.profile.refetch()} />}
    {data.subscriptions.isError && <SubscriptionError error={data.subscriptions.error} retry={() => void data.subscriptions.refetch()} />}
    {invoiceQuery.isError && <SubscriptionError error={invoiceQuery.error} retry={() => void invoiceQuery.refetch()} />}
    {histories.some((query) => query.isError) && <Alert severity="error">Some renewal history could not be loaded.</Alert>}
    {actionError && <Alert severity="error">{actionError}</Alert>}
    {notice && <Alert severity="success">{notice}</Alert>}
    <Alert severity="info">Renewal invoices use the current plan price and preserve the subscription term length. A renewal is applied only after the provider confirms payment settlement.</Alert>
    <Card variant="outlined"><CardContent>
      <Typography variant="h6" fontWeight={700}>Upcoming and expired subscriptions</Typography>
      {data.subscriptions.isLoading || invoiceQuery.isLoading
        ? Array.from({ length: 2 }, (_, index) => <Skeleton key={index} height={70} />)
        : eligible.length
          ? eligible.map((item) => {
            const period = renewalPeriod(item);
            const invoice = period ? renewalInvoiceFor(item, period) : undefined;
            const payments = invoice ? paymentsByInvoice.get(invoice.invoiceId) ?? [] : [];
            const verifiedPayment = invoice ? payments.find((payment) => isVerifiedFullInvoicePayment(payment, invoice)) : undefined;
            const unverifiedSuccess = payments.some((payment) => payment.paymentStatus === 'SUCCESS' && !isVerifiedSettlement(payment));
            const invalidVerifiedPayment = invoice ? payments.some((payment) => isVerifiedSettlement(payment) && !isVerifiedFullInvoicePayment(payment, invoice)) : false;
            const anyPayment = payments.length > 0;
            return <Stack key={item.customerSubscriptionId} direction={{ xs: 'column', md: 'row' }} justifyContent="space-between" alignItems={{ md: 'center' }} gap={1.5} sx={{ mt: 2, p: 2, border: 1, borderColor: 'divider', borderRadius: 1 }}>
              <Box>
                <Typography fontWeight={650}>{item.planName ?? 'Subscription'} · {item.customerName ?? 'Customer'}</Typography>
                <Typography variant="body2" color="text.secondary">Current term ends {item.endDate} · Renewal term {period?.periodStart ?? 'Unavailable'} to {period?.periodEnd ?? 'Unavailable'}</Typography>
                {invoice && <Typography variant="body2" sx={{ mt: 0.5 }}>{invoice.invoiceNumber} · {formatMoney(invoice.invoiceAmount + invoice.taxAmount, invoice.currencyCode)} · {invoice.status.replaceAll('_', ' ')}</Typography>}
              </Box>
              <Stack direction={{ xs: 'column', sm: 'row' }} spacing={1} alignItems={{ sm: 'center' }}>
                <Chip size="small" label={item.status} color={item.status === 'ACTIVE' ? 'success' : 'warning'} />
                {!period
                  ? <Typography variant="body2" color="error">Subscription dates are incomplete; operations must correct the record.</Typography>
                  : !invoice
                    ? <Button variant="contained" disabled={createInvoice.isPending} onClick={() => void create(item, period)}>Prepare renewal invoice</Button>
                    : invoice.status === 'DRAFT' && data.role.isAdmin
                      ? <Button variant="contained" disabled={sendInvoice.isPending} onClick={() => void send(invoice)}>Send invoice</Button>
                      : invoice.status === 'DRAFT'
                        ? <Chip size="small" label="Awaiting admin to send" />
                        : invoice.status === 'PAID' && verifiedPayment
                          ? <Button variant="contained" disabled={renewSubscription.isPending} onClick={() => void finalize(item, invoice)}>Apply verified renewal</Button>
                          : invoice.status === 'PAID' && (unverifiedSuccess || invalidVerifiedPayment)
                            ? <Chip size="small" label="Settlement requires reconciliation" color="warning" />
                            : invoice.status === 'PAID' && !anyPayment
                              ? <Chip size="small" label="Payment record unavailable" color="error" />
                            : invoice.status === 'SENT' || invoice.status === 'PARTIALLY_PAID' || invoice.status === 'OVERDUE'
                              ? data.role.isCustomer
                                ? <Button component={Link} to="/customer/invoices">Review invoice payment</Button>
                                : data.role.isAdmin
                                  ? <Button component={Link} to="/invoices">Review invoice</Button>
                                  : <Chip size="small" label="Admin review required" />
                              : <Chip size="small" label="Renewal invoice is not payable" />}
                <Button component={Link} to={`/subscriptions/${item.customerSubscriptionId}`}>Details</Button>
              </Stack>
            </Stack>;
          })
          : <Typography color="text.secondary" sx={{ mt: 1 }}>No subscriptions need renewal in the next 90 days.</Typography>}
    </CardContent></Card>
    <Card variant="outlined"><CardContent>
      <Typography variant="h6" fontWeight={700}>Renewal history</Typography>
      {histories.some((query) => query.isLoading)
        ? <Skeleton height={100} />
        : history.length
          ? history.map((item) => <Stack key={item.subscriptionRenewalId} direction={{ xs: 'column', sm: 'row' }} justifyContent="space-between" gap={1} sx={{ mt: 1.5 }}>
            <Typography>{item.renewalDate} · {item.renewalType}</Typography>
            <Typography>{item.periodStart} - {item.periodEnd} · <Chip size="small" label={item.status} /></Typography>
          </Stack>)
          : <Typography color="text.secondary" sx={{ mt: 1 }}>No renewals recorded.</Typography>}
    </CardContent></Card>
  </Stack>;
}
