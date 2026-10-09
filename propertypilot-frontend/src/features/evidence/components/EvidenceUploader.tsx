import { useEffect, useMemo, useRef, useState } from 'react';
import { Alert, Box, Button, Card, CardContent, CardMedia, CircularProgress, LinearProgress, MenuItem, Stack, TextField, Typography } from '@mui/material';
import CloudUploadRounded from '@mui/icons-material/CloudUploadRounded';
import GpsFixedRounded from '@mui/icons-material/GpsFixedRounded';
import { zodResolver } from '@hookform/resolvers/zod';
import { Controller, useForm } from 'react-hook-form';
import { z } from 'zod';
import type { EvidenceTypeResponse, UploadEvidenceRequest } from '../types/evidence';
import { useUploadEvidence } from '../hooks/useEvidence';

const schema=z.object({evidenceTypeId:z.string().min(1,'Choose an evidence type.'),files:z.array(z.instanceof(File)).min(1,'Add at least one file.'),remarks:z.string().max(2000,'Notes cannot exceed 2,000 characters.')});
type Values=z.infer<typeof schema>;
type Props = ({ visitId:string; serviceRequestId?:never } | { serviceRequestId:string; visitId?:never }) & { evidenceTypes:EvidenceTypeResponse[]; onUploaded?():void };

export default function EvidenceUploader(props:Props){
  const {evidenceTypes,onUploaded}=props;
  const propsTarget=props.visitId!==undefined?{visitId:props.visitId}:{serviceRequestId:props.serviceRequestId};
  const upload=useUploadEvidence();const [coordinates,setCoordinates]=useState<{latitude:number;longitude:number}|null>(null);const [gpsError,setGpsError]=useState('');const [capturingGps,setCapturingGps]=useState(false);const [dragging,setDragging]=useState(false);const [formError,setFormError]=useState('');const [progress,setProgress]=useState<Record<number,number>>({});const [uploadedCount,setUploadedCount]=useState(0);const [uploading,setUploading]=useState(false);const uploadLock=useRef(false);
  const {control,register,handleSubmit,setValue,watch,formState:{errors}}=useForm<Values>({resolver:zodResolver(schema),defaultValues:{evidenceTypeId:'',files:[],remarks:''}});
  const files=watch('files');const previews=useMemo(()=>files.map((file)=>URL.createObjectURL(file)),[files]);
  useEffect(()=>()=>previews.forEach((url)=>URL.revokeObjectURL(url)),[previews]);
  const locked=uploading||upload.isPending;
  useEffect(()=>{
    if(files.length===0)return;
    const protectPendingFiles=(event:BeforeUnloadEvent)=>{event.preventDefault();event.returnValue='';};
    window.addEventListener('beforeunload',protectPendingFiles);
    return ()=>window.removeEventListener('beforeunload',protectPendingFiles);
  },[files.length]);
  const addFiles=(incoming:File[])=>{if(locked)return;const known=new Set(files.map((file)=>`${file.name}:${file.size}:${file.lastModified}`));const unique=incoming.filter((file)=>{const key=`${file.name}:${file.size}:${file.lastModified}`;if(known.has(key))return false;known.add(key);return true;});if(unique.length){setValue('files',[...files,...unique],{shouldValidate:true,shouldDirty:true});setProgress({});setUploadedCount(0);setFormError('');}};
  const removeFile=(index:number)=>{if(!locked)setValue('files',files.filter((_,i)=>i!==index),{shouldValidate:true,shouldDirty:true});};
  const captureGps=()=>{
    setGpsError('');if(!navigator.geolocation){setGpsError('Location services are not available in this browser.');return;}
    setCapturingGps(true);navigator.geolocation.getCurrentPosition((position)=>{setCoordinates({latitude:position.coords.latitude,longitude:position.coords.longitude});setCapturingGps(false);},(error)=>{const messages:Record<number,string>={1:'Location permission was denied. Allow location access and try again.',2:'Your device could not determine a location. Try again when you have a stronger signal.',3:'Location capture timed out. Try again.'};setGpsError(messages[error.code]??'Location could not be captured.');setCapturingGps(false);},{enableHighAccuracy:true,timeout:10000,maximumAge:0});
  };
  const submit=handleSubmit(async(values)=>{
    if(uploadLock.current)return;
    uploadLock.current=true;setUploading(true);setFormError('');setProgress({});setUploadedCount(0);
    try{
      for(let index=0;index<values.files.length;index+=1){
        const file=values.files[index];const request:UploadEvidenceRequest={...(propsTarget),evidenceTypeId:values.evidenceTypeId,file,capturedAt:new Date().toISOString(),deviceInfo:navigator.userAgent,latitude:coordinates?.latitude,longitude:coordinates?.longitude,remarks:values.remarks.trim()||undefined};
        try{await upload.mutateAsync({request,onProgress:(percentage)=>setProgress((current)=>({...current,[index]:percentage}))});setUploadedCount(index+1);}
        catch(error){setValue('files',values.files.slice(index),{shouldValidate:true,shouldDirty:true});setProgress({});setFormError(`${error instanceof Error?error.message:`Upload failed for ${file.name}.`} ${index>0?`${index} earlier file${index===1?' was':'s were'} uploaded successfully; retry will continue with the remaining files.`:'Your file remains selected so you can retry.'}`);return;}
      }
      setValue('files',[],{shouldValidate:true});setValue('remarks','');setProgress({});onUploaded?.();
    }finally{uploadLock.current=false;setUploading(false);}
  });
  const acceptFiles=(incoming:FileList|null)=>{if(incoming)addFiles(Array.from(incoming));};
  return <Card component="section" aria-labelledby="evidence-uploader-title"><CardContent sx={{p:{xs:2,sm:2.5}}}>
    <Stack spacing={2}>
      <Box><Typography id="evidence-uploader-title" variant="h6" fontWeight={700}>Upload evidence</Typography><Typography variant="body2" color="text.secondary">Each upload records its upload time and device; GPS coordinates are optional. Evidence stays pending until Operations reviews it.</Typography></Box>
      {formError&&<Alert severity="error">{formError}</Alert>}{gpsError&&<Alert severity="warning">{gpsError}</Alert>}
      <Controller name="evidenceTypeId" control={control} render={({field})=><TextField {...field} select required disabled={locked} label="Evidence type" error={Boolean(errors.evidenceTypeId)} helperText={errors.evidenceTypeId?.message}>
        {evidenceTypes.map((type)=><MenuItem key={type.evidenceTypeId} value={type.evidenceTypeId}>{type.name} · {type.category}</MenuItem>)}
      </TextField>}/>
      {!evidenceTypes.length&&<Alert severity="info">No active evidence types are configured. Contact an administrator.</Alert>}
      <Box aria-disabled={locked} onDragEnter={(event)=>{event.preventDefault();if(!locked)setDragging(true);}} onDragOver={(event)=>event.preventDefault()} onDragLeave={(event)=>{if(!event.currentTarget.contains(event.relatedTarget as Node|null))setDragging(false);}} onDrop={(event)=>{event.preventDefault();setDragging(false);if(!locked)acceptFiles(event.dataTransfer.files);}} sx={{border:1,borderStyle:'dashed',borderColor:dragging?'primary.main':'divider',borderRadius:2,p:{xs:2,sm:3},textAlign:'center',bgcolor:dragging?'action.hover':'background.default',opacity:locked?0.65:1}}>
        <Stack alignItems="center" spacing={1}><CloudUploadRounded color="primary"/><Typography fontWeight={650}>Drag files here or select from your device</Typography><Typography variant="caption" color="text.secondary">Images, videos, PDF and Office documents; up to 25 MB per file.</Typography><Button component="label" variant="outlined" disabled={locked}>Select files<input hidden multiple disabled={locked} type="file" accept="image/*,video/mp4,video/quicktime,application/pdf,.doc,.docx,.txt" onChange={(event)=>{acceptFiles(event.target.files);event.target.value='';}} /></Button></Stack>
      </Box>
      {errors.files&&<Typography color="error" variant="caption">{errors.files.message}</Typography>}
      {files.length>0&&<Stack spacing={1.25}>{files.map((file,index)=><Card key={`${file.name}-${file.lastModified}-${index}`} variant="outlined"><Stack direction={{xs:'column',sm:'row'}} alignItems={{sm:'center'}} spacing={1.5} sx={{p:1.25}}>
        {file.type.startsWith('image/')&&<CardMedia component="img" image={previews[index]} alt={`Preview of ${file.name}`} sx={{width:{xs:'100%',sm:72},height:64,objectFit:'cover',borderRadius:1}}/>}
        {file.type.startsWith('video/')&&<Box component="video" src={previews[index]} controls aria-label={`Preview of ${file.name}`} sx={{width:{xs:'100%',sm:120},height:64,objectFit:'cover',borderRadius:1}}/>}
        {!file.type.startsWith('image/')&&!file.type.startsWith('video/')&&<CloudUploadRounded color="action"/>}
        <Box flex={1} minWidth={0}><Typography variant="body2" fontWeight={650} noWrap>{file.name}</Typography><Typography variant="caption" color="text.secondary">{(file.size/1024/1024).toFixed(2)} MB · {file.type||'Unknown type'}</Typography>{progress[index]!==undefined&&<LinearProgress variant="determinate" value={progress[index]} sx={{mt:.75}}/>}</Box>
        <Button size="small" color="inherit" onClick={()=>removeFile(index)} disabled={locked}>Remove</Button>
      </Stack></Card>)}</Stack>}
      <TextField {...register('remarks')} label="Notes" fullWidth multiline minRows={2} disabled={locked} error={Boolean(errors.remarks)} helperText={errors.remarks?.message}/>
      <Stack direction={{xs:'column',sm:'row'}} justifyContent="space-between" alignItems={{sm:'center'}} gap={1}>
        <Stack direction={{xs:'column',sm:'row'}} alignItems={{sm:'center'}} spacing={1}><Button type="button" variant="outlined" startIcon={capturingGps?<CircularProgress size={16}/>:<GpsFixedRounded/>} onClick={captureGps} disabled={capturingGps||locked}>Capture GPS for evidence</Button><Typography variant="caption" color="text.secondary">{coordinates?'Coordinates attached; service GPS verification is separate.':'GPS optional for this evidence file.'}</Typography></Stack>
        <Button variant="contained" sx={{width:{xs:'100%',sm:'auto'}}} startIcon={locked?<CircularProgress size={16} color="inherit"/>:<CloudUploadRounded/>} onClick={()=>void submit()} disabled={!files.length||!evidenceTypes.length||locked}>{locked?`Uploading ${Math.min(uploadedCount+1,files.length)} of ${files.length}`:'Upload evidence'}</Button>
      </Stack>
      {uploadedCount>0&&<Alert severity="success">{uploadedCount} file{uploadedCount===1?'':'s'} uploaded and sent for validation.</Alert>}
    </Stack>
  </CardContent></Card>;
}
