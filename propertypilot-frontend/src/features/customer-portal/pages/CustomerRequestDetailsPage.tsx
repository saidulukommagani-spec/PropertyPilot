import { Box, Button, Card, CardContent, Divider, Skeleton, Stack, Typography } from '@mui/material';
import { Link, useParams } from 'react-router-dom';
import { CustomerPageHeading, CustomerQueryError } from '../components/CustomerStates';
import CustomerRequestProgress from '../components/CustomerRequestProgress';
import CustomerStatusChip, { formatCustomerStatus } from '../components/CustomerStatusChip';
import { useCustomerEvidenceForRequest, useCustomerRequest, useCustomerVisits } from '../hooks/useCustomerPortal';

export default function CustomerRequestDetailsPage() {
  const { id = '' } = useParams();
  const request = useCustomerRequest(id);
  const visits = useCustomerVisits();
  const evidence = useCustomerEvidenceForRequest(id);

  if (request.isLoading) return <Stack spacing={2} aria-label="Loading service request"><Skeleton height={44} /><Skeleton variant="rounded" height={240} /><Skeleton variant="rounded" height={200} /></Stack>;
  if (request.isError || !request.data) return <Stack spacing={2}><Button component={Link} to="/customer/service-requests">Back to requests</Button><CustomerQueryError error={request.error} retry={() => void request.refetch()} /></Stack>;

  const item = request.data;
  const requestVisits = (visits.data ?? []).filter((visit) => visit.serviceRequestId === id).sort((a, b) => Date.parse(a.scheduledAt) - Date.parse(b.scheduledAt));
  const history = item.statusHistory?.length
    ? [...item.statusHistory].sort((a, b) => Date.parse(a.changedAt) - Date.parse(b.changedAt))
    : [{ newStatus: 'NEW', changedAt: item.requestedAt }];

  return <Stack spacing={3}>
    <Button component={Link} to="/customer/service-requests" sx={{ alignSelf: 'flex-start' }}>Back to service requests</Button>
    <CustomerPageHeading title={item.serviceName || item.requestType} subtitle={`${item.propertyTitle} · Requested ${dateTime(item.requestedAt)}`} action={<CustomerStatusChip status={item.status} />} />

    <CustomerRequestProgress status={item.status} />

    <Card variant="outlined"><CardContent>
      <Typography variant="h6" fontWeight={700}>Request details</Typography>
      <Divider sx={{ my: 2 }} />
      <Box display="grid" gridTemplateColumns={{ xs: '1fr', sm: 'repeat(2,minmax(0,1fr))' }} gap={2}>
        <Detail label="Service" value={item.serviceName || item.requestType} />
        <Detail label="Property" value={item.propertyTitle} />
        <Detail label="Priority" value={formatCustomerStatus(item.priority)} />
        <Detail label="Submitted" value={dateTime(item.requestedAt)} />
        {item.slaStatus && <Detail label="Service target" value={formatCustomerStatus(item.slaStatus)} />}
        {item.slaDueAt && <Detail label="Target date" value={dateTime(item.slaDueAt)} />}
      </Box>
      {item.description && <Box sx={{ mt: 2, p: 2, bgcolor: 'action.hover', borderRadius: 1.5 }}><Typography variant="overline" color="text.secondary">Your request</Typography><Typography sx={{ whiteSpace: 'pre-wrap' }}>{item.description}</Typography></Box>}
      <Button component={Link} to={`/customer/properties/${item.propertyId}`} sx={{ mt: 1.5 }}>View property</Button>
    </CardContent></Card>

    <Card variant="outlined"><CardContent>
      <Typography variant="h6" fontWeight={700}>Request timeline</Typography>
      <Stack spacing={1.75} sx={{ mt: 2 }}>
        {history.map((event, index) => <Box key={`${event.changedAt}-${event.newStatus}-${index}`} display="grid" gridTemplateColumns="16px minmax(0,1fr)" columnGap={1.25}>
          <Box aria-hidden="true" sx={{ width: 10, height: 10, borderRadius: '50%', bgcolor: index === history.length - 1 ? 'primary.main' : 'action.disabled', mt: .65 }} />
          <Box><Typography fontWeight={650}>{formatCustomerStatus(event.newStatus)}</Typography><Typography variant="body2" color="text.secondary">{dateTime(event.changedAt)}{event.changeReason ? ` · ${event.changeReason}` : ''}</Typography></Box>
        </Box>)}
      </Stack>
    </CardContent></Card>

    <Card variant="outlined"><CardContent>
      <Typography variant="h6" fontWeight={700}>Visits</Typography>
      <Typography variant="body2" color="text.secondary" sx={{ mt: .5 }}>A visit has its own status and does not by itself complete this service request.</Typography>
      {visits.isError ? <CustomerQueryError error={visits.error} retry={() => void visits.refetch()} />
        : visits.isLoading ? <Stack spacing={1} sx={{ mt: 2 }}><Skeleton height={56} /><Skeleton height={56} /></Stack>
          : requestVisits.length ? <Stack spacing={1.5} sx={{ mt: 2 }}>{requestVisits.map((visit) => <Box key={visit.visitId} display="flex" flexWrap="wrap" alignItems="center" justifyContent="space-between" gap={1}>
            <Box><Typography fontWeight={650}>{dateTime(visit.scheduledAt)}</Typography><Typography variant="body2" color="text.secondary">{visit.serviceName || item.serviceName || item.requestType}{visit.agentName ? ` · ${visit.agentName}` : ''}</Typography></Box>
            <Stack direction="row" alignItems="center" spacing={1}><CustomerStatusChip status={visit.status} /><Button component={Link} to={`/customer/visits/${visit.visitId}`} size="small">View visit</Button></Stack>
          </Box>)}</Stack>
            : <Typography color="text.secondary" sx={{ mt: 1.5 }}>No visit is scheduled yet. Some services do not require an on-site visit.</Typography>}
    </CardContent></Card>

    <Card variant="outlined"><CardContent>
      <Stack direction={{ xs: 'column', sm: 'row' }} justifyContent="space-between" alignItems={{ sm: 'center' }} gap={1}>
        <Box><Typography variant="h6" fontWeight={700}>Shared evidence</Typography><Typography variant="body2" color="text.secondary">Review state is shown as returned by PropertyPilot.</Typography></Box>
        <Button component={Link} to="/customer/evidence">View evidence</Button>
      </Stack>
      {evidence.isError ? <CustomerQueryError error={evidence.error} retry={() => void evidence.refetch()} />
        : evidence.isLoading ? <Skeleton height={70} sx={{ mt: 1 }} />
          : evidence.data?.length ? <Stack spacing={1.5} sx={{ mt: 2 }}>{evidence.data.map((entry) => <Box key={entry.evidenceId} display="flex" flexWrap="wrap" justifyContent="space-between" alignItems="center" gap={1}>
            <Box><Typography fontWeight={650}>{entry.evidenceTypeName}</Typography><Typography variant="body2" color="text.secondary">{entry.fileName || 'Uploaded file'}{entry.capturedAt ? ` · ${dateTime(entry.capturedAt)}` : ''}</Typography></Box>
            {entry.validationStatus && <CustomerStatusChip status={entry.validationStatus} />}
          </Box>)}</Stack>
            : <Typography color="text.secondary" sx={{ mt: 1.5 }}>Evidence will be shown here when shared. Uploading evidence does not mean it has been approved.</Typography>}
    </CardContent></Card>
  </Stack>;
}

function Detail({ label, value }: { label: string; value: string }) {
  return <Box><Typography variant="body2" color="text.secondary">{label}</Typography><Typography fontWeight={650}>{value}</Typography></Box>;
}

function dateTime(value: string): string {
  const date = new Date(value);
  return Number.isNaN(date.getTime()) ? 'Date unavailable' : date.toLocaleString();
}
