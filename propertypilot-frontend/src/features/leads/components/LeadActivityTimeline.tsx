import { Alert, Button, Card, CardContent, Skeleton, Stack, Typography } from '@mui/material';
import { useLeadActivities } from '../hooks/useLeads';

export default function LeadActivityTimeline({ leadId }: { leadId: string }) {
  const query = useLeadActivities(leadId);
  return <Card variant="outlined"><CardContent><Stack spacing={2}><Typography variant="h6" fontWeight={700}>Lead audit trail</Typography>
    {query.isLoading ? <Skeleton variant="rounded" height={100} /> : query.isError ? <Alert severity="error" action={<Button onClick={() => void query.refetch()}>Retry</Button>}>{query.error.message || 'Lead history could not be loaded.'}</Alert> : query.data?.length ? query.data.map((activity) => <Stack key={activity.leadActivityId} direction="row" spacing={1.5} sx={{ borderLeft: 2, borderColor: 'primary.main', pl: 1.5, py: 0.5 }}><Stack spacing={0.4}><Typography fontWeight={700}>{activity.subject}</Typography><Typography variant="body2" color="text.secondary">{activity.activityType.replaceAll('_', ' ')} · {new Date(activity.occurredAt).toLocaleString()} · {activity.performedBy || 'System'}</Typography>{activity.details && <Typography variant="body2">{activity.details}</Typography>}{activity.outcome && <Typography variant="body2" color="text.secondary">Outcome: {activity.outcome}</Typography>}</Stack></Stack>) : <Typography color="text.secondary">No lead activities have been recorded.</Typography>}
  </Stack></CardContent></Card>;
}
