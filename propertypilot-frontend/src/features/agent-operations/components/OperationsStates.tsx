import { Alert, Box, Button, Card, CardContent, Skeleton, Stack, Typography } from '@mui/material';
import type { ReactNode } from 'react';

export function MetricCards({ items, loading }: { items: Array<{ label: string; value: number | string }>; loading: boolean }) {
  return <Box display="grid" gridTemplateColumns={{ xs: 'repeat(2,minmax(0,1fr))', md: 'repeat(3,minmax(0,1fr))', xl: 'repeat(6,minmax(0,1fr))' }} gap={2}>
    {items.map((item) => <Card key={item.label} variant="outlined"><CardContent><Typography variant="body2" color="text.secondary">{item.label}</Typography>{loading ? <Skeleton width={70} height={42} /> : <Typography variant="h4" fontWeight={750}>{item.value}</Typography>}</CardContent></Card>)}
  </Box>;
}

export function QueryFailure({ message, retry }: { message: string; retry: () => void }) {
  return <Alert severity="error" action={<Button color="inherit" size="small" onClick={retry}>Retry</Button>}>{message}</Alert>;
}

export function PageHeading({ title, subtitle, action }: { title: string; subtitle: string; action?: ReactNode }) {
  return <Stack direction={{ xs: 'column', sm: 'row' }} justifyContent="space-between" alignItems={{ sm: 'center' }} gap={1}><Box><Typography variant="h4" fontWeight={750}>{title}</Typography><Typography color="text.secondary" sx={{ mt: .5 }}>{subtitle}</Typography></Box>{action}</Stack>;
}
