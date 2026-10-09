import { useEffect, useMemo, useState } from 'react';
import axios from 'axios';
import FilterAltOffIcon from '@mui/icons-material/FilterAltOff';
import CloseRoundedIcon from '@mui/icons-material/CloseRounded';
import SearchIcon from '@mui/icons-material/Search';
import { Alert, Box, Button, Card, CardContent, Chip, FormControl, IconButton, InputAdornment, LinearProgress, MenuItem, Select, Skeleton, Stack, Table, TableBody, TableCell, TableContainer, TableHead, TablePagination, TableRow, TableSortLabel, TextField, Typography } from '@mui/material';
import { Link } from 'react-router-dom';
import LeadStatusChip from '../components/LeadStatusChip';
import { useOperationsLeads } from '../hooks/useLeads';
import type { LeadSortDirection, LeadStatus, OperationsLeadSearchParams, OperationsLeadSortField, OperationsLeadSummary } from '../types/lead';

const statuses: LeadStatus[] = ['NEW', 'CONTACTED', 'QUALIFIED', 'READY_FOR_DEAL', 'DEAL_CONVERTED', 'CONVERTED', 'LOST', 'CLOSED'];
const sortableFields: Array<{ field: OperationsLeadSortField; label: string }> = [
  { field: 'propertyType', label: 'Property type' },
  { field: 'location', label: 'Location' },
  { field: 'source', label: 'Source' },
  { field: 'status', label: 'Status' },
  { field: 'createdAt', label: 'Created date' },
];
const defaultPageSize = 25;
const pageSizeOptions = [10, 25, 50, 100];

export default function OperationsLeadQueuePage() {
  const [searchInput, setSearchInput] = useState('');
  const [debouncedSearch, setDebouncedSearch] = useState('');
  const [status, setStatus] = useState<LeadStatus | ''>('');
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(defaultPageSize);
  const [sort, setSort] = useState<OperationsLeadSortField>('createdAt');
  const [direction, setDirection] = useState<LeadSortDirection>('DESC');

  const normalizedInput = searchInput.trim();
  useEffect(() => {
    const timeout = window.setTimeout(() => setDebouncedSearch(normalizedInput), 300);
    return () => window.clearTimeout(timeout);
  }, [normalizedInput]);

  const params = useMemo<OperationsLeadSearchParams>(() => ({
    ...(debouncedSearch ? { search: debouncedSearch } : {}),
    ...(status ? { status } : {}),
    page,
    size,
    sort,
    direction,
  }), [debouncedSearch, status, page, size, sort, direction]);
  const query = useOperationsLeads(params);
  const searchPending = normalizedInput !== debouncedSearch;
  const rows = query.data?.content ?? [];
  const hasFilters = Boolean(normalizedInput || status);
  const nothingInQueue = !hasFilters && !query.isError && query.data?.totalElements === 0;

  const clearFilters = () => {
    setSearchInput('');
    setDebouncedSearch('');
    setStatus('');
    setPage(0);
    setSize(defaultPageSize);
    setSort('createdAt');
    setDirection('DESC');
  };

  const handleSort = (field: OperationsLeadSortField) => {
    if (sort === field) setDirection((current) => current === 'ASC' ? 'DESC' : 'ASC');
    else {
      setSort(field);
      setDirection(field === 'createdAt' ? 'DESC' : 'ASC');
    }
    setPage(0);
  };

  return <Stack spacing={3}>
    <Stack direction={{ xs: 'column', sm: 'row' }} alignItems={{ sm: 'center' }} justifyContent="space-between" gap={1.5}>
      <Stack spacing={0.5}>
        <Typography variant="h4" component="h1" fontWeight={750}>Lead queue</Typography>
        <Typography color="text.secondary">Review customer intent, qualification and follow-up across the operations team.</Typography>
      </Stack>
      {query.data && !query.isError && <Chip label={`${query.data.totalElements.toLocaleString()} ${query.data.totalElements === 1 ? 'lead' : 'leads'}`} variant="outlined" aria-label="Total leads" />}
    </Stack>

    <Card variant="outlined"><CardContent>
      <Stack spacing={2}>
        <Stack direction={{ xs: 'column', md: 'row' }} spacing={1.5}>
          <TextField
            fullWidth
            label="Search leads"
            value={searchInput}
            onChange={(event) => { setSearchInput(event.target.value); setPage(0); }}
            placeholder="Customer, contact, source, property type or location"
            inputProps={{ maxLength: 200 }}
            InputProps={{
              startAdornment: <InputAdornment position="start"><SearchIcon fontSize="small" /></InputAdornment>,
              endAdornment: searchInput ? <InputAdornment position="end"><IconButton aria-label="Clear lead search" onClick={() => { setSearchInput(''); setDebouncedSearch(''); setPage(0); }} edge="end" size="small"><CloseRoundedIcon fontSize="small" /></IconButton></InputAdornment> : undefined,
            }}
          />
          <FormControl sx={{ minWidth: { md: 220 } }}>
            <Select
              value={status}
              displayEmpty
              inputProps={{ 'aria-label': 'Lead status filter' }}
              onChange={(event) => { setStatus(event.target.value as LeadStatus | ''); setPage(0); }}
              renderValue={(value) => value ? value.replaceAll('_', ' ') : 'All lead statuses'}
            >
              <MenuItem value="">All lead statuses</MenuItem>
              {statuses.map((item) => <MenuItem key={item} value={item}>{item.replaceAll('_', ' ')}</MenuItem>)}
            </Select>
          </FormControl>
          <Button variant="outlined" startIcon={<FilterAltOffIcon />} onClick={clearFilters} disabled={!searchInput && !status && size === defaultPageSize && sort === 'createdAt' && direction === 'DESC'}>
            Clear filters
          </Button>
        </Stack>
        <Typography variant="caption" color="text.secondary">Search includes customer name and contact details, source, property type, and location. Sort by created date, status, source, property type, or location.</Typography>
      </Stack>
    </CardContent></Card>

    {(query.isFetching || searchPending) && <Box aria-live="polite"><LinearProgress /><Typography variant="caption" color="text.secondary">{searchPending ? 'Applying search…' : 'Updating leads…'}</Typography></Box>}

    {query.isError && <QueueErrorState error={query.error} retry={() => void query.refetch()} />}

    {!query.isError && (query.isLoading || searchPending) && <LeadQueueLoadingState />}

    {!query.isError && !query.isLoading && !searchPending && query.data && rows.length > 0 && <>
      <Box sx={{ display: { xs: 'block', lg: 'none' } }}>
        <Stack spacing={1.5}>
          {rows.map((lead) => <LeadQueueCard key={lead.leadId} lead={lead} />)}
        </Stack>
      </Box>
      <TableContainer component={Card} variant="outlined" sx={{ display: { xs: 'none', lg: 'block' }, overflowX: 'auto' }}>
        <Table aria-label="Operations lead queue" size="medium">
          <caption style={{ textAlign: 'left', padding: 12 }}>Customer leads with current status, source, assignment and created date.</caption>
          <TableHead><TableRow>
            <TableCell>Customer</TableCell>
            {sortableFields.map(({ field, label }) => <TableCell key={field}><SortableHeader field={field} label={label} activeField={sort} direction={direction} onSort={handleSort} /></TableCell>)}
            <TableCell>Assigned agent</TableCell>
            <TableCell align="right">Action</TableCell>
          </TableRow></TableHead>
          <TableBody>{rows.map((lead) => <TableRow key={lead.leadId} hover>
            <TableCell><CustomerContext lead={lead} /></TableCell>
            <TableCell>{lead.propertyType || 'Not provided'}</TableCell>
            <TableCell>{lead.location || 'Not provided'}</TableCell>
            <TableCell>{lead.source || 'Not provided'}</TableCell>
            <TableCell>{lead.status ? <LeadStatusChip status={lead.status} /> : 'Not provided'}</TableCell>
            <TableCell>{formatDate(lead.createdAt)}</TableCell>
            <TableCell>{lead.assignedAgentCode ? `Agent ${lead.assignedAgentCode}` : 'Unassigned'}</TableCell>
            <TableCell align="right"><Button component={Link} to={`/operations/leads/${lead.leadId}`} size="small">Open lead</Button></TableCell>
          </TableRow>)}</TableBody>
        </Table>
      </TableContainer>
      <TablePagination
        component="div"
        count={query.data.totalElements}
        page={query.data.page}
        rowsPerPage={query.data.size}
        rowsPerPageOptions={pageSizeOptions}
        onPageChange={(_, nextPage) => setPage(Math.max(0, nextPage))}
        onRowsPerPageChange={(event) => { const nextSize = Number(event.target.value); if (pageSizeOptions.includes(nextSize)) { setSize(nextSize); setPage(0); } }}
        labelRowsPerPage="Leads per page"
        labelDisplayedRows={({ from, to, count }) => `${from}–${to} of ${count !== -1 ? count : `more than ${to}`}`}
        getItemAriaLabel={(type) => `Go to ${type} page`}
        sx={{ overflowX: 'auto' }}
      />
    </>}

    {!query.isError && !query.isLoading && !searchPending && query.data?.content.length === 0 && <Alert severity="info">{nothingInQueue ? 'No leads have been added yet.' : 'No leads match the current search or status filter.'}</Alert>}
  </Stack>;
}

function LeadQueueCard({ lead }: { lead: OperationsLeadSummary }) {
  return <Card variant="outlined">
    <CardContent><Stack spacing={1.5}>
      <Stack direction="row" justifyContent="space-between" alignItems="flex-start" gap={1}>
        <Stack spacing={0.25} minWidth={0}><Typography fontWeight={700} sx={{ overflowWrap: 'anywhere' }}>{lead.customerName || 'Customer details unavailable'}</Typography><Typography variant="body2" color="text.secondary" sx={{ overflowWrap: 'anywhere' }}>{lead.customerEmail || lead.customerPhone || 'Contact details unavailable'}</Typography></Stack>
        {lead.status ? <LeadStatusChip status={lead.status} /> : <Chip size="small" label="Status unavailable" />}
      </Stack>
      <Stack spacing={0.25}>
        <Typography variant="body2">{lead.propertyType || 'Property type not provided'}</Typography>
        <Typography variant="body2" color="text.secondary">{lead.location || 'Location not provided'}</Typography>
      </Stack>
      <Stack direction="row" flexWrap="wrap" gap={1}>
        <Chip size="small" variant="outlined" label={lead.source || 'Source not provided'} />
        <Chip size="small" variant="outlined" label={lead.assignedAgentCode ? `Agent ${lead.assignedAgentCode}` : 'Unassigned'} />
        <Chip size="small" variant="outlined" label={`Created ${formatDate(lead.createdAt)}`} />
      </Stack>
      <Button component={Link} to={`/operations/leads/${lead.leadId}`} size="small" sx={{ alignSelf: 'flex-start' }}>Open lead</Button>
    </Stack></CardContent>
  </Card>;
}

function CustomerContext({ lead }: { lead: OperationsLeadSummary }) {
  return <Stack spacing={0.25} minWidth={140}>
    <Typography fontWeight={650}>{lead.customerName || 'Customer details unavailable'}</Typography>
    {lead.customerEmail && <Typography variant="body2" color="text.secondary" sx={{ overflowWrap: 'anywhere' }}>{lead.customerEmail}</Typography>}
    {lead.customerPhone && <Typography variant="body2" color="text.secondary">{lead.customerPhone}</Typography>}
    {!lead.customerEmail && !lead.customerPhone && <Typography variant="body2" color="text.secondary">No contact details</Typography>}
  </Stack>;
}

function SortableHeader({ field, label, activeField, direction, onSort }: { field: OperationsLeadSortField; label: string; activeField: OperationsLeadSortField; direction: LeadSortDirection; onSort(field: OperationsLeadSortField): void }) {
  return <TableSortLabel active={activeField === field} direction={activeField === field ? direction.toLowerCase() as 'asc' | 'desc' : 'asc'} onClick={() => onSort(field)}>{label}</TableSortLabel>;
}

function LeadQueueLoadingState() {
  return <Stack spacing={1.5} aria-label="Loading lead queue" role="status">{[1, 2, 3].map((item) => <Skeleton key={item} variant="rounded" height={96} />)}</Stack>;
}

function QueueErrorState({ error, retry }: { error: unknown; retry(): void }) {
  const status = axios.isAxiosError(error) ? error.response?.status : undefined;
  const message = status === 401
    ? 'Your session has expired. Sign in again to access the lead queue.'
    : status === 403
      ? 'Your account does not have permission to access the operations lead queue.'
      : status === 400
        ? 'The search or page settings were not accepted. Clear the filters and try again.'
        : axios.isAxiosError(error) && !error.response
          ? 'Could not connect to PropertyPilot. Check your connection and retry.'
          : 'The lead queue could not be loaded. Retry to try again.';
  return <Alert severity="error" action={<Button color="inherit" onClick={retry}>Retry</Button>}>{message}</Alert>;
}

function formatDate(value: string | null) {
  if (!value) return 'Date unavailable';
  const date = new Date(value);
  return Number.isNaN(date.getTime()) ? 'Date unavailable' : new Intl.DateTimeFormat(undefined, { dateStyle: 'medium' }).format(date);
}
