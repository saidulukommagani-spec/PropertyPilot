import { lazy, Suspense } from 'react';
import { Box, CircularProgress } from '@mui/material';
import { Navigate, Route, Routes, useLocation } from 'react-router-dom';
import DashboardLayout from '../../components/layouts/DashboardLayout';
import RouteErrorBoundary from '../../components/layouts/RouteErrorBoundary';
import ProtectedRoute from './ProtectedRoute';
import RoleGuard from './RoleGuard';
import ServiceRequestWorkspaceErrorBoundary from '../../features/service-requests/components/ServiceRequestWorkspaceErrorBoundary';
import { useAuth } from '../../features/auth/AuthContext';
import { getUserRoles } from '../../api/auth';

const AdminDashboardPage = lazy(() => import('../../features/admin/pages/AdminDashboardPage'));
const LoginPage = lazy(() => import('../../features/auth/pages/LoginPage'));
const CustomerRegistrationPage = lazy(() => import('../../features/auth/pages/CustomerRegistrationPage'));
const ServiceRequestListPage = lazy(() => import('../../features/service-requests/pages/ServiceRequestListPage'));
const ServiceRequestDetailPage = lazy(() => import('../../features/service-requests/pages/ServiceRequestDetailPage'));
const VisitDetailsPage = lazy(() => import('../../features/visits/pages/VisitDetailsPage'));
const AdminDirectoryPage = lazy(() => import('../../features/admin-portal/pages/AdminDirectoryPage'));
const AdminRecordDetailPage = lazy(() => import('../../features/admin-portal/pages/AdminRecordDetailPage'));
const ServiceRequestFormPage = lazy(() => import('../../features/service-requests/pages/ServiceRequestFormPage'));
const ServiceRequestDashboardPage = lazy(() => import('../../features/service-requests/pages/ServiceRequestDashboardPage'));
const VisitListPage = lazy(() => import('../../features/visits/pages/VisitListPage'));
const VisitDashboardPage = lazy(() => import('../../features/visits/pages/VisitDashboardPage'));
const VisitManagementFormPage = lazy(() => import('../../features/visits/pages/VisitManagementFormPage'));
const EvidenceDashboardPage = lazy(() => import('../../features/evidence/pages/EvidenceDashboardPage'));
const EvidenceListPage = lazy(() => import('../../features/evidence/pages/EvidenceListPage'));
const EvidenceDetailsPage = lazy(() => import('../../features/evidence/pages/EvidenceDetailsPage'));
const EvidenceUploadPage = lazy(() => import('../../features/evidence/pages/EvidenceUploadPage'));
const EvidenceValidationPage = lazy(() => import('../../features/evidence/pages/EvidenceValidationPage'));
const VisitEvidenceSummaryPage = lazy(() => import('../../features/evidence/pages/VisitEvidenceSummaryPage'));
const AgentDashboardPage = lazy(() => import('../../features/agent-operations/pages/AgentDashboardPage'));
const AgentWorkQueuePage = lazy(() => import('../../features/agent-operations/pages/AgentWorkQueuePage'));
const AgentProfilePage = lazy(() => import('../../features/agent-operations/pages/AgentProfilePage'));
const OperationsAgentsPage = lazy(() => import('../../features/agent-operations/pages/OperationsAgentsPage'));
const OperationsAssignmentsPage = lazy(() => import('../../features/agent-operations/pages/OperationsAssignmentsPage'));
const CustomerDashboardPage = lazy(() => import('../../features/customer-portal/pages/CustomerDashboardPage'));
const CustomerPropertiesPage = lazy(() => import('../../features/customer-portal/pages/CustomerPropertiesPage'));
const CustomerPropertyDetailsPage = lazy(() => import('../../features/customer-portal/pages/CustomerPropertyDetailsPage'));
const CustomerRequestListPage = lazy(() => import('../../features/customer-portal/pages/CustomerRequestListPage'));
const CustomerRequestCreatePage = lazy(() => import('../../features/customer-portal/pages/CustomerRequestCreatePage'));
const CustomerRequestDetailsPage = lazy(() => import('../../features/customer-portal/pages/CustomerRequestDetailsPage'));
const CustomerVisitsPage = lazy(() => import('../../features/customer-portal/pages/CustomerVisitsPage'));
const CustomerVisitDetailsPage = lazy(() => import('../../features/customer-portal/pages/CustomerVisitDetailsPage'));
const CustomerEvidencePage = lazy(() => import('../../features/customer-portal/pages/CustomerEvidencePage'));
const CustomerSubscriptionsPage = lazy(() => import('../../features/customer-portal/pages/CustomerSubscriptionsPage'));
const CustomerInvoicesPage = lazy(() => import('../../features/customer-portal/pages/CustomerInvoicesPage'));
const PropertyFormPage = lazy(() => import('../../features/properties/pages/PropertyFormPage'));
const AdminPropertyEditPage = lazy(() => import('../../features/admin-portal/pages/AdminPropertyEditPage'));
const AdminCustomerEditPage = lazy(() => import('../../features/admin-portal/pages/AdminCustomerEditPage'));
const SubscriptionDashboardPage = lazy(() => import('../../features/subscriptions/pages/SubscriptionDashboardPage'));
const SubscriptionPlansPage = lazy(() => import('../../features/subscriptions/pages/SubscriptionPlansPage'));
const SubscriptionPlanDetailsPage = lazy(() => import('../../features/subscriptions/pages/SubscriptionPlanDetailsPage'));
const SubscriptionListPage = lazy(() => import('../../features/subscriptions/pages/SubscriptionListPage'));
const SubscriptionDetailsPage = lazy(() => import('../../features/subscriptions/pages/SubscriptionDetailsPage'));
const EntitlementUsagePage = lazy(() => import('../../features/subscriptions/pages/EntitlementUsagePage'));
const RenewalCenterPage = lazy(() => import('../../features/subscriptions/pages/RenewalCenterPage'));
const SubscriptionAddOnsPage = lazy(() => import('../../features/subscriptions/pages/SubscriptionAddOnsPage'));
const QuoteCreatePage = lazy(() => import('../../features/quotes/pages/QuotePages').then((module) => ({ default: module.QuoteCreatePage })));
const QuoteDashboardPage = lazy(() => import('../../features/quotes/pages/QuotePages').then((module) => ({ default: module.QuoteDashboardPage })));
const QuoteDetailPage = lazy(() => import('../../features/quotes/pages/QuotePages').then((module) => ({ default: module.QuoteDetailPage })));
const QuoteListPage = lazy(() => import('../../features/quotes/pages/QuotePages').then((module) => ({ default: module.QuoteListPage })));
const DealCreatePage = lazy(() => import('../../features/deals/pages/DealPages').then((module) => ({ default: module.DealCreatePage })));
const DealDashboardPage = lazy(() => import('../../features/deals/pages/DealPages').then((module) => ({ default: module.DealDashboardPage })));
const DealDetailPage = lazy(() => import('../../features/deals/pages/DealPages').then((module) => ({ default: module.DealDetailPage })));
const DealEditPage = lazy(() => import('../../features/deals/pages/DealPages').then((module) => ({ default: module.DealEditPage })));
const DealListPage = lazy(() => import('../../features/deals/pages/DealPages').then((module) => ({ default: module.DealListPage })));
const InvoiceDashboardPage = lazy(() => import('../../features/invoices/pages/InvoicePages').then((module) => ({ default: module.InvoiceDashboardPage })));
const InvoiceDetailPage = lazy(() => import('../../features/invoices/pages/InvoicePages').then((module) => ({ default: module.InvoiceDetailPage })));
const InvoiceListPage = lazy(() => import('../../features/invoices/pages/InvoicePages').then((module) => ({ default: module.InvoiceListPage })));
const PaymentListPage = lazy(() => import('../../features/payments/pages/PaymentListPage'));
const PaymentDetailPage = lazy(() => import('../../features/payments/pages/PaymentDetailPage'));
const LeadDashboardPage = lazy(() => import('../../features/leads/pages/LeadDashboardPage'));
const LeadListPage = lazy(() => import('../../features/leads/pages/LeadListPage'));
const LeadCreatePage = lazy(() => import('../../features/leads/pages/LeadCreatePage'));
const LeadDetailPage = lazy(() => import('../../features/leads/pages/LeadDetailPage'));
const OperationsLeadQueuePage = lazy(() => import('../../features/leads/pages/OperationsLeadQueuePage'));

function HomeRoute() {
  const { user } = useAuth();
  const roles = getUserRoles(user);
  const destination = roles.includes('CUSTOMER') && !roles.includes('ADMIN') && !roles.includes('OPERATIONS') && !roles.includes('AGENT')
    ? '/customer/dashboard'
    : roles.includes('AGENT') && !roles.includes('ADMIN') && !roles.includes('OPERATIONS') ? '/agent/dashboard' : '/staff';
  return <Navigate to={destination} replace />;
}

export default function AppRoutes() {
  const location = useLocation();
  return <RouteErrorBoundary key={location.pathname}><Suspense fallback={<Box role="status" aria-label="Loading page" sx={{ minHeight: '50vh', display: 'grid', placeItems: 'center' }}><CircularProgress /></Box>}><Routes>
    <Route path="/login" element={<LoginPage />} />
    <Route path="/register" element={<CustomerRegistrationPage />} />
    <Route element={<ProtectedRoute />}>
      <Route element={<RoleGuard allowedRoles={['ADMIN', 'OPERATIONS']} />}>
        <Route path="/operations/leads" element={<DashboardLayout><OperationsLeadQueuePage /></DashboardLayout>} />
        <Route path="/operations/leads/:leadId" element={<DashboardLayout><LeadDetailPage /></DashboardLayout>} />
        <Route path="/operations/agents" element={<DashboardLayout><OperationsAgentsPage /></DashboardLayout>} />
        <Route path="/operations/assignments" element={<DashboardLayout><OperationsAssignmentsPage /></DashboardLayout>} />
        <Route path="/staff" element={<DashboardLayout><AdminDashboardPage /></DashboardLayout>} />
        <Route path="/staff/service-requests" element={<DashboardLayout><ServiceRequestListPage /></DashboardLayout>} />
        <Route path="/staff/service-requests/dashboard" element={<DashboardLayout><ServiceRequestDashboardPage /></DashboardLayout>} />
        <Route path="/staff/service-requests/new" element={<DashboardLayout><ServiceRequestFormPage /></DashboardLayout>} />
        <Route path="/staff/service-requests/:id/edit" element={<DashboardLayout><ServiceRequestFormPage /></DashboardLayout>} />
        <Route path="/staff/service-requests/:id" element={<DashboardLayout><ServiceRequestWorkspaceErrorBoundary><ServiceRequestDetailPage /></ServiceRequestWorkspaceErrorBoundary></DashboardLayout>} />
      </Route>
      <Route element={<RoleGuard allowedRoles={['ADMIN']} />}>
        <Route path="/staff/customers/:id/edit" element={<DashboardLayout><AdminCustomerEditPage /></DashboardLayout>} />
        <Route path="/staff/properties/:id/edit" element={<DashboardLayout><AdminPropertyEditPage /></DashboardLayout>} />
        <Route path="/invoices/dashboard" element={<DashboardLayout><InvoiceDashboardPage /></DashboardLayout>} />
        <Route path="/invoices" element={<DashboardLayout><InvoiceListPage /></DashboardLayout>} />
        <Route path="/invoices/new" element={<Navigate to="/invoices" replace />} />
        <Route path="/invoices/:id/pdf" element={<DashboardLayout><InvoiceDetailPage printView /></DashboardLayout>} />
        <Route path="/invoices/:id" element={<DashboardLayout><InvoiceDetailPage /></DashboardLayout>} />
        <Route path="/deals/dashboard" element={<DashboardLayout><DealDashboardPage /></DashboardLayout>} />
        <Route path="/deals" element={<DashboardLayout><DealListPage /></DashboardLayout>} />
        <Route path="/deals/new" element={<DashboardLayout><DealCreatePage /></DashboardLayout>} />
        <Route path="/deals/:id/edit" element={<DashboardLayout><DealEditPage /></DashboardLayout>} />
        <Route path="/deals/:id" element={<DashboardLayout><DealDetailPage /></DashboardLayout>} />
        <Route path="/staff/visits/:visitId" element={<DashboardLayout><ServiceRequestWorkspaceErrorBoundary><VisitDetailsPage /></ServiceRequestWorkspaceErrorBoundary></DashboardLayout>} />
        <Route path="/staff/payments" element={<DashboardLayout><PaymentListPage /></DashboardLayout>} />
        <Route path="/staff/payments/:paymentId" element={<DashboardLayout><PaymentDetailPage /></DashboardLayout>} />
        <Route path="/staff/leads/:leadId" element={<DashboardLayout><LeadDetailPage /></DashboardLayout>} />
        <Route path="/staff/:resource/:id" element={<DashboardLayout><AdminRecordDetailPage /></DashboardLayout>} />
        <Route path="/staff/:resource" element={<DashboardLayout><AdminDirectoryPage /></DashboardLayout>} />
      </Route>
      <Route element={<RoleGuard allowedRoles={['ADMIN', 'OPERATIONS']} />}>
        <Route path="/visits" element={<DashboardLayout><VisitListPage /></DashboardLayout>} />
        <Route path="/visits/dashboard" element={<DashboardLayout><VisitDashboardPage /></DashboardLayout>} />
        <Route path="/visits/new" element={<DashboardLayout><VisitManagementFormPage /></DashboardLayout>} />
        <Route path="/visits/:visitId/edit" element={<DashboardLayout><VisitManagementFormPage /></DashboardLayout>} />
        <Route path="/visits/:visitId" element={<DashboardLayout><ServiceRequestWorkspaceErrorBoundary><VisitDetailsPage /></ServiceRequestWorkspaceErrorBoundary></DashboardLayout>} />
        <Route path="/visits/:visitId/evidence" element={<DashboardLayout><VisitEvidenceSummaryPage /></DashboardLayout>} />
        <Route path="/evidence" element={<DashboardLayout><EvidenceListPage /></DashboardLayout>} />
        <Route path="/evidence/dashboard" element={<DashboardLayout><EvidenceDashboardPage /></DashboardLayout>} />
        <Route path="/evidence/validation" element={<DashboardLayout><EvidenceValidationPage /></DashboardLayout>} />
        <Route path="/service-requests" element={<DashboardLayout><ServiceRequestListPage /></DashboardLayout>} />
        <Route path="/service-requests/dashboard" element={<DashboardLayout><ServiceRequestDashboardPage /></DashboardLayout>} />
        <Route path="/service-requests/new" element={<DashboardLayout><ServiceRequestFormPage /></DashboardLayout>} />
        <Route path="/service-requests/:id/edit" element={<DashboardLayout><ServiceRequestFormPage /></DashboardLayout>} />
        <Route path="/service-requests/:id" element={<DashboardLayout><ServiceRequestWorkspaceErrorBoundary><ServiceRequestDetailPage /></ServiceRequestWorkspaceErrorBoundary></DashboardLayout>} />
      </Route>
      <Route element={<RoleGuard allowedRoles={['AGENT']} />}>
        <Route path="/agent/dashboard" element={<DashboardLayout><AgentDashboardPage /></DashboardLayout>} />
        <Route path="/agent/work-queue" element={<DashboardLayout><AgentWorkQueuePage /></DashboardLayout>} />
        <Route path="/agent/leads" element={<DashboardLayout><LeadListPage /></DashboardLayout>} />
        <Route path="/agent/leads/:leadId" element={<DashboardLayout><LeadDetailPage /></DashboardLayout>} />
        <Route path="/agent/service-requests/:id" element={<DashboardLayout><ServiceRequestWorkspaceErrorBoundary><ServiceRequestDetailPage /></ServiceRequestWorkspaceErrorBoundary></DashboardLayout>} />
        <Route path="/agent/profile" element={<DashboardLayout><AgentProfilePage /></DashboardLayout>} />
        <Route path="/agent/visits" element={<DashboardLayout><VisitListPage mine /></DashboardLayout>} />
        <Route path="/agent/visit/:visitId" element={<DashboardLayout><ServiceRequestWorkspaceErrorBoundary><VisitDetailsPage agentMode /></ServiceRequestWorkspaceErrorBoundary></DashboardLayout>} />
        <Route path="/agent/visits/:visitId" element={<DashboardLayout><ServiceRequestWorkspaceErrorBoundary><VisitDetailsPage agentMode /></ServiceRequestWorkspaceErrorBoundary></DashboardLayout>} />
        <Route path="/agent/visits/:visitId/evidence" element={<DashboardLayout><VisitEvidenceSummaryPage agentMode /></DashboardLayout>} />
      </Route>
      <Route element={<RoleGuard allowedRoles={['CUSTOMER']} />}>
        <Route path="/customer/leads/dashboard" element={<DashboardLayout><LeadDashboardPage /></DashboardLayout>} />
        <Route path="/customer/leads" element={<DashboardLayout><LeadListPage /></DashboardLayout>} />
        <Route path="/customer/leads/new" element={<DashboardLayout><LeadCreatePage /></DashboardLayout>} />
        <Route path="/customer/leads/:leadId" element={<DashboardLayout><LeadDetailPage /></DashboardLayout>} />
        <Route path="/customer/dashboard" element={<DashboardLayout><CustomerDashboardPage /></DashboardLayout>} />
        <Route path="/customer/properties" element={<DashboardLayout><CustomerPropertiesPage /></DashboardLayout>} />
        <Route path="/customer/properties/new" element={<DashboardLayout><PropertyFormPage /></DashboardLayout>} />
        <Route path="/customer/properties/:id/edit" element={<DashboardLayout><PropertyFormPage /></DashboardLayout>} />
        <Route path="/customer/properties/:id" element={<DashboardLayout><CustomerPropertyDetailsPage /></DashboardLayout>} />
        <Route path="/customer/service-requests" element={<DashboardLayout><CustomerRequestListPage /></DashboardLayout>} />
        <Route path="/customer/service-requests/new" element={<DashboardLayout><CustomerRequestCreatePage /></DashboardLayout>} />
        <Route path="/customer/service-requests/:id" element={<DashboardLayout><CustomerRequestDetailsPage /></DashboardLayout>} />
        <Route path="/customer/visits" element={<DashboardLayout><CustomerVisitsPage /></DashboardLayout>} />
        <Route path="/customer/visits/:id" element={<DashboardLayout><CustomerVisitDetailsPage /></DashboardLayout>} />
        <Route path="/customer/evidence" element={<DashboardLayout><CustomerEvidencePage /></DashboardLayout>} />
        <Route path="/customer/documents" element={<Navigate to="/customer/properties" replace />} />
        <Route path="/customer/subscriptions" element={<DashboardLayout><CustomerSubscriptionsPage /></DashboardLayout>} />
        <Route path="/customer/invoices" element={<DashboardLayout><CustomerInvoicesPage /></DashboardLayout>} />
      </Route>
      <Route element={<RoleGuard allowedRoles={['CUSTOMER','ADMIN','OPERATIONS']} />}>
        <Route path="/quotes/dashboard" element={<DashboardLayout><QuoteDashboardPage /></DashboardLayout>} />
        <Route path="/quotes" element={<DashboardLayout><QuoteListPage /></DashboardLayout>} />
        <Route path="/quotes/:id" element={<DashboardLayout><QuoteDetailPage /></DashboardLayout>} />
        <Route path="/subscriptions/dashboard" element={<DashboardLayout><SubscriptionDashboardPage /></DashboardLayout>} />
        <Route path="/subscriptions/plans" element={<DashboardLayout><SubscriptionPlansPage /></DashboardLayout>} />
        <Route path="/subscriptions/plans/:id" element={<DashboardLayout><SubscriptionPlanDetailsPage /></DashboardLayout>} />
        <Route path="/subscriptions" element={<DashboardLayout><SubscriptionListPage /></DashboardLayout>} />
        <Route path="/subscriptions/:id/usage" element={<DashboardLayout><EntitlementUsagePage /></DashboardLayout>} />
        <Route path="/subscriptions/:id" element={<DashboardLayout><SubscriptionDetailsPage /></DashboardLayout>} />
        <Route path="/subscriptions/renewals" element={<DashboardLayout><RenewalCenterPage /></DashboardLayout>} />
        <Route path="/subscriptions/addons" element={<DashboardLayout><SubscriptionAddOnsPage /></DashboardLayout>} />
      </Route>
      <Route element={<RoleGuard allowedRoles={['ADMIN']} />}>
        <Route path="/quotes/new" element={<DashboardLayout><QuoteCreatePage /></DashboardLayout>} />
      </Route>
      <Route element={<RoleGuard allowedRoles={['ADMIN','OPERATIONS','AGENT']} />}>
        <Route path="/evidence/upload" element={<DashboardLayout><EvidenceUploadPage /></DashboardLayout>} />
        <Route path="/evidence/:evidenceId" element={<DashboardLayout><EvidenceDetailsPage /></DashboardLayout>} />
      </Route>
    </Route>
    <Route path="/" element={<HomeRoute />} />
    <Route path="*" element={<HomeRoute />} />
  </Routes></Suspense></RouteErrorBoundary>;
}
