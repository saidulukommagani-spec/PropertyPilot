import { useMemo, useState } from 'react';
import { Alert, Box, Button, Card, CardContent, Chip, CircularProgress, FormControl, InputLabel, MenuItem, Select, Skeleton, Snackbar, Stack, Table, TableBody, TableCell, TableContainer, TableHead, TablePagination, TableRow, TextField, Typography } from '@mui/material';
import SearchRounded from '@mui/icons-material/SearchRounded';
import RefreshRounded from '@mui/icons-material/RefreshRounded';
import AddRounded from '@mui/icons-material/AddRounded';
import { useLocation, useNavigate, useSearchParams } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { serviceRequestApi } from '../../../api/serviceRequestApi';
import { useServiceRequests } from '../hooks/useServiceRequests';
import { useAssignRequestAgent } from '../hooks/useServiceRequestDetails';
import AssignAgentDialog from '../components/AssignAgentDialog';
import type { ServiceRequestListLocationState } from '../types/serviceRequest';

const statusOptions = ['NEW','PENDING_ASSIGNMENT','ASSIGNED','ACCEPTED','IN_PROGRESS','COMPLETED','FAILED','CANCELLED'];
const tone = (status: string): 'success'|'warning'|'error'|'info'|'default' => status === 'COMPLETED' ? 'success' : ['CANCELLED','FAILED'].includes(status) ? 'error' : ['NEW','PENDING_ASSIGNMENT'].includes(status) ? 'warning' : 'info';
const date = (value?: string) => value ? new Intl.DateTimeFormat(undefined, { dateStyle: 'medium', timeStyle: 'short' }).format(new Date(value)) : 'â€”';

export default function ServiceRequestListPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const [searchParams] = useSearchParams();
  const requestPath = location.pathname.startsWith('/staff/') ? '/staff/service-requests' : '/service-requests';
  const restoredState = (location.state as { restoreServiceRequestListState?: Partial<ServiceRequestListLocationState> } | null)?.restoreServiceRequestListState;
  const { data = [], isLoading, isError, error, refetch, isFetching } = useServiceRequests();
  const assignableAgents = useQuery({ queryKey: ['assignable-agents'], queryFn: serviceRequestApi.getAssignableAgents, staleTime: 60_000 });
  const assign = useAssignRequestAgent();
  const requestedStatus = searchParams.get('status');
  const [search, setSearch] = useState(() => restoredState?.search ?? ''); const [status, setStatus] = useState(() => restoredState?.status ?? (requestedStatus && statusOptions.includes(requestedStatus) ? requestedStatus : 'ALL')); const [priority, setPriority] = useState(() => restoredState?.priority ?? 'ALL');
  const [service, setService] = useState(() => restoredState?.service ?? 'ALL'); const [customer, setCustomer] = useState(() => restoredState?.customer ?? 'ALL'); const [agent, setAgent] = useState(() => restoredState?.agent ?? 'ALL'); const [fromDate, setFromDate] = useState(() => restoredState?.fromDate ?? ''); const [toDate, setToDate] = useState(() => restoredState?.toDate ?? '');
  const [page, setPage] = useState(() => restoredState?.page ?? 0); const [rowsPerPage, setRowsPerPage] = useState(() => restoredState?.rowsPerPage ?? 10); const [sortDesc, setSortDesc] = useState(() => restoredState?.sortDesc ?? true);
  const [assignmentRequest, setAssignmentRequest] = useState<{ id: string; assigned: boolean } | null>(null); const [success, setSuccess] = useState('');
  const services = [...new Set(data.map((entry) => entry.serviceName || entry.requestType).filter(Boolean))].sort();
  const customers = [...new Map(data.flatMap((entry) => entry.customerId && entry.customerName
    ? [[entry.customerId, entry.customerName] as const]
    : [])).entries()].sort((a,b) => a[1].localeCompare(b[1]));
  const assignedAgents = [...new Map(data.filter((entry) => entry.assignedAgentId).map((entry) => [entry.assignedAgentId as string, entry.assignedAgentName || 'Assigned agent'])).entries()].sort((a,b) => a[1].localeCompare(b[1]));
  const priorities = [...new Set(data.map((entry) => entry.priority).filter(Boolean))].sort();
  const filtered = useMemo(() => data.filter((r) => {
    const created = new Date(r.requestedAt).getTime();
    return (status === 'ALL' || r.status === status) && (priority === 'ALL' || r.priority === priority) &&
      (service === 'ALL' || (r.serviceName || r.requestType) === service) && (customer === 'ALL' || r.customerId === customer) && (agent === 'ALL' || r.assignedAgentId === agent) &&
      (!fromDate || created >= new Date(`${fromDate}T00:00:00`).getTime()) && (!toDate || created <= new Date(`${toDate}T23:59:59`).getTime()) &&
      `${r.customerName} ${r.propertyTitle} ${r.serviceName||r.requestType} ${r.assignedAgentName||''}`.toLowerCase().includes(search.trim().toLowerCase());
  }).sort((a,b) => (sortDesc ? -1 : 1) * a.requestedAt.localeCompare(b.requestedAt)), [data,status,priority,service,customer,agent,fromDate,toDate,search,sortDesc]);
  const rows = filtered.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage);
  const openRequest = (id: string, openCreateVisit = false) => {
    const listState: ServiceRequestListLocationState = { search, status, priority, service, customer, agent, fromDate, toDate, page, rowsPerPage, sortDesc };
    navigate(`${requestPath}/${id}`, { state: { returnTo: requestPath, serviceRequestListState: listState, openCreateVisit } });
  };
  const handleAssign = async (agentId: string, notes: string) => {
    if (!assignmentRequest) return;
    await assign.mutateAsync({ id: assignmentRequest.id, agentId, notes });
    setAssignmentRequest(null); setSuccess('Agent assigned successfully.');
  };
  return <Stack spacing={3}>
    <Stack direction={{ xs:'column', sm:'row' }} justifyContent="space-between" alignItems={{ sm:'center' }} spacing={2}>
      <Box><Typography variant="h4" fontWeight={750}>Service requests</Typography><Typography color="text.secondary" sx={{ mt: .5 }}>Track customer work, assignment, and delivery status.</Typography></Box>
      <Stack direction="row" spacing={1}><Button variant="outlined" onClick={() => navigate(`${requestPath}/dashboard`)}>Dashboard</Button><Button variant="contained" startIcon={<AddRounded/>} onClick={() => navigate(`${requestPath}/new`)}>Create request</Button><Button variant="outlined" startIcon={isFetching ? <CircularProgress size={16}/> : <RefreshRounded/>} onClick={() => void refetch()} disabled={isFetching}>Refresh</Button></Stack>
    </Stack>
    <Stack direction={{ xs:'column', md:'row' }} flexWrap="wrap" spacing={1.5} useFlexGap>
      <TextField value={search} onChange={(e) => {setSearch(e.target.value);setPage(0);}} placeholder="Search requests" size="small" sx={{ minWidth:260, flex:1 }} InputProps={{ startAdornment:<SearchRounded color="action" sx={{ mr:1 }}/> }} />
      <FormControl size="small" sx={{ minWidth:175 }}><InputLabel>Status</InputLabel><Select label="Status" value={status} onChange={(e) => {setStatus(e.target.value);setPage(0);}}><MenuItem key="all-statuses" value="ALL">All statuses</MenuItem>{statusOptions.map(s=><MenuItem key={s} value={s}>{s.replaceAll('_',' ')}</MenuItem>)}</Select></FormControl>
      <FormControl size="small" sx={{ minWidth:140 }}><InputLabel>Priority</InputLabel><Select label="Priority" value={priority} onChange={(e) => {setPriority(e.target.value);setPage(0);}}><MenuItem key="all-priorities" value="ALL">All priorities</MenuItem>{priorities.map(s=><MenuItem key={s} value={s}>{s}</MenuItem>)}</Select></FormControl>
      <FormControl size="small" sx={{ minWidth:170 }}><InputLabel>Service</InputLabel><Select label="Service" value={service} onChange={(e) => {setService(e.target.value);setPage(0);}}><MenuItem key="all-services" value="ALL">All services</MenuItem>{services.map(s=><MenuItem key={s} value={s}>{s}</MenuItem>)}</Select></FormControl>
      <FormControl size="small" sx={{ minWidth:185 }}><InputLabel>Customer</InputLabel><Select label="Customer" value={customer} onChange={(e) => {setCustomer(e.target.value);setPage(0);}}><MenuItem key="all-customers" value="ALL">All customers</MenuItem>{customers.map(([id,name])=><MenuItem key={id} value={id}>{name}</MenuItem>)}</Select></FormControl>
      <FormControl size="small" sx={{ minWidth:180 }}><InputLabel>Assigned agent</InputLabel><Select label="Assigned agent" value={agent} onChange={(e) => {setAgent(e.target.value);setPage(0);}}><MenuItem key="all-agents" value="ALL">All agents</MenuItem>{assignedAgents.map(([id,name])=><MenuItem key={id} value={id}>{name}</MenuItem>)}</Select></FormControl>
      <TextField size="small" label="Created from" type="date" value={fromDate} onChange={(e)=>setFromDate(e.target.value)} InputLabelProps={{shrink:true}} />
      <TextField size="small" label="Created to" type="date" value={toDate} onChange={(e)=>setToDate(e.target.value)} InputLabelProps={{shrink:true}} />
      <Button variant="text" onClick={() => setSortDesc((v)=>!v)} sx={{ minWidth:150 }}>Date: {sortDesc?'↓':'↑'}</Button>
    </Stack>
    {isError && <Alert severity="error" action={<Button color="inherit" size="small" onClick={() => void refetch()}>Retry</Button>}>{error instanceof Error ? error.message : 'Could not load service requests.'}</Alert>}
    <Card variant="outlined"><CardContent sx={{ p:0, '&:last-child':{pb:0} }}><TableContainer sx={{ overflowX:'auto' }}><Table size="small" aria-label="Service requests">
      <TableHead><TableRow>{['Request','Customer','Property','Service','Priority','Status','Created Date','Assigned Agent','Actions'].map(h=><TableCell key={h} sx={{ fontWeight:700, whiteSpace:'nowrap' }}>{h}{h==='Created Date' && <Button size="small" aria-label="Sort by created date" onClick={() => setSortDesc((v)=>!v)}>{sortDesc?'↓':'↑'}</Button>}</TableCell>)}</TableRow></TableHead>
      <TableBody>{isLoading ? Array.from({length:6},(_,i)=><TableRow key={i}>{Array.from({length:9},(_,j)=><TableCell key={j}><Skeleton/></TableCell>)}</TableRow>) : rows.map(r=><TableRow key={r.serviceRequestId} hover>
        <TableCell><Button size="small" onClick={()=>openRequest(r.serviceRequestId)} aria-label={`Open request for ${r.customerName || 'customer'} at ${r.propertyTitle || 'property'}`}>View request</Button></TableCell><TableCell>{r.customerName || 'â€”'}</TableCell><TableCell>{r.propertyTitle || 'â€”'}</TableCell><TableCell>{r.serviceName || r.requestType || 'â€”'}</TableCell><TableCell>{r.priority || 'â€”'}</TableCell><TableCell><Chip size="small" color={tone(r.status)} label={r.status.replaceAll('_',' ')} /></TableCell><TableCell sx={{whiteSpace:'nowrap'}}>{date(r.requestedAt)}</TableCell><TableCell>{r.assignedAgentName || 'Unassigned'}</TableCell>
        <TableCell><Stack direction="row" spacing={0.5}><Button size="small" onClick={()=>openRequest(r.serviceRequestId)}>View</Button>{['NEW','PENDING_ASSIGNMENT'].includes(r.status) && <Button size="small" onClick={()=>navigate(`${requestPath}/${r.serviceRequestId}/edit`)}>Edit</Button>}<Button size="small" onClick={()=>setAssignmentRequest({id:r.serviceRequestId,assigned:Boolean(r.assignedAgentId)})}>{r.assignedAgentId?'Reassign':'Assign'}</Button><Button size="small" onClick={()=>openRequest(r.serviceRequestId, true)}>Create visit</Button></Stack></TableCell>
      </TableRow>)}{!isLoading && !isError && rows.length===0 && <TableRow><TableCell colSpan={9} align="center"><Box sx={{py:7}}><Typography fontWeight={700}>{data.length ? 'No matching requests' : 'No service requests yet'}</Typography><Typography color="text.secondary" variant="body2" sx={{mt:.5}}>{data.length ? 'Change the filters or search terms.' : 'Requests will appear here when customers submit them.'}</Typography></Box></TableCell></TableRow>}</TableBody>
    </Table></TableContainer><TablePagination component="div" count={filtered.length} page={page} onPageChange={(_,p)=>setPage(p)} rowsPerPage={rowsPerPage} onRowsPerPageChange={(e)=>{setRowsPerPage(Number(e.target.value));setPage(0);}} rowsPerPageOptions={[10,25,50]} /></CardContent></Card>
    <AssignAgentDialog open={Boolean(assignmentRequest)} isReassign={Boolean(assignmentRequest?.assigned)} agents={assignableAgents.data ?? []} isLoading={assignableAgents.isLoading} error={assignableAgents.error instanceof Error ? assignableAgents.error : null} isSaving={assign.isPending} onClose={()=>setAssignmentRequest(null)} onRetry={()=>void assignableAgents.refetch()} onSubmit={handleAssign} />
    <Snackbar open={Boolean(success)} autoHideDuration={4000} onClose={()=>setSuccess('')} message={success}/>
  </Stack>;
}
