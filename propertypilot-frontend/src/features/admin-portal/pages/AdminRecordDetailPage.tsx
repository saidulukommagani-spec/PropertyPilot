import { useMemo, useState } from 'react';
import { Alert, Box, Button, Card, CardContent, Divider, Link as MuiLink, Skeleton, Stack, Tab, Tabs, Typography } from '@mui/material';
import ArrowBackRounded from '@mui/icons-material/ArrowBackRounded';
import { Link, useParams } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { apiClient } from '../../../api/client';
import { useAdminDirectory, useAdminRecord } from '../hooks/useAdminPortal';
import { directoryLabels } from '../adminPortalApi';
import type { AdminResource, AdminRecord } from '../types';
import PropertyDocumentFileAction from '../../documents/PropertyDocumentFileAction';

const resources: AdminResource[] = ['customers','properties','requests','agents','visits','evidence','quotes','deals','invoices','payments','subscriptions','services','pricing','plans','users'];
const formatKey = (value: string) => value.replace(/([A-Z])/g, ' $1').replaceAll('_', ' ').replace(/^./, (character) => character.toUpperCase());
const display = (value: unknown): string => {
  if (value === null || value === undefined || value === '') return '—';
  if (typeof value === 'boolean') return value ? 'Yes' : 'No';
  if (Array.isArray(value)) return value.map(display).join(', ');
  if (typeof value === 'object') return JSON.stringify(value);
  return String(value);
};
interface RelatedItem { id: string; title: string; status?: string; date?: string; path?: string }

function RelatedList({ title, items, emptyText }: { title: string; items: RelatedItem[]; emptyText: string }) {
  return <Card variant="outlined"><CardContent><Typography variant="subtitle1" fontWeight={700}>{title}</Typography><Divider sx={{ my: 1.5 }}/>
    {!items.length ? <Typography color="text.secondary" variant="body2">{emptyText}</Typography> : <Stack spacing={1}>{items.map((item) => <Stack key={item.id} direction={{ xs: 'column', sm: 'row' }} justifyContent="space-between" gap={0.5}>
      <Box>{item.path ? <MuiLink component={Link} to={item.path} underline="hover" fontWeight={650}>{item.title}</MuiLink> : <Typography fontWeight={650}>{item.title}</Typography>}{item.date && <Typography variant="caption" display="block" color="text.secondary">{item.date}</Typography>}</Box>
      {item.status && <Typography variant="body2" color="text.secondary">{item.status.replaceAll('_', ' ')}</Typography>}
    </Stack>)}</Stack>}
  </CardContent></Card>;
}

function PropertyDocumentsList({ records, emptyText }: { records: AdminRecord[]; emptyText: string }) {
  return <Card variant="outlined"><CardContent><Typography variant="subtitle1" fontWeight={700}>Property documents</Typography><Divider sx={{ my: 1.5 }}/>
    {!records.length ? <Typography color="text.secondary" variant="body2">{emptyText}</Typography> : <Stack spacing={1.5}>{records.map((record, index) => {
      const documentId = text(record, 'documentId', 'id') || `property-document-${index}`;
      return <Stack key={documentId} direction={{ xs: 'column', sm: 'row' }} alignItems={{ sm: 'center' }} justifyContent="space-between" gap={1}>
        <Box><Typography fontWeight={650}>{text(record, 'documentName', 'name', 'title') || 'Document'}</Typography>
          <Typography variant="body2" color="text.secondary">{text(record, 'documentType')}{text(record, 'propertyTitle') ? ` · ${text(record, 'propertyTitle')}` : ''}</Typography>
        </Box>
        <Stack direction="row" alignItems="center" spacing={1}><Typography variant="body2" color="text.secondary">{text(record, 'status').replaceAll('_', ' ')}</Typography>
          {record.hasFile === true ? <PropertyDocumentFileAction documentId={documentId} fileName={text(record, 'fileName')} /> : <Typography variant="caption" color="text.secondary">No file uploaded</Typography>}
        </Stack>
      </Stack>;
    })}</Stack>}
  </CardContent></Card>;
}

function valuesAsRows(data: unknown): AdminRecord[] { return Array.isArray(data) ? data.filter((item): item is AdminRecord => Boolean(item) && typeof item === 'object') : []; }
const text = (record: AdminRecord, ...keys: string[]) => { for (const key of keys) { const value = record[key]; if (typeof value === 'string' || typeof value === 'number') return String(value); } return ''; };
const dateLabel = (value: string) => { if (!value) return ''; const parsed = new Date(value); return Number.isNaN(parsed.valueOf()) ? value : new Intl.DateTimeFormat(undefined, { dateStyle: 'medium' }).format(parsed); };

export default function AdminRecordDetailPage() {
  const { resource: param = '', id = '' } = useParams<{ resource: string; id: string }>();
  const valid = resources.includes(param as AdminResource); const resource = (valid ? param : 'customers') as AdminResource;
  const query = useAdminRecord(resource, id); const [tab, setTab] = useState(0);
  const propertyDirectory = useAdminDirectory('properties');
  const requestDirectory = useAdminDirectory('requests');
  const subscriptionDirectory = useAdminDirectory('subscriptions');
  const visitDirectory = useAdminDirectory('visits');
  const propertyDocuments = useQuery({ queryKey: ['admin-property-documents', id], queryFn: async () => (await apiClient.get<unknown>(`/api/v1/properties/${id}/documents`)).data, enabled: resource === 'properties' && Boolean(id) });
  const ownerDirectory = useQuery({ queryKey: ['admin-property-owners', id], queryFn: async () => (await apiClient.get<unknown>(`/api/v1/properties/${id}/owners`)).data, enabled: resource === 'properties' && Boolean(id) });
  const data = query.data;
  const profileFields = useMemo(() => data ? Object.entries(data).filter(([key, value]) => !key.toLowerCase().endsWith('id') && value !== undefined && value !== null && typeof value !== 'object') : [], [data]);
  if (!valid) return <Alert severity="error">Unknown admin module.</Alert>;
  if (query.isLoading) return <Stack spacing={2}><Skeleton height={42}/><Skeleton variant="rounded" height={250}/></Stack>;
  if (query.isError || !data) return <Stack spacing={2}><Button component={Link} to={`/staff/${resource}`} startIcon={<ArrowBackRounded/>} sx={{ alignSelf: 'flex-start' }}>Back to {directoryLabels[resource].label}</Button><Alert severity="error" action={<Button color="inherit" size="small" onClick={() => void query.refetch()}>Retry</Button>}>{query.error instanceof Error ? query.error.message : 'Unable to load this record.'}</Alert></Stack>;
  const title = text(data, 'fullName', 'title', 'invoiceNumber', 'serviceName', 'name', 'status') || `${directoryLabels[resource].label} record`;
  const customerProperties = (propertyDirectory.data ?? []).filter((entry) => entry.customerId === id);
  const customerSubscriptions = (subscriptionDirectory.data ?? []).filter((entry) => entry.customerId === id);
  const customerRequests = (requestDirectory.data ?? []).filter((entry) => entry.customerId === id);
  const propertyRequests = (requestDirectory.data ?? []).filter((entry) => entry.propertyId === id);
  const propertySubscriptions = (subscriptionDirectory.data ?? []).filter((entry) => entry.customerId === text(data, 'customerId'));
  const visitsForAgent = (visitDirectory.data ?? []).filter((entry) => entry.relationId === id);
  const relatedItems = (entries: typeof customerProperties, titleKey = 'title'): RelatedItem[] => entries.map((entry) => ({ id: entry.id, title: entry.title || text(entry as unknown as AdminRecord, titleKey), status: entry.status, date: dateLabel(entry.date), path: entry.detailPath }));
  const tabs = resource === 'customers' ? ['Profile','Properties','Subscriptions','Service Requests','Activity'] : resource === 'properties' ? ['Overview','Owner Details','Documents','Service History','Subscription History','Activity'] : resource === 'agents' ? ['Profile','Visits','Performance'] : ['Details'];
  const activityRecords: RelatedItem[] = resource === 'customers' ? [
    ...customerRequests.map((entry) => ({ id: entry.id, title: `Service request ${entry.title}`, status: entry.status, date: dateLabel(entry.date), path: entry.detailPath })),
    ...customerSubscriptions.map((entry) => ({ id: entry.id, title: `Subscription ${entry.title}`, status: entry.status, date: dateLabel(entry.date), path: entry.detailPath })),
  ].sort((left, right) => (right.date || '').localeCompare(left.date || '')) : propertyRequests.map((entry) => ({ id: entry.id, title: `Service request ${entry.title}`, status: entry.status, date: dateLabel(entry.date), path: entry.detailPath }));
  const visitRecordRows: RelatedItem[] = visitsForAgent.map((entry) => ({ id: entry.id, title: entry.title, status: entry.status, date: dateLabel(entry.date), path: entry.detailPath }));
  const propertyDocumentRows = valuesAsRows(propertyDocuments.data);

  return <Stack spacing={2.5}>
    <Button component={Link} to={`/staff/${resource}`} startIcon={<ArrowBackRounded/>} sx={{ alignSelf: 'flex-start', ml: -1 }}>Back to {directoryLabels[resource].label}</Button>
      <Box><Typography variant="overline" color="text.secondary">{directoryLabels[resource].label}</Typography><Typography variant="h4" fontWeight={750}>{title}</Typography></Box>
    {resource === 'customers' && <Button component={Link} to={`/staff/customers/${id}/edit`} variant="outlined" sx={{ alignSelf: 'flex-start' }}>Edit contact details</Button>}
    <Card><CardContent><Tabs value={tab} onChange={(_, value: number) => setTab(value)} variant="scrollable" scrollButtons="auto" aria-label="Record sections">{tabs.map((name) => <Tab key={name} label={name}/>)}</Tabs><Divider sx={{ mb: 2.5 }}/>
      {(resource === 'customers' && tab === 0 || resource === 'properties' && tab === 0 || resource === 'agents' && tab === 0 || tabs.length === 1) && <Stack direction={{ xs: 'column', sm: 'row' }} flexWrap="wrap" gap={2.5}>{profileFields.map(([key, value]) => <Stack key={key} sx={{ minWidth: { sm: 220 }, maxWidth: 520 }}><Typography variant="caption" color="text.secondary">{formatKey(key)}</Typography><Typography sx={{ overflowWrap: 'anywhere' }}>{display(value)}</Typography></Stack>)}</Stack>}
      {resource === 'customers' && tab === 1 && <RelatedList title="Properties" items={relatedItems(customerProperties)} emptyText="This customer has no properties."/>}
      {resource === 'customers' && tab === 2 && <RelatedList title="Subscriptions" items={relatedItems(customerSubscriptions)} emptyText="This customer has no subscription records."/>}
      {resource === 'customers' && tab === 3 && <RelatedList title="Service Requests" items={relatedItems(customerRequests)} emptyText="This customer has no service requests."/>}
      {resource === 'customers' && tab === 4 && <RelatedList title="Activity Timeline" items={activityRecords} emptyText="No activity records are available."/>}
      {resource === 'properties' && tab === 1 && (ownerDirectory.isLoading ? <Skeleton height={100}/> : ownerDirectory.isError ? <Alert severity="error" action={<Button color="inherit" size="small" onClick={() => void ownerDirectory.refetch()}>Retry</Button>}>Owner information could not be loaded.</Alert> : <Stack spacing={1}><Typography variant="subtitle1" fontWeight={700}>Owner Details</Typography><Typography>{display(ownerDirectory.data)}</Typography></Stack>)}
      {resource === 'properties' && tab === 2 && (propertyDocuments.isLoading ? <Skeleton height={100}/> : propertyDocuments.isError ? <Alert severity="error" action={<Button color="inherit" size="small" onClick={() => void propertyDocuments.refetch()}>Retry</Button>}>Property documents could not be loaded.</Alert> : <PropertyDocumentsList records={propertyDocumentRows} emptyText="No documents have been uploaded for this property."/>)}
      {resource === 'properties' && tab === 3 && <RelatedList title="Service History" items={relatedItems(propertyRequests)} emptyText="No service requests are recorded for this property."/>}
      {resource === 'properties' && tab === 4 && <RelatedList title="Subscription History" items={relatedItems(propertySubscriptions)} emptyText="No customer subscriptions are available."/>}
      {resource === 'properties' && tab === 5 && <RelatedList title="Activity Timeline" items={activityRecords} emptyText="No activity records are available."/>}
      {resource === 'agents' && tab === 1 && <RelatedList title="Visits" items={visitRecordRows} emptyText="No visits are assigned to this agent."/>}
      {resource === 'agents' && tab === 2 && <Stack spacing={1}><Typography variant="subtitle1" fontWeight={700}>Performance</Typography><Typography>Recorded visits: {visitRecordRows.length}</Typography><Typography>Completed visits: {visitRecordRows.filter((item) => item.status === 'COMPLETED').length}</Typography><Typography variant="caption" color="text.secondary">Agent ratings are not provided by the available backend API.</Typography></Stack>}
    </CardContent></Card>
    {resource === 'agents' && visitDirectory.isError && <Alert severity="error" action={<Button color="inherit" size="small" onClick={() => void visitDirectory.refetch()}>Retry</Button>}>Agent visit history could not be loaded.</Alert>}
  </Stack>;
}
