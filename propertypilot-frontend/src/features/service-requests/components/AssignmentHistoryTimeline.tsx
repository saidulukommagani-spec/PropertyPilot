import { Alert, Box, Button, Card, CardContent, Skeleton, Stack, Typography } from '@mui/material';
import type { AssignmentHistoryResponse } from '../types/serviceRequest';
const format = (value: string) => new Intl.DateTimeFormat(undefined, { dateStyle: 'medium', timeStyle: 'short' }).format(new Date(value));
interface Props { items: AssignmentHistoryResponse[]; isLoading: boolean; error?: Error | null; onRetry(): void }
export default function AssignmentHistoryTimeline({ items, isLoading, error, onRetry }: Props) {
  return <Card component="section" aria-labelledby="assignment-history-title"><CardContent sx={{ p: { xs: 2.5, md: 3 } }}>
    <Typography id="assignment-history-title" variant="h6" fontWeight={700}>Assignment History</Typography>
    {isLoading ? <Stack spacing={1} sx={{ mt: 2 }}><Skeleton height={52}/><Skeleton height={52}/></Stack> : error ? <Alert severity="error" sx={{ mt: 2 }} action={<Button color="inherit" size="small" onClick={onRetry}>Retry</Button>}>{error.message || 'Assignment history could not be loaded.'}</Alert> : !items.length ? <Typography color="text.secondary" sx={{ mt: 2 }}>No assignment history is available.</Typography> : <Stack component="ol" aria-label="Assignment history newest first" spacing={0} sx={{ listStyle: 'none', p: 0, mt: 2 }}>
      {[...items].sort((a, b) => b.assignedAt.localeCompare(a.assignedAt)).map((item, index, all) => <Stack component="li" key={item.assignmentId} direction="row" spacing={1.5} sx={{ pb: index === all.length - 1 ? 0 : 2 }}>
        <Stack alignItems="center" sx={{ width: 16, flexShrink: 0 }}><Box aria-hidden="true" sx={{ width: 12, height: 12, borderRadius: '50%', bgcolor: index === 0 ? 'primary.main' : 'action.disabled', mt: 0.6 }} />{index < all.length - 1 && <Box aria-hidden="true" sx={{ width: 2, flex: 1, bgcolor: 'divider', minHeight: 28 }} />}</Stack>
        <Stack spacing={0.25}><Typography fontWeight={700}>{item.action} to {item.agentName || 'Agent'}{item.agentCode ? ` (${item.agentCode})` : ''}</Typography><Typography variant="body2" color="text.secondary">{format(item.assignedAt)} · {item.assignedByName || 'User not recorded'}</Typography>{item.notes && <Typography variant="body2" sx={{ mt: 0.5 }}>{item.notes}</Typography>}</Stack>
      </Stack>)}
    </Stack>}
  </CardContent></Card>;
}
