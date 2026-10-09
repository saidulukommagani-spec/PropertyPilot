import { Alert, Card, CardContent, Step, StepLabel, Stepper, Typography, useMediaQuery, useTheme } from '@mui/material';

const stages = ['Request received', 'Agent assignment', 'Service work', 'Completed'];

function activeStage(status: string) {
  if (status === 'NEW') return 0;
  if (status === 'PENDING_ASSIGNMENT' || status === 'ASSIGNED') return 1;
  if (status === 'ACCEPTED' || status === 'IN_PROGRESS') return 2;
  if (status === 'COMPLETED') return 3;
  return 0;
}

function progressDescription(status: string) {
  if (status === 'NEW') return 'Your request is received and ready for assignment.';
  if (status === 'PENDING_ASSIGNMENT') return 'Operations is finding an agent for this request.';
  if (status === 'ASSIGNED') return 'An agent is assigned and has not accepted the work yet.';
  if (status === 'ACCEPTED') return 'The assigned agent accepted this request.';
  if (status === 'IN_PROGRESS') return 'Service work is in progress.';
  if (status === 'COMPLETED') return 'Service work is complete.';
  return `Current request status: ${status.replaceAll('_', ' ').toLowerCase()}.`;
}

export default function RequestProgress({ status }: { status: string }) {
  const theme = useTheme();
  const compact = useMediaQuery(theme.breakpoints.down('sm'));

  if (status === 'CANCELLED' || status === 'FAILED') {
    return <Alert severity={status === 'CANCELLED' ? 'warning' : 'error'} role="status">
      <Typography fontWeight={700}>{status === 'CANCELLED' ? 'Request cancelled' : 'Request failed'}</Typography>
      <Typography variant="body2">{progressDescription(status)}</Typography>
    </Alert>;
  }

  const current = activeStage(status);
  const isCompleted = status === 'COMPLETED';

  return <Card component="section" aria-label="Service request progress" variant="outlined">
    <CardContent sx={{ p: { xs: 2, sm: 2.5 }, '&:last-child': { pb: { xs: 2, sm: 2.5 } } }}>
      <Typography variant="subtitle1" fontWeight={700}>Request progress</Typography>
      <Stepper activeStep={current} orientation={compact ? 'vertical' : 'horizontal'} alternativeLabel={!compact} sx={{ mt: 2, mb: compact ? 1 : 0.75 }}>
        {stages.map((label, index) => <Step key={label} completed={isCompleted || index < current}>
          <StepLabel>{label}</StepLabel>
        </Step>)}
      </Stepper>
      <Typography variant="body2" color="text.secondary">{progressDescription(status)}</Typography>
      <Typography variant="caption" color="text.secondary" sx={{ display: 'block', mt: 0.5 }}>
        Visits and evidence are shown below when recorded. Some services do not require an on-site visit.
      </Typography>
    </CardContent>
  </Card>;
}
