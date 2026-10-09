import { useMemo, useState } from 'react';
import { Alert, Box, Button, Card, CardContent, Chip, FormControl, InputLabel, MenuItem, Select, Skeleton, Stack, Table, TableBody, TableCell, TableHead, TablePagination, TableRow, TableSortLabel, TextField, Typography } from '@mui/material';
import { Link } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { paymentsApi } from '../../../api/payments';
import { isVerifiedSettlement, paymentStatusLabel } from '../utils/settlement';

const money = (amount: number, currencyCode?: string | null) => new Intl.NumberFormat(undefined, { style: 'currency', currency: currencyCode || 'INR' }).format(amount);

export default function PaymentListPage() {
  const [search, setSearch] = useState('');
  const [status, setStatus] = useState('ALL');
  const [sortBy, setSortBy] = useState<'paymentDate' | 'amount' | 'paymentStatus'>('paymentDate');
  const [sortDirection, setSortDirection] = useState<'asc' | 'desc'>('desc');
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const query = useQuery({ queryKey: ['payments', 'list'], queryFn: paymentsApi.list });
  const payments = query.data ?? [];
  const filtered = useMemo(() => payments
    .filter((payment) => status === 'ALL' || (status === 'SUCCESS' ? isVerifiedSettlement(payment) : status === 'UNVERIFIED_SUCCESS' ? payment.paymentStatus === 'SUCCESS' && !isVerifiedSettlement(payment) : payment.paymentStatus === status))
    .filter((payment) =>
      `${payment.invoiceNumber ?? ''} ${payment.customerName ?? ''} ${payment.paymentMethod} ${payment.paymentStatus}`
        .toLowerCase().includes(search.trim().toLowerCase()),
    )
    .sort((left, right) => {
      const leftValue = sortBy === 'paymentDate' ? (left.settledAt ?? left.paymentDate ?? '') : sortBy === 'amount' ? left.amount : left.paymentStatus;
      const rightValue = sortBy === 'paymentDate' ? (right.settledAt ?? right.paymentDate ?? '') : sortBy === 'amount' ? right.amount : right.paymentStatus;
      const comparison = typeof leftValue === 'number' && typeof rightValue === 'number'
        ? leftValue - rightValue
        : String(leftValue).localeCompare(String(rightValue));
      return sortDirection === 'asc' ? comparison : -comparison;
    }), [payments, search, sortBy, sortDirection, status]);

  const toggleSort = (column: typeof sortBy) => {
    if (sortBy === column) setSortDirection((current) => current === 'asc' ? 'desc' : 'asc');
    else { setSortBy(column); setSortDirection('asc'); }
    setPage(0);
  };

  return <Stack spacing={3}>
    <Box><Typography variant="h4" fontWeight={750}>Payments</Typography><Typography color="text.secondary" sx={{ mt: 0.5 }}>Payment attempts and verified settlements recorded against invoices.</Typography></Box>
    <Stack direction={{ xs: 'column', sm: 'row' }} spacing={1.5}>
      <TextField label="Search by invoice or customer" value={search} onChange={(event) => { setSearch(event.target.value); setPage(0); }} size="small" fullWidth />
      <FormControl size="small" sx={{ minWidth: { xs: '100%', sm: 200 } }}>
        <InputLabel id="payment-status-filter-label">Payment status</InputLabel>
        <Select labelId="payment-status-filter-label" value={status} label="Payment status" onChange={(event) => { setStatus(event.target.value); setPage(0); }}>
          <MenuItem value="ALL">All statuses</MenuItem>
          <MenuItem value="INITIATED">Initiated</MenuItem>
          <MenuItem value="PENDING">Pending</MenuItem>
          <MenuItem value="SUCCESS">Verified settlement</MenuItem>
          <MenuItem value="UNVERIFIED_SUCCESS">Unverified success</MenuItem>
          <MenuItem value="FAILED">Failed</MenuItem>
          <MenuItem value="REFUNDED">Refunded</MenuItem>
          <MenuItem value="CANCELLED">Cancelled</MenuItem>
        </Select>
      </FormControl>
    </Stack>
    {query.isError && <Alert severity="error" action={<Button color="inherit" onClick={() => void query.refetch()}>Retry</Button>}>{query.error instanceof Error ? query.error.message : 'Payment records could not be loaded.'}</Alert>}
    <Card variant="outlined"><CardContent sx={{ p: 0, '&:last-child': { pb: 0 } }}><Box sx={{ overflowX: 'auto' }}><Table size="small" aria-label="Payments">
      <TableHead><TableRow>
        <TableCell sx={{ fontWeight: 700 }}>Invoice</TableCell><TableCell sx={{ fontWeight: 700 }}>Customer</TableCell>
        <TableCell sx={{ fontWeight: 700 }}><TableSortLabel active={sortBy === 'amount'} direction={sortBy === 'amount' ? sortDirection : 'asc'} onClick={() => toggleSort('amount')}>Amount</TableSortLabel></TableCell>
        <TableCell sx={{ fontWeight: 700 }}>Method</TableCell>
        <TableCell sx={{ fontWeight: 700 }}><TableSortLabel active={sortBy === 'paymentStatus'} direction={sortBy === 'paymentStatus' ? sortDirection : 'asc'} onClick={() => toggleSort('paymentStatus')}>Status</TableSortLabel></TableCell>
        <TableCell sx={{ fontWeight: 700 }}><TableSortLabel active={sortBy === 'paymentDate'} direction={sortBy === 'paymentDate' ? sortDirection : 'asc'} onClick={() => toggleSort('paymentDate')}>Payment / settlement date</TableSortLabel></TableCell><TableCell />
      </TableRow></TableHead>
      <TableBody>
        {query.isLoading && Array.from({ length: 5 }, (_, index) => <TableRow key={index}>{Array.from({ length: 7 }, (_, cell) => <TableCell key={cell}><Skeleton /></TableCell>)}</TableRow>)}
        {!query.isLoading && filtered.slice(page * pageSize, (page + 1) * pageSize).map((payment) => <TableRow key={payment.paymentId} hover>
          <TableCell>{payment.invoiceNumber || 'Invoice'}</TableCell><TableCell>{payment.customerName || 'Customer'}</TableCell><TableCell>{money(payment.amount,payment.currencyCode)}</TableCell><TableCell>{payment.paymentMethod.replaceAll('_', ' ')}</TableCell><TableCell><Chip size="small" label={paymentStatusLabel(payment)} color={isVerifiedSettlement(payment) ? 'success' : payment.paymentStatus === 'FAILED' ? 'error' : 'warning'} /></TableCell><TableCell>{payment.settledAt ? new Date(payment.settledAt).toLocaleString() : payment.paymentDate ? new Date(payment.paymentDate).toLocaleString() : '—'}</TableCell><TableCell><Button component={Link} to={`/staff/payments/${payment.paymentId}`} size="small">View</Button></TableCell>
        </TableRow>)}
        {!query.isLoading && !query.isError && filtered.length === 0 && <TableRow><TableCell colSpan={7} align="center"><Box sx={{ py: 6 }}><Typography fontWeight={700}>{payments.length ? 'No matching payments' : 'No payments recorded'}</Typography><Typography variant="body2" color="text.secondary" sx={{ mt: 0.5 }}>Payments appear after they are recorded against an invoice.</Typography></Box></TableCell></TableRow>}
      </TableBody>
    </Table></Box><TablePagination component="div" count={filtered.length} page={page} onPageChange={(_, nextPage) => setPage(nextPage)} rowsPerPage={pageSize} onRowsPerPageChange={(event) => { setPageSize(Number(event.target.value)); setPage(0); }} rowsPerPageOptions={[10, 25, 50]} /></CardContent></Card>
  </Stack>;
}
