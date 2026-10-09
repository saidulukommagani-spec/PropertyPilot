import { Chip } from '@mui/material';
import type { InvoiceStatus } from '../types/invoice';
const palette:Record<string,'default'|'success'|'error'|'warning'|'info'>={DRAFT:'default',SENT:'warning',PARTIALLY_PAID:'info',PAID:'success',OVERDUE:'error',CANCELLED:'default'};
export default function InvoiceStatusChip({status}:{status:InvoiceStatus}){return <Chip size="small" variant="outlined" color={palette[status]??'default'} label={status.replaceAll('_',' ')}/>;}
