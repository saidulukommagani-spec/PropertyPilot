import { useState } from 'react';
import { Alert, Box, Button, Card, CardContent, Chip, LinearProgress, Skeleton, Stack, Typography } from '@mui/material';
import GpsFixedRounded from '@mui/icons-material/GpsFixedRounded';
import { useEvidenceTypes, useVisitEvidenceSummary } from '../../evidence/hooks/useEvidence';
import { useGpsVerification } from '../hooks/useVisits';

interface Props {
  visitId: string;
  visitStatus: string;
}

export default function VisitExecutionEvidenceCard({ visitId, visitStatus }: Props) {
  const summary = useVisitEvidenceSummary(visitId);
  const types = useEvidenceTypes();
  const gps = useGpsVerification();
  const [locationError, setLocationError] = useState('');
  const [locationPending, setLocationPending] = useState(false);
  const data = summary.data;
  const requiredGps = Boolean(data?.requiredEvidenceTypes.includes('GPS'));
  const getTypeName = (code: string) => types.data?.find((type) => type.code === code)?.name ?? code.replaceAll('_', ' ').toLowerCase().replace(/\b\w/g, (letter) => letter.toUpperCase());

  const captureGps = () => {
    setLocationError('');
    if (!navigator.geolocation) {
      setLocationError('Location services are not available in this browser.');
      return;
    }
    setLocationPending(true);
    navigator.geolocation.getCurrentPosition(
      (position) => {
        void gps.mutateAsync({ visitId, latitude: position.coords.latitude, longitude: position.coords.longitude })
          .catch((error: unknown) => setLocationError(error instanceof Error ? error.message : 'The location check could not be verified.'))
          .finally(() => setLocationPending(false));
      },
      (error) => {
        const messages: Record<number, string> = {
          1: 'Location permission was denied. Allow location access and try again.',
          2: 'Your device could not determine a location. Move to an area with a location signal and retry.',
          3: 'Location capture timed out. Try again when your device has a stronger signal.',
        };
        setLocationError(messages[error.code] ?? 'Location could not be captured. Try again.');
        setLocationPending(false);
      },
      { enableHighAccuracy: true, timeout: 10_000, maximumAge: 0 },
    );
  };

  return <Card component="section" aria-labelledby="visit-evidence-checklist-title">
    <CardContent sx={{ p: { xs: 2, sm: 2.5, md: 3 } }}>
      <Stack spacing={2}>
        <Box>
          <Typography id="visit-evidence-checklist-title" variant="h6" fontWeight={700}>Evidence checklist</Typography>
          <Typography variant="body2" color="text.secondary" sx={{ mt: 0.5 }}>
            Uploading evidence does not approve it. Required evidence counts after Operations review.
          </Typography>
        </Box>
        {summary.isLoading ? <Stack spacing={1}><Skeleton height={32} /><Skeleton height={72} /><Skeleton height={72} /></Stack> : summary.isError || !data ?
          <Alert severity="error" action={<Button color="inherit" size="small" onClick={() => void summary.refetch()}>Retry</Button>}>
            {summary.error instanceof Error ? summary.error.message : 'Evidence requirements could not be loaded.'}
          </Alert> : <>
            <Stack direction={{ xs: 'column', sm: 'row' }} justifyContent="space-between" alignItems={{ sm: 'center' }} gap={0.75}>
              <Typography variant="body2" color="text.secondary">Approved evidence</Typography>
              <Typography variant="subtitle1" fontWeight={700}>{data.completionPercentage}%</Typography>
            </Stack>
            <LinearProgress variant="determinate" value={data.completionPercentage} aria-label="Approved required evidence progress" aria-valuetext={`${data.completionPercentage}% of required evidence approved`} sx={{ height: 8, borderRadius: 4, mt: -1.5 }} />
            {data.requiredEvidenceTypes.length === 0 ? <Alert severity="info">No evidence requirements are configured for this visit.</Alert> :
              <Stack component="ul" spacing={1} sx={{ listStyle: 'none', p: 0, m: 0 }} aria-label="Required evidence status">
                {data.requiredEvidenceTypes.map((code) => {
                  const records = data.uploadedEvidence.filter((item) => item.evidenceTypeCode === code);
                  const approved = records.some((item) => item.validationStatus === 'APPROVED');
                  const rejected = records.some((item) => item.validationStatus === 'REJECTED' || item.validationStatus === 'REUPLOAD_REQUESTED');
                  const pending = records.some((item) => item.validationStatus === 'PENDING');
                  const label = approved ? 'Approved' : pending ? 'Uploaded · awaiting review' : rejected ? 'Review requested · replace evidence' : 'Missing';
                  const color = approved ? 'success' : pending ? 'info' : rejected ? 'error' : 'warning';
                  return <Stack component="li" key={code} direction={{ xs: 'column', sm: 'row' }} justifyContent="space-between" alignItems={{ sm: 'center' }} gap={0.75} sx={{ p: 1.25, border: 1, borderColor: 'divider', borderRadius: 1.5 }}>
                    <Typography fontWeight={650}>{getTypeName(code)}</Typography>
                    <Chip size="small" color={color} label={label} />
                  </Stack>;
                })}
              </Stack>}
          </>}

        {types.isError && <Alert severity="warning" action={<Button color="inherit" size="small" onClick={() => void types.refetch()}>Retry</Button>}>
          Evidence type labels could not be loaded; configured codes are shown instead.
        </Alert>}
        {requiredGps && <Box sx={{ p: 1.5, border: 1, borderColor: 'divider', borderRadius: 1.5 }}>
          <Stack spacing={1.25}>
            <Stack direction={{ xs: 'column', sm: 'row' }} justifyContent="space-between" alignItems={{ sm: 'center' }} gap={1}>
              <Box>
                <Typography fontWeight={700}>GPS site verification required</Typography>
                <Typography variant="body2" color="text.secondary">This checks your current location against the property. It is separate from GPS evidence upload and requires an active visit.</Typography>
                {!gps.data && <Typography variant="caption" color="text.secondary">No location result has been captured in this screen session.</Typography>}
              </Box>
              {visitStatus === 'IN_PROGRESS' && <Button variant="outlined" startIcon={<GpsFixedRounded />} onClick={captureGps} disabled={locationPending || gps.isPending}>
                {locationPending || gps.isPending ? 'Checking location…' : 'Verify current location'}
              </Button>}
            </Stack>
            {visitStatus !== 'IN_PROGRESS' && <Typography variant="caption" color="text.secondary">Start the visit before running this location check.</Typography>}
            {gps.data && <Alert severity={gps.data.validationStatus === 'VERIFIED' ? 'success' : 'error'} role="status">
              {gps.data.validationStatus === 'VERIFIED' ? 'Location verified for this visit.' : 'Location was outside the configured property radius. Capture again at the property.'}
              {' '}Distance recorded: {gps.data.distanceFromExpectedKm.toFixed(3)} km.
            </Alert>}
            {locationError && <Alert severity="error" action={visitStatus === 'IN_PROGRESS' && <Button color="inherit" size="small" onClick={captureGps}>Retry</Button>}>{locationError}</Alert>}
            {gps.isError && !locationError && <Alert severity="error" action={<Button color="inherit" size="small" onClick={captureGps} disabled={locationPending || visitStatus !== 'IN_PROGRESS'}>Retry</Button>}>
              {gps.error instanceof Error ? gps.error.message : 'The location check failed.'}
            </Alert>}
          </Stack>
        </Box>}
        {!requiredGps && data && <Typography variant="caption" color="text.secondary">GPS site verification is not configured as a requirement for this service.</Typography>}
      </Stack>
    </CardContent>
  </Card>;
}
