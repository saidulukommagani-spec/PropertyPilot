import { Alert, Box, Button, Card, CardContent, LinearProgress, Skeleton, Stack, Typography } from '@mui/material';
import { Link } from 'react-router-dom';
import { useServiceRequests } from '../hooks/useServiceRequests';

const statusLabels: Record<string, string> = { NEW: 'New', PENDING_ASSIGNMENT: 'Pending assignment', ASSIGNED: 'Assigned', ACCEPTED: 'Accepted', IN_PROGRESS: 'In progress', COMPLETED: 'Completed', FAILED: 'Failed', CANCELLED: 'Cancelled' };
const colorFor = (status: string): 'primary'|'success'|'warning'|'error'|'inherit' => status === 'COMPLETED' ? 'success' : ['FAILED','CANCELLED'].includes(status) ? 'error' : ['NEW','PENDING_ASSIGNMENT'].includes(status) ? 'warning' : 'primary';

export default function ServiceRequestDashboardPage() {
  const query = useServiceRequests(); const data = query.data ?? [];
  const metrics = [
    ['Total requests', data.length],
    ['Pending assignment', data.filter((item) => ['NEW','PENDING_ASSIGNMENT'].includes(item.status)).length],
    ['Assigned', data.filter((item) => ['ASSIGNED','ACCEPTED'].includes(item.status)).length],
    ['In progress', data.filter((item) => item.status === 'IN_PROGRESS').length],
    ['Completed', data.filter((item) => item.status === 'COMPLETED').length],
    ['Failed', data.filter((item) => item.status === 'FAILED').length],
  ] as const;
  const statusCounts = Object.keys(statusLabels).map((status) => ({ status, count: data.filter((item) => item.status === status).length }));
  const serviceCounts = [...data.reduce((map, item) => { const service = item.serviceName || item.requestType || 'Other'; map.set(service, (map.get(service) ?? 0) + 1); return map; }, new Map<string, number>())].sort((a,b) => b[1] - a[1]).slice(0, 8);
  const maximum = Math.max(1, ...serviceCounts.map(([, count]) => count));
  return <Stack spacing={3}>
    <Stack direction={{ xs:'column', sm:'row' }} justifyContent="space-between" alignItems={{ sm:'center' }} spacing={2}><Box><Typography variant="h4" fontWeight={750}>Service request dashboard</Typography><Typography color="text.secondary" sx={{ mt: .5 }}>Current operational workload from live service requests.</Typography></Box><Button component={Link} to="/service-requests" variant="contained">View requests</Button></Stack>
    {query.isError && <Alert severity="error" action={<Button color="inherit" size="small" onClick={()=>void query.refetch()}>Retry</Button>}>{query.error instanceof Error ? query.error.message : 'Could not load request metrics.'}</Alert>}
    <Box display="grid" gridTemplateColumns={{ xs:'1fr 1fr', md:'repeat(3, 1fr)', xl:'repeat(6, 1fr)' }} gap={2}>{metrics.map(([label,count])=><Card key={label} variant="outlined"><CardContent><Typography variant="body2" color="text.secondary">{label}</Typography>{query.isLoading ? <Skeleton width={60} height={42}/> : <Typography variant="h4" fontWeight={750} sx={{mt:.5}}>{count}</Typography>}</CardContent></Card>)}</Box>
    <Box display="grid" gridTemplateColumns={{ xs:'1fr', lg:'1fr 1fr' }} gap={2}>
      <Card variant="outlined"><CardContent><Typography variant="h6" fontWeight={700}>Status distribution</Typography><Stack spacing={2} sx={{mt:2}}>{query.isLoading ? <Skeleton height={160}/> : statusCounts.map(({status,count})=><Box key={status}><Stack direction="row" justifyContent="space-between" sx={{mb:.5}}><Typography variant="body2">{statusLabels[status]}</Typography><Typography variant="body2" fontWeight={700}>{count}</Typography></Stack><LinearProgress aria-label={`${statusLabels[status]} requests`} color={colorFor(status)} variant="determinate" value={data.length ? count * 100 / data.length : 0}/></Box>)}</Stack>{!query.isLoading && !data.length && <Typography variant="body2" color="text.secondary" sx={{mt:2}}>No requests to report.</Typography>}</CardContent></Card>
      <Card variant="outlined"><CardContent><Typography variant="h6" fontWeight={700}>Requests by service</Typography><Stack spacing={2} sx={{mt:2}}>{query.isLoading ? <Skeleton height={160}/> : serviceCounts.map(([service,count])=><Box key={service}><Stack direction="row" justifyContent="space-between" sx={{mb:.5}}><Typography variant="body2">{service}</Typography><Typography variant="body2" fontWeight={700}>{count}</Typography></Stack><LinearProgress aria-label={`${service} requests`} variant="determinate" value={count * 100 / maximum}/></Box>)}</Stack>{!query.isLoading && !serviceCounts.length && <Typography variant="body2" color="text.secondary" sx={{mt:2}}>No requests to report.</Typography>}</CardContent></Card>
    </Box>
  </Stack>;
}
