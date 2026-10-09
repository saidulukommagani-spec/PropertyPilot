import { Card, CardContent, Chip, Skeleton, Stack, Table, TableBody, TableCell, TableHead, TableRow, Typography } from '@mui/material';
import { useOperationsAgents, useOperationsVisits, useOperationsEvidence, useOperationsSla } from '../hooks/useAgentOperations';
import { MetricCards, PageHeading, QueryFailure } from '../components/OperationsStates';

export default function OperationsAgentsPage() {
  const agentsQuery = useOperationsAgents(); const visitsQuery = useOperationsVisits(); const evidenceQuery = useOperationsEvidence(); const slaQuery = useOperationsSla();
  const agents = agentsQuery.data ?? []; const visits = visitsQuery.data ?? []; const evidence = evidenceQuery.data ?? []; const sla = slaQuery.data?.requests ?? [];
  const completed = visits.filter((visit) => visit.status === 'COMPLETED');
  const evidenceComplete = completed.filter((visit) => evidence.some((item) => item.visitId === visit.visitId && item.validationStatus === 'APPROVED'));
  const duration = completed.map((visit) => visit.startedAt && visit.endedAt ? new Date(visit.endedAt).getTime() - new Date(visit.startedAt).getTime() : null).filter((value): value is number => value !== null && value >= 0);
  const averageHours = duration.length ? (duration.reduce((sum, value) => sum + value, 0) / duration.length / 3_600_000).toFixed(1) : '—';
  const visitsByRequest = new Map(visits.map((visit) => [visit.serviceRequestId, visit]));
  const linkedSla = sla.filter((row) => visitsByRequest.has(row.serviceRequestId));
  const slaCompliance = linkedSla.length ? `${Math.round(linkedSla.filter((row) => row.slaState !== 'BREACHED').length * 100 / linkedSla.length)}%` : '—';
  const evidenceCompliance = completed.length ? `${Math.round(evidenceComplete.length * 100 / completed.length)}%` : '—';
  const metrics = [{ label: 'Total visits', value: visits.length }, { label: 'Completed visits', value: completed.length }, { label: 'SLA compliance', value: slaCompliance }, { label: 'Evidence compliance', value: evidenceCompliance }, { label: 'Avg. completion time', value: `${averageHours}${averageHours === '—' ? '' : ' h'}` }];
  return <Stack spacing={3}><PageHeading title="Agent performance" subtitle="Operational performance from recorded visits and evidence records." />
    {agentsQuery.isError && <QueryFailure message={agentsQuery.error.message} retry={() => void agentsQuery.refetch()} />}{visitsQuery.isError && <QueryFailure message={visitsQuery.error.message} retry={() => void visitsQuery.refetch()} />}{evidenceQuery.isError && <QueryFailure message={evidenceQuery.error.message} retry={() => void evidenceQuery.refetch()} />}{slaQuery.isError && <QueryFailure message={slaQuery.error.message} retry={() => void slaQuery.refetch()} />}
    <MetricCards items={metrics} loading={visitsQuery.isLoading || evidenceQuery.isLoading || slaQuery.isLoading} />
    <Card variant="outlined"><CardContent><Typography variant="h6" fontWeight={700}>Agent workload and results</Typography><Table size="small" aria-label="Agent performance"><TableHead><TableRow>{['Agent', 'Code', 'Area', 'Active work', 'Total visits', 'Completed', 'SLA compliance', 'Evidence approved', 'Availability'].map((label) => <TableCell key={label}>{label}</TableCell>)}</TableRow></TableHead><TableBody>
      {agentsQuery.isLoading && Array.from({ length: 4 }, (_, index) => <TableRow key={index}><TableCell colSpan={9}><Skeleton /></TableCell></TableRow>)}
      {agents.map((agent) => { const assigned = visits.filter((visit) => visit.agentId === agent.agentId); const done = assigned.filter((visit) => visit.status === 'COMPLETED'); const approved = done.filter((visit) => evidence.some((item) => item.visitId === visit.visitId && item.validationStatus === 'APPROVED')); const requestIds = new Set(assigned.map((visit) => visit.serviceRequestId)); const agentSla = sla.filter((row) => requestIds.has(row.serviceRequestId)); const compliance = agentSla.length ? `${Math.round(agentSla.filter((row) => row.slaState !== 'BREACHED').length * 100 / agentSla.length)}%` : '—'; return <TableRow key={agent.agentId}><TableCell>{agent.fullName}</TableCell><TableCell>{agent.agentCode}</TableCell><TableCell>{agent.region || '—'}</TableCell><TableCell>{agent.activeAssignmentCount}</TableCell><TableCell>{assigned.length}</TableCell><TableCell>{done.length}</TableCell><TableCell>{compliance}</TableCell><TableCell>{approved.length}</TableCell><TableCell><Chip size="small" color={agent.availability === 'AVAILABLE' ? 'success' : 'warning'} label={agent.availability} /></TableCell></TableRow>; })}
      {!agentsQuery.isLoading && !agents.length && <TableRow><TableCell colSpan={9} align="center"><Typography color="text.secondary" sx={{ py: 4 }}>No agent records found.</Typography></TableCell></TableRow>}
    </TableBody></Table></CardContent></Card>
  </Stack>;
}
