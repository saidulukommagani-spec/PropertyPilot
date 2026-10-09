import { Box, Card, CardContent, Divider, Stack, Typography } from '@mui/material';
import type { EvidenceDetail } from '../types/evidence';

const formatDate=(value?:string)=>value?new Intl.DateTimeFormat(undefined,{dateStyle:'medium',timeStyle:'short'}).format(new Date(value)):'—';
const label=(status:string)=>status==='UPLOADED'?'Uploaded':status==='REUPLOAD_REQUESTED'?'Reupload requested':status.charAt(0)+status.slice(1).toLowerCase();
export default function EvidenceTimeline({evidence}:{evidence:EvidenceDetail}){
  const history=evidence.validationHistory??[];
  const events=[{key:'created',status:'Created',at:evidence.createdAt??evidence.capturedAt,user:evidence.uploadedByName,comment:undefined},...history.map((item)=>({key:item.historyId,status:item.status,at:item.occurredAt,user:item.userName,comment:item.comment}))]
    .filter((item)=>Boolean(item.at)).sort((left,right)=>(left.at??'').localeCompare(right.at??''));
  return <Card component="section" aria-labelledby="evidence-timeline-heading"><CardContent><Typography id="evidence-timeline-heading" variant="h6" fontWeight={700}>Evidence timeline</Typography><Divider sx={{my:2}}/>
    {!events.length?<Typography color="text.secondary">No timeline activity is available.</Typography>:<Stack component="ol" spacing={0} sx={{listStyle:'none',p:0,m:0}}>{events.map((item,index)=><Stack component="li" key={item.key} direction="row" spacing={1.5} sx={{pb:index===events.length-1?0:2}}><Stack alignItems="center" sx={{width:16,flexShrink:0}}><Box aria-hidden="true" sx={{width:11,height:11,borderRadius:'50%',bgcolor:'primary.main',mt:.6}}/>{index<events.length-1&&<Box aria-hidden="true" sx={{width:2,flex:1,bgcolor:'divider',minHeight:28}}/>}</Stack><Stack><Typography fontWeight={650}>{label(item.status)}</Typography><Typography variant="body2" color="text.secondary">{formatDate(item.at)} · {item.user||'User not recorded'}</Typography>{item.comment&&<Typography variant="body2">{item.comment}</Typography>}</Stack></Stack>)}</Stack>}
  </CardContent></Card>;
}
