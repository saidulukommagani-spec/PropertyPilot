import { Alert, Card, CardContent, Step, StepLabel, Stepper, Typography } from '@mui/material';

const steps = ['Request received', 'Assigned', 'Service in progress', 'Request completed'];

function activeStep(status: string): number {
  switch (status.toUpperCase()) {
    case 'ASSIGNED':
    case 'ACCEPTED':
      return 1;
    case 'IN_PROGRESS':
      return 2;
    case 'COMPLETED':
      return 3;
    default:
      return 0;
  }
}

function nextStepMessage(status: string): string {
  switch (status.toUpperCase()) {
    case 'NEW':
    case 'PENDING_ASSIGNMENT':
      return 'Your request is received. PropertyPilot is arranging service delivery; a visit will appear here if this service requires one.';
    case 'ASSIGNED':
    case 'ACCEPTED':
      return 'Your request is assigned. We will show visit details here when a visit is scheduled.';
    case 'IN_PROGRESS':
      return 'Service work is underway. A completed visit does not by itself complete this service request.';
    case 'COMPLETED':
      return 'This service request is complete. Review any evidence that has been shared below.';
    default:
      return 'Check the latest status and timeline below for updates to your request.';
  }
}

export default function CustomerRequestProgress({ status }: { status: string }) {
  const normalized = status.toUpperCase();

  if (normalized === 'CANCELLED' || normalized === 'FAILED' || normalized === 'REJECTED') {
    return <Alert severity={normalized === 'CANCELLED' ? 'info' : 'warning'}>
      <Typography fontWeight={650}>{normalized === 'CANCELLED' ? 'This request was cancelled.' : 'This request needs attention.'}</Typography>
      {nextStepMessage(status)}
    </Alert>;
  }

  const current = activeStep(status);
  return <Card variant="outlined" component="section" aria-label="Service request progress">
    <CardContent>
      <Typography variant="h6" fontWeight={700}>Service request progress</Typography>
      <Stepper activeStep={current} orientation="vertical" sx={{ mt: 2 }}>
        {steps.map((step, index) => <Step key={step} completed={index < current || normalized === 'COMPLETED'}>
          <StepLabel>{step}</StepLabel>
        </Step>)}
      </Stepper>
      <Typography variant="body2" color="text.secondary" role="status">{nextStepMessage(status)}</Typography>
    </CardContent>
  </Card>;
}
