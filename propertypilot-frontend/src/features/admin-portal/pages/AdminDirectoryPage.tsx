import { useMemo, useState } from 'react';
import { Alert, Box, Button, Card, CardContent, Checkbox, Chip, CircularProgress, Dialog, DialogActions, DialogContent, DialogTitle, FormControl, FormControlLabel, InputLabel, MenuItem, Select, Skeleton, Snackbar, Stack, Table, TableBody, TableCell, TableContainer, TableHead, TablePagination, TableRow, TextField, Typography } from '@mui/material';
import SearchRounded from '@mui/icons-material/SearchRounded';
import RefreshRounded from '@mui/icons-material/RefreshRounded';
import { Link, useParams } from 'react-router-dom';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { apiClient } from '../../../api/client';
import DashboardPageHeading from '../../../components/layouts/DashboardPageHeading';
import { adminPortalKeys, useAdminDirectory } from '../hooks/useAdminPortal';
import { adminPortalApi, directoryLabels } from '../adminPortalApi';
import type { AdminResource } from '../types';

const supported: AdminResource[] = ['customers','properties','requests','agents','visits','evidence','quotes','deals','invoices','payments','subscriptions','services','pricing','plans','users'];
const label = (value: string) => value.replaceAll('_', ' ');
function isResource(value?: string): value is AdminResource { return Boolean(value && supported.includes(value as AdminResource)); }
function statusColor(status: string): 'success'|'warning'|'error'|'info'|'default' { return ['ACTIVE','COMPLETED','PAID','ACCEPTED','CLOSED'].includes(status) ? 'success' : ['PENDING','NEW','SCHEDULED','IN_PROGRESS'].includes(status) ? 'warning' : ['FAILED','CANCELLED','INACTIVE','REJECTED','OVERDUE'].includes(status) ? 'error' : status ? 'info' : 'default'; }
const formatDate = (date: string) => { if (!date) return '—'; const parsed = new Date(date); return Number.isNaN(parsed.valueOf()) ? date : new Intl.DateTimeFormat(undefined, { dateStyle: 'medium' }).format(parsed); };

type ServiceDraft = {
  serviceCode: string; serviceName: string; category: string; catalogGroup: string; catalogPriority: string; deliveryPolicy: string; frequency: string; catalogNotes: string;
  eligiblePropertyTypes: string[]; conditionalPropertyTypes: string[]; basePrice: string; activeFlag: boolean;
  requiresVisit: boolean; visitMandatory: boolean; maxVisitsAllowed: string; requiresGps: boolean; gpsRadiusKm: string;
  requiresBeforePhoto: boolean; requiresAfterPhoto: boolean; requiresSignature: boolean; requiresDocumentUpload: boolean; remote: boolean; billable: boolean;
};
type PricingRuleDraft = { serviceId: string; ruleName: string; baseAmount: string; currencyCode: string; effectiveFrom: string; effectiveTo: string; status: string; ruleDefinition: string };
type PricingRuleConfiguration = { pricingModel: 'FIXED' | 'PERCENT_OF_LOAN_AMOUNT'; amountBasis?: 'REQUESTED_LOAN_AMOUNT'; minimumFee?: number; maximumFee?: number; [key: string]: unknown };
type CatalogServiceOption = { serviceId: string; serviceName: string; serviceCode?: string };
const newPricingRuleDraft = (): PricingRuleDraft => ({ serviceId: '', ruleName: '', baseAmount: '', currencyCode: 'INR', effectiveFrom: '', effectiveTo: '', status: 'DRAFT', ruleDefinition: '{"pricingModel":"FIXED"}' });
const readPricingConfiguration = (raw: string): PricingRuleConfiguration => {
  try {
    const value = JSON.parse(raw) as Partial<PricingRuleConfiguration>;
    return { ...value, pricingModel: value.pricingModel === 'PERCENT_OF_LOAN_AMOUNT' ? 'PERCENT_OF_LOAN_AMOUNT' : 'FIXED',
      ...(value.pricingModel === 'PERCENT_OF_LOAN_AMOUNT' ? { amountBasis: 'REQUESTED_LOAN_AMOUNT' as const } : {}),
      ...(typeof value.minimumFee === 'number' ? { minimumFee: value.minimumFee } : {}),
      ...(typeof value.maximumFee === 'number' ? { maximumFee: value.maximumFee } : {}) };
  } catch { return { pricingModel: 'FIXED' }; }
};
const dateInput = (value: unknown) => { if (typeof value !== 'string' || !value) return ''; const date = new Date(value); return Number.isNaN(date.valueOf()) ? '' : new Date(date.getTime() - date.getTimezoneOffset() * 60000).toISOString().slice(0, 16); };
const PROPERTY_TYPE_OPTIONS = ['PLOT','HOUSE','VILLA','APARTMENT','COMMERCIAL_PROPERTY','AGRICULTURAL_LAND','TOWNHOUSE','INDUSTRIAL_PROPERTY','OTHER'];
const propertyTypeLabel = (value: string) => value.split('_').map((part) => part.charAt(0) + part.slice(1).toLowerCase()).join(' ');
const newServiceDraft = (): ServiceDraft => ({ serviceCode: '', serviceName: '', category: '', catalogGroup: '', catalogPriority: 'CORE', deliveryPolicy: '', frequency: '', catalogNotes: '', eligiblePropertyTypes: [], conditionalPropertyTypes: [], basePrice: '0', activeFlag: false,
  requiresVisit: false, visitMandatory: false, maxVisitsAllowed: '0', requiresGps: false, gpsRadiusKm: '0.200',
  requiresBeforePhoto: false, requiresAfterPhoto: false, requiresSignature: false, requiresDocumentUpload: false, remote: false, billable: true });
const serviceDraftFrom = (record: Record<string, unknown>): ServiceDraft => ({
  serviceCode: String(record.serviceCode ?? ''), serviceName: String(record.serviceName ?? ''), category: String(record.category ?? ''),
  catalogGroup: String(record.catalogGroup ?? record.category ?? 'General Services'), catalogPriority: String(record.catalogPriority ?? 'CORE'),
  deliveryPolicy: String(record.deliveryPolicy ?? ''), frequency: String(record.frequency ?? ''), catalogNotes: String(record.catalogNotes ?? ''),
  eligiblePropertyTypes: Array.isArray(record.eligiblePropertyTypes) ? record.eligiblePropertyTypes.map(String) : [],
  conditionalPropertyTypes: Array.isArray(record.conditionalPropertyTypes) ? record.conditionalPropertyTypes.map(String) : [], basePrice: String(record.basePrice ?? 0),
  activeFlag: record.activeFlag === true, requiresVisit: record.requiresVisit === true, visitMandatory: record.visitMandatory === true,
  maxVisitsAllowed: String(record.maxVisitsAllowed ?? 0), requiresGps: record.requiresGps === true, gpsRadiusKm: String(record.gpsRadiusKm ?? '0.200'),
  requiresBeforePhoto: record.requiresBeforePhoto === true, requiresAfterPhoto: record.requiresAfterPhoto === true, requiresSignature: record.requiresSignature === true,
  requiresDocumentUpload: record.requiresDocumentUpload === true, remote: record.remote === true, billable: record.billable !== false,
});

export default function AdminDirectoryPage() {
  const { resource: resourceParam } = useParams<{ resource: string }>();
  const client = useQueryClient();
  const valid = isResource(resourceParam); const resource = valid ? resourceParam : 'customers';
  const query = useAdminDirectory(resource); const [search, setSearch] = useState(''); const [status, setStatus] = useState('ALL'); const [facet, setFacet] = useState('ALL');
  const [page, setPage] = useState(0); const [pageSize, setPageSize] = useState(10); const [message, setMessage] = useState('');
  const [accessUser, setAccessUser] = useState<{ id: string; name: string; email: string; roles: string } | null>(null);
  const [accessRole, setAccessRole] = useState(''); const [retainCustomer, setRetainCustomer] = useState(false);
  const [agentRegion, setAgentRegion] = useState(''); const [accessReason, setAccessReason] = useState('');
  const [serviceDialogOpen, setServiceDialogOpen] = useState(false); const [serviceId, setServiceId] = useState<string | null>(null);
  const [serviceDraft, setServiceDraft] = useState<ServiceDraft>(newServiceDraft); const [serviceFormError, setServiceFormError] = useState('');
  const [pricingDialogOpen, setPricingDialogOpen] = useState(false); const [pricingRuleId, setPricingRuleId] = useState<string | null>(null);
  const [pricingDraft, setPricingDraft] = useState<PricingRuleDraft>(newPricingRuleDraft); const [pricingFormError, setPricingFormError] = useState('');
  const pricingConfiguration = readPricingConfiguration(pricingDraft.ruleDefinition);
  const updatePricingConfiguration = (patch: Partial<PricingRuleConfiguration>) => setPricingDraft((current) => {
    const next = { ...readPricingConfiguration(current.ruleDefinition), ...patch };
    if (next.pricingModel === 'PERCENT_OF_LOAN_AMOUNT') next.amountBasis = 'REQUESTED_LOAN_AMOUNT';
    else { delete next.amountBasis; delete next.minimumFee; delete next.maximumFee; }
    return { ...current, ruleDefinition: JSON.stringify(next) };
  });
  const [catalogServices, setCatalogServices] = useState<CatalogServiceOption[]>([]); const [catalogServicesLoading, setCatalogServicesLoading] = useState(false);
  const saveService = useMutation({
    mutationFn: async (draft: ServiceDraft) => {
      const payload = { ...draft, serviceCode: draft.serviceCode.trim().toUpperCase(), serviceName: draft.serviceName.trim(), category: draft.category.trim().toUpperCase(), catalogGroup: draft.catalogGroup.trim(), catalogPriority: draft.catalogPriority.toUpperCase(), basePrice: Number(draft.basePrice), maxVisitsAllowed: Number(draft.maxVisitsAllowed), gpsRadiusKm: Number(draft.gpsRadiusKm) };
      if (!/^[A-Z][A-Z0-9_]{1,79}$/.test(payload.serviceCode)) throw new Error('Use a stable service code such as PROPERTY_INSPECTION.');
      if (!/^[A-Z][A-Z0-9_]{1,79}$/.test(payload.category)) throw new Error('Use a stable service type code such as INSPECTION or DOCUMENTS.');
      if (!payload.serviceName || !payload.catalogGroup || !Number.isFinite(payload.basePrice) || payload.basePrice < 0 || !Number.isInteger(payload.maxVisitsAllowed) || payload.maxVisitsAllowed < 0 || !Number.isFinite(payload.gpsRadiusKm) || payload.gpsRadiusKm < 0.001) throw new Error('Check the service name, catalogue category, price, visit limit, and GPS radius.');
      return serviceId ? (await apiClient.put(`/api/services/admin/${serviceId}`, payload)).data : (await apiClient.post('/api/services/admin', payload)).data;
    },
    onSuccess: async () => { await client.invalidateQueries({ queryKey: adminPortalKeys.list('services') }); setServiceDialogOpen(false); setServiceId(null); setServiceFormError(''); setMessage(serviceId ? 'Service updated.' : 'Service added to the catalog.'); },
    onError: (error) => setServiceFormError(error instanceof Error ? error.message : 'The service could not be saved.'),
  });
  const savePricingRule = useMutation({
    mutationFn: async (draft: PricingRuleDraft) => {
      const amount = Number(draft.baseAmount);
      const configuration = readPricingConfiguration(draft.ruleDefinition);
      const starts = new Date(draft.effectiveFrom);
      const ends = draft.effectiveTo ? new Date(draft.effectiveTo) : null;
      if (!draft.serviceId || !draft.ruleName.trim() || !Number.isFinite(amount) || amount <= 0 || (configuration.pricingModel === 'PERCENT_OF_LOAN_AMOUNT' && amount > 100) || !/^[A-Z]{3}$/.test(draft.currencyCode.trim().toUpperCase()) || Number.isNaN(starts.valueOf()) || (ends && (Number.isNaN(ends.valueOf()) || ends < starts))) throw new Error(configuration.pricingModel === 'PERCENT_OF_LOAN_AMOUNT' ? 'Enter a rate greater than zero and no more than 100%, a service, currency and valid effective period.' : 'Enter a service, rule name, positive price, currency and valid effective period.');
      if (configuration.minimumFee !== undefined && configuration.minimumFee < 0) throw new Error('Minimum fee cannot be negative.');
      if (configuration.maximumFee !== undefined && (configuration.maximumFee <= 0 || configuration.maximumFee < (configuration.minimumFee ?? 0))) throw new Error('Maximum fee must be positive and at least the minimum fee.');
      const payload = { ...draft, ruleName: draft.ruleName.trim(), baseAmount: amount, currencyCode: draft.currencyCode.trim().toUpperCase(), effectiveFrom: starts.toISOString(), effectiveTo: ends?.toISOString() ?? null, ruleDefinition: JSON.stringify(configuration) };
      return pricingRuleId ? (await apiClient.put(`/api/pricing-rules/${pricingRuleId}`, payload)).data : (await apiClient.post('/api/pricing-rules', payload)).data;
    },
    onSuccess: async () => { await client.invalidateQueries({ queryKey: adminPortalKeys.list('pricing') }); setPricingDialogOpen(false); setPricingRuleId(null); setPricingFormError(''); setMessage(pricingRuleId ? 'Pricing rule updated.' : 'Pricing rule added.'); },
    onError: (error) => setPricingFormError(error instanceof Error ? error.message : 'The pricing rule could not be saved.'),
  });
  const accessMutation = useMutation({
    mutationFn: () => {
      if (!accessUser) throw new Error('Select a user first.');
      return apiClient.put(`/api/v1/users/${accessUser.id}/access`, {
        roleCode: accessRole, retainCustomerAccess: retainCustomer,
        region: accessRole === 'AGENT' ? agentRegion.trim() || null : null, reason: accessReason.trim(),
      });
    },
    onSuccess: async () => {
      await client.invalidateQueries({ queryKey: adminPortalKeys.list('users') });
      if (accessUser) await client.invalidateQueries({ queryKey: adminPortalKeys.detail('users', accessUser.id) });
      setAccessUser(null); setAccessRole(''); setRetainCustomer(false); setAgentRegion(''); setAccessReason('');
      setMessage('User access updated.');
    },
  });
  const statusMutation = useMutation({
    mutationFn: ({ id, nextStatus }: { id: string; nextStatus: string }) => apiClient.patch(`/api/v1/admin/customers/${id}/status`, { status: nextStatus }),
    onSuccess: async () => { await client.invalidateQueries({ queryKey: adminPortalKeys.list('customers') }); setMessage('Customer status updated.'); },
  });
  const propertyArchiveMutation = useMutation({
    mutationFn: ({ id, restore }: { id: string; restore: boolean }) => apiClient.patch(`/api/v1/properties/${id}/${restore ? 'restore' : 'archive'}`),
    onSuccess: async (_saved, variables) => { await client.invalidateQueries({ queryKey: adminPortalKeys.list('properties') }); setMessage(variables.restore ? 'Property restored.' : 'Property archived.'); },
  });
  const rows = query.data ?? [];
  const statuses = useMemo(() => [...new Set(rows.map((entry) => entry.status).filter(Boolean))].sort(), [rows]);
  const facets = useMemo(() => [...new Set(rows.map((entry) => entry.type).filter(Boolean))].sort(), [rows]);
  const filtered = useMemo(() => rows.filter((entry) => (status === 'ALL' || entry.status === status) && (facet === 'ALL' || entry.type === facet)
    && `${entry.title} ${entry.primary} ${entry.secondary} ${entry.status} ${entry.type}`.toLowerCase().includes(search.trim().toLowerCase()))
    .sort((left, right) => right.date.localeCompare(left.date)), [rows, status, facet, search]);
  const pageRows = filtered.slice(page * pageSize, (page + 1) * pageSize);
  const heading = directoryLabels[resource];
  const openNewService = () => { setServiceId(null); setServiceDraft(newServiceDraft()); setServiceFormError(''); saveService.reset(); setServiceDialogOpen(true); };
  const openEditService = async (id: string) => {
    setServiceFormError(''); saveService.reset();
    try { const record = await adminPortalApi.get('services', id); setServiceId(id); setServiceDraft(serviceDraftFrom(record)); setServiceDialogOpen(true); }
    catch (error) { setMessage(error instanceof Error ? error.message : 'Service details could not be loaded.'); }
  };
  const openPricingRule = async (id?: string, selectedServiceId?: string) => {
    setPricingRuleId(id ?? null); setPricingDraft(newPricingRuleDraft()); setPricingFormError(''); savePricingRule.reset(); setPricingDialogOpen(true); setCatalogServicesLoading(true);
    try {
      const [servicesResult, ruleResult] = await Promise.all([
        apiClient.get<CatalogServiceOption[]>('/api/services/admin'),
        id ? adminPortalApi.get('pricing', id) : Promise.resolve(null),
      ]);
      setCatalogServices(servicesResult.data);
      if (ruleResult) setPricingDraft({ serviceId: String(ruleResult.serviceId ?? ''), ruleName: String(ruleResult.ruleName ?? ''), baseAmount: String(ruleResult.baseAmount ?? ''), currencyCode: String(ruleResult.currencyCode ?? 'INR'), effectiveFrom: dateInput(ruleResult.effectiveFrom), effectiveTo: dateInput(ruleResult.effectiveTo), status: String(ruleResult.status ?? 'DRAFT'), ruleDefinition: String(ruleResult.ruleDefinition ?? '{}') });
      else if (selectedServiceId) setPricingDraft({ ...newPricingRuleDraft(), serviceId: selectedServiceId, ruleName: 'STANDARD_SERVICE_PRICE', effectiveFrom: new Date(Date.now() - new Date().getTimezoneOffset() * 60000).toISOString().slice(0, 16) });
    } catch (error) { setPricingFormError(error instanceof Error ? error.message : 'Pricing rule details could not be loaded.'); }
    finally { setCatalogServicesLoading(false); }
  };

  if (!valid) return <Alert severity="error">Unknown admin module.</Alert>;
  return <Stack spacing={3}>
    <DashboardPageHeading eyebrow="Admin Console" title={heading.label} subtitle={heading.description} action={<Stack direction="row" spacing={1}>
        {resource === 'services' && <Button variant="contained" onClick={openNewService}>Add service</Button>}
        {resource === 'pricing' && <Button variant="contained" onClick={() => void openPricingRule()}>Add pricing rule</Button>}
        <Button variant="outlined" startIcon={query.isFetching ? <CircularProgress size={16}/> : <RefreshRounded/>} onClick={() => void query.refetch()} disabled={query.isFetching}>Refresh</Button>
      </Stack>} />
    <Stack direction={{ xs: 'column', md: 'row' }} spacing={1.5}>
      <TextField size="small" fullWidth value={search} onChange={(event) => { setSearch(event.target.value); setPage(0); }} placeholder={`Search ${heading.label.toLowerCase()}`} inputProps={{ 'aria-label': `Search ${heading.label.toLowerCase()}` }} InputProps={{ startAdornment: <SearchRounded color="action" sx={{ mr: 1 }}/> }}/>
      <FormControl size="small" sx={{ minWidth: 175 }}><InputLabel id="directory-status">Status</InputLabel><Select labelId="directory-status" label="Status" value={status} onChange={(event) => { setStatus(event.target.value); setPage(0); }}><MenuItem value="ALL">All statuses</MenuItem>{statuses.map((item) => <MenuItem key={item} value={item}>{label(item)}</MenuItem>)}</Select></FormControl>
      <FormControl size="small" sx={{ minWidth: 175 }}><InputLabel id="directory-type">Type</InputLabel><Select labelId="directory-type" label="Type" value={facet} onChange={(event) => { setFacet(event.target.value); setPage(0); }}><MenuItem value="ALL">All types</MenuItem>{facets.map((item) => <MenuItem key={item} value={item}>{label(item)}</MenuItem>)}</Select></FormControl>
    </Stack>
    {query.isError && <Alert severity="error" action={<Button color="inherit" size="small" onClick={() => void query.refetch()}>Retry</Button>}>{query.error instanceof Error ? query.error.message : `Unable to load ${heading.label.toLowerCase()}.`}</Alert>}
    {statusMutation.isError && <Alert severity="error">{statusMutation.error instanceof Error ? statusMutation.error.message : 'Could not update customer status.'}</Alert>}
    {propertyArchiveMutation.isError && <Alert severity="error">{propertyArchiveMutation.error instanceof Error ? propertyArchiveMutation.error.message : 'Could not update property archive status.'}</Alert>}
    <Card variant="outlined"><CardContent sx={{ p: 0, '&:last-child': { pb: 0 } }}><TableContainer sx={{ overflowX: 'auto' }}><Table size="small" aria-label={`${heading.label} directory`}>
      <TableHead><TableRow>{[resource === 'customers' ? 'Customer' : 'Record','Details','Status','Type','Date','Amount',''].map((column) => <TableCell key={column} sx={{ fontWeight: 700, whiteSpace: 'nowrap' }}>{column}</TableCell>)}</TableRow></TableHead>
      <TableBody>
        {query.isLoading ? Array.from({ length: 5 }, (_, index) => <TableRow key={index}>{Array.from({ length: 7 }, (_, cell) => <TableCell key={cell}><Skeleton/></TableCell>)}</TableRow>) : pageRows.map((entry) => <TableRow hover key={entry.id}>
          <TableCell sx={{ minWidth: 210 }}><Typography fontWeight={700}>{entry.title}</Typography></TableCell>
          <TableCell sx={{ minWidth: 180 }}>{entry.secondary || '—'}</TableCell><TableCell><Chip size="small" label={label(entry.status) || 'Unknown'} color={statusColor(entry.status)}/></TableCell><TableCell>{label(entry.type) || '—'}</TableCell><TableCell sx={{ whiteSpace: 'nowrap' }}>{formatDate(entry.date)}</TableCell><TableCell sx={{ whiteSpace: 'nowrap' }}>{entry.amount || '—'}</TableCell>
          <TableCell><Stack direction="row" spacing={0.5}><Button size="small" component={Link} to={entry.detailPath}>View</Button>{resource === 'services' && <><Button size="small" onClick={() => void openEditService(entry.id)}>Edit</Button><Button size="small" onClick={() => void openPricingRule(undefined, entry.id)}>Set price</Button></>}{resource === 'pricing' && <Button size="small" onClick={() => void openPricingRule(entry.id)}>Edit</Button>}{resource === 'users' && <Button size="small" disabled={entry.type.split(', ').includes('ADMIN')} onClick={() => { setAccessUser({ id: entry.id, name: entry.title, email: entry.secondary, roles: entry.type }); setAccessRole(''); setRetainCustomer(false); setAgentRegion(''); setAccessReason(''); accessMutation.reset(); }}>Manage access</Button>}{resource === 'customers' && <><Button size="small" component={Link} to={`/staff/customers/${entry.id}/edit`}>Edit</Button><Button size="small" color={entry.status === 'ACTIVE' ? 'warning' : 'success'} disabled={statusMutation.isPending} onClick={() => statusMutation.mutate({ id: entry.id, nextStatus: entry.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE' })}>{entry.status === 'ACTIVE' ? 'Deactivate' : 'Activate'}</Button></>}{resource === 'properties' && <><Button size="small" component={Link} to={`/staff/properties/${entry.id}/edit`} disabled={entry.status === 'ARCHIVED'}>Edit</Button><Button size="small" color={entry.status === 'ARCHIVED' ? 'success' : 'warning'} disabled={propertyArchiveMutation.isPending} onClick={() => propertyArchiveMutation.mutate({ id: entry.id, restore: entry.status === 'ARCHIVED' })}>{entry.status === 'ARCHIVED' ? 'Restore' : 'Archive'}</Button></>}</Stack></TableCell>
        </TableRow>)}
        {!query.isLoading && !query.isError && pageRows.length === 0 && <TableRow><TableCell colSpan={7} align="center"><Box sx={{ py: 7 }}><Typography fontWeight={700}>{rows.length ? 'No matching records' : `No ${heading.label.toLowerCase()} found`}</Typography><Typography variant="body2" color="text.secondary" sx={{ mt: 0.5 }}>{rows.length ? 'Adjust your search or filters.' : 'Records will appear here when they are available from the service API.'}</Typography></Box></TableCell></TableRow>}
      </TableBody>
    </Table></TableContainer><TablePagination component="div" count={filtered.length} page={page} onPageChange={(_, nextPage) => setPage(nextPage)} rowsPerPage={pageSize} onRowsPerPageChange={(event) => { setPageSize(Number(event.target.value)); setPage(0); }} rowsPerPageOptions={[10,25,50]}/></CardContent></Card>
    <Dialog open={serviceDialogOpen} onClose={() => !saveService.isPending && setServiceDialogOpen(false)} fullWidth maxWidth="md" aria-labelledby="service-catalog-dialog-title">
      <DialogTitle id="service-catalog-dialog-title">{serviceId ? 'Edit catalog service' : 'Add catalog service'}</DialogTitle>
      <DialogContent><Stack component="form" id="service-catalog-form" spacing={2} sx={{ pt: 1 }} onSubmit={(event) => { event.preventDefault(); setServiceFormError(''); saveService.mutate(serviceDraft); }}>
        <Typography variant="body2" color="text.secondary">Active services appear in customer request options when the selected property meets their eligibility rules. Deactivate retired services to preserve historical references.</Typography>
        {serviceFormError && <Alert severity="error">{serviceFormError}</Alert>}
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
          <TextField label="Service code" value={serviceDraft.serviceCode} onChange={(event) => setServiceDraft({ ...serviceDraft, serviceCode: event.target.value.toUpperCase().replace(/[^A-Z0-9_]/g, '_') })} required inputProps={{ maxLength: 80 }} helperText="Stable code, for example PROPERTY_INSPECTION" fullWidth />
          <TextField label="Service type code" value={serviceDraft.category} onChange={(event) => setServiceDraft({ ...serviceDraft, category: event.target.value.toUpperCase().replace(/[^A-Z0-9_]/g, '_') })} required inputProps={{ maxLength: 80 }} helperText="For example INSPECTION or DOCUMENTS" fullWidth />
        </Stack>
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
          <TextField label="Catalogue category" value={serviceDraft.catalogGroup} onChange={(event) => setServiceDraft({ ...serviceDraft, catalogGroup: event.target.value })} required inputProps={{ maxLength: 100 }} helperText="Customer-facing group from the regular service catalogue" fullWidth />
          <FormControl fullWidth required><InputLabel id="catalog-priority-label">Priority</InputLabel><Select labelId="catalog-priority-label" label="Priority" value={serviceDraft.catalogPriority} onChange={(event) => setServiceDraft({ ...serviceDraft, catalogPriority: event.target.value })}><MenuItem value="CORE">Core</MenuItem><MenuItem value="OPTIONAL">Optional</MenuItem></Select></FormControl>
        </Stack>
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
          <TextField label="Customer-visible service name" value={serviceDraft.serviceName} onChange={(event) => setServiceDraft({ ...serviceDraft, serviceName: event.target.value })} required inputProps={{ maxLength: 200 }} fullWidth />
          <TextField label="Fallback base price" type="number" value={serviceDraft.basePrice} onChange={(event) => setServiceDraft({ ...serviceDraft, basePrice: event.target.value })} required inputProps={{ min: 0, step: '0.01' }} helperText="Used only when no effective service price rule exists. Use Set price for the administered customer pricing rate." fullWidth />
        </Stack>
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
          <TextField label="Delivery / visit policy" value={serviceDraft.deliveryPolicy} onChange={(event) => setServiceDraft({ ...serviceDraft, deliveryPolicy: event.target.value })} inputProps={{ maxLength: 255 }} fullWidth />
          <TextField label="Frequency" value={serviceDraft.frequency} onChange={(event) => setServiceDraft({ ...serviceDraft, frequency: event.target.value })} inputProps={{ maxLength: 120 }} fullWidth />
        </Stack>
        <Typography fontWeight={700}>Property eligibility</Typography>
        <Typography variant="body2" color="text.secondary">Eligible types can request this service. Conditional types stay unavailable to customers until an administrator confirms eligibility.</Typography>
        <Stack direction={{ xs: 'column', md: 'row' }} spacing={2}>
          <Box sx={{ flex: 1 }}><Typography variant="subtitle2">Eligible</Typography>{PROPERTY_TYPE_OPTIONS.map((type) => <FormControlLabel key={`eligible-${type}`} sx={{ display: 'block' }} control={<Checkbox checked={serviceDraft.eligiblePropertyTypes.includes(type)} onChange={(event) => setServiceDraft({ ...serviceDraft, eligiblePropertyTypes: event.target.checked ? [...serviceDraft.eligiblePropertyTypes, type] : serviceDraft.eligiblePropertyTypes.filter((item) => item !== type), conditionalPropertyTypes: event.target.checked ? serviceDraft.conditionalPropertyTypes.filter((item) => item !== type) : serviceDraft.conditionalPropertyTypes })} />} label={propertyTypeLabel(type)} />)}</Box>
          <Box sx={{ flex: 1 }}><Typography variant="subtitle2">Conditional · Admin review</Typography>{PROPERTY_TYPE_OPTIONS.map((type) => <FormControlLabel key={`conditional-${type}`} sx={{ display: 'block' }} control={<Checkbox checked={serviceDraft.conditionalPropertyTypes.includes(type)} onChange={(event) => setServiceDraft({ ...serviceDraft, conditionalPropertyTypes: event.target.checked ? [...serviceDraft.conditionalPropertyTypes, type] : serviceDraft.conditionalPropertyTypes.filter((item) => item !== type), eligiblePropertyTypes: event.target.checked ? serviceDraft.eligiblePropertyTypes.filter((item) => item !== type) : serviceDraft.eligiblePropertyTypes })} />} label={propertyTypeLabel(type)} />)}</Box>
        </Stack>
        <TextField label="Catalogue notes" value={serviceDraft.catalogNotes} onChange={(event) => setServiceDraft({ ...serviceDraft, catalogNotes: event.target.value })} multiline minRows={2} inputProps={{ maxLength: 4000 }} fullWidth />
        <Stack direction={{ xs: 'column', sm: 'row' }} flexWrap="wrap" gap={1}>
          {([['activeFlag','Available to customers'],['requiresVisit','May require an on-site visit'],['visitMandatory','On-site visit is mandatory'],['requiresGps','Require GPS verification'],['requiresBeforePhoto','Require before photos'],['requiresAfterPhoto','Require after photos'],['requiresSignature','Require customer signature'],['requiresDocumentUpload','Require document upload'],['remote','Remote delivery'],['billable','Billable']] as const).map(([key, labelText]) => <FormControlLabel key={key} control={<Checkbox checked={serviceDraft[key]} onChange={(event) => setServiceDraft({ ...serviceDraft, [key]: event.target.checked })} />} label={labelText} />)}
        </Stack>
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
          <TextField label="Maximum visits" type="number" value={serviceDraft.maxVisitsAllowed} onChange={(event) => setServiceDraft({ ...serviceDraft, maxVisitsAllowed: event.target.value })} inputProps={{ min: 0, step: 1 }} fullWidth />
          <TextField label="GPS verification radius (km)" type="number" value={serviceDraft.gpsRadiusKm} onChange={(event) => setServiceDraft({ ...serviceDraft, gpsRadiusKm: event.target.value })} inputProps={{ min: 0.001, step: 0.001 }} fullWidth />
        </Stack>
      </Stack></DialogContent>
      <DialogActions><Button onClick={() => setServiceDialogOpen(false)} disabled={saveService.isPending}>Cancel</Button><Button type="submit" form="service-catalog-form" variant="contained" disabled={saveService.isPending}>{saveService.isPending ? 'Saving…' : serviceId ? 'Save changes' : 'Add service'}</Button></DialogActions>
    </Dialog>
    <Dialog open={pricingDialogOpen} onClose={() => !savePricingRule.isPending && setPricingDialogOpen(false)} fullWidth maxWidth="md" aria-labelledby="pricing-rule-dialog-title">
      <DialogTitle id="pricing-rule-dialog-title">{pricingRuleId ? 'Edit pricing rule' : 'Add pricing rule'}</DialogTitle>
      <DialogContent><Stack component="form" id="pricing-rule-form" spacing={2} sx={{ pt: 1 }} onSubmit={(event) => { event.preventDefault(); setPricingFormError(''); savePricingRule.mutate(pricingDraft); }}>
        <Typography variant="body2" color="text.secondary">Configure an effective service base price. The active pricing engine applies the current Standard and Customer profile rules, including travel, overhead, margin, tax and supported discounts. Accepted estimates keep their original amounts.</Typography>
          <Alert severity="info">Fixed prices and percentage of requested loan amount are supported. The existing pricing engine applies configured Standard and Customer profile rules to the resulting service fee. Travel-dependent estimates stay with Operations until a trusted routing provider is configured.</Alert>
        {pricingFormError && <Alert severity="error">{pricingFormError}</Alert>}
        {catalogServicesLoading ? <Skeleton variant="rounded" height={56}/> : <FormControl fullWidth required><InputLabel id="pricing-service-label">Service</InputLabel><Select labelId="pricing-service-label" label="Service" value={pricingDraft.serviceId} onChange={(event) => setPricingDraft({ ...pricingDraft, serviceId: event.target.value })}>{catalogServices.map((service) => <MenuItem key={service.serviceId} value={service.serviceId}>{service.serviceName}{service.serviceCode ? ` · ${service.serviceCode}` : ''}</MenuItem>)}</Select></FormControl>}
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
          <TextField label="Rule name" value={pricingDraft.ruleName} onChange={(event) => setPricingDraft({ ...pricingDraft, ruleName: event.target.value })} required inputProps={{ maxLength: 150 }} fullWidth/>
            <FormControl fullWidth required><InputLabel id="pricing-model-label">Pricing model</InputLabel><Select labelId="pricing-model-label" label="Pricing model" value={pricingConfiguration.pricingModel} onChange={(event) => updatePricingConfiguration({ pricingModel: event.target.value as PricingRuleConfiguration['pricingModel'] })}><MenuItem value="FIXED">Fixed service fee</MenuItem><MenuItem value="PERCENT_OF_LOAN_AMOUNT">Percentage of requested loan amount</MenuItem></Select></FormControl>
            <TextField label={pricingConfiguration.pricingModel === 'PERCENT_OF_LOAN_AMOUNT' ? 'Rate (%)' : 'Base price'} type="number" value={pricingDraft.baseAmount} onChange={(event) => setPricingDraft({ ...pricingDraft, baseAmount: event.target.value })} required inputProps={{ min: 0.01, max: pricingConfiguration.pricingModel === 'PERCENT_OF_LOAN_AMOUNT' ? 100 : undefined, step: '0.01' }} helperText={pricingConfiguration.pricingModel === 'PERCENT_OF_LOAN_AMOUNT' ? 'Example: 0.5 means 0.5% of the customer’s requested loan amount.' : 'Must be greater than zero to generate an estimate.'} fullWidth/>
          <TextField label="Currency code" value={pricingDraft.currencyCode} onChange={(event) => setPricingDraft({ ...pricingDraft, currencyCode: event.target.value.toUpperCase().slice(0, 3) })} required inputProps={{ maxLength: 3 }} fullWidth/>
        </Stack>
          {pricingConfiguration.pricingModel === 'PERCENT_OF_LOAN_AMOUNT' && <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
            <TextField label="Minimum service fee (optional)" type="number" value={pricingConfiguration.minimumFee ?? ''} onChange={(event) => updatePricingConfiguration({ minimumFee: event.target.value === '' ? undefined : Number(event.target.value) })} inputProps={{ min: 0, step: '0.01' }} helperText="Applied after calculating the percentage fee." fullWidth/>
            <TextField label="Maximum service fee (optional)" type="number" value={pricingConfiguration.maximumFee ?? ''} onChange={(event) => updatePricingConfiguration({ maximumFee: event.target.value === '' ? undefined : Number(event.target.value) })} inputProps={{ min: 0.01, step: '0.01' }} helperText="Caps the percentage fee when configured." fullWidth/>
          </Stack>}
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
          <TextField label="Effective from" type="datetime-local" value={pricingDraft.effectiveFrom} onChange={(event) => setPricingDraft({ ...pricingDraft, effectiveFrom: event.target.value })} required InputLabelProps={{ shrink: true }} fullWidth/>
          <TextField label="Effective to (optional)" type="datetime-local" value={pricingDraft.effectiveTo} onChange={(event) => setPricingDraft({ ...pricingDraft, effectiveTo: event.target.value })} InputLabelProps={{ shrink: true }} fullWidth/>
          <FormControl fullWidth required><InputLabel id="pricing-status-label">Status</InputLabel><Select labelId="pricing-status-label" label="Status" value={pricingDraft.status} onChange={(event) => setPricingDraft({ ...pricingDraft, status: event.target.value })}>{['DRAFT','ACTIVE','INACTIVE','RETIRED'].map((value) => <MenuItem key={value} value={value}>{label(value)}</MenuItem>)}</Select></FormControl>
        </Stack>
        <Typography variant="caption" color="text.secondary">For overlapping active rules, the current engine uses the applicable rule with the latest effective start date. Retire or deactivate old rules instead of deleting historical pricing.</Typography>
      </Stack></DialogContent>
      <DialogActions><Button onClick={() => setPricingDialogOpen(false)} disabled={savePricingRule.isPending}>Cancel</Button><Button type="submit" form="pricing-rule-form" variant="contained" disabled={savePricingRule.isPending || catalogServicesLoading || catalogServices.length === 0}>{savePricingRule.isPending ? 'Saving…' : pricingRuleId ? 'Save changes' : 'Add pricing rule'}</Button></DialogActions>
    </Dialog>
    <Dialog open={Boolean(accessUser)} onClose={() => !accessMutation.isPending && setAccessUser(null)} fullWidth maxWidth="sm" aria-labelledby="manage-access-title">
      <DialogTitle id="manage-access-title">Manage account access</DialogTitle>
      <DialogContent><Stack spacing={2} sx={{ pt: 1 }}>
        <Typography variant="body2" color="text.secondary"><strong>{accessUser?.name}</strong>{accessUser?.email ? ` · ${accessUser.email}` : ''}<br/>Current access: {accessUser?.roles || 'No active roles returned'}. Customer and property records remain in place when access changes.</Typography>
        {accessMutation.isError && <Alert severity="error">Access was not saved. {accessMutation.error instanceof Error ? accessMutation.error.message : 'The server rejected the request.'} Check that the backend has been rebuilt and restarted, then retry.</Alert>}
        <FormControl fullWidth required><InputLabel id="access-role-label">Grant access</InputLabel><Select labelId="access-role-label" label="Grant access" value={accessRole} onChange={(event) => setAccessRole(event.target.value)}><MenuItem value="AGENT">Field agent</MenuItem><MenuItem value="OPERATIONS">Operations staff</MenuItem><MenuItem value="SUPPORT">Support staff</MenuItem></Select></FormControl>
        {accessRole === 'AGENT' && <TextField label="Primary operating region (optional)" value={agentRegion} onChange={(event) => setAgentRegion(event.target.value)} inputProps={{ maxLength: 120 }} fullWidth/>}
        <FormControlLabel control={<Checkbox checked={retainCustomer} onChange={(event) => setRetainCustomer(event.target.checked)}/>} label="Keep Customer portal access as well"/>
        <TextField label="Reason for access change" value={accessReason} onChange={(event) => setAccessReason(event.target.value)} required multiline minRows={2} inputProps={{ maxLength: 500 }} fullWidth/>
        <Typography variant="caption" color="text.secondary">{accessRole === 'AGENT' ? 'An Agent profile and operational code will be created or linked.' : 'The selected staff role replaces Customer access unless you keep Customer access above.'} Administrator access cannot be granted here.</Typography>
      </Stack></DialogContent>
      <DialogActions><Button onClick={() => setAccessUser(null)} disabled={accessMutation.isPending}>Cancel</Button><Button variant="contained" onClick={() => accessMutation.mutate()} disabled={!accessRole || !accessReason.trim() || accessMutation.isPending}>{accessMutation.isPending ? 'Saving…' : 'Save access'}</Button></DialogActions>
    </Dialog>
    <Snackbar open={Boolean(message)} autoHideDuration={3500} onClose={() => setMessage('')} message={message}/>
  </Stack>;
}
