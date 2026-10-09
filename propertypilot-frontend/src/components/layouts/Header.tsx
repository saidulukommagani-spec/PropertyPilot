import { useState } from 'react';
import { AppBar, Avatar, Box, IconButton, Menu, MenuItem, Toolbar, Typography } from '@mui/material';
import { useLocation } from 'react-router-dom';
import MenuRounded from '@mui/icons-material/MenuRounded';
import NotificationsNoneRounded from '@mui/icons-material/NotificationsNoneRounded';
import { useAuth } from '../../features/auth/AuthContext';
import { getUserRoles } from '../../api/auth';
import { SIDEBAR_WIDTH } from './Sidebar';
import BrandLogo from './BrandLogo';

interface HeaderProps { onMenuClick(): void }

function workspaceForRoles(roles: string[]) {
  if (roles.includes('ADMIN')) return { title: 'Admin console', description: 'Configuration, access, and business oversight' };
  if (roles.includes('OPERATIONS')) return { title: 'Operations', description: 'PropertyPilot operations workspace' };
  if (roles.includes('AGENT')) return { title: 'Field operations', description: 'Assigned work and visit execution' };
  if (roles.includes('CUSTOMER')) return { title: 'Customer portal', description: 'Your properties, services, and account' };
  return { title: 'PropertyPilot', description: 'Your workspace' };
}

function sectionForPath(pathname: string, workspaceTitle: string) {
  const segments = pathname.split('/').filter(Boolean);
  if (segments.length === 0 || (segments.length === 1 && segments[0] === 'staff')) return workspaceTitle;
  const labels: Record<string, string> = {
    'service-requests': 'Service requests', 'service-request': 'Service request', visits: 'Visits', visit: 'Visit',
    evidence: 'Evidence', customers: 'Customers', customer: 'Customer', properties: 'Properties', property: 'Property',
    subscriptions: 'Subscriptions', subscription: 'Subscription', quotes: 'Quotes', quote: 'Quote', deals: 'Deals', deal: 'Deal',
    invoices: 'Invoices', invoice: 'Invoice', payments: 'Payments', payment: 'Payment', leads: 'Leads', lead: 'Lead',
    agents: 'Agents', 'work-queue': 'Work queue', profile: 'Profile', users: 'Users and roles', pricing: 'Pricing rules',
    plans: 'Subscription plans', dashboard: 'Dashboard', validation: 'Evidence validation', renewals: 'Renewals',
    addons: 'Add-ons', assignments: 'Agent assignments',
  };
  let sectionIndex = -1;
  for (let index = 0; index < segments.length; index += 1) {
    if (segments[index] === 'dashboard' && index > 0 && labels[segments[index - 1]]) continue;
    if (labels[segments[index]]) sectionIndex = index;
  }
  if (sectionIndex < 0) return workspaceTitle;
  const key = segments[sectionIndex];
  const label = labels[key];
  const singularLabel = label.endsWith('s') ? label.slice(0, -1) : label;
  const tail = segments.slice(sectionIndex + 1);
  if (tail.at(-1) === 'dashboard') return `${label} dashboard`;
  if (tail.at(-1) === 'new') return `New ${singularLabel.toLowerCase()}`;
  if (tail.at(-1) === 'edit') return `Edit ${singularLabel.toLowerCase()}`;
  if (tail.length > 0 && !labels[tail.at(-1) ?? '']) return `${singularLabel} details`;
  return label;
}

export default function Header({ onMenuClick }: HeaderProps) {
  const { user, signOut } = useAuth();
  const { pathname } = useLocation();
  const [anchor, setAnchor] = useState<HTMLElement | null>(null);
  const workspace = workspaceForRoles(getUserRoles(user));
  const sectionTitle = sectionForPath(pathname, workspace.title);
  return <AppBar position="fixed" color="inherit" elevation={0} sx={{ backgroundImage: 'linear-gradient(90deg, rgba(184,138,59,.08), transparent 320px)', borderBottom: '1px solid', borderColor: 'divider', width: { md: `calc(100% - ${SIDEBAR_WIDTH}px)` }, ml: { md: `${SIDEBAR_WIDTH}px` } }}>
    <Toolbar sx={{ minHeight: '64px !important', gap: 1.25, px: { xs: 2, md: 2.5 } }}>
      <IconButton onClick={onMenuClick} aria-label="Open navigation" sx={{ display: { md: 'none' } }}><MenuRounded /></IconButton>
      <BrandLogo variant="mark" alt="PropertyPilot" sx={{ width: 27, height: 32, flexShrink: 0 }} />
      <Box sx={{ flexGrow: 1, minWidth: 0 }} role="group" aria-label={`${workspace.title} workspace`}>
        <Typography variant="subtitle1" fontWeight={700} noWrap>{sectionTitle}</Typography>
        <Typography variant="caption" color="text.secondary" noWrap sx={{ display: { xs: 'none', sm: 'block' } }}><Box component="span" sx={{ color: '#A8792E', fontWeight: 750, textTransform: 'uppercase', letterSpacing: 0.7 }}>{workspace.title}</Box> · {workspace.description}</Typography>
      </Box>
      <IconButton aria-label="Notifications"><NotificationsNoneRounded /></IconButton>
      <IconButton onClick={(event) => setAnchor(event.currentTarget)} aria-label="Account menu" sx={{ p: 0.5 }}><Avatar sx={{ width: 34, height: 34, bgcolor: 'primary.main', fontSize: 14 }}>{user?.fullName?.charAt(0)?.toUpperCase() ?? 'S'}</Avatar></IconButton>
      <Menu anchorEl={anchor} open={Boolean(anchor)} onClose={() => setAnchor(null)}>
        <MenuItem disabled>{user?.fullName ?? user?.email}</MenuItem>
        <MenuItem onClick={() => { setAnchor(null); signOut(); }}>Sign out</MenuItem>
      </Menu>
    </Toolbar>
  </AppBar>;
}
