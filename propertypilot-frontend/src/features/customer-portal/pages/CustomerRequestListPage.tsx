import { useMemo, useState } from 'react';
import { Alert, Box, Button, Card, CardActionArea, CardContent, MenuItem, Skeleton, Stack, TablePagination, TextField, Typography } from '@mui/material';
import SearchRounded from '@mui/icons-material/SearchRounded';
import { Link } from 'react-router-dom';
import { CustomerPageHeading, CustomerQueryError } from '../components/CustomerStates';
import CustomerStatusChip, { formatCustomerStatus } from '../components/CustomerStatusChip';
import { useCustomerRequests } from '../hooks/useCustomerPortal';

const PAGE_SIZE_OPTIONS = [5, 10, 25];

export default function CustomerRequestListPage() {
  const query = useCustomerRequests();
  const requests = query.data ?? [];
  const [search, setSearch] = useState('');
  const [status, setStatus] = useState('ALL');
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(PAGE_SIZE_OPTIONS[0]);
  const statuses = [...new Set(requests.map((request) => request.status))].sort();
  const filtered = useMemo(() => requests
    .filter((request) => status === 'ALL' || request.status === status)
    .filter((request) => `${request.serviceName ?? ''} ${request.requestType} ${request.propertyTitle} ${request.status}`.toLowerCase().includes(search.trim().toLowerCase()))
    .sort((a, b) => Date.parse(b.requestedAt) - Date.parse(a.requestedAt)), [requests, search, status]);
  const pageItems = filtered.slice(page * pageSize, (page + 1) * pageSize);

  return <Stack spacing={3}>
    <CustomerPageHeading title="My service requests" subtitle="Track each service request, its progress, visits, and shared evidence." action={<Button component={Link} to="/customer/service-requests/new" variant="contained">Request a service</Button>} />
    {query.isError && <CustomerQueryError error={query.error} retry={() => void query.refetch()} />}

    <Stack direction={{ xs: 'column', sm: 'row' }} spacing={1.5}>
      <TextField fullWidth size="small" label="Search requests" value={search} onChange={(event) => { setSearch(event.target.value); setPage(0); }} placeholder="Service or property" InputProps={{ startAdornment: <SearchRounded color="action" sx={{ mr: 1 }} aria-hidden="true" /> }} />
      <TextField select size="small" label="Request status" value={status} onChange={(event) => { setStatus(event.target.value); setPage(0); }} sx={{ minWidth: { sm: 190 } }}>
        <MenuItem value="ALL">All statuses</MenuItem>
        {statuses.map((value) => <MenuItem key={value} value={value}>{formatCustomerStatus(value)}</MenuItem>)}
      </TextField>
    </Stack>

    {query.isLoading ? <Stack spacing={1.5} aria-label="Loading service requests">{[0, 1, 2].map((item) => <Skeleton key={item} variant="rounded" height={118} />)}</Stack>
      : filtered.length > 0 ? <>
        <Stack spacing={1.5}>
          {pageItems.map((request) => <Card key={request.serviceRequestId} variant="outlined">
            <CardActionArea component={Link} to={`/customer/service-requests/${request.serviceRequestId}`} aria-label={`View ${request.serviceName || request.requestType} for ${request.propertyTitle}`}>
              <CardContent>
                <Stack direction={{ xs: 'column', sm: 'row' }} justifyContent="space-between" alignItems={{ sm: 'center' }} gap={1.5}>
                  <Box sx={{ minWidth: 0 }}>
                    <Typography variant="h6" fontWeight={700} sx={{ overflowWrap: 'anywhere' }}>{request.serviceName || request.requestType}</Typography>
                    <Typography color="text.secondary" sx={{ mt: .25 }}>{request.propertyTitle}</Typography>
                    <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>Requested {dateLabel(request.requestedAt)} · Priority {formatCustomerStatus(request.priority)}</Typography>
                  </Box>
                  <Stack direction="row" alignItems="center" spacing={1}>
                    <CustomerStatusChip status={request.status} />
                    <Typography variant="body2" color="primary.main" fontWeight={650}>View details</Typography>
                  </Stack>
                </Stack>
              </CardContent>
            </CardActionArea>
          </Card>)}
        </Stack>
        <TablePagination component="div" count={filtered.length} page={page} onPageChange={(_, next) => setPage(next)} rowsPerPage={pageSize} onRowsPerPageChange={(event) => { setPageSize(Number(event.target.value)); setPage(0); }} rowsPerPageOptions={PAGE_SIZE_OPTIONS} labelRowsPerPage="Requests per page" />
      </> : !query.isError && (requests.length > 0 ? <Alert severity="info" action={<Button onClick={() => { setSearch(''); setStatus('ALL'); }}>Clear filters</Button>}>No service requests match your search.</Alert>
        : <Alert severity="info" action={<Button component={Link} to="/customer/service-requests/new">Request a service</Button>}>You have no service requests yet.</Alert>)}
  </Stack>;
}

function dateLabel(value: string): string {
  const date = new Date(value);
  return Number.isNaN(date.getTime()) ? 'Date unavailable' : date.toLocaleDateString();
}
