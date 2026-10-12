import { useQueries } from '@tanstack/react-query';
import { Alert, Box, Button, Card, CardContent, LinearProgress, Skeleton, Stack, Typography } from '@mui/material';
import { Link } from 'react-router-dom';
import { useSubscriptionCatalog, useSubscriptions } from '../hooks/useSubscriptions';
import { SubscriptionApi } from '../services/SubscriptionApi';
import { SubscriptionError, SubscriptionHeading, SubscriptionMetrics } from '../components/SubscriptionStates';
import { countSubscriptionsInDateWindow, formatActiveSubscriptionRevenue, getRenewalWindows } from '../utils/subscriptionMetrics';

export default function SubscriptionDashboardPage(){
  const record=useSubscriptions();const catalog=useSubscriptionCatalog();const subs=record.subscriptions.data??[];const active=subs.filter((item)=>item.status==='ACTIVE');
  const usage=useQueries({queries:active.map((item)=>({queryKey:['subscriptions','usage',item.customerSubscriptionId],queryFn:()=>SubscriptionApi.usage(item.customerSubscriptionId)}))});
  const versions=catalog.versions.data??[];const windows=getRenewalWindows();
  const expiring=countSubscriptionsInDateWindow(active,windows.today,windows.expiringBy);const renewals=countSubscriptionsInDateWindow(active,windows.today,windows.dueBy);
  const revenue=formatActiveSubscriptionRevenue(active,versions);
  const consumed=usage.flatMap((query)=>query.data??[]).reduce((sum,item)=>sum+(item.consumedQuantity??0),0);const entitled=usage.flatMap((query)=>query.data??[]).reduce((sum,item)=>sum+(item.entitledQuantity??0),0);
  const metrics=[{label:'Active subscriptions',value:active.length},{label:'Expiring soon · 60 days',value:expiring},{label:'Renewals due · 30 days',value:renewals},{label:'Estimated recurring value by currency',value:revenue},{label:'Usage summary',value:`${consumed} / ${entitled}`}];
  const errors=[record.profile,record.subscriptions,catalog.versions].filter((query)=>query.isError);const loading=record.subscriptions.isLoading||catalog.versions.isLoading||usage.some((query)=>query.isLoading);
  const byStatus=[['Active',active.length],['Expiring soon',expiring],['Renewals due',renewals]] as const;
  return <Stack spacing={3}><SubscriptionHeading title="Subscription Dashboard" subtitle="A live view of subscriptions, upcoming renewals, plan value, and entitlement usage." action={<Button component={Link} to="/subscriptions/plans" variant="contained">Browse plans</Button>}/>
    {errors.map((query,index)=><SubscriptionError key={index} error={query.error} retry={()=>void query.refetch()}/ >)}{usage.some((query)=>query.isError)&&<Alert severity="error">Some subscription usage could not be loaded. Refresh to retry.</Alert>}
    <SubscriptionMetrics items={metrics} loading={loading}/>
    <Box display="grid" gridTemplateColumns={{xs:'1fr',lg:'1fr 1fr'}} gap={2}><Card variant="outlined"><CardContent><Typography variant="h6" fontWeight={700}>Subscription status</Typography><Stack spacing={2} sx={{mt:2}}>{loading?<Skeleton height={140}/>:byStatus.map(([label,value])=><Box key={label}><Stack direction="row" justifyContent="space-between" sx={{mb:.5}}><Typography variant="body2">{label}</Typography><Typography fontWeight={700}>{value}</Typography></Stack><LinearProgress variant="determinate" value={subs.length?value*100/subs.length:0}/></Box>)}</Stack>{!loading&&!subs.length&&<Typography color="text.secondary" sx={{mt:2}}>Subscription activity will appear here.</Typography>}</CardContent></Card>
      <Card variant="outlined"><CardContent><Typography variant="h6" fontWeight={700}>Entitlement utilization</Typography>{loading?<Skeleton height={150}/>:usage.flatMap((query)=>query.data??[]).length?<Stack spacing={1.5} sx={{mt:2}}>{usage.flatMap((query)=>query.data??[]).map((item,index)=><Box key={`${item.serviceId}-${index}`}><Stack direction="row" justifyContent="space-between" sx={{mb:.5}}><Typography variant="body2">{item.serviceName ?? 'Included service'}</Typography><Typography variant="body2">{item.consumedQuantity} of {item.entitledQuantity}</Typography></Stack><LinearProgress variant="determinate" value={item.entitledQuantity?Math.min(100,item.consumedQuantity*100/item.entitledQuantity):0}/></Box>)}</Stack>:<Typography color="text.secondary" sx={{mt:2}}>No entitlement usage has been recorded.</Typography>}<Button component={Link} to="/subscriptions" sx={{mt:1}}>View subscriptions</Button></CardContent></Card></Box>
  </Stack>;
}
