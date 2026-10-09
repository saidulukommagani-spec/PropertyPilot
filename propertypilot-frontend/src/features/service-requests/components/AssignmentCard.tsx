import { Avatar, Button, Card, CardContent, Divider, Stack, Typography } from '@mui/material';
import AssignmentIndRounded from '@mui/icons-material/AssignmentIndRounded';
import type { ServiceRequestDetailsResponse } from '../types/serviceRequest';

const dateTime = (value?: string) => value ? new Intl.DateTimeFormat(undefined, { dateStyle: 'medium', timeStyle: 'short' }).format(new Date(value)) : '—';
interface Props { request: ServiceRequestDetailsResponse; onAssignClick(): void; canManageAssignment?: boolean }

export default function AssignmentCard({ request, onAssignClick, canManageAssignment = true }: Props) {
  const assigned = Boolean(request.assignedToName);
  return <Card component="section" aria-labelledby="assignment-card-title">
    <CardContent sx={{ p: { xs: 2.5, md: 3 } }}>
      <Stack direction="row" alignItems="center" justifyContent="space-between" spacing={1}>
        <Typography id="assignment-card-title" variant="h6" fontWeight={700}>Assignment</Typography>
        {canManageAssignment && <Button size="small" startIcon={<AssignmentIndRounded />} onClick={onAssignClick}>{assigned ? 'Reassign agent' : 'Assign agent'}</Button>}
      </Stack>
      <Divider sx={{ my: 2 }} />
      {assigned ? <Stack direction="row" spacing={1.5} alignItems="flex-start">
        <Avatar aria-hidden="true">{request.assignedToName?.slice(0, 1).toUpperCase()}</Avatar>
        <Stack spacing={0.5}>
          <Typography fontWeight={700}>{request.assignedToName}</Typography>
          {request.assignedAgentCode && <Typography variant="body2" color="text.secondary">Agent code: {request.assignedAgentCode}</Typography>}
          <Typography variant="body2" color="text.secondary">Phone: {request.assignedAgentPhone || 'Not provided'}</Typography>
          <Typography variant="body2" color="text.secondary">Assigned by {request.assignedByName || '—'}</Typography>
          <Typography variant="body2" color="text.secondary">{dateTime(request.assignedAt)}</Typography>
          <Typography variant="body2">Notes: {request.assignmentNotes || '—'}</Typography>
        </Stack>
      </Stack> : <Typography color="text.secondary">No agent is assigned to this request. Assign an active agent to begin operations.</Typography>}
    </CardContent>
  </Card>;
}
