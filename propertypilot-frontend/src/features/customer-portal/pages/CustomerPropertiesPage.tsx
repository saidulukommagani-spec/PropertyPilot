import { useMemo, useState } from 'react';
import { Alert, Box, Button, Card, CardActionArea, CardContent, Chip, MenuItem, Skeleton, Stack, TablePagination, TextField, Typography } from '@mui/material';
import SearchRounded from '@mui/icons-material/SearchRounded';
import { Link } from 'react-router-dom';
import { useCustomerProperties } from '../hooks/useCustomerPortal';
import { CustomerPageHeading, CustomerQueryError } from '../components/CustomerStates';
import { formatCustomerStatus } from '../components/CustomerStatusChip';

const PAGE_SIZE_OPTIONS = [6, 12, 24];

export default function CustomerPropertiesPage() {
  const query = useCustomerProperties();
  const [search, setSearch] = useState('');
  const [status, setStatus] = useState('ALL');
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(PAGE_SIZE_OPTIONS[0]);
  const properties = query.data ?? [];
  const statuses = [...new Set(properties.map((property) => property.status ?? 'ACTIVE'))].sort();
  const filtered = useMemo(() => properties.filter((property) => {
    const searchable = `${property.title} ${property.propertyType ?? ''} ${property.listingStatus ?? ''}`.toLowerCase();
    return searchable.includes(search.trim().toLowerCase()) && (status === 'ALL' || (property.status ?? 'ACTIVE') === status);
  }), [properties, search, status]);
  const pageItems = filtered.slice(page * pageSize, (page + 1) * pageSize);

  return <Stack spacing={3}>
    <CustomerPageHeading title="My properties" subtitle="Your registered properties and their service activity." action={<Button component={Link} to="/customer/properties/new" variant="contained">Add property</Button>} />
    {query.isError && <CustomerQueryError error={query.error} retry={() => void query.refetch()} />}

    <Stack direction={{ xs: 'column', sm: 'row' }} spacing={1.5}>
      <TextField fullWidth size="small" label="Search properties" value={search} onChange={(event) => { setSearch(event.target.value); setPage(0); }} InputProps={{ startAdornment: <SearchRounded color="action" sx={{ mr: 1 }} aria-hidden="true" /> }} />
      <TextField select size="small" label="Property status" value={status} onChange={(event) => { setStatus(event.target.value); setPage(0); }} sx={{ minWidth: { sm: 190 } }}>
        <MenuItem value="ALL">All statuses</MenuItem>
        {statuses.map((value) => <MenuItem key={value} value={value}>{formatCustomerStatus(value)}</MenuItem>)}
      </TextField>
    </Stack>

    {query.isLoading ? <Box display="grid" gridTemplateColumns={{ xs: '1fr', sm: 'repeat(2,minmax(0,1fr))', lg: 'repeat(3,minmax(0,1fr))' }} gap={2} aria-label="Loading properties">
      {Array.from({ length: 3 }, (_, index) => <Skeleton key={index} variant="rounded" height={170} />)}
    </Box> : filtered.length > 0 ? <>
      <Box display="grid" gridTemplateColumns={{ xs: '1fr', sm: 'repeat(2,minmax(0,1fr))', lg: 'repeat(3,minmax(0,1fr))' }} gap={2}>
        {pageItems.map((property) => <Card key={property.propertyId} variant="outlined">
          <CardActionArea component={Link} to={`/customer/properties/${property.propertyId}`} aria-label={`View ${property.title}`} sx={{ height: '100%' }}>
            <CardContent>
              <Stack direction="row" justifyContent="space-between" alignItems="flex-start" gap={1}>
                <Typography variant="h6" fontWeight={700} sx={{ overflowWrap: 'anywhere' }}>{property.title}</Typography>
                <Chip size="small" label={formatCustomerStatus(property.status ?? 'ACTIVE')} color={(property.status ?? 'ACTIVE') === 'ACTIVE' ? 'success' : 'default'} />
              </Stack>
              <Typography color="text.secondary" sx={{ mt: 1 }}>{property.propertyType || 'Property type not provided'}</Typography>
              {property.listingStatus && <Typography variant="body2" color="text.secondary" sx={{ mt: .5 }}>Listing status: {formatCustomerStatus(property.listingStatus)}</Typography>}
              <Typography variant="body2" color="primary.main" fontWeight={650} sx={{ mt: 2 }}>View property details</Typography>
            </CardContent>
          </CardActionArea>
        </Card>)}
      </Box>
      <TablePagination component="div" count={filtered.length} page={page} onPageChange={(_, next) => setPage(next)} rowsPerPage={pageSize} onRowsPerPageChange={(event) => { setPageSize(Number(event.target.value)); setPage(0); }} rowsPerPageOptions={PAGE_SIZE_OPTIONS} labelRowsPerPage="Properties per page" />
    </> : !query.isError && (properties.length > 0 ? <Alert severity="info" action={<Button onClick={() => { setSearch(''); setStatus('ALL'); }}>Clear filters</Button>}>No properties match your search.</Alert> : <Alert severity="info" action={<Button component={Link} to="/customer/properties/new">Add property</Button>}>You have no registered properties yet.</Alert>)}
  </Stack>;
}
