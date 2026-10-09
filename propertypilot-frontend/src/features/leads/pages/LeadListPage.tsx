import { useMemo, useState } from 'react';
import { Alert, Button, Card, CardActionArea, CardContent, FormControl, InputLabel, MenuItem, Pagination, Select, Stack, TextField, Typography } from '@mui/material';
import { Link, useLocation } from 'react-router-dom';
import { useMyLeads } from '../hooks/useLeads';
import LeadStatusChip from '../components/LeadStatusChip';
import { LeadErrorState, LeadLoadingState } from '../components/LeadPageStates';

export default function LeadListPage() {
  const agentView = useLocation().pathname.startsWith('/agent/leads');
  const basePath = agentView ? '/agent/leads' : '/customer/leads';
  const query = useMyLeads();
  const [search, setSearch] = useState('');
  const [status, setStatus] = useState('ALL');
  const [page, setPage] = useState(1);
  const pageSize = 10;
  const filtered = useMemo(() => (query.data ?? []).filter((lead) => {
    const term = search.trim().toLowerCase();
    return (!term || [lead.source, lead.location, lead.propertyType, lead.notes].some((value) => value?.toLowerCase().includes(term))) && (status === 'ALL' || lead.status === status);
  }), [query.data, search, status]);
  const pageCount = Math.max(1, Math.ceil(filtered.length / pageSize));
  const rows = filtered.slice((page - 1) * pageSize, page * pageSize);
  return <Stack spacing={3}>
    <Stack direction={{ xs: 'column', sm: 'row' }} justifyContent="space-between" gap={2}>
      <Stack><Typography variant="h4" fontWeight={750}>{agentView ? 'Assigned leads' : 'My leads'}</Typography><Typography color="text.secondary">{agentView ? 'Review leads assigned to you and their linked service work.' : 'Review and track your submitted property enquiries.'}</Typography></Stack>
      {!agentView && <Button component={Link} to="/customer/leads/new" variant="contained">Create lead</Button>}
    </Stack>
    {query.isError && <LeadErrorState error={query.error} retry={() => void query.refetch()} />}
    <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
      <TextField label="Search leads" value={search} onChange={(event) => { setSearch(event.target.value); setPage(1); }} fullWidth />
      <FormControl sx={{ minWidth: { sm: 220 } }}><InputLabel id="lead-status-label">Status</InputLabel><Select labelId="lead-status-label" label="Status" value={status} onChange={(event) => { setStatus(event.target.value); setPage(1); }}><MenuItem value="ALL">All statuses</MenuItem>{['NEW','CONTACTED','QUALIFIED','READY_FOR_DEAL','DEAL_CONVERTED','CONVERTED','LOST','CLOSED'].map((item) => <MenuItem key={item} value={item}>{item.replaceAll('_', ' ')}</MenuItem>)}</Select></FormControl>
    </Stack>
    {query.isLoading ? <LeadLoadingState /> : rows.length ? <Stack spacing={1.5}>{rows.map((lead) => <Card key={lead.leadId} variant="outlined"><CardActionArea component={Link} to={`${basePath}/${lead.leadId}`}><CardContent><Stack direction={{ xs: 'column', sm: 'row' }} justifyContent="space-between" gap={1} alignItems={{ sm: 'center' }}><Stack spacing={0.5}><Typography variant="subtitle1" fontWeight={700}>Lead from {lead.source || 'customer enquiry'}</Typography><Typography variant="body2" color="text.secondary">{lead.propertyType || 'Property details not provided'}{lead.location ? ` · ${lead.location}` : ''}</Typography></Stack><LeadStatusChip status={lead.status} /></Stack></CardContent></CardActionArea></Card>)}</Stack> : !query.isError && <Alert severity="info">{query.data?.length ? 'No leads match these filters.' : agentView ? 'No leads are assigned to you.' : 'You have not created any leads yet.'}</Alert>}
    {filtered.length > pageSize && <Pagination count={pageCount} page={page} onChange={(_, next) => setPage(next)} color="primary" aria-label="Lead pages" />}
  </Stack>;
}
