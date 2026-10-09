import { useMemo, useState } from 'react';
import { Alert, Box, Button, Card, CardContent, Dialog, DialogActions, DialogContent, DialogTitle, Skeleton, Snackbar, Stack, TextField, Typography } from '@mui/material';
import ArrowBackRounded from '@mui/icons-material/ArrowBackRounded';
import { Link, useNavigate, useParams } from 'react-router-dom';
import EvidenceSummary from '../../service-requests/components/EvidenceSummary';
import { useCancelVisit, useRescheduleVisit, useVisitAction, useVisitDetails } from '../hooks/useVisits';
import { VisitService } from '../services/VisitService';
import VisitSummaryCard from '../components/VisitSummaryCard';
import VisitTimeline from '../components/VisitTimeline';
import type { VisitDetails } from '../types/visit';
import VisitRescheduleDialog from '../components/VisitRescheduleDialog';
import EvidenceUploader from '../../evidence/components/EvidenceUploader';
import { useEvidenceTypes } from '../../evidence/hooks/useEvidence';
import VisitExecutionEvidenceCard from '../components/VisitExecutionEvidenceCard';

export default function VisitDetailsPage({agentMode=false}:{agentMode?:boolean}) {
  const { visitId = '' } = useParams<{ visitId: string }>();
  const navigate = useNavigate();
  const queries = useVisitDetails(visitId,agentMode); const action = useVisitAction();
  const evidenceTypes = useEvidenceTypes();
  const reschedule = useRescheduleVisit();
  const cancelVisit = useCancelVisit();
  const [rescheduleOpen,setRescheduleOpen]=useState(false);
  const [cancelOpen,setCancelOpen]=useState(false); const [cancelReason,setCancelReason]=useState(''); const [confirmCancelDiscard,setConfirmCancelDiscard]=useState(false);
  const [message, setMessage] = useState(''); const [actionError, setActionError] = useState<string | null>(null);
  const [failedAction, setFailedAction] = useState<'start' | 'complete' | null>(null);
  const visit = queries.visit.data;
  const visitNumber = useMemo(() => visit ? VisitService.visitNumber(visit, queries.visits.data ?? [visit]) : 1, [visit, queries.visits.data]);
  if (queries.visit.isLoading) return <Stack spacing={2} aria-label="Loading visit"><Skeleton height={42}/><Skeleton variant="rounded" height={180}/><Skeleton variant="rounded" height={180}/></Stack>;
  if (queries.visit.isError || !visit) return <Stack spacing={2}>
      <Button component={Link} to={agentMode?'/agent/visits':'/visits'} startIcon={<ArrowBackRounded />} sx={{ alignSelf: 'flex-start' }}>Back to Visits</Button>
    <Alert severity="error" action={<Button color="inherit" size="small" onClick={() => void queries.visit.refetch()}>Retry</Button>}>{queries.visit.error instanceof Error ? queries.visit.error.message : 'Unable to load this visit.'}</Alert>
  </Stack>;
  const request = queries.request.data;
  const assignment = queries.assignmentHistory.data?.find((entry) => entry.assignmentId === visit.assignmentId);
  const view: VisitDetails = { ...visit, request, evidence: (queries.evidence.data ?? []).filter((item) => item.visitId === visit.visitId), visitNumber };
  const perform = async (workflow: 'start' | 'complete') => {
    if (action.isPending) return;
    setActionError(null);
    setFailedAction(workflow);
    try {
      await action.mutateAsync({ visitId, action: workflow });
      setMessage(workflow === 'start' ? 'Visit started.' : 'Visit marked complete. Service request completion is evaluated separately against its requirements.');
      setFailedAction(null);
    } catch (cause) {
      setActionError(cause instanceof Error ? cause.message : 'Unable to update visit status.');
    }
  };
  const requestCancelClose = () => {
    if (cancelVisit.isPending) return;
    if (cancelReason.trim()) setConfirmCancelDiscard(true);
    else setCancelOpen(false);
  };
  return <Stack spacing={2.5}>
    <Stack direction={{ xs: 'column', sm: 'row' }} alignItems={{ sm: 'center' }} justifyContent="space-between" gap={1}>
      {agentMode ? <Button component={Link} to="/agent/visits" startIcon={<ArrowBackRounded />} sx={{ ml: { sm: -1 }, alignSelf: 'flex-start' }}>My visits</Button> : <Button component={Link} to={`/service-requests/${visit.serviceRequestId}`} startIcon={<ArrowBackRounded />} sx={{ ml: { sm: -1 }, alignSelf: 'flex-start' }}>Back to Request</Button>}
    </Stack>
    <Box component="header">
      <Typography variant="h4" fontWeight={750}>Visit {visitNumber}</Typography>
      <Typography color="text.secondary" sx={{ mt: 0.5 }}>
        {[visit.customerName, visit.propertyName, visit.serviceName].filter(Boolean).join(' · ') || 'Visit details'}
      </Typography>
    </Box>
    <Card component="section" aria-labelledby="visit-next-action-title">
      <CardContent sx={{ p: { xs: 2, sm: 2.5 } }}>
        <Stack spacing={1.5}>
          <Box><Typography id="visit-next-action-title" variant="subtitle1" fontWeight={700}>Next action</Typography>
            <Typography variant="body2" color="text.secondary">
              {visit.status === 'SCHEDULED' ? 'Start the visit when you arrive at the property.' : visit.status === 'IN_PROGRESS' ? 'Capture required evidence and complete the visit when your work is done.' : 'This visit has no further workflow actions.'}
            </Typography>
          </Box>
          {actionError && <Alert severity="error" action={failedAction && <Button color="inherit" size="small" disabled={action.isPending} onClick={() => void perform(failedAction)}>Retry</Button>}>{actionError}</Alert>}
          {visit.status === 'SCHEDULED' && <Stack direction={{ xs: 'column', sm: 'row' }} spacing={1} flexWrap="wrap">
            <Button variant="contained" sx={{ width: { xs: '100%', sm: 'auto' } }} disabled={action.isPending} onClick={() => void perform('start')}>{action.isPending ? 'Starting…' : 'Start Visit'}</Button>
            <Button variant="outlined" sx={{ width: { xs: '100%', sm: 'auto' } }} disabled={reschedule.isPending || cancelVisit.isPending} onClick={() => setRescheduleOpen(true)}>Reschedule</Button>
            <Button color="error" variant="text" sx={{ width: { xs: '100%', sm: 'auto' } }} disabled={reschedule.isPending || cancelVisit.isPending} onClick={() => setCancelOpen(true)}>Cancel visit</Button>
            {!agentMode && <Button variant="text" sx={{ width: { xs: '100%', sm: 'auto' } }} onClick={() => navigate(`/visits/${visitId}/edit`)}>Edit schedule details</Button>}
          </Stack>}
          {visit.status === 'IN_PROGRESS' && <Stack direction={{ xs: 'column', sm: 'row' }} spacing={1}>
            <Button variant="contained" sx={{ width: { xs: '100%', sm: 'auto' } }} disabled={action.isPending} onClick={() => void perform('complete')}>{action.isPending ? 'Completing…' : 'Complete Visit'}</Button>
          </Stack>}
        </Stack>
      </CardContent>
    </Card>
    {!agentMode&&queries.request.isError && <Alert severity="error" action={<Button color="inherit" size="small" onClick={() => void queries.request.refetch()}>Retry</Button>}>Request information could not be loaded.</Alert>}
    {!agentMode&&queries.assignmentHistory.isError && <Alert severity="error" action={<Button color="inherit" size="small" onClick={() => void queries.assignmentHistory.refetch()}>Retry</Button>}>Assignment information could not be loaded.</Alert>}
    <VisitSummaryCard visit={view} />
    <VisitTimeline visit={visit} assignment={assignment} />
    <VisitExecutionEvidenceCard visitId={visitId} visitStatus={visit.status} />
    {queries.evidence.isLoading ? <Skeleton variant="rounded" height={160}/> : queries.evidence.isError ? <Alert severity="error" action={<Button color="inherit" size="small" onClick={() => void queries.evidence.refetch()}>Retry</Button>}>Evidence could not be loaded.</Alert> : <EvidenceSummary evidence={view.evidence} />}
    {visit.status==='IN_PROGRESS'&&<>{evidenceTypes.isError?<Alert severity="error" action={<Button color="inherit" size="small" onClick={()=>void evidenceTypes.refetch()}>Retry</Button>}>Evidence types could not be loaded.</Alert>:evidenceTypes.isLoading?<Skeleton variant="rounded" height={300}/>:<EvidenceUploader visitId={visitId} evidenceTypes={evidenceTypes.data??[]} onUploaded={()=>setMessage('Evidence uploaded for validation.')}/>}</>}
    <Button component={Link} to={`${agentMode?'/agent':''}/visits/${visitId}/evidence`} variant="outlined" sx={{alignSelf:'flex-start'}}>Open full evidence summary</Button>
    <Snackbar open={Boolean(message)} autoHideDuration={4000} onClose={() => setMessage('')} message={message}/>
    <VisitRescheduleDialog open={rescheduleOpen} scheduledAt={visit.scheduledAt} isSaving={reschedule.isPending} error={reschedule.error instanceof Error?reschedule.error.message:null} onClose={()=>setRescheduleOpen(false)} onSubmit={async(request)=>{try{await reschedule.mutateAsync({visitId,request});setRescheduleOpen(false);setMessage('Visit rescheduled.');}catch{/* The dialog displays the mutation error and retains the user's entered values. */}}} />
    <Dialog open={cancelOpen} onClose={requestCancelClose} fullWidth maxWidth="sm"><DialogTitle>Cancel visit</DialogTitle><DialogContent><TextField autoFocus fullWidth multiline minRows={3} label="Cancellation reason" value={cancelReason} onChange={(event)=>setCancelReason(event.target.value)} required inputProps={{maxLength:500}} sx={{mt:1}}/>{cancelVisit.isError&&<Alert severity="error" sx={{mt:2}}>{cancelVisit.error instanceof Error?cancelVisit.error.message:'Visit could not be cancelled.'}</Alert>}{confirmCancelDiscard&&<Alert severity="warning" sx={{mt:2}} action={<Stack direction="row" spacing={0.5}><Button color="inherit" size="small" onClick={()=>setConfirmCancelDiscard(false)}>Keep editing</Button><Button color="inherit" size="small" onClick={()=>{setConfirmCancelDiscard(false);setCancelReason('');setCancelOpen(false);}}>Discard</Button></Stack>}>Your cancellation reason has not been submitted.</Alert>}</DialogContent><DialogActions><Button onClick={requestCancelClose} disabled={cancelVisit.isPending}>Close</Button><Button color="error" variant="contained" disabled={cancelVisit.isPending||cancelReason.trim().length<3} onClick={()=>void cancelVisit.mutateAsync({visitId,reason:cancelReason.trim()}).then(()=>{setCancelOpen(false);setCancelReason('');setConfirmCancelDiscard(false);setMessage('Visit cancelled.');}).catch(()=>undefined)}>{cancelVisit.isPending?'Cancelling…':'Confirm cancellation'}</Button></DialogActions></Dialog>
  </Stack>;
}
