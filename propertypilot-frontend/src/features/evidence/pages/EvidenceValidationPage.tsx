import { useEffect, useState } from 'react';
import { Alert, Box, Button, Card, CardContent, Chip, Dialog, DialogActions, DialogContent, DialogTitle, Divider, Skeleton, Snackbar, Stack, TextField, Typography } from '@mui/material';
import { Link, useSearchParams } from 'react-router-dom';
import { useEvidenceRequestContext, useEvidenceValidationQueue, useReviewEvidence } from '../hooks/useEvidence';
import type { ReviewEvidenceRequest } from '../types/evidence';
import type { EvidenceResponse } from '../../service-requests/types/serviceRequest';
import EvidenceFilePreview from '../components/EvidenceFilePreview';

const dateLabel = (value?: string) => value ? new Intl.DateTimeFormat(undefined, { dateStyle: 'medium', timeStyle: 'short' }).format(new Date(value)) : 'Not recorded';
const reviewableStatuses = ['PENDING', 'REJECTED', 'REUPLOAD_REQUESTED'];
const decisionLabels: Record<ReviewEvidenceRequest['status'], string> = {
  APPROVED: 'Approve evidence',
  REJECTED: 'Reject evidence',
  REUPLOAD_REQUESTED: 'Request reupload',
};

export default function EvidenceValidationPage() {
  const query = useEvidenceValidationQueue();
  const review = useReviewEvidence();
  const entries = query.data ?? [];
  const [searchParams, setSearchParams] = useSearchParams();
  const requestedEvidenceId = searchParams.get('evidenceId') ?? '';
  const [selectedId, setSelectedId] = useState(requestedEvidenceId);
  const [reviewedEvidence, setReviewedEvidence] = useState<EvidenceResponse | null>(null);
  const [comment, setComment] = useState('');
  const [pendingReview, setPendingReview] = useState<ReviewEvidenceRequest['status'] | null>(null);
  const [message, setMessage] = useState('');
  const selected = entries.find((entry) => entry.evidenceId === selectedId)
    ?? (reviewedEvidence?.evidenceId === selectedId ? reviewedEvidence : undefined);
  const requestContext = useEvidenceRequestContext(selected?.serviceRequestId ?? '');

  useEffect(() => {
    const requested = entries.find((entry) => entry.evidenceId === requestedEvidenceId);
    if (requested && selectedId !== requested.evidenceId) {
      setSelectedId(requested.evidenceId);
      return;
    }
    if (reviewedEvidence?.evidenceId === selectedId) return;
    if (!entries.some((entry) => entry.evidenceId === selectedId)) {
      const firstId = entries[0]?.evidenceId ?? '';
      setSelectedId(firstId);
      if (firstId && requestedEvidenceId !== firstId) setSearchParams({ evidenceId: firstId }, { replace: true });
    }
  }, [entries, requestedEvidenceId, reviewedEvidence?.evidenceId, selectedId, setSearchParams]);

  const chooseEvidence = (evidenceId: string) => {
    setSelectedId(evidenceId);
    setReviewedEvidence(null);
    setComment('');
    review.reset();
    setSearchParams({ evidenceId }, { replace: true });
  };

  const submit = async () => {
    if (!selected || !pendingReview || !comment.trim() || !reviewableStatuses.includes(selected.validationStatus ?? 'PENDING')) return;
    // Keep the selected record mounted while the successful mutation refreshes the queue.
    // An approved record leaves that queue, but its updated details and history remain useful.
    setReviewedEvidence(selected);
    try {
      const reviewed = await review.mutateAsync({ evidenceId: selected.evidenceId, request: { status: pendingReview, comment: comment.trim() } });
      setReviewedEvidence(reviewed);
      const actionLabel = decisionLabels[pendingReview];
      setPendingReview(null);
      setComment('');
      setMessage(`${actionLabel} saved.`);
    } catch {
      // Keep the confirmation and comment visible so the Operations user can retry.
    }
  };

  return <Stack spacing={2.5}>
    <Stack direction={{ xs: 'column', sm: 'row' }} justifyContent="space-between" alignItems={{ sm: 'center' }} gap={1}>
      <Box>
        <Typography variant="h4" fontWeight={750}>Evidence review</Typography>
        <Typography color="text.secondary" sx={{ mt: .5 }}>Review submitted proof against the service request and visit context before it can satisfy configured completion requirements.</Typography>
      </Box>
      <Button component={Link} to="/evidence" variant="outlined">All evidence</Button>
    </Stack>

    {query.isError && <Alert severity="error" action={<Button color="inherit" size="small" onClick={() => void query.refetch()}>Retry</Button>}>{query.error instanceof Error ? query.error.message : 'Evidence review queue could not be loaded.'}</Alert>}

    <Box display="grid" gridTemplateColumns={{ xs: '1fr', lg: 'minmax(280px, .8fr) minmax(0, 1.7fr)' }} gap={2} alignItems="start">
      <Card variant="outlined"><CardContent>
        <Typography variant="h6" fontWeight={700}>Review queue ({entries.length})</Typography>
        <Divider sx={{ my: 1.5 }} />
        {query.isLoading ? <Stack spacing={1} aria-label="Loading review queue"><Skeleton height={76} /><Skeleton height={76} /><Skeleton height={76} /></Stack>
          : query.isError ? <Typography color="text.secondary">Retry to load review submissions.</Typography>
            : entries.length === 0 ? <Stack spacing={.5} role="status"><Typography fontWeight={650}>The review queue is clear.</Typography><Typography variant="body2" color="text.secondary">New submissions and returned evidence appear here when they need review.</Typography></Stack>
              : <Stack spacing={1}>
                {entries.map((entry) => <Button key={entry.evidenceId} fullWidth variant={selectedId === entry.evidenceId ? 'contained' : 'outlined'} color={selectedId === entry.evidenceId ? 'primary' : 'inherit'} onClick={() => chooseEvidence(entry.evidenceId)} aria-pressed={selectedId === entry.evidenceId} sx={{ justifyContent: 'flex-start', textAlign: 'left', textTransform: 'none', minHeight: 68 }}>
                  <Stack alignItems="flex-start" minWidth={0} width="100%">
                    <Typography fontWeight={700} noWrap>{entry.evidenceTypeName} · {entry.fileName || dateLabel(entry.capturedAt ?? entry.createdAt)}</Typography>
                    <Typography variant="caption" color={selectedId === entry.evidenceId ? 'inherit' : 'text.secondary'} noWrap>{entry.customerName || 'Customer'} · {entry.propertyName || 'Property not listed'} · {dateLabel(entry.capturedAt ?? entry.createdAt)}</Typography>
                    <Typography variant="caption" color={selectedId === entry.evidenceId ? 'inherit' : 'text.secondary'} noWrap>{entry.serviceName || 'Service not listed'} · {(entry.validationStatus ?? 'PENDING').replaceAll('_', ' ')}</Typography>
                  </Stack>
                </Button>)}
              </Stack>}
      </CardContent></Card>

      {selected ? <Stack spacing={2}>
        <EvidenceFilePreview evidenceId={selected.evidenceId} fileName={selected.fileName} mediaType={selected.mediaType} fileUrl={selected.fileUrl} />
        <Card variant="outlined"><CardContent>
          <Stack direction={{ xs: 'column', sm: 'row' }} justifyContent="space-between" alignItems={{ sm: 'center' }} gap={1}>
            <Box><Typography variant="h6" fontWeight={700}>Submission context</Typography><Typography variant="body2" color="text.secondary">{selected.serviceName || 'Service not listed'}</Typography></Box>
            <Chip size="small" color={statusColor(selected.validationStatus)} label={(selected.validationStatus ?? 'PENDING').replaceAll('_', ' ')} />
          </Stack>
          <Divider sx={{ my: 1.5 }} />
          <Box display="grid" gridTemplateColumns={{ xs: '1fr', sm: 'repeat(2,minmax(0,1fr))' }} gap={1.5}>
            <ContextValue label="Customer" value={selected.customerName} />
            <ContextValue label="Property" value={selected.propertyName} />
            <ContextValue label="Service request status" value={requestContext.data?.status?.replaceAll('_', ' ')} loading={requestContext.isLoading} />
            <ContextValue label="Evidence type" value={selected.evidenceTypeName} />
            <ContextValue label="Visit status" value={selected.visitStatus?.replaceAll('_', ' ') || (selected.visitId ? 'Visit linked' : 'No visit linked')} />
            <ContextValue label="Visit scheduled" value={selected.visitScheduledAt ? dateLabel(selected.visitScheduledAt) : undefined} />
            <ContextValue label="Agent" value={selected.agentName} />
            <ContextValue label="Submitted" value={dateLabel(selected.capturedAt ?? selected.createdAt)} />
            <ContextValue label="Uploaded by" value={selected.uploadedByName} />
            <ContextValue label="GPS coordinates" value={selected.latitude != null && selected.longitude != null ? `${selected.latitude}, ${selected.longitude}` : undefined} />
          </Box>
          {requestContext.isError && <Alert severity="warning" sx={{ mt: 2 }} action={<Button color="inherit" size="small" onClick={() => void requestContext.refetch()}>Retry</Button>}>The service request status could not be loaded. Evidence review remains available.</Alert>}
          {selected.remarks && <Typography variant="body2" sx={{ mt: 1.5 }}><strong>Uploader notes:</strong> {selected.remarks}</Typography>}
          {selected.validationComment && <Alert severity={selected.validationStatus === 'REJECTED' ? 'error' : selected.validationStatus === 'APPROVED' ? 'success' : 'info'} sx={{ mt: 2 }}>
            <strong>Latest review comment:</strong> {selected.validationComment}{selected.validatedByName ? ` · ${selected.validatedByName}` : ''}{selected.validatedAt ? ` · ${dateLabel(selected.validatedAt)}` : ''}
          </Alert>}
          <Stack direction={{ xs: 'column', sm: 'row' }} flexWrap="wrap" gap={1} sx={{ mt: 2 }}>
            <Button component={Link} to={`/service-requests/${selected.serviceRequestId}`} variant="outlined">Open service request</Button>
            {selected.visitId && <Button component={Link} to={`/visits/${selected.visitId}`} variant="outlined">Open visit</Button>}
            <Button component={Link} to={`/evidence/${selected.evidenceId}`} size="small">Evidence details</Button>
          </Stack>
        </CardContent></Card>

        {Boolean(selected.validationHistory?.length) && <Card variant="outlined"><CardContent>
          <Typography variant="h6" fontWeight={700}>Review history</Typography><Divider sx={{ my: 1.5 }} />
          <Stack component="ol" spacing={1.25} sx={{ m: 0, pl: 2.5 }}>
            {selected.validationHistory?.map((item) => <Box component="li" key={item.historyId}>
              <Typography fontWeight={650}>{item.status.replaceAll('_', ' ')}</Typography>
              <Typography variant="body2">{item.comment}</Typography>
              <Typography variant="caption" color="text.secondary">{item.userName || 'Operations reviewer'} · {dateLabel(item.occurredAt)}</Typography>
            </Box>)}
          </Stack>
        </CardContent></Card>}

        <Card variant="outlined"><CardContent>
          <Typography variant="h6" fontWeight={700}>Review submission</Typography>
          <Typography variant="body2" color="text.secondary" sx={{ mt: .5 }}>A comment is required for every review outcome. Returned or rejected submissions can be reviewed again after correction.</Typography>
          <TextField fullWidth required multiline minRows={2} label="Review comments" value={comment} onChange={(event) => setComment(event.target.value)} inputProps={{ maxLength: 2000 }} sx={{ mt: 2 }} helperText="Explain the decision or what needs to change." />
          <Stack direction={{ xs: 'column', sm: 'row' }} justifyContent="flex-end" spacing={1} sx={{ mt: 2 }}>
            <Button color="warning" variant="outlined" disabled={!canReview(selected.validationStatus) || review.isPending || !comment.trim()} onClick={() => setPendingReview('REUPLOAD_REQUESTED')}>Request reupload</Button>
            <Button color="error" variant="outlined" disabled={!canReview(selected.validationStatus) || review.isPending || !comment.trim()} onClick={() => setPendingReview('REJECTED')}>Reject</Button>
            <Button variant="contained" disabled={!canReview(selected.validationStatus) || review.isPending || !comment.trim()} onClick={() => setPendingReview('APPROVED')}>{review.isPending ? 'Saving…' : 'Approve'}</Button>
          </Stack>
          {!canReview(selected.validationStatus) && <Typography variant="caption" color="text.secondary" sx={{ display: 'block', mt: 1 }}>This submission is not in a backend-reviewable state.</Typography>}
        </CardContent></Card>
      </Stack> : !query.isLoading && !query.isError ? <Card variant="outlined"><CardContent><Typography color="text.secondary">Choose an evidence submission to review.</Typography></CardContent></Card> : null}
    </Box>

    <Dialog open={Boolean(pendingReview)} onClose={() => !review.isPending && setPendingReview(null)} fullWidth maxWidth="sm" aria-labelledby="evidence-review-confirm-title">
      <DialogTitle id="evidence-review-confirm-title">{pendingReview ? decisionLabels[pendingReview] : 'Confirm evidence review'}</DialogTitle>
      <DialogContent>
        <Typography sx={{ mt: 1 }}>
          {pendingReview === 'APPROVED'
            ? 'Approve this submission with the comment shown. If this satisfies the configured service requirements, the backend may complete the service request after validating its visits, evidence, and GPS rules.'
            : pendingReview === 'REJECTED'
              ? 'Reject this submission. The comment will be recorded in its review history and shown as the reason.'
              : 'Return this submission for correction. The comment will be recorded in its review history.'}
        </Typography>
        {comment.trim() && <Alert severity="info" sx={{ mt: 2 }}>{comment.trim()}</Alert>}
        {review.error && <Alert severity="error" sx={{ mt: 2 }}>{review.error instanceof Error ? review.error.message : 'The review action failed. Try again or cancel.'}</Alert>}
      </DialogContent>
      <DialogActions>
        <Button onClick={() => setPendingReview(null)} disabled={review.isPending}>Cancel</Button>
        <Button onClick={() => void submit()} variant="contained" color={pendingReview === 'REJECTED' ? 'error' : pendingReview === 'REUPLOAD_REQUESTED' ? 'warning' : 'primary'} disabled={review.isPending || !pendingReview || !comment.trim()}>
          {review.isPending ? 'Saving…' : pendingReview ? decisionLabels[pendingReview] : 'Confirm'}
        </Button>
      </DialogActions>
    </Dialog>
    <Snackbar open={Boolean(message)} autoHideDuration={3500} onClose={() => setMessage('')} message={message} />
  </Stack>;
}

function ContextValue({ label, value, loading = false }: { label: string; value?: string | null; loading?: boolean }) {
  return <Stack spacing={.25} minWidth={0}>
    <Typography variant="caption" color="text.secondary">{label}</Typography>
    {loading ? <Skeleton width="70%" /> : <Typography variant="body2" sx={{ overflowWrap: 'anywhere' }}>{value || 'Not provided'}</Typography>}
  </Stack>;
}

function canReview(status?: string) {
  return reviewableStatuses.includes(status ?? 'PENDING');
}

function statusColor(status?: string): 'success' | 'warning' | 'error' | 'info' | 'default' {
  if (status === 'APPROVED') return 'success';
  if (status === 'REJECTED') return 'error';
  if (status === 'REUPLOAD_REQUESTED') return 'info';
  if (status === 'PENDING') return 'warning';
  return 'default';
}
