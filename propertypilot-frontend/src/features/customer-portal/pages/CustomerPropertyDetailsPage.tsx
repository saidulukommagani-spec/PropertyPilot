import { useMemo, useState } from 'react';
import { useMutation, useQueries, useQuery, useQueryClient } from '@tanstack/react-query';
import { Alert, Box, Button, Card, CardContent, Chip, Dialog, DialogActions, DialogContent, DialogTitle, Skeleton, Stack, Typography } from '@mui/material';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { propertiesApi } from '../../../api/properties';
import CustomerStatusChip, { formatCustomerStatus } from '../components/CustomerStatusChip';
import { CustomerPageHeading, CustomerQueryError } from '../components/CustomerStates';
import { customerKeys, useCustomerProperty, useCustomerRequests, useCustomerVisits } from '../hooks/useCustomerPortal';
import { CustomerPortalApi } from '../services/CustomerPortalApi';

export default function CustomerPropertyDetailsPage() {
  const { id = '' } = useParams();
  const property = useCustomerProperty(id);
  const requestsQuery = useCustomerRequests();
  const visitsQuery = useCustomerVisits();
  const navigate = useNavigate();
  const client = useQueryClient();
  const [archiveOpen, setArchiveOpen] = useState(false);
  const archive = useMutation({
    mutationFn: () => propertiesApi.archive(id),
    onSuccess: async () => {
      await Promise.all([client.invalidateQueries({ queryKey: customerKeys.properties }), client.invalidateQueries({ queryKey: customerKeys.property(id) })]);
      navigate('/customer/properties');
    },
  });
  const docs = useQuery({ queryKey: [...customerKeys.documents, id], queryFn: () => CustomerPortalApi.documents(id), enabled: Boolean(id) });
  const requests = useMemo(() => (requestsQuery.data ?? []).filter((request) => request.propertyId === id), [id, requestsQuery.data]);
  const evidenceQueries = useQueries({ queries: requests.map((request) => ({ queryKey: customerKeys.evidenceForRequest(request.serviceRequestId), queryFn: () => CustomerPortalApi.requestEvidence(request.serviceRequestId) })) });

  if (property.isLoading) return <Stack spacing={2} aria-label="Loading property"><Skeleton height={45} /><Skeleton variant="rounded" height={180} /><Skeleton variant="rounded" height={240} /></Stack>;
  if (property.isError || !property.data) return <Stack spacing={2}><Button component={Link} to="/customer/properties">Back to properties</Button><CustomerQueryError error={property.error} retry={() => void property.refetch()} /></Stack>;

  const item = property.data;
  const visits = (visitsQuery.data ?? []).filter((visit) => requests.some((request) => request.serviceRequestId === visit.serviceRequestId));
  const evidence = evidenceQueries.flatMap((query) => query.data ?? []);
  const evidenceFailed = evidenceQueries.some((query) => query.isError);
  const retryEvidence = () => { void Promise.all(evidenceQueries.map((query) => query.refetch())); };
  const requestLink = `/customer/service-requests/new?propertyId=${encodeURIComponent(id)}`;
  const serviceRequests = [...requests].sort((a, b) => Date.parse(b.requestedAt) - Date.parse(a.requestedAt));
  const propertyAddress = [
    item.addressLine1, item.addressLine2, item.localityName, item.city,
    item.district ? `${item.district} District` : null, item.state, item.postalCode,
    item.countryCode === 'IN' ? 'India' : item.countryCode,
  ].map((part) => part?.trim()).filter((part): part is string => Boolean(part)).join(', ');

  return <Stack spacing={3}>
    <CustomerPageHeading title={item.title} subtitle={[item.propertyType, item.status ? formatCustomerStatus(item.status) : 'Active'].filter(Boolean).join(' · ')} action={<Stack direction={{ xs: 'column', sm: 'row' }} spacing={1}>
      <Button component={Link} to="/customer/properties">Back to properties</Button>
      <Button component={Link} to={`/customer/properties/${id}/edit`} variant="outlined" disabled={item.status === 'ARCHIVED'}>Edit property</Button>
      <Button color="error" variant="outlined" onClick={() => setArchiveOpen(true)} disabled={item.status === 'ARCHIVED' || archive.isPending}>Archive</Button>
    </Stack>} />

    {archive.isError && <Alert severity="error">{archive.error instanceof Error ? archive.error.message : 'Property could not be archived.'}</Alert>}
    <Card variant="outlined"><CardContent>
      <Typography variant="h6" fontWeight={700}>Property overview</Typography>
      <Box display="grid" gridTemplateColumns={{ xs: '1fr', sm: 'repeat(2,minmax(0,1fr))' }} gap={2} sx={{ mt: 2 }}>
        <PropertyFact label="Property name" value={item.title} />
        <PropertyFact label="Property type" value={item.propertyType ? formatCustomerStatus(item.propertyType) : 'Not provided'} />
        <Box>
          <PropertyFact label="Property address" value={propertyAddress || 'No property address on file'} />
          {!propertyAddress && item.status !== 'ARCHIVED' && <Button component={Link} to={`/customer/properties/${id}/edit`} size="small" sx={{ mt: .5, px: 0 }}>Add property address</Button>}
        </Box>
        {item.listingStatus && <PropertyFact label="Listing status" value={formatCustomerStatus(item.listingStatus)} />}
        <PropertyFact label="Account status" value={formatCustomerStatus(item.status || 'ACTIVE')} />
      </Box>
      <Button component={Link} to={requestLink} variant="contained" disabled={item.status === 'ARCHIVED'} sx={{ mt: 2 }}>Request a service for this property</Button>
    </CardContent></Card>

    <Card variant="outlined"><CardContent>
      <Typography variant="h6" fontWeight={700}>Service requests</Typography>
      {requestsQuery.isLoading ? <Stack spacing={1.5} sx={{ mt: 2 }}><Skeleton height={52} /><Skeleton height={52} /></Stack>
        : requestsQuery.isError ? <CustomerQueryError error={requestsQuery.error} retry={() => void requestsQuery.refetch()} />
          : serviceRequests.length > 0 ? <Stack spacing={1.5} sx={{ mt: 2 }}>{serviceRequests.map((request) => <Box key={request.serviceRequestId} display="flex" flexWrap="wrap" alignItems="center" justifyContent="space-between" gap={1}>
            <Box><Typography fontWeight={650}>{request.serviceName || request.requestType}</Typography><Typography variant="body2" color="text.secondary">Requested {dateTime(request.requestedAt)}</Typography></Box>
            <Stack direction="row" alignItems="center" spacing={1}><CustomerStatusChip status={request.status} /><Button component={Link} to={`/customer/service-requests/${request.serviceRequestId}`} size="small">View request</Button></Stack>
          </Box>)}</Stack>
            : <Typography color="text.secondary" sx={{ mt: 1 }}>No service requests have been raised for this property yet.</Typography>}
    </CardContent></Card>

    {visitsQuery.isError && <CustomerQueryError error={visitsQuery.error} retry={() => void visitsQuery.refetch()} />}
    {visitsQuery.isLoading ? <Card variant="outlined"><CardContent><Skeleton height={100} /></CardContent></Card> : visits.length > 0 && <Card variant="outlined"><CardContent>
      <Typography variant="h6" fontWeight={700}>Visit history</Typography>
      <Stack spacing={1.5} sx={{ mt: 2 }}>{[...visits].sort((a, b) => Date.parse(b.scheduledAt) - Date.parse(a.scheduledAt)).map((visit) => <Box key={visit.visitId} display="flex" flexWrap="wrap" alignItems="center" justifyContent="space-between" gap={1}>
        <Box><Typography fontWeight={650}>{visit.serviceName || 'Service visit'}</Typography><Typography variant="body2" color="text.secondary">{dateTime(visit.scheduledAt)}</Typography></Box>
        <Stack direction="row" spacing={1} alignItems="center"><CustomerStatusChip status={visit.status} /><Button component={Link} to={`/customer/visits/${visit.visitId}`} size="small">View visit</Button></Stack>
      </Box>)}</Stack>
    </CardContent></Card>}

    {docs.isError && <CustomerQueryError error={docs.error} retry={() => void docs.refetch()} />}
    {!docs.isLoading && (docs.data?.length ?? 0) > 0 && <Card variant="outlined"><CardContent>
      <Typography variant="h6" fontWeight={700}>Property documents</Typography>
      <Stack spacing={1} sx={{ mt: 1.5 }}>{docs.data?.slice(0, 4).map((document) => <Box key={document.documentId} display="flex" flexWrap="wrap" justifyContent="space-between" gap={1}>
        <Typography>{document.documentName}</Typography><Chip size="small" label={formatCustomerStatus(document.status)} />
      </Box>)}</Stack>
      <Button component={Link} to="/customer/documents" sx={{ mt: 1 }}>View all documents</Button>
    </CardContent></Card>}

    {evidenceFailed && <Alert severity="error" action={<Button color="inherit" size="small" onClick={retryEvidence}>Retry</Button>}>Some evidence records could not be loaded.</Alert>}
    {!evidenceQueries.some((query) => query.isLoading) && !evidenceFailed && evidence.length > 0 && <Card variant="outlined"><CardContent>
      <Typography variant="h6" fontWeight={700}>Service evidence</Typography>
      <Typography color="text.secondary" variant="body2" sx={{ mt: .5 }}>Evidence status reflects the review state returned by PropertyPilot.</Typography>
      <Stack spacing={1} sx={{ mt: 1.5 }}>{evidence.slice(0, 5).map((entry) => <Box key={entry.evidenceId} display="flex" flexWrap="wrap" justifyContent="space-between" gap={1}>
        <Box><Typography>{entry.evidenceTypeName}</Typography><Typography variant="body2" color="text.secondary">{entry.fileName || 'Uploaded file'}{entry.capturedAt ? ` · ${dateTime(entry.capturedAt)}` : ''}</Typography></Box>
        {entry.validationStatus && <CustomerStatusChip status={entry.validationStatus} />}
      </Box>)}</Stack>
      <Button component={Link} to="/customer/evidence" sx={{ mt: 1 }}>View all evidence</Button>
    </CardContent></Card>}

    <Dialog open={archiveOpen} onClose={() => { if (!archive.isPending) setArchiveOpen(false); }} aria-labelledby="archive-property-title">
      <DialogTitle id="archive-property-title">Archive this property?</DialogTitle>
      <DialogContent>It will be removed from your active property list. Existing service history will remain available.</DialogContent>
      <DialogActions><Button onClick={() => setArchiveOpen(false)} disabled={archive.isPending}>Keep property</Button><Button color="error" variant="contained" disabled={archive.isPending} onClick={() => archive.mutate()}>{archive.isPending ? 'Archiving…' : 'Archive property'}</Button></DialogActions>
    </Dialog>
  </Stack>;
}

function PropertyFact({ label, value }: { label: string; value: string }) {
  return <Box><Typography variant="body2" color="text.secondary">{label}</Typography><Typography fontWeight={650}>{value}</Typography></Box>;
}

function dateTime(value: string): string {
  const date = new Date(value);
  return Number.isNaN(date.getTime()) ? 'Date unavailable' : date.toLocaleString();
}
