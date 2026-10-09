import { Alert, Card, CardContent, Chip, Divider, Stack, Step, StepLabel, Stepper, Typography, useMediaQuery, useTheme } from '@mui/material';
import type { VisitDetails } from '../types/visit';
const value = (text?: string) => text || 'Not available';
const visitSteps = ['Scheduled', 'In progress', 'Visit completed'];
const visitStatusDate = (value?: string) => value ? new Intl.DateTimeFormat(undefined, { dateStyle: 'medium', timeStyle: 'short' }).format(new Date(value)) : 'Not recorded';

export default function VisitSummaryCard({ visit }: { visit: VisitDetails }) {
  const theme = useTheme();
  const compact = useMediaQuery(theme.breakpoints.down('sm'));
  const terminal = visit.status === 'CANCELLED' || visit.status === 'FAILED';
  const activeStep = visit.status === 'IN_PROGRESS' ? 1 : visit.status === 'COMPLETED' ? 2 : 0;
  return <Card component="section" aria-labelledby="visit-summary-title"><CardContent sx={{ p: { xs: 2.5, md: 3 } }}>
    <Stack direction={{ xs: 'column', sm: 'row' }} justifyContent="space-between" alignItems={{ sm: 'center' }} gap={1}>
      <Typography id="visit-summary-title" variant="h6" fontWeight={700}>Visit Information</Typography>
      <Chip label={visit.status.replaceAll('_', ' ')} color={visit.status === 'COMPLETED' ? 'success' : visit.status === 'IN_PROGRESS' ? 'warning' : 'default'} />
    </Stack><Divider sx={{ my: 2 }} />
    {terminal ? <Alert severity={visit.status === 'CANCELLED' ? 'warning' : 'error'} role="status">Visit {visit.status === 'CANCELLED' ? 'cancelled' : 'failed'}{visit.cancellationReason ? `: ${visit.cancellationReason}` : '.'}</Alert> :
      <Stepper activeStep={activeStep} orientation={compact ? 'vertical' : 'horizontal'} alternativeLabel={!compact} sx={{ mb: 2, '& .MuiStepLabel-label': { fontSize: { xs: '0.8rem', sm: '0.875rem' } } }} aria-label="Visit progress">
        {visitSteps.map((label, index) => <Step key={label} completed={visit.status === 'COMPLETED' || index < activeStep}><StepLabel>{label}</StepLabel></Step>)}
      </Stepper>}
    <Stack display="grid" gridTemplateColumns={{ xs: '1fr', sm: 'repeat(2, minmax(0, 1fr))', lg: 'repeat(3, minmax(0, 1fr))' }} gap={{ xs: 1.5, sm: 2 }}>
      <Stack><Typography variant="caption" color="text.secondary">Visit Number</Typography><Typography fontWeight={650}>{visit.visitNumber}</Typography></Stack>
      <Stack><Typography variant="caption" color="text.secondary">Scheduled Visit</Typography><Typography fontWeight={650}>{visitStatusDate(visit.scheduledAt)}</Typography></Stack>
      <Stack><Typography variant="caption" color="text.secondary">Customer</Typography><Typography fontWeight={650} sx={{ overflowWrap: 'anywhere' }}>{value(visit.request?.customerName || visit.customerName)}</Typography></Stack>
      <Stack><Typography variant="caption" color="text.secondary">Property</Typography><Typography fontWeight={650} sx={{ overflowWrap: 'anywhere' }}>{value(visit.request?.propertyTitle || visit.propertyName)}</Typography></Stack>
      <Stack><Typography variant="caption" color="text.secondary">Service</Typography><Typography fontWeight={650} sx={{ overflowWrap: 'anywhere' }}>{value(visit.request?.serviceName || visit.request?.requestType || visit.serviceName)}</Typography></Stack>
      <Stack><Typography variant="caption" color="text.secondary">Assigned Agent</Typography><Typography fontWeight={650}>{value(visit.agentName)}</Typography></Stack>
    </Stack>
    {visit.notes && <Stack spacing={0.5} sx={{ mt: 2, p: 1.5, bgcolor: 'background.default', borderRadius: 1.5 }}><Typography variant="caption" color="text.secondary">Visit notes</Typography><Typography sx={{ whiteSpace: 'pre-wrap', overflowWrap: 'anywhere' }}>{visit.notes}</Typography></Stack>}
  </CardContent></Card>;
}
