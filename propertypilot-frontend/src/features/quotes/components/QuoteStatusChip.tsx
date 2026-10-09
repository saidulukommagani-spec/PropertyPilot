import { Chip } from '@mui/material';

const colors: Record<string, 'default' | 'success' | 'error' | 'warning' | 'info'> = { ACTIVE: 'info', ACCEPTED: 'success', REJECTED: 'error', EXPIRED: 'default' };
export default function QuoteStatusChip({ status }: { status: string }) { return <Chip size="small" label={status.replaceAll('_', ' ')} color={colors[status] ?? 'default'} variant="outlined" />; }
