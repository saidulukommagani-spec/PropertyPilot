import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { cleanup, render, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter, Route, Routes } from 'react-router-dom';

const ids = vi.hoisted(() => ({ evidenceId: '00000000-0000-4000-8000-000000000201', requestId: '00000000-0000-4000-8000-000000000202', visitId: '00000000-0000-4000-8000-000000000203' }));
const state = vi.hoisted(() => ({
  queue: { data: [] as Array<Record<string, unknown>>, isLoading: false, isError: false, error: null as Error | null, refetch: vi.fn() },
  context: { data: { serviceRequestId: ids.requestId, status: 'IN_PROGRESS' }, isLoading: false, isError: false, error: null as Error | null, refetch: vi.fn() },
  reviewError: null as Error | null,
  mutateAsync: vi.fn(),
}));

vi.mock('../hooks/useEvidence', async () => {
  const React = await vi.importActual<typeof import('react')>('react');
  return {
    useEvidenceValidationQueue: () => state.queue,
    useEvidenceRequestContext: () => state.context,
    useReviewEvidence: () => {
      const [isPending, setIsPending] = React.useState(false);
      const [error, setError] = React.useState<Error | null>(state.reviewError);
      return {
        isPending,
        error,
        reset: () => setError(null),
        mutateAsync: async (...args: Parameters<typeof state.mutateAsync>) => {
          setIsPending(true);
          setError(null);
          try { return await state.mutateAsync(...args); }
          catch (cause) {
            const failure = cause instanceof Error ? cause : new Error('Review action failed');
            setError(failure);
            throw failure;
          }
          finally { setIsPending(false); }
        },
      };
    },
  };
});
vi.mock('../components/EvidenceFilePreview', () => ({ default: () => <div>Evidence preview</div> }));

import EvidenceValidationPage from './EvidenceValidationPage';

const pendingEvidence = {
  evidenceId: ids.evidenceId,
  serviceRequestId: ids.requestId,
  visitId: ids.visitId,
  evidenceTypeName: 'After photo',
  fileName: 'kitchen-after.jpg',
  fileUrl: '/api/evidence/file',
  mediaType: 'image/jpeg',
  validationStatus: 'PENDING',
  capturedAt: '2026-10-08T10:30:00Z',
  createdAt: '2026-10-08T10:30:00Z',
  customerName: 'Mina Owner',
  propertyName: 'Willow House',
  serviceName: 'Property inspection',
  agentName: 'Ari Field',
  visitStatus: 'COMPLETED',
  visitScheduledAt: '2026-10-07T09:00:00Z',
  uploadedByName: 'Ari Field',
  latitude: null,
  longitude: null,
  validationHistory: [{ historyId: 'history-1', status: 'REUPLOAD_REQUESTED', comment: 'Please retake in better light.', userName: 'Ops Reviewer', occurredAt: '2026-10-08T09:00:00Z' }],
};

function renderPage(initialEntry = '/evidence/validation') {
  return render(<MemoryRouter initialEntries={[initialEntry]}><Routes>
    <Route path="/evidence/validation" element={<EvidenceValidationPage />} />
    <Route path="/service-requests/:id" element={<div>Service request destination</div>} />
  </Routes></MemoryRouter>);
}

afterEach(cleanup);
beforeEach(() => {
  state.queue.data = [pendingEvidence];
  state.queue.isLoading = false;
  state.queue.isError = false;
  state.queue.error = null;
  state.queue.refetch.mockReset();
  state.context.data = { serviceRequestId: ids.requestId, status: 'IN_PROGRESS' };
  state.context.isLoading = false;
  state.context.isError = false;
  state.context.error = null;
  state.context.refetch.mockReset();
  state.mutateAsync.mockReset();
  state.mutateAsync.mockResolvedValue({ ...pendingEvidence, validationStatus: 'APPROVED' });
  state.reviewError = null;
});

describe('EvidenceValidationPage', () => {
  it('shows customer, property, service, visit, request status, and history without displaying opaque IDs', async () => {
    renderPage();
    expect(await screen.findByText(/kitchen-after\.jpg/)).toBeTruthy();
    expect(screen.getAllByText('Mina Owner').length).toBeGreaterThan(0);
    expect(screen.getAllByText('Willow House').length).toBeGreaterThan(0);
    expect(screen.getAllByText('Property inspection').length).toBeGreaterThan(0);
    expect(screen.getByText('COMPLETED')).toBeTruthy();
    expect(screen.getByText('IN PROGRESS')).toBeTruthy();
    expect(screen.getAllByText('Please retake in better light.').length).toBeGreaterThan(0);
    expect(screen.queryByText(ids.evidenceId)).toBeNull();
    expect(screen.queryByText(ids.requestId)).toBeNull();
    expect(screen.queryByText(ids.visitId)).toBeNull();
    expect(screen.getByRole('link', { name: 'Open service request' }).getAttribute('href')).toBe(`/service-requests/${ids.requestId}`);
  }, 15000);

  it('confirms approval, prevents repeat submission while pending, and sends the required comment', async () => {
    const user = userEvent.setup();
    let resolveReview!: (result: typeof pendingEvidence) => void;
    state.mutateAsync.mockImplementation(() => new Promise((resolve) => { resolveReview = resolve; }));
    renderPage();

    await user.type(screen.getByRole('textbox', { name: 'Review comments' }), 'Evidence is clear and matches the visit.');
    await user.click(screen.getByRole('button', { name: 'Approve' }));
    expect(await screen.findByRole('dialog', { name: 'Approve evidence' })).toBeTruthy();
    expect(screen.getByText(/may complete the service request after validating its visits/)).toBeTruthy();

    await user.click(screen.getByRole('button', { name: 'Approve evidence' }));
    const saving = await screen.findByRole('button', { name: 'Saving…' });
    expect(saving.hasAttribute('disabled')).toBe(true);
    expect(state.mutateAsync).toHaveBeenCalledTimes(1);
    expect(state.mutateAsync).toHaveBeenCalledWith({ evidenceId: ids.evidenceId, request: { status: 'APPROVED', comment: 'Evidence is clear and matches the visit.' } });
    resolveReview({ ...pendingEvidence, validationStatus: 'APPROVED' });
    expect(await screen.findByText('Approve evidence saved.')).toBeTruthy();
  }, 15000);

  it('supports reupload and rejection decisions only with comments and confirms before posting', async () => {
    const user = userEvent.setup();
    renderPage();
    expect(screen.getByRole('button', { name: 'Reject' }).hasAttribute('disabled')).toBe(true);
    await user.type(screen.getByRole('textbox', { name: 'Review comments' }), 'Please capture this again without glare.');
    await user.click(screen.getByRole('button', { name: 'Request reupload' }));
    await user.click(within(await screen.findByRole('dialog')).getByRole('button', { name: 'Request reupload' }));
    await waitFor(() => expect(state.mutateAsync).toHaveBeenCalledWith({ evidenceId: ids.evidenceId, request: { status: 'REUPLOAD_REQUESTED', comment: 'Please capture this again without glare.' } }));
  }, 15000);

  it('keeps the comment and confirmation available after a rejected review request', async () => {
    const user = userEvent.setup();
    state.mutateAsync.mockRejectedValue(new Error('Review service unavailable'));
    renderPage();
    await user.type(screen.getByRole('textbox', { name: 'Review comments' }), 'Does not meet the evidence requirement.');
    await user.click(screen.getByRole('button', { name: 'Reject' }));
    await user.click(await screen.findByRole('button', { name: 'Reject evidence' }));
    expect(await screen.findByText('Review service unavailable')).toBeTruthy();
    expect(screen.getAllByText('Does not meet the evidence requirement.').length).toBeGreaterThan(0);
    expect(screen.getByRole('dialog', { name: 'Reject evidence' })).toBeTruthy();
  }, 15000);

  it('shows loading, empty, and retryable queue error states', async () => {
    state.queue.isLoading = true;
    const { rerender } = renderPage();
    expect(screen.getByLabelText('Loading review queue')).toBeTruthy();

    state.queue.isLoading = false;
    state.queue.data = [];
    rerender(<MemoryRouter initialEntries={['/evidence/validation']}><Routes><Route path="/evidence/validation" element={<EvidenceValidationPage />} /></Routes></MemoryRouter>);
    expect(screen.getByText('The review queue is clear.')).toBeTruthy();

    state.queue.isError = true;
    state.queue.error = new Error('Queue unavailable');
    state.queue.data = [];
    rerender(<MemoryRouter initialEntries={['/evidence/validation']}><Routes><Route path="/evidence/validation" element={<EvidenceValidationPage />} /></Routes></MemoryRouter>);
    expect(screen.getByText('Queue unavailable')).toBeTruthy();
    const user = userEvent.setup();
    await user.click(screen.getByRole('button', { name: 'Retry' }));
    expect(state.queue.refetch).toHaveBeenCalledOnce();
  });

  it('opens the evidence selected by the request-detail review link', async () => {
    renderPage(`/evidence/validation?evidenceId=${ids.evidenceId}`);
    expect(await screen.findByText(/kitchen-after\.jpg/)).toBeTruthy();
    expect(screen.getByRole('button', { name: /After photo/ }).getAttribute('aria-pressed')).toBe('true');
  });
});
