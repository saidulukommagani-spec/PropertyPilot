import { Chip } from '@mui/material';

interface CustomerStatusChipProps {
  status: string;
}

export function formatCustomerStatus(status: string): string {
  return status.toLowerCase().split('_').map((part) => part.charAt(0).toUpperCase() + part.slice(1)).join(' ');
}

export default function CustomerStatusChip({ status }: CustomerStatusChipProps) {
  const normalized = status.toUpperCase();
  const color = ['COMPLETED', 'APPROVED', 'PAID', 'SETTLED'].includes(normalized)
    ? 'success'
    : ['CANCELLED', 'FAILED', 'REJECTED', 'OVERDUE'].includes(normalized)
      ? 'error'
      : ['IN_PROGRESS', 'PENDING_REVIEW', 'PENDING_VALIDATION', 'ASSIGNED', 'ACCEPTED'].includes(normalized)
        ? 'warning'
        : 'info';

  return <Chip size="small" color={color} label={formatCustomerStatus(status)} />;
}
