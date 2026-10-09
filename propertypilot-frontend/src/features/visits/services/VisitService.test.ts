import { describe, expect, it } from 'vitest';
import { VisitService } from './VisitService';

describe('VisitService.eligibleRequests', () => {
  it('only offers accepted or in-progress requests for non-remote services with visit workflow enabled', () => {
    const requests = [
      { serviceRequestId: 'onsite-request', serviceId: 'onsite', propertyId: 'property-1', propertyTitle: 'Lakeview', customerId: 'customer-1', customerName: 'Asha', requestType: 'Inspection', status: 'ACCEPTED', priority: 'HIGH', requestedAt: '2026-10-01T00:00:00Z' },
      { serviceRequestId: 'no-visit-request', serviceId: 'no-visit', propertyId: 'property-2', propertyTitle: 'Garden Court', customerId: 'customer-2', customerName: 'Dev', requestType: 'Legal document', status: 'IN_PROGRESS', priority: 'NORMAL', requestedAt: '2026-10-01T00:00:00Z' },
      { serviceRequestId: 'remote-request', serviceId: 'remote', propertyId: 'property-3', propertyTitle: 'Hill House', customerId: 'customer-3', customerName: 'Mina', requestType: 'Remote legal help', status: 'ACCEPTED', priority: 'NORMAL', requestedAt: '2026-10-01T00:00:00Z' },
      { serviceRequestId: 'mandatory-visit-request', serviceId: 'mandatory-visit', propertyId: 'property-5', propertyTitle: 'Cedar House', customerId: 'customer-5', customerName: 'Ira', requestType: 'Inspection', status: 'ACCEPTED', priority: 'NORMAL', requestedAt: '2026-10-01T00:00:00Z' },
      { serviceRequestId: 'new-request', serviceId: 'onsite', propertyId: 'property-4', propertyTitle: 'River Place', customerId: 'customer-4', customerName: 'Sam', requestType: 'Inspection', status: 'NEW', priority: 'NORMAL', requestedAt: '2026-10-01T00:00:00Z' },
    ];
    const services = [
      { serviceId: 'onsite', serviceName: 'Property inspection', requiresVisit: true, visitMandatory: false, remote: false },
      { serviceId: 'no-visit', serviceName: 'Document service', requiresVisit: false, visitMandatory: false, remote: false },
      { serviceId: 'remote', serviceName: 'Remote legal help', requiresVisit: false, visitMandatory: false, remote: true },
      { serviceId: 'mandatory-visit', serviceName: 'Mandatory inspection', requiresVisit: false, visitMandatory: true, remote: false },
    ];

    expect(VisitService.eligibleRequests(requests, services).map((request) => request.serviceRequestId)).toEqual(['onsite-request', 'mandatory-visit-request']);
  });

  it('numbers an agent visit only among visits for its own service request', () => {
    const visit = { visitId: 'second-inspection', serviceRequestId: 'inspection-request', status: 'SCHEDULED', scheduledAt: '2026-10-10T10:00:00Z' };
    const agentVisits = [
      { visitId: 'unrelated-earlier', serviceRequestId: 'legal-request', status: 'SCHEDULED', scheduledAt: '2026-09-01T10:00:00Z' },
      { visitId: 'first-inspection', serviceRequestId: 'inspection-request', status: 'COMPLETED', scheduledAt: '2026-10-01T10:00:00Z' },
      visit,
    ];

    expect(VisitService.visitNumber(visit, agentVisits)).toBe(2);
  });
});
