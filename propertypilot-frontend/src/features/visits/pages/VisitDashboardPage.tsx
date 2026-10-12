import { Alert, Box, Button, Card, CardContent, LinearProgress, Skeleton, Stack, Typography } from '@mui/material';
import { Link } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { serviceRequestApi } from '../../../api/serviceRequestApi';
import { useAllVisits } from '../hooks/useVisits';
import DashboardMetricCards from '../../../components/layouts/DashboardMetricCards';
import DashboardPageHeading from '../../../components/layouts/DashboardPageHeading';

const statusNames: Record<string,string> = { SCHEDULED:'Scheduled', IN_PROGRESS:'In progress', COMPLETED:'Completed', CANCELLED:'Cancelled', FAILED:'Failed' };
export default function VisitDashboardPage() {
  const visitsQuery = useAllVisits(); const requestsQuery = useQuery({queryKey:['service-requests'],queryFn:serviceRequestApi.list});
  const visits = visitsQuery.data ?? []; const requests = requestsQuery.data ?? []; const now = new Date();
  const sameDay = (value:string) => { const date=new Date(value); return date.getFullYear()===now.getFullYear() && date.getMonth()===now.getMonth() && date.getDate()===now.getDate(); };
  const counts = [
    ['Total visits',visits.length], ['Scheduled today',visits.filter((visit)=>visit.status==='SCHEDULED' && sameDay(visit.scheduledAt)).length],
    ['Upcoming',visits.filter((visit)=>visit.status==='SCHEDULED' && new Date(visit.scheduledAt)>now && !sameDay(visit.scheduledAt)).length],
    ['Completed',visits.filter((visit)=>visit.status==='COMPLETED').length], ['Cancelled',visits.filter((visit)=>visit.status==='CANCELLED').length],
    ['Overdue',visits.filter((visit)=>visit.status==='SCHEDULED' && new Date(visit.scheduledAt)<now).length],
  ] as const;
  const statusCounts=Object.keys(statusNames).map((status)=>({status,count:visits.filter((visit)=>visit.status===status).length}));
  const requestMap=new Map(requests.map((request)=>[request.serviceRequestId,request]));
  const serviceCounts=[...visits.reduce((map,visit)=>{const request=requestMap.get(visit.serviceRequestId);const name=request?.serviceName||request?.requestType||'Unknown service';map.set(name,(map.get(name)??0)+1);return map;},new Map<string,number>())].sort((a,b)=>b[1]-a[1]).slice(0,8);
  const maxService=Math.max(1,...serviceCounts.map((item)=>item[1]));
  return <Stack spacing={3}>
    <DashboardPageHeading eyebrow="Field operations" title="Visit Dashboard" subtitle="Field operations workload from scheduled visit records." action={<Button component={Link} to="/visits" variant="contained">View visits</Button>} />
    {visitsQuery.isError && <Alert severity="error" action={<Button color="inherit" size="small" onClick={()=>void visitsQuery.refetch()}>Retry</Button>}>{visitsQuery.error instanceof Error?visitsQuery.error.message:'Visit metrics could not be loaded.'}</Alert>}
    {requestsQuery.isError && <Alert severity="error" action={<Button color="inherit" size="small" onClick={()=>void requestsQuery.refetch()}>Retry</Button>}>Service labels could not be loaded for the service chart.</Alert>}
    <DashboardMetricCards items={counts.map(([label, value]) => ({ label, value }))} loading={visitsQuery.isLoading} />
    <Box display="grid" gridTemplateColumns={{xs:'1fr',lg:'1fr 1fr'}} gap={2}>
      <Card variant="outlined"><CardContent><Typography variant="h6" fontWeight={700}>Visits by status</Typography><Stack spacing={2} sx={{mt:2}}>{visitsQuery.isLoading?<Skeleton height={160}/>:statusCounts.map(({status,count})=><Box key={status}><Stack direction="row" justifyContent="space-between" sx={{mb:.5}}><Typography variant="body2">{statusNames[status]}</Typography><Typography variant="body2" fontWeight={700}>{count}</Typography></Stack><LinearProgress aria-label={`${statusNames[status]} visits`} variant="determinate" value={visits.length?count*100/visits.length:0}/></Box>)}</Stack>{!visitsQuery.isLoading&&!visits.length&&<Typography variant="body2" color="text.secondary" sx={{mt:2}}>No visits to report.</Typography>}</CardContent></Card>
      <Card variant="outlined"><CardContent><Typography variant="h6" fontWeight={700}>Visits by service</Typography><Stack spacing={2} sx={{mt:2}}>{visitsQuery.isLoading||requestsQuery.isLoading?<Skeleton height={160}/>:serviceCounts.map(([name,count])=><Box key={name}><Stack direction="row" justifyContent="space-between" sx={{mb:.5}}><Typography variant="body2">{name}</Typography><Typography variant="body2" fontWeight={700}>{count}</Typography></Stack><LinearProgress aria-label={`${name} visits`} variant="determinate" value={count*100/maxService}/></Box>)}</Stack>{!visitsQuery.isLoading&&!serviceCounts.length&&<Typography variant="body2" color="text.secondary" sx={{mt:2}}>No visits to report.</Typography>}</CardContent></Card>
    </Box>
  </Stack>;
}
