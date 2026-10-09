import { Card, CardContent, Divider, Stack, Typography } from '@mui/material';
import ImageOutlined from '@mui/icons-material/ImageOutlined';
import VideocamOutlined from '@mui/icons-material/VideocamOutlined';
import DescriptionOutlined from '@mui/icons-material/DescriptionOutlined';
import GpsFixedRounded from '@mui/icons-material/GpsFixedRounded';
import type { EvidenceResponse } from '../types/serviceRequest';

export default function EvidenceSummary({ evidence }: { evidence: EvidenceResponse[] }) {
  const groups = [
    { label: 'Photos', icon: <ImageOutlined />, items: evidence.filter((item) => !isGps(item) && item.mediaType?.startsWith('image/')) },
    { label: 'Videos', icon: <VideocamOutlined />, items: evidence.filter((item) => !isGps(item) && item.mediaType?.startsWith('video/')) },
    { label: 'Documents', icon: <DescriptionOutlined />, items: evidence.filter((item) => !item.mediaType?.startsWith('image/') && !item.mediaType?.startsWith('video/') && !isGps(item)) },
    { label: 'GPS evidence', icon: <GpsFixedRounded />, items: evidence.filter(isGps) },
  ];
  return <Card component="section" aria-labelledby="evidence-summary-title"><CardContent sx={{ p: { xs: 2.5, md: 3 } }}>
    <Typography id="evidence-summary-title" variant="h6" fontWeight={700}>Evidence summary</Typography><Divider sx={{ my: 2 }} />
    <Stack display="grid" gridTemplateColumns={{ xs: 'repeat(2, minmax(0, 1fr))', sm: 'repeat(4, minmax(0, 1fr))' }} gap={1.5}>
      {groups.map((group) => <Stack key={group.label} spacing={0.75} sx={{ p: 1.5, borderRadius: 2, bgcolor: 'background.default' }}>
        <Stack direction="row" spacing={1} alignItems="center" color="primary.main">{group.icon}<Typography variant="caption" color="text.secondary">{group.label}</Typography></Stack>
        <Typography variant="h5" fontWeight={750}>{group.items.length}</Typography>
      </Stack>)}
    </Stack>
    {evidence.some(hasCoordinates) && <Stack spacing={1} sx={{ mt: 2 }}>
      {evidence.filter(hasCoordinates).map((item) => <Typography key={item.evidenceId} variant="caption" color="text.secondary">{item.evidenceTypeName}: {item.latitude}, {item.longitude}</Typography>)}
    </Stack>}
    <Typography variant="caption" color="text.secondary" sx={{ display: 'block', mt: 1.5 }}>{evidence.filter((item)=>item.validationStatus==='APPROVED').length} approved · {evidence.filter((item)=>!item.validationStatus||item.validationStatus==='PENDING').length} pending validation</Typography>
  </CardContent></Card>;
}
function hasCoordinates(item: EvidenceResponse) { return item.latitude != null && item.longitude != null; }
function isGps(item: EvidenceResponse) { return hasCoordinates(item) || `${item.evidenceTypeCode} ${item.evidenceTypeName}`.toLowerCase().includes('gps'); }
