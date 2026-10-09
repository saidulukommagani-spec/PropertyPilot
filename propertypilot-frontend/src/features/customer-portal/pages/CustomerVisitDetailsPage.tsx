import { Alert, Button, Card, CardContent, Chip, Skeleton, Stack, Typography } from '@mui/material';
import { Link, useParams } from 'react-router-dom';
import { useCustomerEvidenceForVisit, useCustomerVisit } from '../hooks/useCustomerPortal';
import { CustomerPageHeading, CustomerQueryError } from '../components/CustomerStates';

export default function CustomerVisitDetailsPage() {
  const {id=''}=useParams(); const visit=useCustomerVisit(id); const evidence=useCustomerEvidenceForVisit(id);
  if(visit.isLoading)return <Stack spacing={2}><Skeleton height={42}/><Skeleton variant="rounded" height={200}/></Stack>;
  if(visit.isError||!visit.data)return <Stack spacing={2}><Button component={Link} to="/customer/visits">Back to visits</Button><CustomerQueryError error={visit.error} retry={()=>void visit.refetch()}/></Stack>;
  const item=visit.data;
  return <Stack spacing={3}><CustomerPageHeading title={item.serviceName || 'Visit details'} subtitle={`${item.propertyName || 'Property visit'} · ${new Date(item.scheduledAt).toLocaleString()}`} action={<Button component={Link} to="/customer/visits">Back to visits</Button>} />
    {evidence.isError&&<CustomerQueryError error={evidence.error} retry={()=>void evidence.refetch()}/>}
    <Card variant="outlined"><CardContent><Stack direction="row" justifyContent="space-between"><Typography variant="h6" fontWeight={700}>Visit information</Typography><Chip label={item.status.replaceAll('_',' ')} color={item.status==='COMPLETED'?'success':'info'}/></Stack><Typography sx={{mt:2}}>Property: {item.propertyName||'Property'}</Typography><Typography sx={{mt:1}}>Customer: {item.customerName||'You'}</Typography><Typography sx={{mt:1}}>Agent: {item.agentName||'To be confirmed'}</Typography><Typography sx={{mt:1}}>Scheduled: {new Date(item.scheduledAt).toLocaleString()}</Typography>{item.startedAt&&<Typography sx={{mt:1}}>Started: {new Date(item.startedAt).toLocaleString()}</Typography>}{item.endedAt&&<Typography sx={{mt:1}}>Completed: {new Date(item.endedAt).toLocaleString()}</Typography>}{item.rescheduleReason&&<Alert severity="info" sx={{mt:2}}>Rescheduled: {item.rescheduleReason}</Alert>}<Button component={Link} to={`/customer/service-requests/${item.serviceRequestId}`} sx={{mt:2}}>View service request</Button></CardContent></Card>
    <Card variant="outlined"><CardContent><Typography variant="h6" fontWeight={700}>Visit timeline</Typography><Stack spacing={1} sx={{mt:2}}>{[{label:'Scheduled',value:item.scheduledAt},{label:'Started',value:item.startedAt},{label:'Completed',value:item.endedAt}].filter((event)=>event.value).map((event)=><Typography key={event.label}><strong>{event.label}</strong> · {new Date(event.value!).toLocaleString()}</Typography>)}</Stack></CardContent></Card>
    <Card variant="outlined"><CardContent><Typography variant="h6" fontWeight={700}>Evidence</Typography>{evidence.isLoading?<Skeleton height={80}/>:evidence.data?.length?<Stack spacing={1} sx={{mt:2}}>{evidence.data.map((entry)=><Typography key={entry.evidenceId}>{entry.evidenceTypeName} · {entry.validationStatus||'Uploaded'} · {entry.capturedAt?new Date(entry.capturedAt).toLocaleString():'Timestamp not recorded'}</Typography>)}</Stack>:<Typography color="text.secondary" sx={{mt:1}}>Evidence will be available here after it is uploaded.</Typography>}</CardContent></Card>
  </Stack>;
}
