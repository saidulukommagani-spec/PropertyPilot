import { useEffect, useState } from 'react';
import { Alert, Button, CircularProgress, Dialog, DialogActions, DialogContent, DialogTitle, FormControl, InputLabel, MenuItem, Select, Stack, TextField, Typography } from '@mui/material';
import type { AssignableAgent } from '../types/serviceRequest';

interface Props { open: boolean; isReassign: boolean; agents: AssignableAgent[]; isLoading: boolean; error?: Error | null; isSaving: boolean; onClose(): void; onRetry(): void; onSubmit(agentId: string, notes: string): Promise<void> }
export default function AssignAgentDialog({ open, isReassign, agents, isLoading, error, isSaving, onClose, onRetry, onSubmit }: Props) {
  const [agentId, setAgentId] = useState(''); const [notes, setNotes] = useState(''); const [submitError, setSubmitError] = useState<string | null>(null);
  const [search, setSearch] = useState(''); const [skill, setSkill] = useState('ALL'); const [availability, setAvailability] = useState('ALL');
  useEffect(() => { if (!open) { setAgentId(''); setNotes(''); setSubmitError(null); setSearch(''); setSkill('ALL'); setAvailability('ALL'); } }, [open]);
  const skills = [...new Set(agents.flatMap((agent) => agent.skills ?? []))].sort();
  const filteredAgents = agents.filter((agent) => `${agent.fullName} ${agent.agentCode}`.toLowerCase().includes(search.trim().toLowerCase()) && (skill === 'ALL' || agent.skills?.includes(skill)) && (availability === 'ALL' || agent.availability === availability));
  const submit = async () => { if (!agentId) return; setSubmitError(null); try { await onSubmit(agentId, notes.trim()); onClose(); } catch (cause) { setSubmitError(cause instanceof Error ? cause.message : 'Unable to assign this agent.'); } };
  return <Dialog open={open} onClose={() => !isSaving && onClose()} fullWidth maxWidth="sm" aria-labelledby="assign-agent-title">
    <DialogTitle id="assign-agent-title">{isReassign ? 'Reassign agent' : 'Assign agent'}</DialogTitle>
    <DialogContent><Stack spacing={2} sx={{ pt: 1 }}>
      {error && <Alert severity="error" action={<Button color="inherit" size="small" onClick={onRetry}>Retry</Button>}>{error.message}</Alert>}
      {submitError && <Alert severity="error">{submitError}</Alert>}
      <TextField fullWidth label="Search agents" value={search} onChange={(event) => setSearch(event.target.value)} />
      <Stack direction={{ xs: 'column', sm: 'row' }} spacing={1.5}>
        <FormControl fullWidth><InputLabel id="agent-skill-label">Skill</InputLabel><Select labelId="agent-skill-label" label="Skill" value={skill} onChange={(event) => setSkill(event.target.value)}><MenuItem value="ALL">All skills</MenuItem>{skills.map((value) => <MenuItem key={value} value={value}>{value}</MenuItem>)}</Select></FormControl>
        <FormControl fullWidth><InputLabel id="agent-availability-label">Availability</InputLabel><Select labelId="agent-availability-label" label="Availability" value={availability} onChange={(event) => setAvailability(event.target.value)}><MenuItem value="ALL">All statuses</MenuItem><MenuItem value="AVAILABLE">Available</MenuItem><MenuItem value="BUSY">Busy</MenuItem></Select></FormControl>
      </Stack>
      <FormControl fullWidth required disabled={isLoading || !agents.length}>
        <InputLabel id="assign-agent-label">Agent</InputLabel>
        <Select labelId="assign-agent-label" label="Agent" value={agentId} onChange={(event) => setAgentId(event.target.value)}>
          {filteredAgents.map((agent) => <MenuItem key={agent.agentId} value={agent.agentId}>
            <Stack><Typography>{agent.fullName} · {agent.agentCode}</Typography><Typography variant="caption" color="text.secondary">{agent.status} · {agent.availability}{agent.phone ? ` · ${agent.phone}` : ''}{agent.skills?.length ? ` · ${agent.skills.join(', ')}` : ''}</Typography></Stack>
          </MenuItem>)}
        </Select>
      </FormControl>
      {isLoading && <CircularProgress size={22} aria-label="Loading active agents" />}
      {!isLoading && !error && !filteredAgents.length && <Alert severity="info">No agents match these filters.</Alert>}
      <TextField fullWidth multiline minRows={3} label="Assignment notes" value={notes} onChange={(event) => setNotes(event.target.value)} inputProps={{ maxLength: 2000 }} />
    </Stack></DialogContent>
    <DialogActions><Button onClick={onClose} disabled={isSaving}>Cancel</Button><Button variant="contained" onClick={() => void submit()} disabled={!agentId || isSaving}>{isSaving ? <CircularProgress size={18} /> : isReassign ? 'Reassign' : 'Assign'}</Button></DialogActions>
  </Dialog>;
}
