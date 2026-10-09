import { Alert, Button, Skeleton, Stack } from '@mui/material';

export function LeadLoadingState() {
  return <Stack spacing={2} aria-label="Loading leads">{[1, 2, 3].map((item) => <Skeleton key={item} variant="rounded" height={76} />)}</Stack>;
}

export function LeadErrorState({ error, retry }: { error: Error | null; retry(): void }) {
  return <Alert severity="error" action={<Button color="inherit" onClick={retry}>Retry</Button>}>
    {error?.message || 'Lead information could not be loaded.'}
  </Alert>;
}
