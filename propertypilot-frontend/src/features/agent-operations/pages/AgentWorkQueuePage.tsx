import { useMemo, useState } from 'react';
import { Alert, Button, Card, CardContent, Chip, Skeleton, Stack, Tab, Tabs, Table, TableBody, TableCell, TableHead, TableRow, Typography } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { useQueries } from '@tanstack/react-query';
import { useAgentRequests, useAgentVisits } from '../hooks/useAgentOperations';
import { AgentOperationsApi } from '../services/AgentOperationsApi';
import { PageHeading, QueryFailure } from '../components/OperationsStates';

const tabs = ['Assigned', 'Accepted', 'In Progress', 'Pending Evidence', 'Completed'] as const;
type QueueTab = typeof tabs[number];
function matchesTab(visit: { status: string; acceptedAt?: string }, tab: QueueTab, evidencePending: boolean) {
  if (tab === 'Assigned') return visit.status === 'SCHEDULED' && !visit.acceptedAt;
  if (tab === 'Accepted') return visit.status === 'SCHEDULED' && Boolean(visit.acceptedAt);
  if (tab === 'In Progress') return visit.status === 'IN_PROGRESS';
  if (tab === 'Pending Evidence') return evidencePending;
  return visit.status === 'COMPLETED';
}
export default function AgentWorkQueuePage() {
  const query = useAgentVisits(); const requestQuery = useAgentRequests(); const navigate = useNavigate(); const [tab, setTab] = useState<QueueTab>('Assigned');
  const visits = query.data ?? [];
  const evidenceSummaries = useQueries({ queries: visits.map((visit) => ({ queryKey: ['evidence', 'visit-summary', visit.visitId], queryFn: () => AgentOperationsApi.visitEvidenceSummary(visit.visitId) })) });
  const filtered = useMemo(() => visits.filter((visit, index) => matchesTab(visit, tab, Boolean(evidenceSummaries[index]?.data && evidenceSummaries[index].data.completionPercentage < 100))), [visits, tab, evidenceSummaries]);
  const requestsWithoutVisits = (requestQuery.data ?? []).filter((request) =>
    ['ASSIGNED', 'ACCEPTED', 'IN_PROGRESS'].includes(request.status)
    && !visits.some((visit) => visit.serviceRequestId === request.serviceRequestId));
  return <Stack spacing={3}>
    <PageHeading title="My work queue" subtitle="Review visits and service work assigned to your account." action={<Button variant="contained" onClick={() => navigate('/agent/visits')}>My visits</Button>} />
    {query.isError && <QueryFailure message={query.error.message} retry={() => void query.refetch()} />}
    {evidenceSummaries.some((summary) => summary.isError) && <Alert severity="error">Some evidence requirements could not be loaded. Retry by refreshing this page.</Alert>}
    <Card variant="outlined"><Tabs value={tab} onChange={(_, value: QueueTab) => setTab(value)} variant="scrollable" scrollButtons="auto" aria-label="Work queue status">{tabs.map((value) => <Tab key={value} value={value} label={value} />)}</Tabs></Card>
    <Card variant="outlined"><CardContent sx={{ p: 0, '&:last-child': { pb: 0 } }}><Table aria-label={`${tab} work queue`}><TableHead><TableRow>{['Visit', 'Request', 'Customer', 'Property', 'Scheduled', 'Status', ''].map((name) => <TableCell key={name}>{name}</TableCell>)}</TableRow></TableHead><TableBody>
      {(query.isLoading || (tab === 'Pending Evidence' && evidenceSummaries.some((summary) => summary.isLoading))) && Array.from({ length: 4 }, (_, index) => <TableRow key={index}><TableCell colSpan={7}><Skeleton /></TableCell></TableRow>)}
      {filtered.map((visit) => <TableRow key={visit.visitId} hover><TableCell>Visit</TableCell><TableCell>Service request</TableCell><TableCell>{visit.customerName || '-'}</TableCell><TableCell>{visit.propertyName || '-'}</TableCell><TableCell>{new Date(visit.scheduledAt).toLocaleString()}</TableCell><TableCell><Chip size="small" label={visit.status.replaceAll('_', ' ')} /></TableCell><TableCell><Button onClick={() => navigate(`/agent/visit/${visit.visitId}`)}>Open</Button></TableCell></TableRow>)}
      {!query.isLoading && !query.isError && !(tab === 'Pending Evidence' && evidenceSummaries.some((summary) => summary.isLoading)) && !filtered.length && <TableRow><TableCell colSpan={7} align="center"><Typography color="text.secondary" sx={{ py: 5 }}>No visits in this queue.</Typography></TableCell></TableRow>}
    </TableBody></Table></CardContent></Card>
    <Stack spacing={1.5}>
      <Typography variant="h6" fontWeight={700}>Assigned service requests without a visit</Typography>
      <Typography variant="body2" color="text.secondary">Remote and document-based work appears here. A visit is only required when the service workflow calls for one.</Typography>
      {requestQuery.isError && <QueryFailure message={requestQuery.error.message} retry={() => void requestQuery.refetch()} />}
      <Card variant="outlined"><CardContent sx={{ p: 0, '&:last-child': { pb: 0 } }}><Table aria-label="Assigned service requests without a visit"><TableHead><TableRow>{['Request', 'Customer', 'Property', 'Service', 'SLA', 'Status', ''].map((name) => <TableCell key={name}>{name}</TableCell>)}</TableRow></TableHead><TableBody>
        {requestQuery.isLoading && Array.from({ length: 3 }, (_, index) => <TableRow key={index}><TableCell colSpan={7}><Skeleton /></TableCell></TableRow>)}
        {requestsWithoutVisits.map((request) => <TableRow key={request.serviceRequestId} hover><TableCell>Service request</TableCell><TableCell>{request.customerName || '-'}</TableCell><TableCell>{request.propertyTitle || '-'}</TableCell><TableCell>{request.serviceName || request.requestType}</TableCell><TableCell>{request.slaStatus?.replaceAll('_', ' ') || 'Not configured'}</TableCell><TableCell><Chip size="small" label={request.status.replaceAll('_', ' ')} /></TableCell><TableCell><Stack direction={{xs:'column',sm:'row'}} spacing={0.5}><Button onClick={() => navigate(`/agent/service-requests/${request.serviceRequestId}`)}>Open work</Button><Button disabled={request.status!=='IN_PROGRESS'} onClick={() => navigate(`/evidence/upload?serviceRequestId=${request.serviceRequestId}`)}>Upload evidence</Button></Stack></TableCell></TableRow>)}
        {!requestQuery.isLoading && !requestQuery.isError && !requestsWithoutVisits.length && <TableRow><TableCell colSpan={7} align="center"><Typography color="text.secondary" sx={{ py: 4 }}>No assigned requests without a visit.</Typography></TableCell></TableRow>}
      </TableBody></Table></CardContent></Card>
    </Stack>
  </Stack>;
}


