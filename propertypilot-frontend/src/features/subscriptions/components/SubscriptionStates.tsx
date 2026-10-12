import { Alert, Button } from '@mui/material';
import type { ReactNode } from 'react';
import DashboardMetricCards from '../../../components/layouts/DashboardMetricCards';
import DashboardPageHeading from '../../../components/layouts/DashboardPageHeading';

export function SubscriptionHeading({title,subtitle,action}:{title:string;subtitle:string;action?:ReactNode}){return <DashboardPageHeading eyebrow="Subscription overview" title={title} subtitle={subtitle} action={action}/>;}
export function SubscriptionError({error,retry}:{error:unknown;retry:()=>void}){return <Alert severity="error" action={<Button size="small" color="inherit" onClick={retry}>Retry</Button>}>{error instanceof Error?error.message:'Subscription data could not be loaded.'}</Alert>;}
export function SubscriptionMetrics({items,loading}:{items:Array<{label:string;value:string|number}>;loading:boolean}){return <DashboardMetricCards items={items} loading={loading} columns={{xs:2,md:3,xl:5}}/>;}
