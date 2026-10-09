import { useState, type ReactNode } from 'react';
import { Box, Toolbar } from '@mui/material';
import Header from './Header';
import Sidebar, { SIDEBAR_WIDTH } from './Sidebar';

export default function DashboardLayout({ children }: { children: ReactNode }) {
  const [mobileOpen, setMobileOpen] = useState(false);
  return <Box sx={{ minHeight: '100vh', bgcolor: 'background.default', backgroundImage: 'radial-gradient(ellipse at 88% 0%, rgba(22,127,131,.045), transparent 34rem)' }}>
    <Box component="a" href="#main-content" className="skip-link">Skip to main content</Box>
    <Header onMenuClick={() => setMobileOpen(true)} /><Sidebar mobileOpen={mobileOpen} onClose={() => setMobileOpen(false)} />
    <Box component="main" id="main-content" tabIndex={-1} aria-label="Main content" sx={{ ml: { md: `${SIDEBAR_WIDTH}px` }, minHeight: '100vh' }}>
      <Toolbar sx={{ minHeight: '64px !important' }} /><Box sx={{ p: { xs: 2, sm: 2.5, md: 2.5 }, maxWidth: 1480, mx: 'auto' }}>{children}</Box>
    </Box>
  </Box>;
}
