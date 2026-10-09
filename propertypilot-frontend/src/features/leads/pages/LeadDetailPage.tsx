import { useState } from 'react';
import { Alert, Button, Card, CardContent, Grid, MenuItem, Stack, TextField, Typography } from '@mui/material';
import { Link, useLocation, useParams } from 'react-router-dom';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { useAuth } from '../../auth/AuthContext';
import { getUserRoles } from '../../../api/auth';
import { leadApi } from '../services/leadApi';
import { leadKeys, useLead } from '../hooks/useLeads';
import LeadStatusChip from '../components/LeadStatusChip';
import LeadAssignmentPanel from '../components/LeadAssignmentPanel';
import LeadActivityTimeline from '../components/LeadActivityTimeline';
import LeadServiceRequestsPanel from '../components/LeadServiceRequestsPanel';
import LeadDealConversionPanel from '../components/LeadDealConversionPanel';
import { LeadErrorState, LeadLoadingState } from '../components/LeadPageStates';
import type { LeadStatus } from '../types/lead';

const nextLeadStatuses: Record<LeadStatus, LeadStatus[]> = {
  NEW: ['CONTACTED', 'LOST', 'CLOSED'],
  CONTACTED: ['QUALIFIED', 'LOST', 'CLOSED'],
  QUALIFIED: ['READY_FOR_DEAL', 'LOST', 'CLOSED'],
  READY_FOR_DEAL: ['QUALIFIED', 'LOST', 'CLOSED'],
  DEAL_CONVERTED: ['CLOSED'],
  CONVERTED: ['CLOSED'],
  LOST: [],
  CLOSED: [],
};

export default function LeadDetailPage() {
  const { leadId = '' } = useParams();
  const query = useLead(leadId);
  const { user } = useAuth();
  const roles = getUserRoles(user);
  const admin = roles.includes('ADMIN');
  const operations = roles.includes('OPERATIONS') && !admin;
  const agent = roles.includes('AGENT') && !admin;
  const operationsQueueDetail = useLocation().pathname.startsWith('/operations/leads/');
  const backPath = operationsQueueDetail ? '/operations/leads' : admin ? '/staff' : agent ? '/agent/leads' : '/customer/leads';
  const client = useQueryClient();
  const [nextStatus, setNextStatus] = useState<LeadStatus | ''>('');
  const statusMutation = useMutation({ mutationFn: (status: LeadStatus) => leadApi.updateStatus(leadId, status), onSuccess: async (lead) => { client.setQueryData(leadKeys.detail(leadId), lead); await Promise.all([client.invalidateQueries({ queryKey: leadKeys.mine }), client.invalidateQueries({ queryKey: leadKeys.operations })]); } });
  if (query.isLoading) return <LeadLoadingState />;
  if (query.isError || !query.data) return <Stack spacing={2}><Button component={Link} to={backPath}>Back</Button><LeadErrorState error={query.error} retry={() => void query.refetch()} /></Stack>;
  const lead = query.data;
  return <Stack spacing={3}>
      <Stack direction={{ xs: 'column', sm: 'row' }} justifyContent="space-between" gap={2}>
      <Stack spacing={1}><Button component={Link} to={backPath} sx={{ alignSelf: 'flex-start', px: 0 }}>{operationsQueueDetail ? 'Back to lead queue' : admin ? 'Back to workspace' : agent ? 'Back to assigned leads' : 'Back to leads'}</Button><Typography variant="h4" fontWeight={750}>Lead from {lead.source || 'customer enquiry'}</Typography><Stack direction="row" spacing={1} alignItems="center"><LeadStatusChip status={lead.status} /></Stack></Stack>
    </Stack>
    <Grid container spacing={2}>
      <Grid item xs={12} md={8}><Card variant="outlined"><CardContent><Stack spacing={2}><Typography variant="h6" fontWeight={700}>Lead information</Typography><Info label="Source" value={lead.source} /><Info label="Property type" value={lead.propertyType} /><Info label="Location" value={lead.location} /><Info label="Budget" value={formatBudget(lead.budgetMin, lead.budgetMax)} /><Info label="Notes" value={lead.notes} /></Stack></CardContent></Card></Grid>
      {lead.contactedAt && <Grid item xs={12} md={4}><Card variant="outlined"><CardContent><Stack spacing={1}><Typography variant="h6" fontWeight={700}>Last contact</Typography><Typography>{new Date(lead.contactedAt).toLocaleString()}</Typography></Stack></CardContent></Card></Grid>}
      {admin && <><Grid item xs={12} md={6}><LeadAssignmentPanel lead={lead} /></Grid><Grid item xs={12} md={6}><Card variant="outlined"><CardContent><Stack spacing={2}><Typography variant="h6" fontWeight={700}>Lead status</Typography><TextField select label="Status" value={nextStatus || lead.status} onChange={(event) => setNextStatus(event.target.value as LeadStatus)} disabled={nextLeadStatuses[lead.status].length === 0}>{[lead.status, ...nextLeadStatuses[lead.status]].map((status, index) => <MenuItem key={status} value={status} disabled={index === 0}>{status.replaceAll('_', ' ')}</MenuItem>)}</TextField>{statusMutation.isError && <Alert severity="error">{statusMutation.error.message || 'Status update failed.'}</Alert>}{statusMutation.isSuccess && <Alert severity="success">Status updated.</Alert>}{nextLeadStatuses[lead.status].length === 0 && <Typography variant="caption" color="text.secondary">This lead stage is closed. Reopening is not available in the current workflow.</Typography>}<Button variant="contained" disabled={statusMutation.isPending || (nextStatus || lead.status) === lead.status || nextLeadStatuses[lead.status].length === 0} onClick={() => statusMutation.mutate(nextStatus || lead.status)}>Save status</Button></Stack></CardContent></Card></Grid></>}
    </Grid>
    {admin && <LeadDealConversionPanel lead={lead} />}
    <LeadServiceRequestsPanel leadId={leadId} operations={admin || operations} agent={agent} />
    <LeadActivityTimeline leadId={leadId} />
  </Stack>;
}

function Info({ label, value }: { label: string; value: string | number | null }) { return <Stack spacing={0.25}><Typography variant="caption" color="text.secondary">{label}</Typography><Typography sx={{ whiteSpace: 'pre-wrap', overflowWrap: 'anywhere' }}>{value ?? 'Not provided'}</Typography></Stack>; }
function formatBudget(min: number | null, max: number | null) { if (min == null && max == null) return 'Not provided'; if (min != null && max != null) return `${min.toLocaleString()} – ${max.toLocaleString()}`; return min != null ? `From ${min.toLocaleString()}` : `Up to ${max?.toLocaleString()}`; }
