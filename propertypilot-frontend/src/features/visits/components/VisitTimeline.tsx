import { Box, Card, CardContent, Divider, Stack, Typography } from '@mui/material';
import type { AssignmentHistoryResponse } from '../../service-requests/types/serviceRequest';
import type { VisitSummary } from '../types/visit';
interface Props { visit: VisitSummary; assignment?: AssignmentHistoryResponse }
const dateTime = (value?: string) => value ? new Intl.DateTimeFormat(undefined, { dateStyle: 'medium', timeStyle: 'short' }).format(new Date(value)) : null;
export default function VisitTimeline({ visit, assignment }: Props) {
  const events = [
    { label: 'Visit created', at: visit.createdAt || visit.scheduledAt, user: visit.createdByName },
    { label: 'Assigned', at: assignment?.assignedAt || visit.assignedAt, user: assignment?.assignedByName || visit.assignedByName },
    { label: 'Accepted', at: assignment?.respondedAt || visit.acceptedAt, user: assignment?.agentName || visit.agentName },
    { label: 'Started', at: visit.startedAt, user: visit.startedByName },
    { label: visit.status === 'CANCELLED' ? 'Cancelled' : visit.status === 'FAILED' ? 'Failed' : 'Completed', at: visit.endedAt, user: visit.status === 'CANCELLED' || visit.status === 'FAILED' ? visit.updatedByName : visit.completedByName },
    { label: 'Rescheduled', at: visit.rescheduleReason ? visit.updatedAt : undefined, user: visit.updatedByName, detail: visit.rescheduleReason },
  ].filter((event) => Boolean(event.at)).sort((left, right) => Date.parse(left.at ?? '') - Date.parse(right.at ?? ''));
  return <Card component="section" aria-labelledby="visit-history-title"><CardContent sx={{ p: { xs: 2.5, md: 3 } }}>
    <Typography id="visit-history-title" variant="h6" fontWeight={700}>Visit Timeline</Typography><Divider sx={{ my: 2 }} />
    {events.length === 0 ? <Typography color="text.secondary">No visit timeline events are available.</Typography> : <Stack component="ol" spacing={0} sx={{ listStyle: 'none', p: 0, m: 0 }}>
      {events.map((event, index) => <Stack component="li" key={`${event.label}-${event.at}`} direction="row" spacing={1.5} sx={{ pb: index === events.length - 1 ? 0 : 2 }}>
        <Stack alignItems="center" sx={{ width: 16, flexShrink: 0 }}><Box aria-hidden="true" sx={{ width: 11, height: 11, borderRadius: '50%', bgcolor: 'primary.main', mt: 0.6 }} />{index < events.length - 1 && <Box aria-hidden="true" sx={{ width: 2, flex: 1, bgcolor: 'divider', minHeight: 28 }} />}</Stack>
        <Stack><Typography fontWeight={650}>{event.label}</Typography><Typography variant="body2" color="text.secondary">{dateTime(event.at) || '—'} · {event.user || 'User not recorded'}</Typography>{event.detail && <Typography variant="caption" color="text.secondary">{event.detail}</Typography>}</Stack>
      </Stack>)}
    </Stack>}
  </CardContent></Card>;
}
