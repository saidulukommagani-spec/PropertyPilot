import { useMemo, useState } from 'react';
import { Alert, Button, Card, CardActionArea, CardContent, Chip, MenuItem, Pagination, Skeleton, Stack, TextField, Typography } from '@mui/material';
import SearchRounded from '@mui/icons-material/SearchRounded';
import { Link } from 'react-router-dom';
import { useCustomerVisits } from '../hooks/useCustomerPortal';
import { CustomerPageHeading, CustomerQueryError } from '../components/CustomerStates';

const PAGE_SIZE = 10;
const VISIT_STATUSES = ['SCHEDULED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED', 'FAILED'] as const;

export default function CustomerVisitsPage() {
  const query = useCustomerVisits();
  const [filter, setFilter] = useState('ALL');
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(1);
  const visits = query.data ?? [];

  const filtered = useMemo(() => visits.filter((visit) =>
    (filter === 'ALL' || visit.status === filter)
    && `${visit.serviceName ?? ''} ${visit.propertyName ?? ''} ${visit.agentName ?? ''}`
      .toLowerCase().includes(search.trim().toLowerCase()),
  ), [visits, filter, search]);
  const pageCount = Math.max(1, Math.ceil(filtered.length / PAGE_SIZE));
  const rows = filtered.slice((page - 1) * PAGE_SIZE, page * PAGE_SIZE);

  return <Stack spacing={3}>
    <CustomerPageHeading title="My visits" subtitle="See upcoming appointments, completed work, and schedule changes." />
    {query.isError && <CustomerQueryError error={query.error} retry={() => void query.refetch()} />}
    <Stack direction={{ xs: 'column', sm: 'row' }} spacing={1.5}>
      <TextField size="small" label="Search visits" value={search} onChange={(event) => { setSearch(event.target.value); setPage(1); }} InputProps={{ startAdornment: <SearchRounded color="action" sx={{ mr: 1 }} /> }} />
      <TextField select size="small" label="Status" value={filter} onChange={(event) => { setFilter(event.target.value); setPage(1); }} sx={{ minWidth: 180 }}>
        <MenuItem value="ALL">All visits</MenuItem>
        {VISIT_STATUSES.map((status) => <MenuItem key={status} value={status}>{status.replaceAll('_', ' ')}</MenuItem>)}
      </TextField>
    </Stack>
    {query.isLoading ? Array.from({ length: 3 }, (_, index) => <Skeleton key={index} variant="rounded" height={110} />)
      : rows.length ? rows.map((visit) => <Card key={visit.visitId} variant="outlined">
        <CardActionArea component={Link} to={`/customer/visits/${visit.visitId}`}>
          <CardContent>
            <Stack direction={{ xs: 'column', sm: 'row' }} justifyContent="space-between" gap={1}>
              <Stack>
                <Typography variant="h6" fontWeight={700}>{visit.serviceName || 'Service visit'}</Typography>
                <Typography color="text.secondary">{visit.propertyName || 'Property'} · {new Date(visit.scheduledAt).toLocaleString()}</Typography>
                <Typography variant="body2" color="text.secondary">Agent: {visit.agentName || 'To be confirmed'}{visit.rescheduleReason ? ` · Rescheduled: ${visit.rescheduleReason}` : ''}</Typography>
              </Stack>
              <Chip label={visit.status.replaceAll('_', ' ')} color={visit.status === 'COMPLETED' ? 'success' : 'info'} />
            </Stack>
            <Button component="span" size="small" sx={{ mt: 1 }}>View visit</Button>
          </CardContent>
        </CardActionArea>
      </Card>) : !query.isError && <Alert severity="info">{visits.length ? 'No visits match your search or status filter.' : 'No visits have been scheduled for your service requests yet.'}</Alert>}
    {filtered.length > PAGE_SIZE && <Pagination count={pageCount} page={page} onChange={(_, nextPage) => setPage(nextPage)} color="primary" aria-label="Visit pages" />}
  </Stack>;
}
