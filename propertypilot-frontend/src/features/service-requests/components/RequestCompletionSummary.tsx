import { Alert, Box, Button, Card, CardContent, Chip, Divider, Stack, Typography } from '@mui/material';
import { Link } from 'react-router-dom';
import type { EvidenceResponse, VisitResponse } from '../types/serviceRequest';

interface Props {
  status: string;
  visits: VisitResponse[];
  evidence: EvidenceResponse[];
  canReviewEvidence?: boolean;
}

export default function RequestCompletionSummary({ status, visits, evidence, canReviewEvidence = false }: Props) {
  const completedVisits = visits.filter((visit) => visit.status === 'COMPLETED').length;
  const cancelledVisits = visits.filter((visit) => visit.status === 'CANCELLED').length;
  const openVisits = visits.length - completedVisits - cancelledVisits;
  const approvedEvidence = evidence.filter((item) => item.validationStatus === 'APPROVED').length;
  const awaitingReview = evidence.filter((item) => item.validationStatus === 'PENDING').length;
  const returnedEvidence = evidence.filter((item) => ['REJECTED', 'REUPLOAD_REQUESTED'].includes(item.validationStatus ?? '')).length;
  const reviewableEvidence = evidence.find((item) => ['PENDING', 'REJECTED', 'REUPLOAD_REQUESTED'].includes(item.validationStatus ?? ''));

  return <Card component="section" aria-labelledby="request-completion-title" variant="outlined">
    <CardContent sx={{ p: { xs: 2.5, md: 3 } }}>
      <Stack direction={{ xs: 'column', sm: 'row' }} justifyContent="space-between" alignItems={{ sm: 'center' }} gap={1}>
        <Box><Typography id="request-completion-title" variant="h6" fontWeight={700}>Request completion</Typography><Typography variant="body2" color="text.secondary" sx={{ mt: .5 }}>Execution and evidence facts for this request</Typography></Box>
        <Chip size="small" label={status.replaceAll('_', ' ')} color={status === 'COMPLETED' ? 'success' : status === 'CANCELLED' || status === 'FAILED' ? 'error' : 'default'} />
      </Stack>
      <Divider sx={{ my: 2 }} />
      <Box display="grid" gridTemplateColumns={{ xs: 'repeat(2,minmax(0,1fr))', sm: 'repeat(3,minmax(0,1fr))' }} gap={1.5}>
        <Fact label="Visits completed" value={completedVisits} />
        <Fact label="Visits cancelled" value={cancelledVisits} />
        <Fact label="Visits still open" value={openVisits} />
        <Fact label="Evidence approved" value={approvedEvidence} />
        <Fact label="Evidence awaiting review" value={awaitingReview} />
        <Fact label="Evidence returned" value={returnedEvidence} />
      </Box>
      {status === 'IN_PROGRESS' && <Alert severity="info" sx={{ mt: 2 }}>
        Completing a visit does not by itself complete this request. The backend checks configured visit, approved-evidence, and GPS verification requirements when completion is requested. Approval of evidence may complete the request automatically only after those backend checks pass; the screen does not make that decision.
      </Alert>}
      {canReviewEvidence && reviewableEvidence && <Button component={Link} to={`/evidence/validation?evidenceId=${encodeURIComponent(reviewableEvidence.evidenceId)}`} variant="outlined" sx={{ mt: 2 }}>Review submitted evidence</Button>}
    </CardContent>
  </Card>;
}

function Fact({ label, value }: { label: string; value: number }) {
  return <Stack spacing={.25} sx={{ p: 1.5, borderRadius: 1.5, bgcolor: 'background.default' }}>
    <Typography variant="caption" color="text.secondary">{label}</Typography>
    <Typography variant="h5" fontWeight={750}>{value}</Typography>
  </Stack>;
}
