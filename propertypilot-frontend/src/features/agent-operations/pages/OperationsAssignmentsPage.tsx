import { useMemo, useState } from 'react';
import { Alert, Button, Card, CardContent, Chip, FormControl, InputLabel, MenuItem, Select, Skeleton, Snackbar, Stack, Table, TableBody, TableCell, TableHead, TablePagination, TableRow, TextField, Typography } from '@mui/material';
import SearchRounded from '@mui/icons-material/SearchRounded';
import { Link, useSearchParams } from 'react-router-dom';
import AssignAgentDialog from '../../service-requests/components/AssignAgentDialog';
import { useAgentAssignment, useAgentAssignmentsData } from '../hooks/useAgentOperations';
import type { ServiceRequestSummaryResponse } from '../../service-requests/types/serviceRequest';
import { PageHeading, QueryFailure } from '../components/OperationsStates';

export default function OperationsAssignmentsPage() {
  const data = useAgentAssignmentsData(); const assign = useAgentAssignment();
  const [searchParams] = useSearchParams();
  const [search, setSearch] = useState(''); const [skill, setSkill] = useState('ALL'); const [area, setArea] = useState('ALL'); const [availability, setAvailability] = useState('ALL'); const [workload, setWorkload] = useState('ALL');
  const [assignmentState, setAssignmentState] = useState(() => searchParams.get('assignment') === 'unassigned' ? 'UNASSIGNED' : 'ALL');
  const [requestSearch, setRequestSearch] = useState(''); const [requestStatus, setRequestStatus] = useState('ALL'); const [requestPage, setRequestPage] = useState(0); const [requestPageSize, setRequestPageSize] = useState(10); const [requestNewestFirst, setRequestNewestFirst] = useState(true);
  const [selected, setSelected] = useState<ServiceRequestSummaryResponse | null>(null); const [message, setMessage] = useState('');
  const agents = data.agents.data ?? []; const requests = data.requests.data ?? [];
  const skills = [...new Set(agents.flatMap((agent) => agent.skills))].sort(); const areas = [...new Set(agents.map((agent) => agent.region).filter((value): value is string => Boolean(value)))].sort();
  const filteredAgents = useMemo(() => agents.filter((agent) => `${agent.fullName} ${agent.agentCode}`.toLowerCase().includes(search.toLowerCase()) && (skill === 'ALL' || agent.skills.includes(skill)) && (area === 'ALL' || agent.region === area) && (availability === 'ALL' || agent.availability === availability) && (workload === 'ALL' || (workload === 'LIGHT' ? agent.activeAssignmentCount <= 2 : agent.activeAssignmentCount > 2))), [agents, search, skill, area, availability, workload]);
  const openRequests = requests.filter((request) => !['COMPLETED', 'CANCELLED', 'FAILED'].includes(request.status));
  const filteredRequests = useMemo(() => openRequests.filter((request) => {
    const unassigned = ['NEW', 'PENDING_ASSIGNMENT'].includes(request.status);
    return (assignmentState === 'ALL' || (assignmentState === 'UNASSIGNED' ? unassigned : !unassigned))
      && (requestStatus === 'ALL' || request.status === requestStatus)
      && `${request.customerName} ${request.propertyTitle} ${request.serviceName || request.requestType} ${request.assignedAgentName || ''} ${request.status}`.toLowerCase().includes(requestSearch.trim().toLowerCase());
  }).sort((left, right) => (requestNewestFirst ? -1 : 1) * left.requestedAt.localeCompare(right.requestedAt)), [openRequests, assignmentState, requestStatus, requestSearch, requestNewestFirst]);
  const requestRows = filteredRequests.slice(requestPage * requestPageSize, requestPage * requestPageSize + requestPageSize);
  const submit = async (agentId: string, notes: string) => { if (!selected) return; await assign.mutateAsync({ requestId: selected.serviceRequestId, agentId, notes }); setSelected(null); setMessage('Agent assignment saved.'); };
  return <Stack spacing={3}><PageHeading title="Agent assignment console" subtitle="Match service requests with agents by skill, area, availability, and active workload." />
    {data.agents.isError && <QueryFailure message={data.agents.error.message} retry={() => void data.agents.refetch()} />}{data.requests.isError && <QueryFailure message={data.requests.error.message} retry={() => void data.requests.refetch()} />}
    <Card variant="outlined"><CardContent><Typography variant="h6" fontWeight={700}>Agent directory</Typography><Stack direction={{ xs: 'column', md: 'row' }} flexWrap="wrap" spacing={1.5} useFlexGap sx={{ mt: 2 }}>
      <TextField size="small" label="Search agents" value={search} onChange={(event) => setSearch(event.target.value)} InputProps={{ startAdornment: <SearchRounded color="action" sx={{ mr: 1 }} /> }} />
      <FormControl size="small" sx={{ minWidth: 150 }}><InputLabel>Skill</InputLabel><Select label="Skill" value={skill} onChange={(event) => setSkill(event.target.value)}><MenuItem value="ALL">All skills</MenuItem>{skills.map((item) => <MenuItem key={item} value={item}>{item}</MenuItem>)}</Select></FormControl>
      <FormControl size="small" sx={{ minWidth: 150 }}><InputLabel>Area</InputLabel><Select label="Area" value={area} onChange={(event) => setArea(event.target.value)}><MenuItem value="ALL">All areas</MenuItem>{areas.map((item) => <MenuItem key={item} value={item}>{item}</MenuItem>)}</Select></FormControl>
      <FormControl size="small" sx={{ minWidth: 150 }}><InputLabel>Availability</InputLabel><Select label="Availability" value={availability} onChange={(event) => setAvailability(event.target.value)}><MenuItem value="ALL">All</MenuItem><MenuItem value="AVAILABLE">Available</MenuItem><MenuItem value="BUSY">Busy</MenuItem></Select></FormControl>
      <FormControl size="small" sx={{ minWidth: 150 }}><InputLabel>Workload</InputLabel><Select label="Workload" value={workload} onChange={(event) => setWorkload(event.target.value)}><MenuItem value="ALL">All workloads</MenuItem><MenuItem value="LIGHT">Light (0-2)</MenuItem><MenuItem value="HIGH">High (3+)</MenuItem></Select></FormControl>
    </Stack><Table size="small" sx={{ mt: 2 }} aria-label="Agent assignment workload"><TableHead><TableRow>{['Agent', 'Skills', 'Area', 'Availability', 'Active workload'].map((label) => <TableCell key={label}>{label}</TableCell>)}</TableRow></TableHead><TableBody>
      {data.agents.isLoading && Array.from({ length: 3 }, (_, index) => <TableRow key={index}><TableCell colSpan={5}><Skeleton /></TableCell></TableRow>)}
      {filteredAgents.map((agent) => <TableRow key={agent.agentId}><TableCell>{agent.fullName}  -  {agent.agentCode}</TableCell><TableCell>{agent.skills.join(', ') || '-'}</TableCell><TableCell>{agent.region || '-'}</TableCell><TableCell><Chip size="small" label={agent.availability} /></TableCell><TableCell>{agent.activeAssignmentCount}</TableCell></TableRow>)}
      {!data.agents.isLoading && !filteredAgents.length && <TableRow><TableCell colSpan={5} align="center"><Typography color="text.secondary" sx={{ py: 3 }}>No agents match the selected filters.</Typography></TableCell></TableRow>}
    </TableBody></Table></CardContent></Card>
    <Card variant="outlined"><CardContent><Typography variant="h6" fontWeight={700}>Open service requests</Typography><Typography variant="body2" color="text.secondary" sx={{ mt: .5 }}>Find work by customer, property, service, or current assignment and open the relevant request.</Typography>
      <Stack direction={{ xs: 'column', md: 'row' }} flexWrap="wrap" spacing={1.5} useFlexGap sx={{ mt: 2 }}>
        <TextField size="small" label="Search requests" value={requestSearch} onChange={(event) => { setRequestSearch(event.target.value); setRequestPage(0); }} InputProps={{ startAdornment: <SearchRounded color="action" sx={{ mr: 1 }} /> }} />
        <FormControl size="small" sx={{ minWidth: 170 }}><InputLabel id="assignment-filter-label">Assignment</InputLabel><Select labelId="assignment-filter-label" label="Assignment" value={assignmentState} onChange={(event) => { setAssignmentState(event.target.value); setRequestPage(0); }}><MenuItem value="ALL">All open work</MenuItem><MenuItem value="UNASSIGNED">Awaiting assignment</MenuItem><MenuItem value="ASSIGNED">Assigned work</MenuItem></Select></FormControl>
        <FormControl size="small" sx={{ minWidth: 180 }}><InputLabel id="request-status-filter-label">Request status</InputLabel><Select labelId="request-status-filter-label" label="Request status" value={requestStatus} onChange={(event) => { setRequestStatus(event.target.value); setRequestPage(0); }}><MenuItem value="ALL">All statuses</MenuItem>{[...new Set(openRequests.map((request) => request.status))].sort().map((status) => <MenuItem key={status} value={status}>{status.replaceAll('_', ' ')}</MenuItem>)}</Select></FormControl>
        <Button onClick={() => setRequestNewestFirst((current) => !current)} aria-label="Sort requests by received date">Received: {requestNewestFirst ? 'Newest first' : 'Oldest first'}</Button>
      </Stack>
      <Table size="small" sx={{ mt: 1 }} aria-label="Requests for agent assignment"><TableHead><TableRow>{['Request received', 'Customer', 'Property', 'Service', 'Status', 'Assigned agent', 'Actions'].map((label) => <TableCell key={label}>{label}</TableCell>)}</TableRow></TableHead><TableBody>
      {data.requests.isLoading && Array.from({ length: 3 }, (_, index) => <TableRow key={index}><TableCell colSpan={7}><Skeleton /></TableCell></TableRow>)}
      {requestRows.map((request) => <TableRow key={request.serviceRequestId} hover><TableCell>{new Intl.DateTimeFormat(undefined, { dateStyle: 'medium', timeStyle: 'short' }).format(new Date(request.requestedAt))}</TableCell><TableCell>{request.customerName || 'Customer not listed'}</TableCell><TableCell>{request.propertyTitle || 'Property not listed'}</TableCell><TableCell>{request.serviceName || request.requestType}</TableCell><TableCell><Chip size="small" label={request.status.replaceAll('_', ' ')} /></TableCell><TableCell>{request.assignedAgentName || 'Unassigned'}</TableCell><TableCell><Stack direction={{ xs: 'column', sm: 'row' }} spacing={.5}><Button component={Link} size="small" to={`/service-requests/${request.serviceRequestId}`}>Open request</Button><Button size="small" onClick={() => setSelected(request)}>{request.assignedAgentName ? 'Reassign' : 'Assign'}</Button></Stack></TableCell></TableRow>)}
      {!data.requests.isLoading && !data.requests.isError && !requestRows.length && <TableRow><TableCell colSpan={7} align="center"><Typography color="text.secondary" sx={{ py: 3 }}>{openRequests.length ? 'No requests match the current search and filters.' : 'No open service requests need assignment.'}</Typography></TableCell></TableRow>}
    </TableBody></Table><TablePagination component="div" count={filteredRequests.length} page={requestPage} onPageChange={(_, page) => setRequestPage(page)} rowsPerPage={requestPageSize} onRowsPerPageChange={(event) => { setRequestPageSize(Number(event.target.value)); setRequestPage(0); }} rowsPerPageOptions={[10, 25, 50]} /></CardContent></Card>
    {assign.isError && <Alert severity="error">{assign.error.message}</Alert>}
    <AssignAgentDialog open={Boolean(selected)} isReassign={Boolean(selected?.assignedAgentName)} agents={data.agents.data ?? []} isLoading={data.agents.isLoading} error={data.agents.error instanceof Error ? data.agents.error : null} isSaving={assign.isPending} onClose={() => setSelected(null)} onRetry={() => void data.agents.refetch()} onSubmit={submit} />
    <Snackbar open={Boolean(message)} autoHideDuration={4000} onClose={() => setMessage('')} message={message} />
  </Stack>;
}

