import { useEffect } from 'react';
import { Alert, Box, Button, Card, CardContent, FormControl, FormHelperText, InputLabel, MenuItem, Select, Skeleton, Stack, TextField, Typography } from '@mui/material';
import ArrowBackRounded from '@mui/icons-material/ArrowBackRounded';
import { zodResolver } from '@hookform/resolvers/zod';
import { Controller, useForm, useWatch } from 'react-hook-form';
import { z } from 'zod';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { serviceRequestApi } from '../../../api/serviceRequestApi';
import { VisitApi } from '../../../api/VisitApi';
import { useCreateVisit, useRescheduleVisit } from '../hooks/useVisits';
import { VisitService } from '../services/VisitService';

const fields={date:z.string().min(1,'Select a date.'),time:z.string().min(1,'Select a time.'),notes:z.string().max(1000,'Notes cannot exceed 1,000 characters.')};
const createSchema=z.object({...fields,serviceRequestId:z.string().min(1,'Select a service request.'),assignmentId:z.string().min(1,'Select an accepted agent assignment.')}).refine((values)=>new Date(`${values.date}T${values.time}`).getTime()>Date.now(),{message:'Select a future date and time.',path:['date']});
const editSchema=z.object({...fields,reason:z.string().trim().min(3,'Enter a reschedule reason.').max(1000,'Reason cannot exceed 1,000 characters.')}).refine((values)=>new Date(`${values.date}T${values.time}`).getTime()>Date.now(),{message:'Select a future date and time.',path:['date']});
type CreateValues=z.infer<typeof createSchema>; type EditValues=z.infer<typeof editSchema>;
const localParts=(value?:string)=>{if(!value)return {date:'',time:''};const d=new Date(value);if(Number.isNaN(d.valueOf()))return {date:'',time:''};const local=new Date(d.getTime()-d.getTimezoneOffset()*60000).toISOString();return {date:local.slice(0,10),time:local.slice(11,16)};};

export default function VisitManagementFormPage(){
  const {visitId=''}=useParams<{visitId:string}>();const editing=Boolean(visitId);const navigate=useNavigate();
  const requests=useQuery({queryKey:['service-requests'],queryFn:serviceRequestApi.list,enabled:!editing});
  const serviceCatalog=useQuery({queryKey:['service-catalog','active'],queryFn:VisitApi.listServices,enabled:!editing});
  const visit=useQuery({queryKey:['visits','detail',visitId],queryFn:()=>VisitApi.getDetails(visitId),enabled:editing});
  const requestId=visit.data?.serviceRequestId??'';
  const requestDetail=useQuery({queryKey:['service-request-detail',requestId],queryFn:()=>serviceRequestApi.getDetails(requestId),enabled:Boolean(requestId)});
  const createForm=useForm<CreateValues>({resolver:zodResolver(createSchema),defaultValues:{serviceRequestId:'',assignmentId:'',date:'',time:'',notes:''}});
  const editForm=useForm<EditValues>({resolver:zodResolver(editSchema),defaultValues:{date:'',time:'',notes:'',reason:''}});
  const selectedRequestId=useWatch({control:createForm.control,name:'serviceRequestId'});
  const assignmentHistory=useQuery({queryKey:['service-request-assignment-history',selectedRequestId],queryFn:()=>serviceRequestApi.getAssignmentHistory(selectedRequestId),enabled:!editing&&Boolean(selectedRequestId)});
  const assignments=(assignmentHistory.data??[]).filter((entry)=>['ACCEPTED','IN_PROGRESS'].includes(entry.assignmentStatus));
  const eligibleRequests=VisitService.eligibleRequests(requests.data??[],serviceCatalog.data??[]);
  const create=useCreateVisit(selectedRequestId);const reschedule=useRescheduleVisit();
  useEffect(()=>{if(editing&&visit.data){const parts=localParts(visit.data.scheduledAt);editForm.reset({...parts,notes:visit.data.notes??'',reason:''});}},[editing,visit.data,editForm]);
  const createSubmit=createForm.handleSubmit(async(values)=>{try{const result=await create.mutateAsync({assignmentId:values.assignmentId,scheduledAt:new Date(`${values.date}T${values.time}`).toISOString(),notes:values.notes.trim()||undefined});navigate(`/visits/${result.visitId}`,{replace:true});}catch{/* Mutation error is shown in the form. */}});
  const editSubmit=editForm.handleSubmit(async(values)=>{try{await reschedule.mutateAsync({visitId,request:{scheduledAt:new Date(`${values.date}T${values.time}`).toISOString(),notes:values.notes.trim()||undefined,reason:values.reason.trim()}});navigate(`/visits/${visitId}`,{replace:true});}catch{/* Mutation error is shown in the form. */}});
  if(editing&&visit.isLoading)return <Stack spacing={2}><Skeleton height={42}/><Skeleton variant="rounded" height={320}/></Stack>;
  if(editing&&(visit.isError||!visit.data))return <Stack spacing={2}><Button component={Link} to="/visits" startIcon={<ArrowBackRounded/>}>Back to Visits</Button><Alert severity="error" action={<Button color="inherit" size="small" onClick={()=>void visit.refetch()}>Retry</Button>}>{visit.error instanceof Error?visit.error.message:'Visit could not be loaded.'}</Alert></Stack>;
  if(editing&&visit.data?.status!=='SCHEDULED')return <Stack spacing={2}><Button component={Link} to={`/visits/${visitId}`} startIcon={<ArrowBackRounded/>}>Back to Visit</Button><Alert severity="info">Only scheduled visits can be edited or rescheduled.</Alert></Stack>;
  const isPending=editing?reschedule.isPending:create.isPending;const error=editing?reschedule.error:create.error;
  return <Stack spacing={2.5}>
    <Button component={Link} to={editing?`/visits/${visitId}`:'/visits'} startIcon={<ArrowBackRounded/>} sx={{alignSelf:'flex-start',ml:-1}}>{editing?'Back to Visit':'Back to Visits'}</Button>
    <Box><Typography variant="h4" fontWeight={750}>{editing?'Edit / reschedule visit':'Create visit'}</Typography><Typography color="text.secondary" sx={{mt:.5}}>{editing?'Update the schedule and record why it changed.':'Schedule field work for an accepted service request assignment.'}</Typography></Box>
    {editing&&requestDetail.isError&&<Alert severity="error" action={<Button color="inherit" size="small" onClick={()=>void requestDetail.refetch()}>Retry</Button>}>Service request details could not be loaded.</Alert>}
    {!editing&&requests.isError&&<Alert severity="error" action={<Button color="inherit" size="small" onClick={()=>void requests.refetch()}>Retry</Button>}>{requests.error instanceof Error?requests.error.message:'Service requests could not be loaded.'}</Alert>}
    {!editing&&serviceCatalog.isError&&<Alert severity="error" action={<Button color="inherit" size="small" onClick={()=>void serviceCatalog.refetch()}>Retry</Button>}>Visit service configuration could not be loaded. Try again before scheduling a visit.</Alert>}
    {!editing&&assignmentHistory.isError&&<Alert severity="error" action={<Button color="inherit" size="small" onClick={()=>void assignmentHistory.refetch()}>Retry</Button>}>Available agent assignments could not be loaded.</Alert>}
    {error&&<Alert severity="error">{error instanceof Error?error.message:'Unable to save this visit.'}</Alert>}
    {editing? <Card component="form" onSubmit={(event)=>{void editSubmit(event);}} noValidate><CardContent><Stack spacing={2.5}>
      <Stack direction={{xs:'column',sm:'row'}} spacing={2}><TextField label="Service request" value={requestDetail.data?`${requestDetail.data.customerName} · ${requestDetail.data.propertyTitle}`:requestDetail.isLoading?'Loading request details…':'Request details unavailable'} fullWidth disabled/><TextField label="Agent" value={visit.data?.agentName??'—'} fullWidth disabled/></Stack>
      <Stack direction={{xs:'column',sm:'row'}} spacing={2}><TextField {...editForm.register('date')} label="Visit date" type="date" fullWidth InputLabelProps={{shrink:true}} inputProps={{min:new Date().toISOString().slice(0,10)}} error={Boolean(editForm.formState.errors.date)} helperText={editForm.formState.errors.date?.message}/><TextField {...editForm.register('time')} label="Visit time" type="time" fullWidth InputLabelProps={{shrink:true}} error={Boolean(editForm.formState.errors.time)} helperText={editForm.formState.errors.time?.message}/></Stack>
      <TextField {...editForm.register('reason')} label="Reschedule reason" required fullWidth error={Boolean(editForm.formState.errors.reason)} helperText={editForm.formState.errors.reason?.message}/><TextField {...editForm.register('notes')} label="Visit notes" multiline minRows={3} fullWidth error={Boolean(editForm.formState.errors.notes)} helperText={editForm.formState.errors.notes?.message}/>
      <Stack direction="row" justifyContent="flex-end" spacing={1}><Button component={Link} to={`/visits/${visitId}`} disabled={isPending}>Cancel</Button><Button type="submit" variant="contained" disabled={isPending}>{isPending?'Saving…':'Save changes'}</Button></Stack>
    </Stack></CardContent></Card> : <Card component="form" onSubmit={(event)=>{void createSubmit(event);}} noValidate><CardContent><Stack spacing={2.5}>
      <Controller name="serviceRequestId" control={createForm.control} render={({field})=><FormControl fullWidth error={Boolean(createForm.formState.errors.serviceRequestId)}><InputLabel id="visit-request-label">Service request</InputLabel><Select {...field} labelId="visit-request-label" label="Service request" disabled={requests.isLoading||serviceCatalog.isLoading||serviceCatalog.isError}>{eligibleRequests.map((entry)=><MenuItem value={entry.serviceRequestId} key={entry.serviceRequestId}>{entry.customerName} · {entry.propertyTitle} · {(entry.serviceName||entry.requestType)}</MenuItem>)}</Select>{createForm.formState.errors.serviceRequestId&&<FormHelperText>{createForm.formState.errors.serviceRequestId.message}</FormHelperText>}{!requests.isLoading&&!serviceCatalog.isLoading&&!serviceCatalog.isError&&!eligibleRequests.length&&<FormHelperText>No accepted requests for services configured for field visits. Remote services and services without a visit workflow are excluded.</FormHelperText>}</FormControl>}/>
      <Controller name="assignmentId" control={createForm.control} render={({field})=><FormControl fullWidth error={Boolean(createForm.formState.errors.assignmentId)}><InputLabel id="visit-assignment-label">Agent</InputLabel><Select {...field} labelId="visit-assignment-label" label="Agent" disabled={!selectedRequestId||assignmentHistory.isLoading}>{assignments.map((entry)=><MenuItem key={entry.assignmentId} value={entry.assignmentId}>{entry.agentName||'Agent'}{entry.agentCode?` · ${entry.agentCode}`:''} · {entry.assignmentStatus.replaceAll('_',' ')}</MenuItem>)}</Select>{createForm.formState.errors.assignmentId&&<FormHelperText>{createForm.formState.errors.assignmentId.message}</FormHelperText>}{selectedRequestId&&!assignmentHistory.isLoading&&!assignments.length&&<FormHelperText>No accepted agent assignment is available for this request.</FormHelperText>}</FormControl>}/>
      <Stack direction={{xs:'column',sm:'row'}} spacing={2}><TextField {...createForm.register('date')} label="Visit date" type="date" fullWidth InputLabelProps={{shrink:true}} inputProps={{min:new Date().toISOString().slice(0,10)}} error={Boolean(createForm.formState.errors.date)} helperText={createForm.formState.errors.date?.message}/><TextField {...createForm.register('time')} label="Visit time" type="time" fullWidth InputLabelProps={{shrink:true}} error={Boolean(createForm.formState.errors.time)} helperText={createForm.formState.errors.time?.message}/></Stack>
      <TextField {...createForm.register('notes')} label="Visit notes" multiline minRows={3} fullWidth error={Boolean(createForm.formState.errors.notes)} helperText={createForm.formState.errors.notes?.message}/>
      <Stack direction="row" justifyContent="flex-end" spacing={1}><Button component={Link} to="/visits" disabled={isPending}>Cancel</Button><Button type="submit" variant="contained" disabled={isPending||requests.isLoading||serviceCatalog.isLoading||serviceCatalog.isError||assignmentHistory.isLoading}>{isPending?'Creating…':'Create Visit'}</Button></Stack>
    </Stack></CardContent></Card>}
  </Stack>;
}
