import { useEffect, useMemo, useState } from 'react';
import { Alert, Box, Button, Chip, Skeleton, Snackbar, Stack, Typography } from '@mui/material';
import ArrowBackRounded from '@mui/icons-material/ArrowBackRounded';
import { Link, useLocation, useParams } from 'react-router-dom';
import AssignmentCard from '../components/AssignmentCard';
import ActivityTimeline from '../components/ActivityTimeline';
import EvidenceSummary from '../components/EvidenceSummary';
import RequestCompletionSummary from '../components/RequestCompletionSummary';
import RequestSummaryCard from '../components/RequestSummaryCard';
import VisitTimeline from '../components/VisitTimeline';
import AssignAgentDialog from '../components/AssignAgentDialog';
import AssignmentHistoryTimeline from '../components/AssignmentHistoryTimeline';
import StatusActions from '../components/StatusActions';
import { useAssignRequestAgent, useCancelServiceRequest, useRequestWorkflowAction, useServiceRequestDetails } from '../hooks/useServiceRequestDetails';
import type { ActivityResponse, ServiceRequestListLocationState } from '../types/serviceRequest';
import RequestProgress from '../components/RequestProgress';
import CreateVisitDialog from '../../visits/components/CreateVisitDialog';
import { useCreateVisit } from '../../visits/hooks/useVisits';
import type { CreateVisitRequest, VisitAssignmentOption } from '../../visits/types/visit';
import EvidenceUploader from '../../evidence/components/EvidenceUploader';
import { useEvidenceTypes } from '../../evidence/hooks/useEvidence';

export default function ServiceRequestDetailPage() {
  const { id = '' } = useParams<{ id: string }>();
  const location = useLocation();
  const canManageRequest = !location.pathname.startsWith('/agent/');
  const navigationState = location.state as { returnTo?: string; serviceRequestListState?: ServiceRequestListLocationState; openCreateVisit?: boolean } | null;
  const defaultReturnTo = location.pathname.startsWith('/staff/') ? '/staff/service-requests' : location.pathname.startsWith('/agent/') ? '/agent/work-queue' : '/service-requests';
  const returnTo = navigationState?.returnTo === '/staff/service-requests' || navigationState?.returnTo === '/service-requests' || navigationState?.returnTo === '/agent/work-queue' ? navigationState.returnTo : defaultReturnTo;
  const backState = navigationState?.serviceRequestListState ? { restoreServiceRequestListState: navigationState.serviceRequestListState } : undefined;
  const queries = useServiceRequestDetails(id, !canManageRequest);
  const assign = useAssignRequestAgent();
  const updateStatus = useRequestWorkflowAction();
  const cancelRequest = useCancelServiceRequest();
  const createVisit = useCreateVisit(id);
  const evidenceTypes = useEvidenceTypes();
  const [assignOpen, setAssignOpen] = useState(false);
  const [visitOpen, setVisitOpen] = useState(false);
  const [successMessage, setSuccessMessage] = useState('');
  useEffect(() => {
    if (navigationState?.openCreateVisit) {
      setVisitOpen(true);
    }
  }, [location.state]);
  const request = queries.request.data;
  const activities = useMemo(() => request ? buildActivity(request, queries.visits.data ?? [], queries.evidence.data ?? []) : [], [request, queries.visits.data, queries.evidence.data]);

  if (queries.request.isLoading) return <Stack spacing={2} aria-label="Loading service request"><Skeleton height={42} /><Skeleton variant="rounded" height={260} /><Skeleton variant="rounded" height={200} /></Stack>;
  if (queries.request.isError || !request) return <Stack spacing={2}>
    <Button component={Link} to={returnTo} state={backState} startIcon={<ArrowBackRounded />} sx={{ alignSelf: 'flex-start' }}>Back to Requests</Button>
    <Alert severity="error" action={<Button color="inherit" size="small" onClick={() => void queries.request.refetch()}>Retry</Button>}>{queries.request.error instanceof Error ? queries.request.error.message : 'Unable to load this service request.'}</Alert>
  </Stack>;

  const handleAssign = async (agentId: string, notes: string) => { await assign.mutateAsync({ id, agentId, notes }); setSuccessMessage('Agent assigned successfully.'); };
  const handleStatus = async (action: 'accept' | 'start' | 'complete') => { await updateStatus.mutateAsync({ id, action }); setSuccessMessage(action === 'accept' ? 'Request accepted.' : action === 'start' ? 'Work started.' : 'Request completed.'); };
  const serviceName = request.serviceName || request.requestType;
  const visitAssignments: VisitAssignmentOption[] = (queries.assignmentHistory.data ?? [])
    .filter((entry) => ['ACCEPTED', 'IN_PROGRESS'].includes(entry.assignmentStatus))
    .map((entry) => ({ assignmentId: entry.assignmentId, agentId: entry.agentId, agentName: entry.agentName || 'Assigned agent', agentCode: entry.agentCode, assignmentStatus: entry.assignmentStatus }));
  const handleCreateVisit = async (payload: CreateVisitRequest) => {
    await createVisit.mutateAsync(payload);
    setSuccessMessage('Visit created successfully.');
  };

  return <Stack spacing={2.5}>
    <Stack direction="row" alignItems="center" justifyContent="space-between" flexWrap="wrap" gap={1}>
      <Button component={Link} to={returnTo} state={backState} startIcon={<ArrowBackRounded />} sx={{ ml: -1 }}>Back to Requests</Button>
      <Stack direction="row" spacing={1} flexWrap="wrap">{canManageRequest && <Button component={Link} to={`${returnTo}/${id}/edit`} variant="outlined" disabled={!['NEW','PENDING_ASSIGNMENT'].includes(request.status)}>Edit</Button>}<StatusActions status={request.status} isPending={updateStatus.isPending || cancelRequest.isPending} canManageRequest={canManageRequest} onAssign={() => setAssignOpen(true)} onAction={handleStatus} onCancel={async (reason) => { await cancelRequest.mutateAsync({ id, reason }); setSuccessMessage('Service request cancelled.'); }} /></Stack>
    </Stack>
    <Box component="header" sx={{ pb: 0.5 }}>
      <Stack direction={{ xs: 'column', sm: 'row' }} alignItems={{ sm: 'center' }} spacing={1.25} flexWrap="wrap">
        <Typography variant="h4" fontWeight={750}>{serviceName || request.requestType || 'Service request'}</Typography>
        <Chip size="small" label={request.status.replaceAll('_', ' ')} color={request.status === 'COMPLETED' ? 'success' : request.status === 'CANCELLED' || request.status === 'FAILED' ? 'error' : 'info'} />
        <Chip size="small" variant="outlined" label={`${request.priority} priority`} />
      </Stack>
      <Typography color="text.secondary" sx={{ mt: 0.75 }}>{request.customerName} · {request.propertyTitle} · {serviceName}</Typography>
      {request.assignedToName && <Typography variant="body2" color="text.secondary" sx={{ mt: 0.35 }}>Assigned to {request.assignedToName}</Typography>}
    </Box>
    <RequestProgress status={request.status} />
    <RequestSummaryCard request={request} />
    <AssignmentCard request={request} canManageAssignment={canManageRequest} onAssignClick={() => setAssignOpen(true)} />
    {canManageRequest && <AssignmentHistoryTimeline items={queries.assignmentHistory.data ?? []} isLoading={queries.assignmentHistory.isLoading} error={queries.assignmentHistory.error instanceof Error ? queries.assignmentHistory.error : null} onRetry={() => void queries.assignmentHistory.refetch()} />}
    <VisitTimeline visits={queries.visits.data ?? []} isLoading={queries.visits.isLoading} error={queries.visits.error instanceof Error ? queries.visits.error : null} onRetry={() => void queries.visits.refetch()} onCreate={canManageRequest ? () => setVisitOpen(true) : undefined} />
    {!queries.visits.isLoading && !queries.visits.isError && !queries.evidence.isLoading && !queries.evidence.isError && <RequestCompletionSummary status={request.status} visits={queries.visits.data ?? []} evidence={queries.evidence.data ?? []} canReviewEvidence={canManageRequest} />}
    {queries.evidence.isError ? <Alert severity="error" action={<Button color="inherit" size="small" onClick={() => void queries.evidence.refetch()}>Retry</Button>}>Evidence could not be loaded. {queries.evidence.error instanceof Error ? queries.evidence.error.message : ''}</Alert> : queries.evidence.isLoading ? <Skeleton variant="rounded" height={170} /> : <EvidenceSummary evidence={queries.evidence.data ?? []} />}
    {request.status === 'IN_PROGRESS' && (evidenceTypes.isError
      ? <Alert severity="error" action={<Button color="inherit" size="small" onClick={() => void evidenceTypes.refetch()}>Retry</Button>}>Evidence types could not be loaded.</Alert>
      : evidenceTypes.isLoading
        ? <Skeleton variant="rounded" height={220} />
        : <EvidenceUploader key={id} serviceRequestId={id} evidenceTypes={evidenceTypes.data ?? []} onUploaded={() => { void queries.evidence.refetch(); setSuccessMessage('Evidence uploaded and sent for validation.'); }} />)}
    <ActivityTimeline activities={activities} />
    {canManageRequest && <AssignAgentDialog open={assignOpen} isReassign={Boolean(request.assignedToName)} agents={queries.agents.data ?? []} isLoading={queries.agents.isLoading} error={queries.agents.error instanceof Error ? queries.agents.error : null} isSaving={assign.isPending} onClose={() => setAssignOpen(false)} onRetry={() => void queries.agents.refetch()} onSubmit={handleAssign} />}
    <CreateVisitDialog open={visitOpen} assignments={visitAssignments} isSaving={createVisit.isPending} onClose={() => setVisitOpen(false)} onSubmit={handleCreateVisit} />
    <Snackbar open={Boolean(successMessage)} autoHideDuration={4000} onClose={() => setSuccessMessage('')} message={successMessage} />
  </Stack>;
}

function buildActivity(request: NonNullable<ReturnType<typeof useServiceRequestDetails>['request']['data']>, visits: NonNullable<ReturnType<typeof useServiceRequestDetails>['visits']['data']>, evidence: NonNullable<ReturnType<typeof useServiceRequestDetails>['evidence']['data']>): ActivityResponse[] {
  const items: ActivityResponse[] = [{ id: `created-${request.serviceRequestId}`, label: 'Request created', occurredAt: request.requestedAt, kind: 'request' }];
  for (const entry of request.statusHistory ?? []) {
    if (entry.newStatus === 'NEW') continue;
    items.push({ id: `status-${entry.newStatus}-${entry.changedAt}`, label: statusActivityLabel(entry.newStatus), detail: entry.changeReason, occurredAt: entry.changedAt, kind: 'status' });
  }
  for (const visit of visits) {
    items.push({ id: `visit-scheduled-${visit.visitId}`, label: 'Visit scheduled', detail: `Visit · ${visit.agentName || 'Agent not listed'}`, occurredAt: visit.scheduledAt, kind: 'visit' });
    if (visit.startedAt) items.push({ id: `visit-started-${visit.visitId}`, label: 'Visit started', detail: visit.agentName, occurredAt: visit.startedAt, kind: 'visit' });
    if (visit.endedAt) items.push({ id: `visit-ended-${visit.visitId}`, label: visit.status === 'COMPLETED' ? 'Visit completed' : `Visit ${visit.status.toLowerCase()}`, detail: visit.agentName, occurredAt: visit.endedAt, kind: 'visit' });
  }
  for (const item of evidence) if (item.capturedAt) items.push({ id: `evidence-${item.evidenceId}`, label: 'Evidence uploaded', detail: item.evidenceTypeName, occurredAt: item.capturedAt, kind: 'evidence' });
  return items.sort((a, b) => b.occurredAt.localeCompare(a.occurredAt));
}

function statusActivityLabel(status: string) {
  if (status === 'ASSIGNED') return 'Agent assigned';
  if (status === 'COMPLETED') return 'Request completed';
  return `Status changed to ${status.replaceAll('_', ' ').toLowerCase()}`;
}
