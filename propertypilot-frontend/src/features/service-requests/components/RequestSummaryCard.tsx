import { Card, CardContent, Divider, Stack, Typography } from '@mui/material';
import type { ServiceRequestDetailsResponse } from '../types/serviceRequest';

const dateTime = (value?: string) => value ? new Intl.DateTimeFormat(undefined, { dateStyle: 'medium', timeStyle: 'short' }).format(new Date(value)) : '—';
const slaLabel = (status?: string) => status === 'ON_TRACK' ? 'On track' : status === 'BREACHED' ? 'Breached' : status === 'MET' ? 'Met' : 'No active policy';

export default function RequestSummaryCard({ request }: { request: ServiceRequestDetailsResponse }) {
  const cells = [
    ['Customer', request.customerName],
    ['Property', request.propertyTitle],
    ['Service', request.serviceName || request.requestType],
    ['Requested on', dateTime(request.requestedAt)],
    ['Target date', dateTime(request.scheduledAt)],
    ['SLA status', slaLabel(request.slaStatus)],
    ['SLA due', request.slaDueAt ? dateTime(request.slaDueAt) : '—'],
  ];

  return <Card component="section" aria-labelledby="request-summary-title" variant="outlined">
    <CardContent sx={{ p: { xs: 2, sm: 2.5, md: 3 } }}>
      <Typography id="request-summary-title" variant="h6" fontWeight={700}>Request details</Typography>
      <Divider sx={{ my: 2 }} />
      <Stack display="grid" gridTemplateColumns={{ xs: '1fr', sm: 'repeat(2, minmax(0, 1fr))', xl: 'repeat(3, minmax(0, 1fr))' }} gap={{ xs: 1.5, sm: 2 }}>
        {cells.map(([label, value]) => <Stack key={label} spacing={0.5}>
          <Typography variant="caption" color="text.secondary">{label}</Typography>
          <Typography variant="body2" fontWeight={650} sx={{ overflowWrap: 'anywhere' }}>{value || '—'}</Typography>
        </Stack>)}
      </Stack>
      <Stack spacing={0.75} sx={{ mt: 2.5, p: { xs: 1.5, sm: 2 }, borderRadius: 1.5, bgcolor: 'background.default' }}>
        <Typography variant="overline" color="text.secondary" lineHeight={1.3}>Description</Typography>
        <Typography variant="body2" sx={{ whiteSpace: 'pre-wrap', overflowWrap: 'anywhere' }}>{request.description || 'No description provided.'}</Typography>
      </Stack>
    </CardContent>
  </Card>;
}
