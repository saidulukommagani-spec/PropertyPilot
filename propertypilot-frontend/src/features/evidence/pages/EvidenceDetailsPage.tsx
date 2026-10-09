import { Alert, Box, Button, Card, CardContent, Chip, Divider, Skeleton, Stack, Typography } from '@mui/material';
import ArrowBackRounded from '@mui/icons-material/ArrowBackRounded';
import { Link, useParams } from 'react-router-dom';
import { useEvidenceDetails } from '../hooks/useEvidence';
import EvidenceFilePreview from '../components/EvidenceFilePreview';
import EvidenceTimeline from '../components/EvidenceTimeline';

const dateLabel=(value?:string)=>value?new Intl.DateTimeFormat(undefined,{dateStyle:'full',timeStyle:'short'}).format(new Date(value)):'Not recorded';
export default function EvidenceDetailsPage(){
  const {evidenceId=''}=useParams<{evidenceId:string}>();const query=useEvidenceDetails(evidenceId);const item=query.data;
  if(query.isLoading)return <Stack spacing={2}><Skeleton height={42}/><Skeleton variant="rounded" height={250}/><Skeleton variant="rounded" height={200}/></Stack>;
  if(query.isError||!item)return <Stack spacing={2}><Button component={Link} to="/evidence" startIcon={<ArrowBackRounded/>} sx={{alignSelf:'flex-start'}}>Back to Evidence</Button><Alert severity="error" action={<Button color="inherit" size="small" onClick={()=>void query.refetch()}>Retry</Button>}>{query.error instanceof Error?query.error.message:'Evidence could not be loaded.'}</Alert></Stack>;
  return <Stack spacing={2.5}>
    <Button component={Link} to="/evidence" startIcon={<ArrowBackRounded/>} sx={{alignSelf:'flex-start',ml:-1}}>Back to Evidence</Button>
    <Box><Typography variant="overline" color="text.secondary">Evidence record</Typography><Typography variant="h4" fontWeight={750}>{item.evidenceTypeName}</Typography><Typography variant="body2" color="text.secondary" sx={{mt:.5}}>{item.fileName || dateLabel(item.capturedAt ?? item.createdAt)}</Typography></Box>
    <EvidenceFilePreview evidenceId={item.evidenceId} fileName={item.fileName} mediaType={item.mediaType} fileUrl={item.fileUrl}/>
    <Card component="section"><CardContent><Stack direction="row" alignItems="center" justifyContent="space-between"><Typography variant="h6" fontWeight={700}>Metadata</Typography><Chip size="small" color={item.validationStatus==='APPROVED'?'success':item.validationStatus==='REJECTED'?'error':'warning'} label={(item.validationStatus??'PENDING').replaceAll('_',' ')}/></Stack><Divider sx={{my:2}}/>
      <Box display="grid" gridTemplateColumns={{xs:'1fr',sm:'repeat(2,minmax(0,1fr))',lg:'repeat(3,minmax(0,1fr))'}} gap={2}>{[
        ['Evidence ID',item.evidenceId],['Evidence type',`${item.evidenceTypeName} · ${item.evidenceTypeCode}`],['File name',item.fileName],['Media type',item.mediaType],['Uploaded by',item.uploadedByName],['Uploaded timestamp',dateLabel(item.capturedAt??item.createdAt)],['Device',item.deviceInfo],['Checksum SHA-256',item.checksumSha256],['GPS coordinates',item.latitude!=null&&item.longitude!=null?`${item.latitude}, ${item.longitude}`:'Not captured'],['Property',item.propertyName],['Customer',item.customerName],['Agent',item.agentName],['Service request',item.serviceRequestId],['Visit',item.visitId],['Visit status',item.visitStatus],['Visit scheduled',dateLabel(item.visitScheduledAt)],
      ].map(([label,value])=>label==='Evidence ID'||label==='Service request'||label==='Visit'?null:<Stack key={label} spacing={.5} minWidth={0}><Typography variant="caption" color="text.secondary">{label}</Typography><Typography variant="body2" sx={{overflowWrap:'anywhere'}}>{value||'Not provided'}</Typography></Stack>)}</Box>
      {item.remarks&&<Stack spacing={.5} sx={{mt:2}}><Typography variant="caption" color="text.secondary">Notes</Typography><Typography variant="body2">{item.remarks}</Typography></Stack>}
      {item.validationComment&&<Alert severity={item.validationStatus==='APPROVED'?'success':item.validationStatus==='REJECTED'?'error':'info'} sx={{mt:2}}>{item.validationComment}{item.validatedByName?` · ${item.validatedByName}`:''}{item.validatedAt?` · ${dateLabel(item.validatedAt)}`:''}</Alert>}
    </CardContent></Card>
    <EvidenceTimeline evidence={item}/>
    <Stack direction="row" flexWrap="wrap" gap={1}><Button component={Link} to={`/service-requests/${item.serviceRequestId}`} variant="outlined">Open service request</Button>{item.visitId&&<><Button component={Link} to={`/visits/${item.visitId}`} variant="outlined">Open visit</Button><Button component={Link} to={`/visits/${item.visitId}/evidence`} variant="outlined">Visit evidence summary</Button></>}</Stack>
  </Stack>;
}
