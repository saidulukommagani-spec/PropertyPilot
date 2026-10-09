import { useQueries } from '@tanstack/react-query';
import { Alert, Box, Card, CardContent, Chip, Skeleton, Stack, Typography } from '@mui/material';
import { Link } from 'react-router-dom';
import EvidenceFilePreview from '../../evidence/components/EvidenceFilePreview';
import { useCustomerRequests } from '../hooks/useCustomerPortal';
import { CustomerPageHeading, CustomerQueryError } from '../components/CustomerStates';
import { CustomerPortalApi } from '../services/CustomerPortalApi';

export default function CustomerEvidencePage() {
  const requests=useCustomerRequests(); const entries=requests.data??[];
  const evidence=useQueries({queries:entries.map((request)=>({queryKey:['customer-portal','request-evidence',request.serviceRequestId],queryFn:()=>CustomerPortalApi.requestEvidence(request.serviceRequestId)}))});
  const records=evidence.flatMap((query)=>query.data??[]).sort((a,b)=>(b.capturedAt??b.createdAt??'').localeCompare(a.capturedAt??a.createdAt??''));
  return <Stack spacing={3}><CustomerPageHeading title="Service evidence" subtitle="Review field photos, files, location records, and capture times from your service visits." />{requests.isError&&<CustomerQueryError error={requests.error} retry={()=>void requests.refetch()}/ >}{evidence.some((query)=>query.isError)&&<Alert severity="error">Some evidence records could not be loaded. Reload to retry.</Alert>}
    {requests.isLoading||evidence.some((query)=>query.isLoading)?Array.from({length:2},(_,i)=><Skeleton key={i} variant="rounded" height={200}/>):records.length?<Box display="grid" gridTemplateColumns={{xs:'1fr',lg:'repeat(2,minmax(0,1fr))'}} gap={2}>{records.map((item)=><Stack key={item.evidenceId} spacing={1}><Card variant="outlined"><CardContent><Stack direction="row" justifyContent="space-between" gap={1}><Typography variant="h6" fontWeight={700}>{item.evidenceTypeName}</Typography><Chip size="small" label={item.validationStatus||'Uploaded'} color={item.validationStatus==='APPROVED'?'success':'default'}/></Stack><Typography variant="body2" color="text.secondary" sx={{mt:1}}><Link to={`/customer/service-requests/${item.serviceRequestId}`}>Open service request</Link>  -  {item.propertyName||'Property'}</Typography><Typography variant="body2" sx={{mt:.5}}>Captured: {item.capturedAt?new Date(item.capturedAt).toLocaleString():item.createdAt?new Date(item.createdAt).toLocaleString():'Not recorded'}</Typography>{item.latitude!==undefined&&item.longitude!==undefined&&<Typography variant="body2" sx={{mt:.5}}>GPS: {item.latitude}, {item.longitude}</Typography>}{item.remarks&&<Typography variant="body2" sx={{mt:.5}}>{item.remarks}</Typography>}</CardContent></Card><EvidenceFilePreview evidenceId={item.evidenceId} fileName={item.fileName} mediaType={item.mediaType} fileUrl={item.fileUrl}/></Stack>)}</Box>:!requests.isError&&<Alert severity="info">Evidence becomes available after it is uploaded during a visit.</Alert>}
  </Stack>;
}


