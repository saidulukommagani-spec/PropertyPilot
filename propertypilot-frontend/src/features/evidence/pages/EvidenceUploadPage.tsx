import { useEffect, useState } from 'react';
import { Alert, Box, Button, FormControl, FormHelperText, InputLabel, MenuItem, Select, Skeleton, Stack, TextField, Typography } from '@mui/material';
import ArrowBackRounded from '@mui/icons-material/ArrowBackRounded';
import { Controller, useForm } from 'react-hook-form';
import { Link, useNavigate, useSearchParams } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { VisitApi } from '../../../api/VisitApi';
import { serviceRequestApi } from '../../../api/serviceRequestApi';
import { getUserRoles } from '../../../api/auth';
import { useAuth } from '../../auth/AuthContext';
import { AgentOperationsApi } from '../../agent-operations/services/AgentOperationsApi';
import { useEvidenceTypes } from '../hooks/useEvidence';
import EvidenceUploader from '../components/EvidenceUploader';

const schema=z.object({visitId:z.string().min(1,'Select a visit.')});type Values=z.infer<typeof schema>;
export default function EvidenceUploadPage(){
  const [params]=useSearchParams();const navigate=useNavigate();const initialVisitId=params.get('visitId')??'';const initialServiceRequestId=params.get('serviceRequestId')??'';const [serviceRequestId,setServiceRequestId]=useState(initialServiceRequestId);
  const {user}=useAuth();const roles=getUserRoles(user);const agentOnly=roles.includes('AGENT')&&!roles.includes('ADMIN')&&!roles.includes('OPERATIONS');
  const backTo=initialVisitId?(agentOnly?`/agent/visit/${initialVisitId}`:`/visits/${initialVisitId}`):initialServiceRequestId?(agentOnly?`/agent/service-requests/${initialServiceRequestId}`:`/service-requests/${initialServiceRequestId}`):agentOnly?'/agent/work-queue':'/evidence';
  const {control,watch,reset,formState:{errors}}=useForm<Values>({resolver:zodResolver(schema),defaultValues:{visitId:initialVisitId}});const visitId=watch('visitId');
  const visits=useQuery({queryKey:initialVisitId?['visits','detail',initialVisitId]:['visits',agentOnly?'mine':'all'],queryFn:async()=>initialVisitId?[await VisitApi.getDetails(initialVisitId)]:agentOnly?await VisitApi.listMine():await VisitApi.listAll(),enabled:!initialServiceRequestId});
  const requests=useQuery({queryKey:['service-requests',agentOnly?'mine':'all'],queryFn:agentOnly?AgentOperationsApi.myRequests:serviceRequestApi.list,enabled:!initialVisitId});
  const services=useQuery({queryKey:['services','catalog'],queryFn:VisitApi.listServices,enabled:!initialVisitId});
  const types=useEvidenceTypes();
  useEffect(()=>{if(initialVisitId)reset({visitId:initialVisitId});},[initialVisitId,reset]);useEffect(()=>setServiceRequestId(initialServiceRequestId),[initialServiceRequestId]);
  const available=(visits.data??[]).filter((item)=>item.status==='IN_PROGRESS');const selectedVisit=available.find((item)=>item.visitId===visitId);const selected=serviceRequestId?undefined:selectedVisit;
  const serviceById=new Map((services.data??[]).map((service)=>[service.serviceId,service]));const requestOptions=(requests.data??[]).filter((request)=>request.status==='IN_PROGRESS'&&request.serviceId&&serviceById.get(request.serviceId)?.requiresVisit===false&&serviceById.get(request.serviceId)?.visitMandatory===false);const selectedRequest=requestOptions.find((request)=>request.serviceRequestId===serviceRequestId);
  if(visits.isLoading||types.isLoading||(!initialVisitId&&(requests.isLoading||services.isLoading)))return <Stack spacing={2}><Skeleton height={42}/><Skeleton variant="rounded" height={340}/></Stack>;
  return <Stack spacing={2.5}>
    <Button component={Link} to={backTo} startIcon={<ArrowBackRounded/>} sx={{alignSelf:'flex-start',ml:-1}}>{initialVisitId?'Back to Visit':initialServiceRequestId?'Back to Request':agentOnly?'Back to My Work Queue':'Back to Evidence'}</Button>
    <Box><Typography variant="h4" fontWeight={750}>Upload evidence</Typography><Typography color="text.secondary" sx={{mt:.5}}>Attach evidence to an active visit or an in-progress service that does not require a visit. Uploads are stored and sent for validation.</Typography></Box>
    {visits.isError&&<Alert severity="error" action={<Button color="inherit" size="small" onClick={()=>void visits.refetch()}>Retry</Button>}>{visits.error instanceof Error?visits.error.message:'Visits could not be loaded.'}</Alert>}
    {requests.isError&&<Alert severity="error" action={<Button color="inherit" size="small" onClick={()=>void requests.refetch()}>Retry</Button>}>{requests.error instanceof Error?requests.error.message:'In-progress service requests could not be loaded.'}</Alert>}
    {services.isError&&<Alert severity="error" action={<Button color="inherit" size="small" onClick={()=>void services.refetch()}>Retry</Button>}>{services.error instanceof Error?services.error.message:'Service configuration could not be loaded.'}</Alert>}
    {types.isError&&<Alert severity="error" action={<Button color="inherit" size="small" onClick={()=>void types.refetch()}>Retry</Button>}>{types.error instanceof Error?types.error.message:'Evidence type catalog could not be loaded.'}</Alert>}
    {!visits.isError&&!requests.isError&&!services.isError&&!available.length&&!requestOptions.length&&<Alert severity="info">Evidence can be uploaded after a visit starts or when an in-progress no-visit service is ready for evidence.</Alert>}
    {(available.length>0||requestOptions.length>0)&&<>
      {!initialVisitId&&!initialServiceRequestId&&available.length>0&&<Controller name="visitId" control={control} render={({field})=><FormControl fullWidth error={Boolean(errors.visitId)}><InputLabel id="evidence-visit-label">Active visit</InputLabel><Select {...field} labelId="evidence-visit-label" label="Active visit" onChange={(event)=>{field.onChange(event);setServiceRequestId('');}}>{available.map((visit)=><MenuItem key={visit.visitId} value={visit.visitId}>{visit.customerName??'Customer'} · {visit.propertyName??'Property'} · {visit.serviceName??'Service'} · {new Date(visit.scheduledAt).toLocaleString()}</MenuItem>)}</Select>{errors.visitId&&<FormHelperText>{errors.visitId.message}</FormHelperText>}</FormControl>}/>}
      {!initialVisitId&&!initialServiceRequestId&&requestOptions.length>0&&<TextField select fullWidth label="In-progress no-visit service" value={serviceRequestId} onChange={(event)=>{setServiceRequestId(event.target.value);reset({visitId:''});}} helperText="Evidence can be attached directly when the service does not require a visit.">{requestOptions.map((request)=><MenuItem key={request.serviceRequestId} value={request.serviceRequestId}>{request.customerName} · {request.propertyTitle} · {request.serviceName??'Service'} · {new Date(request.requestedAt).toLocaleDateString()}</MenuItem>)}</TextField>}
      {selected&&types.data&&<EvidenceUploader key={selected.visitId} visitId={selected.visitId} evidenceTypes={types.data} onUploaded={()=>navigate(agentOnly?`/agent/visits/${selected.visitId}/evidence`:`/visits/${selected.visitId}/evidence`)}/>}
      {selectedRequest&&types.data&&<EvidenceUploader key={selectedRequest.serviceRequestId} serviceRequestId={selectedRequest.serviceRequestId} evidenceTypes={types.data} onUploaded={()=>navigate(agentOnly?`/agent/service-requests/${selectedRequest.serviceRequestId}`:`/service-requests/${selectedRequest.serviceRequestId}`)}/>}
    </>}
  </Stack>;
}
