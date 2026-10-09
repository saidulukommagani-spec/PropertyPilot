import { Alert, Box, Button, Card, CardContent, Chip, Divider, Skeleton, Stack, Typography } from '@mui/material';
import EventAvailableRounded from '@mui/icons-material/EventAvailableRounded';
import { useLocation, useNavigate } from 'react-router-dom';
import type { VisitSummary } from '../../visits/types/visit';
import { VisitService } from '../../visits/services/VisitService';

const dateTime = (value?: string) => value ? new Intl.DateTimeFormat(undefined, { dateStyle: 'medium', timeStyle: 'short' }).format(new Date(value)) : '—';
interface Props { visits: VisitSummary[]; isLoading?: boolean; error?: Error | null; onRetry?(): void; onCreate?(): void }
export default function VisitTimeline({ visits, isLoading = false, error, onRetry, onCreate }: Props) {
  const navigate = useNavigate(); const { pathname } = useLocation(); const orderedVisits = VisitService.newestFirst(visits);
  const visitRoute = pathname.startsWith('/agent/') ? '/agent/visits' : '/visits';
  return <Card component="section" aria-labelledby="visit-timeline-title"><CardContent sx={{ p: { xs: 2.5, md: 3 } }}>
    <Stack direction="row" alignItems="center" justifyContent="space-between" flexWrap="wrap" gap={1}>
      <Typography id="visit-timeline-title" variant="h6" fontWeight={700}>Visits</Typography>
      {onCreate && <Button variant="contained" size="small" startIcon={<EventAvailableRounded />} onClick={onCreate}>Create Visit</Button>}
    </Stack>
    <Divider sx={{ my: 2 }} />
    {isLoading ? <Stack spacing={1}><Skeleton height={72}/><Skeleton height={72}/></Stack> : error ? <Alert severity="error" action={onRetry && <Button color="inherit" size="small" onClick={onRetry}>Retry</Button>}>{error.message || 'Visits could not be loaded.'}</Alert> : !orderedVisits.length ? <Stack spacing={0.5} role="status"><Typography color="text.secondary">No visits have been scheduled.</Typography><Typography variant="caption" color="text.secondary">Some services can be completed without an on-site visit.</Typography></Stack> : <Stack component="ol" aria-label="Visits newest first" spacing={1.25} sx={{ listStyle: 'none', p: 0, m: 0 }}>
      {orderedVisits.map((visit, index) => <Box component="li" key={visit.visitId}>
        <Button fullWidth variant="text" color="inherit" onClick={() => navigate(`${visitRoute}/${visit.visitId}`)} aria-label={`Open visit ${orderedVisits.length - index}`} sx={{ justifyContent: 'stretch', textAlign: 'left', p: 1.5, border: 1, borderColor: 'divider', borderRadius: 1.5, '&:hover': { bgcolor: 'action.hover' } }}>
          <Stack width="100%" spacing={0.75}>
            <Stack direction={{ xs: 'column', sm: 'row' }} alignItems={{ sm: 'center' }} justifyContent="space-between" gap={0.75}>
              <Typography fontWeight={700}>Visit {VisitService.visitNumber(visit, visits)}</Typography>
              <Chip size="small" variant="outlined" label={visit.status.replaceAll('_', ' ')} color={visit.status === 'COMPLETED' ? 'success' : visit.status === 'IN_PROGRESS' ? 'warning' : 'default'} />
            </Stack>
            <Typography variant="body2" color="text.secondary">Agent: {visit.agentName || '—'} · Scheduled: {dateTime(visit.scheduledAt)}</Typography>
            <Typography variant="caption" color="text.secondary">Started: {dateTime(visit.startedAt)} · Completed: {dateTime(visit.endedAt)}</Typography>
          </Stack>
        </Button>
      </Box>)}
    </Stack>}
  </CardContent></Card>;
}
