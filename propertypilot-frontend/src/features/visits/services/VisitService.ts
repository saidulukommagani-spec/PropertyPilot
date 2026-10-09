import type { VisitSummary } from '../types/visit';
import type { VisitServiceOption } from '../types/visit';
import type { ServiceRequestSummaryResponse } from '../../service-requests/types/serviceRequest';

export const VisitService = {
  newestFirst(visits: VisitSummary[]): VisitSummary[] {
    return [...visits].sort((left, right) => right.scheduledAt.localeCompare(left.scheduledAt));
  },
  visitNumber(visit: VisitSummary, allVisits: VisitSummary[]): number {
    const requestVisits = allVisits.filter((candidate) => candidate.serviceRequestId === visit.serviceRequestId);
    if (!requestVisits.some((candidate) => candidate.visitId === visit.visitId)) requestVisits.push(visit);
    const oldestFirst = requestVisits.sort((left, right) => left.scheduledAt.localeCompare(right.scheduledAt));
    return oldestFirst.findIndex((candidate) => candidate.visitId === visit.visitId) + 1;
  },
  eligibleRequests(requests: ServiceRequestSummaryResponse[], services: VisitServiceOption[]): ServiceRequestSummaryResponse[] {
    const visitServiceIds = new Set(services.filter((service) => (service.requiresVisit === true || service.visitMandatory === true) && service.remote !== true).map((service) => service.serviceId));
    return requests.filter((request) =>
      ['ACCEPTED', 'IN_PROGRESS'].includes(request.status) && request.serviceId !== undefined && visitServiceIds.has(request.serviceId),
    );
  },
};
