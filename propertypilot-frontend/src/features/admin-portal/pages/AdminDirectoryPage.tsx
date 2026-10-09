import { useMemo, useState } from 'react';
import { Alert, Box, Button, Card, CardContent, Chip, CircularProgress, FormControl, InputLabel, MenuItem, Select, Skeleton, Snackbar, Stack, Table, TableBody, TableCell, TableContainer, TableHead, TablePagination, TableRow, TextField, Typography } from '@mui/material';
import SearchRounded from '@mui/icons-material/SearchRounded';
import RefreshRounded from '@mui/icons-material/RefreshRounded';
import { Link, useParams } from 'react-router-dom';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { apiClient } from '../../../api/client';
import { adminPortalKeys, useAdminDirectory } from '../hooks/useAdminPortal';
import { directoryLabels } from '../adminPortalApi';
import type { AdminResource } from '../types';

const supported: AdminResource[] = ['customers','properties','requests','agents','visits','evidence','quotes','deals','invoices','payments','subscriptions','services','pricing','plans','users'];
const label = (value: string) => value.replaceAll('_', ' ');
function isResource(value?: string): value is AdminResource { return Boolean(value && supported.includes(value as AdminResource)); }
function statusColor(status: string): 'success'|'warning'|'error'|'info'|'default' { return ['ACTIVE','COMPLETED','PAID','ACCEPTED','CLOSED'].includes(status) ? 'success' : ['PENDING','NEW','SCHEDULED','IN_PROGRESS'].includes(status) ? 'warning' : ['FAILED','CANCELLED','INACTIVE','REJECTED','OVERDUE'].includes(status) ? 'error' : status ? 'info' : 'default'; }
const formatDate = (date: string) => { if (!date) return '—'; const parsed = new Date(date); return Number.isNaN(parsed.valueOf()) ? date : new Intl.DateTimeFormat(undefined, { dateStyle: 'medium' }).format(parsed); };

export default function AdminDirectoryPage() {
  const { resource: resourceParam } = useParams<{ resource: string }>();
  const client = useQueryClient();
  const valid = isResource(resourceParam); const resource = valid ? resourceParam : 'customers';
  const query = useAdminDirectory(resource); const [search, setSearch] = useState(''); const [status, setStatus] = useState('ALL'); const [facet, setFacet] = useState('ALL');
  const [page, setPage] = useState(0); const [pageSize, setPageSize] = useState(10); const [message, setMessage] = useState('');
  const statusMutation = useMutation({
    mutationFn: ({ id, nextStatus }: { id: string; nextStatus: string }) => apiClient.patch(`/api/v1/admin/customers/${id}/status`, { status: nextStatus }),
    onSuccess: async () => { await client.invalidateQueries({ queryKey: adminPortalKeys.list('customers') }); setMessage('Customer status updated.'); },
  });
  const propertyArchiveMutation = useMutation({
    mutationFn: ({ id, restore }: { id: string; restore: boolean }) => apiClient.patch(`/api/v1/properties/${id}/${restore ? 'restore' : 'archive'}`),
    onSuccess: async (_saved, variables) => { await client.invalidateQueries({ queryKey: adminPortalKeys.list('properties') }); setMessage(variables.restore ? 'Property restored.' : 'Property archived.'); },
  });
  const rows = query.data ?? [];
  const statuses = useMemo(() => [...new Set(rows.map((entry) => entry.status).filter(Boolean))].sort(), [rows]);
  const facets = useMemo(() => [...new Set(rows.map((entry) => entry.type).filter(Boolean))].sort(), [rows]);
  const filtered = useMemo(() => rows.filter((entry) => (status === 'ALL' || entry.status === status) && (facet === 'ALL' || entry.type === facet)
    && `${entry.title} ${entry.primary} ${entry.secondary} ${entry.status} ${entry.type}`.toLowerCase().includes(search.trim().toLowerCase()))
    .sort((left, right) => right.date.localeCompare(left.date)), [rows, status, facet, search]);
  const pageRows = filtered.slice(page * pageSize, (page + 1) * pageSize);
  const heading = directoryLabels[resource];

  if (!valid) return <Alert severity="error">Unknown admin module.</Alert>;
  return <Stack spacing={3}>
    <Stack direction={{ xs: 'column', sm: 'row' }} alignItems={{ sm: 'center' }} justifyContent="space-between" gap={2}>
      <Box><Typography variant="h4" fontWeight={750}>{heading.label}</Typography><Typography color="text.secondary" sx={{ mt: 0.5 }}>{heading.description}</Typography></Box>
      <Button variant="outlined" startIcon={query.isFetching ? <CircularProgress size={16}/> : <RefreshRounded/>} onClick={() => void query.refetch()} disabled={query.isFetching}>Refresh</Button>
    </Stack>
    <Stack direction={{ xs: 'column', md: 'row' }} spacing={1.5}>
      <TextField size="small" fullWidth value={search} onChange={(event) => { setSearch(event.target.value); setPage(0); }} placeholder={`Search ${heading.label.toLowerCase()}`} inputProps={{ 'aria-label': `Search ${heading.label.toLowerCase()}` }} InputProps={{ startAdornment: <SearchRounded color="action" sx={{ mr: 1 }}/> }}/>
      <FormControl size="small" sx={{ minWidth: 175 }}><InputLabel id="directory-status">Status</InputLabel><Select labelId="directory-status" label="Status" value={status} onChange={(event) => { setStatus(event.target.value); setPage(0); }}><MenuItem value="ALL">All statuses</MenuItem>{statuses.map((item) => <MenuItem key={item} value={item}>{label(item)}</MenuItem>)}</Select></FormControl>
      <FormControl size="small" sx={{ minWidth: 175 }}><InputLabel id="directory-type">Type</InputLabel><Select labelId="directory-type" label="Type" value={facet} onChange={(event) => { setFacet(event.target.value); setPage(0); }}><MenuItem value="ALL">All types</MenuItem>{facets.map((item) => <MenuItem key={item} value={item}>{label(item)}</MenuItem>)}</Select></FormControl>
    </Stack>
    {query.isError && <Alert severity="error" action={<Button color="inherit" size="small" onClick={() => void query.refetch()}>Retry</Button>}>{query.error instanceof Error ? query.error.message : `Unable to load ${heading.label.toLowerCase()}.`}</Alert>}
    {statusMutation.isError && <Alert severity="error">{statusMutation.error instanceof Error ? statusMutation.error.message : 'Could not update customer status.'}</Alert>}
    {propertyArchiveMutation.isError && <Alert severity="error">{propertyArchiveMutation.error instanceof Error ? propertyArchiveMutation.error.message : 'Could not update property archive status.'}</Alert>}
    <Card variant="outlined"><CardContent sx={{ p: 0, '&:last-child': { pb: 0 } }}><TableContainer sx={{ overflowX: 'auto' }}><Table size="small" aria-label={`${heading.label} directory`}>
      <TableHead><TableRow>{[resource === 'customers' ? 'Customer' : 'Record','Details','Status','Type','Date','Amount',''].map((column) => <TableCell key={column} sx={{ fontWeight: 700, whiteSpace: 'nowrap' }}>{column}</TableCell>)}</TableRow></TableHead>
      <TableBody>
        {query.isLoading ? Array.from({ length: 5 }, (_, index) => <TableRow key={index}>{Array.from({ length: 7 }, (_, cell) => <TableCell key={cell}><Skeleton/></TableCell>)}</TableRow>) : pageRows.map((entry) => <TableRow hover key={entry.id}>
          <TableCell sx={{ minWidth: 210 }}><Typography fontWeight={700}>{entry.title}</Typography></TableCell>
          <TableCell sx={{ minWidth: 180 }}>{entry.secondary || '—'}</TableCell><TableCell><Chip size="small" label={label(entry.status) || 'Unknown'} color={statusColor(entry.status)}/></TableCell><TableCell>{label(entry.type) || '—'}</TableCell><TableCell sx={{ whiteSpace: 'nowrap' }}>{formatDate(entry.date)}</TableCell><TableCell sx={{ whiteSpace: 'nowrap' }}>{entry.amount || '—'}</TableCell>
          <TableCell><Stack direction="row" spacing={0.5}><Button size="small" component={Link} to={entry.detailPath}>View</Button>{resource === 'customers' && <><Button size="small" component={Link} to={`/staff/customers/${entry.id}/edit`}>Edit</Button><Button size="small" color={entry.status === 'ACTIVE' ? 'warning' : 'success'} disabled={statusMutation.isPending} onClick={() => statusMutation.mutate({ id: entry.id, nextStatus: entry.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE' })}>{entry.status === 'ACTIVE' ? 'Deactivate' : 'Activate'}</Button></>}{resource === 'properties' && <><Button size="small" component={Link} to={`/staff/properties/${entry.id}/edit`} disabled={entry.status === 'ARCHIVED'}>Edit</Button><Button size="small" color={entry.status === 'ARCHIVED' ? 'success' : 'warning'} disabled={propertyArchiveMutation.isPending} onClick={() => propertyArchiveMutation.mutate({ id: entry.id, restore: entry.status === 'ARCHIVED' })}>{entry.status === 'ARCHIVED' ? 'Restore' : 'Archive'}</Button></>}</Stack></TableCell>
        </TableRow>)}
        {!query.isLoading && !query.isError && pageRows.length === 0 && <TableRow><TableCell colSpan={7} align="center"><Box sx={{ py: 7 }}><Typography fontWeight={700}>{rows.length ? 'No matching records' : `No ${heading.label.toLowerCase()} found`}</Typography><Typography variant="body2" color="text.secondary" sx={{ mt: 0.5 }}>{rows.length ? 'Adjust your search or filters.' : 'Records will appear here when they are available from the service API.'}</Typography></Box></TableCell></TableRow>}
      </TableBody>
    </Table></TableContainer><TablePagination component="div" count={filtered.length} page={page} onPageChange={(_, nextPage) => setPage(nextPage)} rowsPerPage={pageSize} onRowsPerPageChange={(event) => { setPageSize(Number(event.target.value)); setPage(0); }} rowsPerPageOptions={[10,25,50]}/></CardContent></Card>
    <Snackbar open={Boolean(message)} autoHideDuration={3500} onClose={() => setMessage('')} message={message}/>
  </Stack>;
}
