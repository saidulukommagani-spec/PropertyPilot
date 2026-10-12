import { Alert, Box, Button, Card, CardContent, LinearProgress, Skeleton, Stack, Typography } from '@mui/material';
import { Link } from 'react-router-dom';
import { useEvidenceDashboard } from '../hooks/useEvidence';
import DashboardMetricCards from '../../../components/layouts/DashboardMetricCards';
import DashboardPageHeading from '../../../components/layouts/DashboardPageHeading';

export default function EvidenceDashboardPage(){
  const query=useEvidenceDashboard();const data=query.data;
  const metrics=[["Total evidence",data?.totalEvidence??'—'],["Pending validation",data?.pendingValidation??'—'],["Approved",data?.approved??'—'],["Rejected",data?.rejected??'—'],["Missing evidence",data?.missingEvidence??'—'],["Today's uploads",data?.todaysUploads??'—']] as const;
  const byType=Object.entries(data?.byType??{}).sort((a,b)=>b[1]-a[1]);const byService=Object.entries(data?.byService??{}).sort((a,b)=>b[1]-a[1]);
  const maxService=Math.max(1,...byService.map(([,count])=>count));
  return <Stack spacing={3}>
    <DashboardPageHeading eyebrow="Trust and compliance" title="Evidence Dashboard" subtitle="Trust and validation status across field work." action={<Stack direction="row" spacing={1}><Button component={Link} to="/evidence/validation" variant="outlined">Review evidence</Button><Button component={Link} to="/evidence" variant="contained">View evidence</Button></Stack>} />
    {query.isError&&<Alert severity="error" action={<Button color="inherit" size="small" onClick={()=>void query.refetch()}>Retry</Button>}>{query.error instanceof Error?query.error.message:'Evidence metrics could not be loaded.'}</Alert>}
    <DashboardMetricCards items={metrics.map(([label, value]) => ({ label, value }))} loading={query.isLoading} />
    <Box display="grid" gridTemplateColumns={{xs:'1fr',lg:'1fr 1fr'}} gap={2}>
      <Card variant="outlined"><CardContent><Typography variant="h6" fontWeight={700}>Evidence by type</Typography><Stack spacing={2} sx={{mt:2}}>{query.isLoading?<Skeleton height={160}/>:byType.map(([name,count])=><Box key={name}><Stack direction="row" justifyContent="space-between" sx={{mb:.5}}><Typography variant="body2">{name}</Typography><Typography variant="body2" fontWeight={700}>{count}</Typography></Stack><LinearProgress aria-label={`${name} evidence`} variant="determinate" value={data?.totalEvidence?count*100/data.totalEvidence:0}/></Box>)}</Stack>{data&&byType.length===0&&<Typography color="text.secondary" variant="body2" sx={{mt:2}}>No evidence has been uploaded.</Typography>}</CardContent></Card>
      <Card variant="outlined"><CardContent><Typography variant="h6" fontWeight={700}>Evidence by service</Typography><Stack spacing={2} sx={{mt:2}}>{query.isLoading?<Skeleton height={160}/>:byService.map(([name,count])=><Box key={name}><Stack direction="row" justifyContent="space-between" sx={{mb:.5}}><Typography variant="body2">{name}</Typography><Typography variant="body2" fontWeight={700}>{count}</Typography></Stack><LinearProgress aria-label={`${name} evidence`} variant="determinate" value={count*100/maxService}/></Box>)}</Stack>{data&&byService.length===0&&<Typography color="text.secondary" variant="body2" sx={{mt:2}}>No evidence has been uploaded.</Typography>}</CardContent></Card>
    </Box>
  </Stack>;
}
