import { useEffect, useState } from 'react';
import { Alert, Box, Button, Card, CardContent, Skeleton, Stack, Typography } from '@mui/material';
import DownloadRounded from '@mui/icons-material/DownloadRounded';
import { apiClient } from '../../../api/client';

export default function EvidenceFilePreview({evidenceId,fileName,mediaType,fileUrl}:{evidenceId:string;fileName?:string;mediaType?:string;fileUrl?:string}){
  const [url,setUrl]=useState('');const [error,setError]=useState('');
  useEffect(()=>{const controller=new AbortController();let current='';let external='';setUrl('');setError('');try{const parsed=fileUrl?new URL(fileUrl,window.location.origin):null;if(parsed&&(parsed.protocol==='https:'||parsed.protocol==='http:')&&parsed.origin!==window.location.origin)external=parsed.href;}catch{/* The backend file route is used for relative URLs. */}if(external){setUrl(external);return()=>{controller.abort();};}void apiClient.get<Blob>(`/api/evidence/${evidenceId}/file`,{responseType:'blob',signal:controller.signal}).then((response)=>{current=URL.createObjectURL(response.data);setUrl(current);}).catch((cause:unknown)=>{if(!controller.signal.aborted)setError(cause instanceof Error?cause.message:'File preview could not be loaded.');});return()=>{controller.abort();if(current)URL.revokeObjectURL(current);};},[evidenceId,fileUrl]);
  if(error)return <Alert severity="error">{error}</Alert>;
  if(!url)return <Skeleton variant="rounded" height={240}/>;
  const type=mediaType??'';
  return <Card variant="outlined"><CardContent><Stack direction="row" justifyContent="space-between" alignItems="center" spacing={1}><Typography fontWeight={650} noWrap>{fileName||'Uploaded file'}</Typography><Button component="a" href={url} download={fileName||true} size="small" startIcon={<DownloadRounded/>}>Download</Button></Stack>
    {type.startsWith('image/')?<Box component="img" src={url} alt={fileName||'Evidence image'} sx={{display:'block',maxWidth:'100%',maxHeight:560,objectFit:'contain',mx:'auto',mt:2}}/>:type.startsWith('video/')?<Box component="video" src={url} controls sx={{display:'block',width:'100%',maxHeight:560,mt:2}}/>:type==='application/pdf'?<Box component="iframe" src={url} title={fileName||'Evidence PDF'} sx={{width:'100%',height:560,border:0,mt:2}}/>:<Typography color="text.secondary" sx={{mt:2}}>Preview is not available for this file type. Download the file to view it.</Typography>}
  </CardContent></Card>;
}
