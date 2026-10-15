import { Box, Drawer, List, ListItemButton, ListItemIcon, ListItemText, ListSubheader, Toolbar, Typography } from '@mui/material';
import DashboardRounded from '@mui/icons-material/DashboardRounded';
import AssignmentRounded from '@mui/icons-material/AssignmentRounded';
import PeopleAltRounded from '@mui/icons-material/PeopleAltRounded';
import HomeWorkRounded from '@mui/icons-material/HomeWorkRounded';
import EngineeringRounded from '@mui/icons-material/EngineeringRounded';
import EventNoteRounded from '@mui/icons-material/EventNoteRounded';
import FactCheckRounded from '@mui/icons-material/FactCheckRounded';
import ReceiptLongRounded from '@mui/icons-material/ReceiptLongRounded';
import PaymentsRounded from '@mui/icons-material/PaymentsRounded';
import LayersRounded from '@mui/icons-material/LayersRounded';
import HandshakeRounded from '@mui/icons-material/HandshakeRounded';
import Inventory2Rounded from '@mui/icons-material/Inventory2Rounded';
import SettingsRounded from '@mui/icons-material/SettingsRounded';
import AccountCircleRounded from '@mui/icons-material/AccountCircleRounded';
import ReceiptRounded from '@mui/icons-material/ReceiptRounded';
import AutorenewRounded from '@mui/icons-material/AutorenewRounded';
import PersonSearchRounded from '@mui/icons-material/PersonSearchRounded';
import { NavLink } from 'react-router-dom';
import BrandLogo from './BrandLogo';
import { useAuth } from '../../features/auth/AuthContext';
import { getUserRoles } from '../../api/auth';

export const SIDEBAR_WIDTH = 232;
const groups = [
  { title: 'WORKSPACE', items: [{ label: 'Overview', to: '/staff', icon: <DashboardRounded /> }] },
  { title: 'CUSTOMERS & ASSETS', items: [
    { label: 'Customers', to: '/staff/customers', icon: <PeopleAltRounded /> },
    { label: 'Properties', to: '/staff/properties', icon: <HomeWorkRounded /> },
    { label: 'Lead queue', to: '/operations/leads', icon: <PersonSearchRounded /> },
    { label: 'Subscriptions', to: '/subscriptions/dashboard', icon: <LayersRounded /> },
  ] },
  { title: 'SERVICE DELIVERY', items: [
    { label: 'Service requests', to: '/staff/service-requests', icon: <AssignmentRounded /> },
    { label: 'Agents', to: '/staff/agents', icon: <EngineeringRounded /> },
    { label: 'Agent performance', to: '/operations/agents', icon: <PeopleAltRounded /> },
    { label: 'Agent assignments', to: '/operations/assignments', icon: <EngineeringRounded /> },
    { label: 'Visits', to: '/visits', icon: <EventNoteRounded /> },
    { label: 'Evidence', to: '/evidence', icon: <FactCheckRounded /> },
  ] },
  { title: 'QUOTE TO CASH', items: [
    { label: 'Quotes & estimates', to: '/quotes/dashboard', icon: <ReceiptLongRounded /> },
    { label: 'Deals', to: '/deals/dashboard', icon: <HandshakeRounded /> },
    { label: 'Invoices', to: '/invoices/dashboard', icon: <ReceiptLongRounded /> },
    { label: 'Payments', to: '/staff/payments', icon: <PaymentsRounded /> },
  ] },
  { title: 'ADMIN SETTINGS', items: [
    { label: 'Users and roles', to: '/staff/users', icon: <PeopleAltRounded /> },
    { label: 'Service catalog', to: '/staff/services', icon: <Inventory2Rounded /> },
    { label: 'Pricing rules', to: '/staff/pricing', icon: <SettingsRounded /> },
    { label: 'Subscription plans', to: '/staff/plans', icon: <LayersRounded /> },
  ] },
];

interface SidebarProps { mobileOpen: boolean; onClose(): void }

export default function Sidebar({ mobileOpen, onClose }: SidebarProps) {
  const { user } = useAuth();
  const roles = getUserRoles(user);
  const customerOnly = roles.includes('CUSTOMER') && !roles.includes('ADMIN') && !roles.includes('OPERATIONS') && !roles.includes('AGENT');
  const agentOnly = roles.includes('AGENT') && !roles.includes('ADMIN') && !roles.includes('OPERATIONS');
  const operationsOnly = roles.includes('OPERATIONS') && !roles.includes('ADMIN');
  const visibleGroups = customerOnly ? [{ title: 'MY PROPERTY PORTAL', items: [
    { label: 'Dashboard', to: '/customer/dashboard', icon: <DashboardRounded /> },
    { label: 'My Properties', to: '/customer/properties', icon: <HomeWorkRounded /> },
    { label: 'My Leads', to: '/customer/leads/dashboard', icon: <PersonSearchRounded /> },
    { label: 'Service Requests', to: '/customer/service-requests', icon: <AssignmentRounded /> },
    { label: 'My Visits', to: '/customer/visits', icon: <EventNoteRounded /> },
    { label: 'Evidence', to: '/customer/evidence', icon: <FactCheckRounded /> },
    { label: 'Subscriptions', to: '/subscriptions', icon: <LayersRounded /> },
    { label: 'Quotes', to: '/quotes', icon: <ReceiptLongRounded /> },
    { label: 'Subscription overview', to: '/subscriptions/dashboard', icon: <DashboardRounded /> },
    { label: 'Subscription plans', to: '/subscriptions/plans', icon: <Inventory2Rounded /> },
    { label: 'Renewals', to: '/subscriptions/renewals', icon: <AutorenewRounded /> },
    { label: 'Add-ons', to: '/subscriptions/addons', icon: <LayersRounded /> },
    { label: 'Invoices', to: '/customer/invoices', icon: <ReceiptRounded /> },
  ] }] : agentOnly ? [{ title: 'FIELD OPERATIONS', items: [
    { label: 'Dashboard', to: '/agent/dashboard', icon: <DashboardRounded /> },
    { label: 'My Work Queue', to: '/agent/work-queue', icon: <AssignmentRounded /> },
    { label: 'Assigned leads', to: '/agent/leads', icon: <PersonSearchRounded /> },
    { label: 'My Visits', to: '/agent/visits', icon: <EventNoteRounded /> },
    { label: 'My Profile', to: '/agent/profile', icon: <AccountCircleRounded /> },
  ] }] : operationsOnly ? [
    { title: 'WORKSPACE', items: [{ label: 'Overview', to: '/staff', icon: <DashboardRounded /> }] },
    { title: 'OPERATIONS', items: [
      { label: 'Lead queue', to: '/operations/leads', icon: <PersonSearchRounded /> },
      { label: 'Service requests', to: '/service-requests', icon: <AssignmentRounded /> },
      { label: 'Agent performance', to: '/operations/agents', icon: <PeopleAltRounded /> },
      { label: 'Agent assignments', to: '/operations/assignments', icon: <EngineeringRounded /> },
      { label: 'Visits', to: '/visits', icon: <EventNoteRounded /> },
      { label: 'Evidence', to: '/evidence', icon: <FactCheckRounded /> },
      { label: 'Quotes & estimates', to: '/quotes/dashboard', icon: <ReceiptLongRounded /> },
      { label: 'Subscriptions', to: '/subscriptions/dashboard', icon: <LayersRounded /> },
    ] },
  ] : groups;
  const roleCaption = roles.includes('ADMIN') ? 'ADMIN CONSOLE' : customerOnly ? 'OWNER PORTAL' : agentOnly ? 'FIELD OPERATIONS' : operationsOnly ? 'OPERATIONS' : 'STAFF PORTAL';
  const content = <Box sx={{ height: '100%', display: 'flex', flexDirection: 'column', overflow: 'hidden', bgcolor: '#09283D', color: 'common.white', backgroundImage: 'linear-gradient(180deg, rgba(19, 70, 88, .22), transparent 250px)' }}>
    <Toolbar sx={{ minHeight: '88px !important', px: 2.25, py: 1.5, flexShrink: 0, flexDirection: 'column', alignItems: 'flex-start', justifyContent: 'center', gap: 0.5, borderBottom: '1px solid rgba(232, 198, 126, .2)' }}>
      <BrandLogo variant="horizontal-dark" alt="PropertyPilot — Your property. On autopilot." sx={{ width: '100%', maxWidth: 198, height: 'auto', flexShrink: 0 }} />
      <Typography variant="overline" sx={{ pl: 0.6, color: '#D8B76E', fontSize: 9, lineHeight: 1.5, fontWeight: 700, letterSpacing: 1.5 }}>{roleCaption}</Typography>
    </Toolbar>
    <List component="nav" aria-label="Primary navigation" sx={{ flex: 1, minHeight: 0, overflowY: 'auto', overflowX: 'hidden', overscrollBehaviorY: 'contain', px: 1.5, pt: 1 }}>{visibleGroups.map((group) => <Box key={group.title} sx={{ mb: 1 }}>
      <ListSubheader disableSticky sx={{ bgcolor: 'transparent', color: 'rgba(255,255,255,.52)', fontSize: 9, fontWeight: 750, letterSpacing: 1.4, lineHeight: '32px', pl: 1.5 }}>{group.title}</ListSubheader>
      {group.items.map((item) => <ListItemButton key={item.to} component={NavLink} to={item.to} end={item.to === '/staff'} onClick={onClose} sx={{ minHeight: { xs: 44, md: 40 }, position: 'relative', borderRadius: 1.75, mb: 0.25, color: 'rgba(255,255,255,.78)', '&.active': { color: 'white', bgcolor: 'rgba(255,255,255,.12)', '&::before': { content: '""', position: 'absolute', left: 0, top: 8, bottom: 8, width: 3, borderRadius: 2, bgcolor: '#D8B76E' } }, '&:hover': { bgcolor: 'rgba(255,255,255,.08)' } }}>
        <ListItemIcon sx={{ minWidth: 36, color: 'inherit' }}>{item.icon}</ListItemIcon><ListItemText primary={item.label} primaryTypographyProps={{ fontSize: 12.5, fontWeight: 600 }} />
      </ListItemButton>)}
    </Box>)}</List>
  </Box>;
  return <>
    <Drawer variant="temporary" open={mobileOpen} onClose={onClose} ModalProps={{ keepMounted: true }} sx={{ display: { xs: 'block', md: 'none' }, '& .MuiDrawer-paper': { width: SIDEBAR_WIDTH, border: 0 } }}>{content}</Drawer>
    <Drawer variant="permanent" open sx={{ display: { xs: 'none', md: 'block' }, '& .MuiDrawer-paper': { width: SIDEBAR_WIDTH, border: 0 } }}>{content}</Drawer>
  </>;
}
