import { useState } from 'react';
import { Alert, Button, Card, CardContent, MenuItem, Stack, TextField, Typography } from '@mui/material';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { leadApi } from '../services/leadApi';
import { leadKeys } from '../hooks/useLeads';
import { serviceRequestApi } from '../../../api/serviceRequestApi';
import type { Lead } from '../types/lead';

export default function LeadAssignmentPanel({ lead }: { lead: Lead }) {
  const [agentId, setAgentId] = useState(lead.assignedAgentId ?? '');
  const client = useQueryClient();
  const agents = useQuery({ queryKey: ['assignable-agents'], queryFn: serviceRequestApi.getAssignableAgents, staleTime: 60_000 });
  const assign = useMutation({
    mutationFn: () => leadApi.assign(lead.leadId, { agentId }),
    onSuccess: async () => Promise.all([
      client.invalidateQueries({ queryKey: leadKeys.detail(lead.leadId) }),
      client.invalidateQueries({ queryKey: leadKeys.operations }),
    ]),
  });
  return <Card variant="outlined"><CardContent><Stack spacing={2}>
    <Typography variant="h6" fontWeight={700}>Assignment</Typography>
    {lead.assignedAgentId ? <Alert severity="info">Assigned agent {lead.assignedAgentCode || 'Agent'}</Alert> : <Typography color="text.secondary">No agent is assigned.</Typography>}
    {agents.isError && <Alert severity="error" action={<Button onClick={() => void agents.refetch()}>Retry</Button>}>{agents.error.message || 'Agents could not be loaded.'}</Alert>}
    <TextField select fullWidth label="Agent" value={agentId} onChange={(event) => setAgentId(event.target.value)} disabled={agents.isLoading}>
      {(agents.data ?? []).filter((agent) => agent.status === 'ACTIVE').map((agent) => <MenuItem key={agent.agentId} value={agent.agentId}>{agent.fullName} · {agent.agentCode} · {agent.status}</MenuItem>)}
    </TextField>
    {assign.isError && <Alert severity="error">{assign.error.message || 'Assignment failed.'}</Alert>}
    {assign.isSuccess && <Alert severity="success">Agent assignment saved.</Alert>}
    <Button variant="contained" disabled={!agentId || assign.isPending || agents.isLoading} onClick={() => assign.mutate()}>{lead.assignedAgentId ? 'Reassign agent' : 'Assign agent'}</Button>
  </Stack></CardContent></Card>;
}
