import { Box, Stack, Typography } from '@mui/material';
import type { ReactNode } from 'react';

type DashboardPageHeadingProps = {
  eyebrow: string;
  title: string;
  subtitle: string;
  action?: ReactNode;
};

export default function DashboardPageHeading({ eyebrow, title, subtitle, action }: DashboardPageHeadingProps) {
  return <Box sx={{ pb: 1.75, borderBottom: '1px solid', borderColor: 'divider' }}>
    <Stack direction={{ xs: 'column', sm: 'row' }} justifyContent="space-between" alignItems={{ sm: 'center' }} spacing={1.5}>
      <Box>
        <Typography variant="overline" sx={{ color: '#A8792E', fontWeight: 750, fontSize: 10, letterSpacing: 1.25 }}>{eyebrow}</Typography>
        <Typography variant="h4" sx={{ mt: 0.15 }}>{title}</Typography>
        <Typography variant="body2" color="text.secondary" sx={{ mt: 0.5, maxWidth: 680 }}>{subtitle}</Typography>
        <Box aria-hidden="true" sx={{ mt: 1.4, width: 72, height: 3, borderRadius: 2, background: 'linear-gradient(90deg, #B88A3B 0 35%, #167F83 35% 100%)' }} />
      </Box>
      {action}
    </Stack>
  </Box>;
}
