import { useEffect, useState } from 'react';
import { Alert, Button, CircularProgress, Dialog, DialogActions, DialogContent, DialogTitle, Stack, TextField } from '@mui/material';
import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';
import { z } from 'zod';
import type { RescheduleVisitRequest } from '../types/visit';

const schema=z.object({date:z.string().min(1,'Select a date.'),time:z.string().min(1,'Select a time.'),reason:z.string().trim().min(3,'Enter a reschedule reason.').max(1000,'Reason cannot exceed 1,000 characters.'),notes:z.string().max(1000,'Notes cannot exceed 1,000 characters.')}).refine((values)=>new Date(`${values.date}T${values.time}`).getTime()>Date.now(),{message:'Select a future date and time.',path:['date']});
type Values=z.infer<typeof schema>;
const splitDate=(value:string)=>{const d=new Date(value);if(Number.isNaN(d.valueOf()))return {date:'',time:''};return {date:new Date(d.getTime()-d.getTimezoneOffset()*60_000).toISOString().slice(0,10),time:new Date(d.getTime()-d.getTimezoneOffset()*60_000).toISOString().slice(11,16)};};
interface Props { open:boolean; scheduledAt:string; isSaving:boolean; error?:string|null; onClose():void; onSubmit(request:RescheduleVisitRequest):Promise<void> }
export default function VisitRescheduleDialog({open,scheduledAt,isSaving,error,onClose,onSubmit}:Props) {
  const [confirmDiscard,setConfirmDiscard]=useState(false);
  const {register,handleSubmit,reset,formState:{errors,isDirty}}=useForm<Values>({resolver:zodResolver(schema),defaultValues:{...splitDate(scheduledAt),reason:'',notes:''}});
  useEffect(()=>{if(open){reset({...splitDate(scheduledAt),reason:'',notes:''});setConfirmDiscard(false);}},[open,scheduledAt,reset]);
  const requestClose=()=>{if(isSaving)return;if(isDirty)setConfirmDiscard(true);else onClose();};
  const discardChanges=()=>{setConfirmDiscard(false);reset({...splitDate(scheduledAt),reason:'',notes:''});onClose();};
  const submit=handleSubmit(async(values)=>onSubmit({scheduledAt:new Date(`${values.date}T${values.time}`).toISOString(),reason:values.reason.trim(),notes:values.notes.trim()||undefined}));
  return <Dialog open={open} onClose={requestClose} fullWidth maxWidth="sm" aria-labelledby="reschedule-visit-title"><DialogTitle id="reschedule-visit-title">Reschedule visit</DialogTitle><DialogContent><Stack component="form" id="visit-reschedule-form" spacing={2} sx={{pt:1}} onSubmit={(event)=>{void submit(event);}}>
    {error&&<Alert severity="error">{error}</Alert>}
    <Stack direction={{xs:'column',sm:'row'}} spacing={2}><TextField {...register('date')} label="New date" type="date" fullWidth InputLabelProps={{shrink:true}} inputProps={{min:new Date().toISOString().slice(0,10)}} error={Boolean(errors.date)} helperText={errors.date?.message}/><TextField {...register('time')} label="New time" type="time" fullWidth InputLabelProps={{shrink:true}} error={Boolean(errors.time)} helperText={errors.time?.message}/></Stack>
    <TextField {...register('reason')} label="Reason" required fullWidth multiline minRows={2} error={Boolean(errors.reason)} helperText={errors.reason?.message}/><TextField {...register('notes')} label="Visit notes" fullWidth multiline minRows={2} error={Boolean(errors.notes)} helperText={errors.notes?.message}/>
  </Stack>{confirmDiscard&&<Alert severity="warning" sx={{mt:2}} action={<Stack direction="row" spacing={0.5}><Button size="small" color="inherit" onClick={()=>setConfirmDiscard(false)}>Keep editing</Button><Button size="small" color="inherit" onClick={discardChanges}>Discard</Button></Stack>}>Unsaved schedule changes will be lost.</Alert>}</DialogContent><DialogActions><Button onClick={requestClose} disabled={isSaving}>Close</Button><Button type="submit" form="visit-reschedule-form" variant="contained" disabled={isSaving}>{isSaving?<CircularProgress size={18}/>: 'Save schedule'}</Button></DialogActions></Dialog>;
}
