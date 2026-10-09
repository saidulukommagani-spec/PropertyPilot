import { useMemo, useState } from 'react';
import { Alert, Box, Button, Card, CardContent, Chip, FormControl, InputLabel, MenuItem, Select, Skeleton, Stack, Table, TableBody, TableCell, TableContainer, TableHead, TablePagination, TableRow, TextField, Typography } from '@mui/material';
import SearchRounded from '@mui/icons-material/SearchRounded';
import { Link, useNavigate } from 'react-router-dom';
import { useEvidenceList } from '../hooks/useEvidence';

const dateLabel = (value?: string) => value ? new Intl.DateTimeFormat(undefined, { dateStyle: 'medium', timeStyle: 'short' }).format(new Date(value)) : '—';
const statusColor = (status?: string): 'success' | 'warning' | 'error' | 'default' => status === 'APPROVED' ? 'success' : status === 'REJECTED' ? 'error' : status === 'PENDING' ? 'warning' : 'default';

export default function EvidenceListPage() {
  const navigate = useNavigate();
  const query = useEvidenceList();
  const records = query.data ?? [];
  const [search, setSearch] = useState('');
  const [type, setType] = useState('ALL');
  const [status, setStatus] = useState('ALL');
  const [from, setFrom] = useState('');
  const [to, setTo] = useState('');
  const [newest, setNewest] = useState(true);
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const types = [...new Set(records.map((entry) => entry.evidenceTypeName))].sort();
  const statuses = [...new Set(records.map((entry) => entry.validationStatus ?? 'PENDING'))].sort();
  const filtered = useMemo(() => records.filter((item) => {
    const timestamp = item.capturedAt ?? item.createdAt ?? '';
    const at = new Date(timestamp || 0).getTime();
    const content = `${item.fileName ?? ''} ${item.serviceName ?? ''} ${item.propertyName ?? ''} ${item.evidenceTypeName} ${item.uploadedByName ?? ''} ${item.customerName ?? ''}`.toLowerCase();
    return (type === 'ALL' || item.evidenceTypeName === type) &&
      (status === 'ALL' || (item.validationStatus ?? 'PENDING') === status) &&
      (!from || at >= new Date(`${from}T00:00:00`).getTime()) &&
      (!to || at <= new Date(`${to}T23:59:59`).getTime()) &&
      content.includes(search.trim().toLowerCase());
  }).sort((left, right) => (newest ? -1 : 1) * (left.capturedAt ?? left.createdAt ?? '').localeCompare(right.capturedAt ?? right.createdAt ?? '')),
  [records, type, status, from, to, search, newest]);
  const rows = filtered.slice(page * size, page * size + size);

  return <Stack spacing={3}>
    <Stack direction={{ xs: 'column', sm: 'row' }} justifyContent="space-between" alignItems={{ sm: 'center' }} spacing={2}>
      <Box><Typography variant="h4" fontWeight={750}>Evidence</Typography><Typography color="text.secondary" sx={{ mt: .5 }}>Browse uploaded field evidence and its review state.</Typography></Box>
      <Stack direction="row" spacing={1}><Button component={Link} to="/evidence/dashboard" variant="outlined">Dashboard</Button><Button component={Link} to="/evidence/validation" variant="outlined">Validation queue</Button><Button component={Link} to="/evidence/upload" variant="contained">Upload evidence</Button></Stack>
    </Stack>
    {query.isError && <Alert severity="error" action={<Button color="inherit" size="small" onClick={() => void query.refetch()}>Retry</Button>}>{query.error instanceof Error ? query.error.message : 'Evidence records could not be loaded.'}</Alert>}
    <Stack direction={{ xs: 'column', md: 'row' }} flexWrap="wrap" spacing={1.5} useFlexGap>
      <TextField size="small" placeholder="Search evidence, request, visit, property" value={search} onChange={(event) => { setSearch(event.target.value); setPage(0); }} sx={{ minWidth: 260, flex: 1 }} InputProps={{ startAdornment: <SearchRounded color="action" sx={{ mr: 1 }} /> }} />
      <FormControl size="small" sx={{ minWidth: 190 }}><InputLabel>Evidence type</InputLabel><Select label="Evidence type" value={type} onChange={(event) => { setType(event.target.value); setPage(0); }}><MenuItem value="ALL">All types</MenuItem>{types.map((value) => <MenuItem key={value} value={value}>{value}</MenuItem>)}</Select></FormControl>
      <FormControl size="small" sx={{ minWidth: 180 }}><InputLabel>Validation status</InputLabel><Select label="Validation status" value={status} onChange={(event) => { setStatus(event.target.value); setPage(0); }}><MenuItem value="ALL">All statuses</MenuItem>{statuses.map((value) => <MenuItem key={value} value={value}>{value.replaceAll('_', ' ')}</MenuItem>)}</Select></FormControl>
      <TextField size="small" label="Uploaded from" type="date" value={from} onChange={(event) => { setFrom(event.target.value); setPage(0); }} InputLabelProps={{ shrink: true }} />
      <TextField size="small" label="Uploaded to" type="date" value={to} onChange={(event) => { setTo(event.target.value); setPage(0); }} InputLabelProps={{ shrink: true }} />
      <Button onClick={() => setNewest((value) => !value)}>Date: {newest ? 'Newest first' : 'Oldest first'}</Button>
    </Stack>
    <Card variant="outlined"><CardContent sx={{ p: 0, '&:last-child': { pb: 0 } }}><TableContainer sx={{ overflowX: 'auto' }}><Table size="small" aria-label="Evidence records">
      <TableHead><TableRow>{['Evidence', 'Service Request', 'Visit', 'Property', 'Evidence Type', 'Uploaded By', 'Uploaded Date', 'Validation Status', 'Actions'].map((label) => <TableCell key={label} sx={{ fontWeight: 700, whiteSpace: 'nowrap' }}>{label}</TableCell>)}</TableRow></TableHead>
      <TableBody>
        {query.isLoading ? Array.from({ length: 6 }, (_, row) => <TableRow key={row}>{Array.from({ length: 9 }, (_, cell) => <TableCell key={cell}><Skeleton /></TableCell>)}</TableRow>) : rows.map((item) => <TableRow hover key={item.evidenceId}>
          <TableCell><Button size="small" onClick={() => navigate(`/evidence/${item.evidenceId}`)}>{item.fileName || dateLabel(item.capturedAt ?? item.createdAt)}</Button></TableCell>
          <TableCell><Button size="small" component={Link} to={`/service-requests/${item.serviceRequestId}`}>Open request</Button></TableCell>
          <TableCell>{item.visitId ? <Button size="small" component={Link} to={`/visits/${item.visitId}`}>Open visit</Button> : '—'}</TableCell>
          <TableCell>{item.propertyName ?? '—'}</TableCell><TableCell>{item.evidenceTypeName}</TableCell><TableCell>{item.uploadedByName ?? '—'}</TableCell>
          <TableCell sx={{ whiteSpace: 'nowrap' }}>{dateLabel(item.capturedAt ?? item.createdAt)}</TableCell>
          <TableCell><Chip size="small" color={statusColor(item.validationStatus ?? 'PENDING')} label={(item.validationStatus ?? 'PENDING').replaceAll('_', ' ')} /></TableCell>
          <TableCell><Button size="small" onClick={() => navigate(`/evidence/${item.evidenceId}`)}>View</Button></TableCell>
        </TableRow>)}
        {!query.isLoading && !query.isError && !rows.length && <TableRow><TableCell colSpan={9} align="center"><Box sx={{ py: 7 }}><Typography fontWeight={700}>{records.length ? 'No matching evidence' : 'No evidence uploaded yet'}</Typography><Typography variant="body2" color="text.secondary" sx={{ mt: .5 }}>{records.length ? 'Change the filters or search.' : 'Evidence uploaded from field visits appears here.'}</Typography></Box></TableCell></TableRow>}
      </TableBody>
    </Table></TableContainer><TablePagination component="div" count={filtered.length} page={page} onPageChange={(_, value) => setPage(value)} rowsPerPage={size} onRowsPerPageChange={(event) => { setSize(Number(event.target.value)); setPage(0); }} rowsPerPageOptions={[10, 25, 50]} /></CardContent></Card>
  </Stack>;
}
