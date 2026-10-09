import { Box, Card, CardContent, Divider, Stack, Typography } from '@mui/material';
import type { ActivityResponse } from '../types/serviceRequest';

const dateTime = (value: string) => new Intl.DateTimeFormat(undefined, { dateStyle: 'medium', timeStyle: 'short' }).format(new Date(value));
export default function ActivityTimeline({ activities }: { activities: ActivityResponse[] }) {
  return <Card component="section" aria-labelledby="activity-timeline-title"><CardContent sx={{ p: { xs: 2.5, md: 3 } }}>
    <Typography id="activity-timeline-title" variant="h6" fontWeight={700}>Activity</Typography><Divider sx={{ my: 2 }} />
    {!activities.length ? <Typography color="text.secondary">No activity has been recorded.</Typography> : <Stack spacing={0}>
      {activities.map((item, index) => <Stack key={item.id} direction="row" spacing={1.5} sx={{ pb: index === activities.length - 1 ? 0 : 2 }}>
        <Stack alignItems="center" sx={{ width: 16, flexShrink: 0 }}><Box sx={{ width: 10, height: 10, borderRadius: '50%', bgcolor: item.kind === 'status' ? 'success.main' : 'primary.main', mt: 0.6 }} />{index < activities.length - 1 && <Box sx={{ width: 2, flex: 1, bgcolor: 'divider', minHeight: 28 }} />}</Stack>
        <Stack spacing={0.25}><Typography variant="body2" fontWeight={650}>{item.label}</Typography><Typography variant="caption" color="text.secondary">{dateTime(item.occurredAt)}{item.detail ? ` · ${item.detail}` : ''}</Typography></Stack>
      </Stack>)}
    </Stack>}
  </CardContent></Card>;
}
