import { Chip } from '@mui/material';
import type { LeadStatus } from '../types/lead';

const colors: Record<LeadStatus, 'default' | 'info' | 'success' | 'warning' | 'error'> = {
  NEW: 'info', CONTACTED: 'warning', QUALIFIED: 'success', READY_FOR_DEAL: 'warning',
  DEAL_CONVERTED: 'success', CONVERTED: 'success', LOST: 'error', CLOSED: 'default',
};

export default function LeadStatusChip({ status }: { status: LeadStatus }) {
  return <Chip size="small" label={status.replaceAll('_', ' ')} color={colors[status]} />;
}
