import { Box, Card, CardActionArea, CardContent, Skeleton, Typography } from '@mui/material';
import type { ReactNode } from 'react';
import { Link } from 'react-router-dom';

type Columns = Partial<Record<'xs' | 'sm' | 'md' | 'lg' | 'xl', number>>;
type DashboardMetric = {
  label: string;
  value: ReactNode;
  description?: string;
  action?: string;
  to?: string;
  accent?: 'teal' | 'error' | 'warning';
};

export default function DashboardMetricCards({
  items,
  loading = false,
  columns = { xs: 2, md: 3, xl: 6 },
}: {
  items: DashboardMetric[];
  loading?: boolean;
  columns?: Columns;
}) {
  const gridColumns = Object.fromEntries(Object.entries(columns).map(([breakpoint, count]) => [breakpoint, `repeat(${count}, minmax(0, 1fr))`]));
  return <Box sx={{ display: 'grid', gridTemplateColumns: gridColumns, gap: 2, alignItems: 'stretch' }}>
    {items.map((item) => {
      const content = <CardContent sx={{ p: { xs: 2, sm: 2.25 }, pl: { xs: 2.25, sm: 2.5 } }}>
        <Typography variant="overline" color="text.secondary" sx={{ display: 'block', fontSize: 9, lineHeight: 1.4, fontWeight: 750, letterSpacing: 0.85, textTransform: 'uppercase' }}>{item.label}</Typography>
        <Typography variant="h4" sx={{ mt: 0.4, fontVariantNumeric: 'tabular-nums', color: item.accent === 'error' ? 'error.dark' : item.accent === 'warning' ? 'warning.dark' : 'primary.main' }}>
          {loading ? <Skeleton width={72} /> : item.value}
        </Typography>
        {item.description && <Typography variant="caption" color="text.secondary" sx={{ display: 'block', mt: 0.25 }}>{item.description}</Typography>}
        {item.action && <Typography variant="caption" sx={{ color: 'secondary.dark', fontWeight: 700, display: 'block', mt: 1 }}>{item.action}{' '}<Box component="span" aria-hidden="true">→</Box></Typography>}
      </CardContent>;
      const accent = item.accent === 'error' ? 'error.main' : item.accent === 'warning' ? 'warning.main' : 'secondary.main';
      return <Card key={item.label} variant="outlined" sx={{ height: '100%', minWidth: 0, overflow: 'hidden', position: 'relative', '&::before': { content: '""', position: 'absolute', left: 0, top: 0, width: 4, height: '100%', bgcolor: accent, opacity: 0.8 } }}>
        {item.to ? <CardActionArea component={Link} to={item.to} sx={{ height: '100%', display: 'block', '&:focus-visible': { outline: '3px solid', outlineColor: 'primary.main', outlineOffset: -3 } }}>{content}</CardActionArea> : content}
      </Card>;
    })}
  </Box>;
}
