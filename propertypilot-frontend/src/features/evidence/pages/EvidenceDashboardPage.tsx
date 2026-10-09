import { Alert, Box, Button, Card, CardContent, LinearProgress, Skeleton, Stack, Typography } from '@mui/material';
import { Link } from 'react-router-dom';
import { useEvidenceDashboard } from '../hooks/useEvidence';

export default function EvidenceDashboardPage(){
  const query=useEvidenceDashboard();const data=query.data;
  const metrics=data?[["Total evidence",data.totalEvidence],["Pending validation",data.pendingValidation],["Approved",data.approved],["Rejected",data.rejected],["Missing evidence",data.missingEvidence],["Today's uploads",data.todaysUploads]] as const:[];
  const byType=Object.entries(data?.byType??{}).sort((a,b)=>b[1]-a[1]);const byService=Object.entries(data?.byService??{}).sort((a,b)=>b[1]-a[1]);
  const maxService=Math.max(1,...byService.map(([,count])=>count));
  return <Stack spacing={3}>
    <Stack direction={{xs:'column',sm:'row'}} justifyContent="space-between" alignItems={{sm:'center'}} spacing={2}><Box><Typography variant="h4" fontWeight={750}>Evidence dashboard</Typography><Typography color="text.secondary" sx={{mt:.5}}>Trust and validation status across field work.</Typography></Box><Stack direction="row" spacing={1}><Button component={Link} to="/evidence/validation" variant="outlined">Review evidence</Button><Button component={Link} to="/evidence" variant="contained">View evidence</Button></Stack></Stack>
    {query.isError&&<Alert severity="error" action={<Button color="inherit" size="small" onClick={()=>void query.refetch()}>Retry</Button>}>{query.error instanceof Error?query.error.message:'Evidence metrics could not be loaded.'}</Alert>}
    <Box display="grid" gridTemplateColumns={{xs:'1fr 1fr',md:'repeat(3,1fr)',xl:'repeat(6,1fr)'}} gap={2}>{query.isLoading?Array.from({length:6},(_,i)=><Card variant="outlined" key={i}><CardContent><Skeleton width={100}/><Skeleton width={60} height={42}/></CardContent></Card>):metrics.map(([label,count])=><Card variant="outlined" key={label}><CardContent><Typography variant="body2" color="text.secondary">{label}</Typography><Typography variant="h4" fontWeight={750} sx={{mt:.5}}>{count}</Typography></CardContent></Card>)}</Box>
    <Box display="grid" gridTemplateColumns={{xs:'1fr',lg:'1fr 1fr'}} gap={2}>
      <Card variant="outlined"><CardContent><Typography variant="h6" fontWeight={700}>Evidence by type</Typography><Stack spacing={2} sx={{mt:2}}>{query.isLoading?<Skeleton height={160}/>:byType.map(([name,count])=><Box key={name}><Stack direction="row" justifyContent="space-between" sx={{mb:.5}}><Typography variant="body2">{name}</Typography><Typography variant="body2" fontWeight={700}>{count}</Typography></Stack><LinearProgress aria-label={`${name} evidence`} variant="determinate" value={data?.totalEvidence?count*100/data.totalEvidence:0}/></Box>)}</Stack>{data&&byType.length===0&&<Typography color="text.secondary" variant="body2" sx={{mt:2}}>No evidence has been uploaded.</Typography>}</CardContent></Card>
      <Card variant="outlined"><CardContent><Typography variant="h6" fontWeight={700}>Evidence by service</Typography><Stack spacing={2} sx={{mt:2}}>{query.isLoading?<Skeleton height={160}/>:byService.map(([name,count])=><Box key={name}><Stack direction="row" justifyContent="space-between" sx={{mb:.5}}><Typography variant="body2">{name}</Typography><Typography variant="body2" fontWeight={700}>{count}</Typography></Stack><LinearProgress aria-label={`${name} evidence`} variant="determinate" value={count*100/maxService}/></Box>)}</Stack>{data&&byService.length===0&&<Typography color="text.secondary" variant="body2" sx={{mt:2}}>No evidence has been uploaded.</Typography>}</CardContent></Card>
    </Box>
  </Stack>;
}
