import { useEffect, useState } from 'react';
import { Alert, Button, CircularProgress, Dialog, DialogActions, DialogContent, DialogTitle, FormControl, InputLabel, MenuItem, Select, Stack, TextField, Typography } from '@mui/material';
import type { CreateVisitRequest, VisitAssignmentOption } from '../types/visit';

interface Props { open: boolean; assignments: VisitAssignmentOption[]; isSaving: boolean; onClose(): void; onSubmit(request: CreateVisitRequest): Promise<void> }
export default function CreateVisitDialog({ open, assignments, isSaving, onClose, onSubmit }: Props) {
  const [assignmentId, setAssignmentId] = useState(''); const [scheduledAt, setScheduledAt] = useState(''); const [notes, setNotes] = useState(''); const [error, setError] = useState<string | null>(null);
  useEffect(() => { if (!open) { setAssignmentId(''); setScheduledAt(''); setNotes(''); setError(null); } }, [open]);
  const handleSubmit = async () => {
    if (!assignmentId || !scheduledAt) return;
    setError(null);
    try { await onSubmit({ assignmentId, scheduledAt: new Date(scheduledAt).toISOString(), notes: notes.trim() || undefined }); onClose(); }
    catch (cause) { setError(cause instanceof Error ? cause.message : 'Unable to create the visit.'); }
  };
  const minDateTime = new Date(Date.now() - new Date().getTimezoneOffset() * 60_000).toISOString().slice(0, 16);
  return <Dialog open={open} onClose={() => !isSaving && onClose()} fullWidth maxWidth="sm" aria-labelledby="create-visit-title">
    <DialogTitle id="create-visit-title">Create Visit</DialogTitle>
    <DialogContent><Stack spacing={2} sx={{ pt: 1 }}>
      {error && <Alert severity="error">{error}</Alert>}
      {!assignments.length && <Alert severity="warning">A request must have an accepted or in-progress agent assignment before a visit can be created.</Alert>}
      <FormControl fullWidth required disabled={!assignments.length}>
        <InputLabel id="visit-agent-label">Agent</InputLabel>
        <Select labelId="visit-agent-label" label="Agent" value={assignmentId} onChange={(event) => setAssignmentId(event.target.value)}>
          {assignments.map((assignment) => <MenuItem value={assignment.assignmentId} key={assignment.assignmentId}><Stack><Typography>{assignment.agentName}{assignment.agentCode ? ` · ${assignment.agentCode}` : ''}</Typography><Typography variant="caption" color="text.secondary">{assignment.assignmentStatus.replaceAll('_', ' ')}</Typography></Stack></MenuItem>)}
        </Select>
      </FormControl>
      <TextField required fullWidth type="datetime-local" label="Scheduled date" value={scheduledAt} onChange={(event) => setScheduledAt(event.target.value)} InputLabelProps={{ shrink: true }} inputProps={{ min: minDateTime }} />
      <TextField fullWidth multiline minRows={3} label="Visit notes" value={notes} onChange={(event) => setNotes(event.target.value)} inputProps={{ maxLength: 1000 }} />
    </Stack></DialogContent>
    <DialogActions><Button onClick={onClose} disabled={isSaving}>Cancel</Button><Button variant="contained" onClick={() => void handleSubmit()} disabled={!assignmentId || !scheduledAt || !assignments.length || isSaving}>{isSaving ? <CircularProgress size={18}/> : 'Create Visit'}</Button></DialogActions>
  </Dialog>;
}
