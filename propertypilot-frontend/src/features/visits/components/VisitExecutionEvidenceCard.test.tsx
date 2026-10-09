import { afterEach, describe, expect, it, vi } from 'vitest';
import { cleanup, fireEvent, render, screen, waitFor } from '@testing-library/react';

const mocked = vi.hoisted(() => ({
  summary: { data: null as unknown, isLoading: false, isError: false, error: null as unknown, refetch: vi.fn() },
  types: { data: [] as Array<{ code: string; name: string }>, isError: false, refetch: vi.fn() },
  gpsResult: null as unknown,
  verifyGps: vi.fn(),
}));

vi.mock('../../evidence/hooks/useEvidence', () => ({
  useVisitEvidenceSummary: () => mocked.summary,
  useEvidenceTypes: () => mocked.types,
}));
vi.mock('../hooks/useVisits', () => ({ useGpsVerification: () => ({ data: mocked.gpsResult, mutateAsync: mocked.verifyGps, isPending: false, isError: false, error: null }) }));

import VisitExecutionEvidenceCard from './VisitExecutionEvidenceCard';

afterEach(() => {
  cleanup();
  mocked.summary = { data: null, isLoading: false, isError: false, error: null, refetch: vi.fn() };
  mocked.types = { data: [], isError: false, refetch: vi.fn() };
  mocked.gpsResult = null;
  vi.clearAllMocks();
});

describe('VisitExecutionEvidenceCard', () => {
  it('distinguishes uploaded evidence awaiting review from approved evidence and missing requirements', () => {
    mocked.summary.data = {
      visitId: 'visit-1', requiredEvidenceTypes: ['BEFORE_PHOTO', 'AFTER_PHOTO', 'GPS'],
      missingEvidenceTypes: ['BEFORE_PHOTO', 'AFTER_PHOTO', 'GPS'], completionPercentage: 0,
      uploadedEvidence: [{ evidenceId: 'evidence-1', visitId: 'visit-1', serviceRequestId: 'request-1', evidenceTypeId: 'type-1', evidenceTypeCode: 'BEFORE_PHOTO', evidenceTypeName: 'Before photo', fileUrl: 'storage://photo', validationStatus: 'PENDING' }],
    };
    render(<VisitExecutionEvidenceCard visitId="visit-1" visitStatus="IN_PROGRESS" />);
    expect(screen.getByText('Uploaded · awaiting review')).toBeTruthy();
    expect(screen.getAllByText('Missing')).toHaveLength(2);
    expect(screen.getByText('GPS site verification required')).toBeTruthy();
    expect(screen.getByText(/separate from GPS evidence upload/)).toBeTruthy();
  });

  it('shows a truthful backend verification result after capturing location', async () => {
    mocked.summary.data = { visitId: 'visit-1', requiredEvidenceTypes: ['GPS'], missingEvidenceTypes: ['GPS'], completionPercentage: 0, uploadedEvidence: [] };
    mocked.verifyGps.mockResolvedValueOnce({ visitId: 'visit-1', validationStatus: 'VERIFIED', distanceFromExpectedKm: 0.042 });
    const getCurrentPosition = vi.fn((success: PositionCallback) => success({ coords: { latitude: 12.9716, longitude: 77.5946, accuracy: 10 } as GeolocationCoordinates, timestamp: Date.now() } as GeolocationPosition));
    Object.defineProperty(navigator, 'geolocation', { configurable: true, value: { getCurrentPosition } });

    const result = render(<VisitExecutionEvidenceCard visitId="visit-1" visitStatus="IN_PROGRESS" />);
    fireEvent.click(screen.getByRole('button', { name: 'Verify current location' }));
    await waitFor(() => expect(mocked.verifyGps).toHaveBeenCalledWith({ visitId: 'visit-1', latitude: 12.9716, longitude: 77.5946 }));
    mocked.gpsResult = { visitId: 'visit-1', validationStatus: 'VERIFIED', distanceFromExpectedKm: 0.042 };
    result.rerender(<VisitExecutionEvidenceCard visitId="visit-1" visitStatus="IN_PROGRESS" />);
    expect(await screen.findByText(/Location verified for this visit/)).toBeTruthy();
    expect(screen.getByText(/Distance recorded: 0.042 km/)).toBeTruthy();
  });

  it('reports denied location permission without claiming verification or calling the API', async () => {
    mocked.summary.data = { visitId: 'visit-1', requiredEvidenceTypes: ['GPS'], missingEvidenceTypes: ['GPS'], completionPercentage: 0, uploadedEvidence: [] };
    const getCurrentPosition = vi.fn((_success: PositionCallback, error: PositionErrorCallback) => error({ code: 1, message: 'denied' } as GeolocationPositionError));
    Object.defineProperty(navigator, 'geolocation', { configurable: true, value: { getCurrentPosition } });
    render(<VisitExecutionEvidenceCard visitId="visit-1" visitStatus="IN_PROGRESS" />);
    fireEvent.click(screen.getByRole('button', { name: 'Verify current location' }));
    expect(await screen.findByText(/Location permission was denied/)).toBeTruthy();
    expect(mocked.verifyGps).not.toHaveBeenCalled();
  });

  it('does not start GPS verification while a visit is not in progress', () => {
    mocked.summary.data = { visitId: 'visit-1', requiredEvidenceTypes: ['GPS'], missingEvidenceTypes: ['GPS'], completionPercentage: 0, uploadedEvidence: [] };
    render(<VisitExecutionEvidenceCard visitId="visit-1" visitStatus="SCHEDULED" />);
    expect(screen.queryByRole('button', { name: 'Verify current location' })).toBeNull();
    expect(screen.getByText('Start the visit before running this location check.')).toBeTruthy();
  });

  it('offers retry when required evidence data cannot be loaded', () => {
    mocked.summary = { data: null, isLoading: false, isError: true, error: new Error('Request failed'), refetch: vi.fn() };
    render(<VisitExecutionEvidenceCard visitId="visit-1" visitStatus="IN_PROGRESS" />);
    fireEvent.click(screen.getByRole('button', { name: 'Retry' }));
    expect(mocked.summary.refetch).toHaveBeenCalled();
  });

  it('shows loading state for service evidence requirements', () => {
    mocked.summary = { data: null, isLoading: true, isError: false, error: null, refetch: vi.fn() };
    render(<VisitExecutionEvidenceCard visitId="visit-1" visitStatus="IN_PROGRESS" />);
    expect(screen.getByRole('heading', { name: 'Evidence checklist' })).toBeTruthy();
    expect(screen.queryByText('No evidence requirements are configured for this visit.')).toBeNull();
  });
});
