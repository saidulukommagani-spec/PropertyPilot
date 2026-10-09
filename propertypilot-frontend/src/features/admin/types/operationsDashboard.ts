export interface OperationsDashboardResponse {
  totalRequests: number;
  unassignedRequests: number;
  activeRequests: number;
  completedRequests: number;
  failedRequests: number;
  overdueInvoices: number;
  failedPayments: number;
}
