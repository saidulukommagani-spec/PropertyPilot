import { Avatar, Box, Card, CardContent, Chip, Divider, Skeleton, Stack, Typography } from '@mui/material';
import { useAgentProfile, useAgentVisits } from '../hooks/useAgentOperations';
import { MetricCards, PageHeading, QueryFailure } from '../components/OperationsStates';

export default function AgentProfilePage() {
  const profile = useAgentProfile(); const visits = useAgentVisits(); const agent = profile.data; const allVisits = visits.data ?? [];
  const metrics = [{ label: 'Total visits', value: allVisits.length }, { label: 'Completed visits', value: allVisits.filter((visit) => visit.status === 'COMPLETED').length }, { label: 'Active workload', value: agent?.activeAssignmentCount ?? 0 }];
  return <Stack spacing={3}><PageHeading title="Agent profile" subtitle="Your operational profile, skills, assigned area, and performance." />
    {profile.isError && <QueryFailure message={profile.error.message} retry={() => void profile.refetch()} />}{visits.isError && <QueryFailure message={visits.error.message} retry={() => void visits.refetch()} />}
    {profile.isLoading ? <Skeleton variant="rounded" height={210} /> : agent && <Card variant="outlined"><CardContent><Stack direction={{ xs: 'column', sm: 'row' }} spacing={2.5} alignItems={{ sm: 'center' }}><Avatar sx={{ width: 64, height: 64 }}>{agent.fullName.slice(0, 1)}</Avatar><Box flex={1}><Typography variant="h5" fontWeight={750}>{agent.fullName}</Typography><Typography color="text.secondary">{agent.agentCode}{agent.phone ? ` · ${agent.phone}` : ''}</Typography></Box><Chip color={agent.status === 'ACTIVE' ? 'success' : 'default'} label={agent.status} /></Stack><Divider sx={{ my: 2.5 }} /><Box display="grid" gridTemplateColumns={{ xs: '1fr', sm: '1fr 1fr' }} gap={2}><Box><Typography variant="overline" color="text.secondary">Assigned area</Typography><Typography>{agent.region || 'No area recorded'}</Typography></Box><Box><Typography variant="overline" color="text.secondary">Availability</Typography><Typography>{agent.availability}</Typography></Box><Box gridColumn={{ sm: '1 / -1' }}><Typography variant="overline" color="text.secondary">Skills</Typography><Stack direction="row" spacing={1} flexWrap="wrap" useFlexGap sx={{ mt: .5 }}>{agent.skills.length ? agent.skills.map((skill) => <Chip key={skill} label={skill} variant="outlined" />) : <Typography color="text.secondary">No active skills recorded.</Typography>}</Stack></Box></Box></CardContent></Card>}
    <MetricCards items={metrics} loading={visits.isLoading || profile.isLoading} />
  </Stack>;
}
