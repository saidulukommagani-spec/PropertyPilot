import { afterEach, describe, expect, it, vi } from 'vitest';
import { cleanup, fireEvent, render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import OperationsLeadQueuePage from './OperationsLeadQueuePage';
import type { LeadStatus, OperationsLeadPage, OperationsLeadSearchParams } from '../types/lead';

const lead = {
  leadId: 'internal-lead-id-1',
  source: 'Partner referral',
  propertyType: 'Townhouse',
  location: 'North District',
  status: 'QUALIFIED' as const,
  customerName: 'Mira Chen',
  customerEmail: 'mira@example.invalid',
  customerPhone: '5550101',
  assignedAgentCode: 'AGT-12',
  createdAt: '2026-10-08T09:30:00Z',
};
const page = (content = [lead], totalElements = content.length): OperationsLeadPage => ({
  content, page: 0, size: 25, totalElements, totalPages: totalElements ? Math.ceil(totalElements / 25) : 0,
  first: true, last: totalElements <= 25,
});

const state = vi.hoisted(() => ({
  calls: [] as OperationsLeadSearchParams[],
  data: undefined as OperationsLeadPage | undefined,
  isLoading: false,
  isFetching: false,
  isError: false,
  error: undefined as unknown,
  refetch: vi.fn(),
}));

vi.mock('../hooks/useLeads', () => ({
  useOperationsLeads: (params: OperationsLeadSearchParams) => {
    state.calls.push(params);
    return { data: state.data, isLoading: state.isLoading, isFetching: state.isFetching, isError: state.isError, error: state.error, refetch: state.refetch };
  },
}));

function renderPage() {
  return render(<MemoryRouter initialEntries={['/operations/leads']}><OperationsLeadQueuePage /></MemoryRouter>);
}

afterEach(() => {
  cleanup();
  state.calls = [];
  state.data = page();
  state.isLoading = false;
  state.isFetching = false;
  state.isError = false;
  state.error = undefined;
  state.refetch.mockReset();
});

describe('OperationsLeadQueuePage', () => {
  it('renders API business context and never promotes the internal Lead ID or private notes', () => {
    state.data = page();
    renderPage();

    expect(screen.getAllByText('Mira Chen').length).toBeGreaterThan(0);
    expect(screen.getAllByText('mira@example.invalid').length).toBeGreaterThan(0);
    expect(screen.getAllByText('Townhouse').length).toBeGreaterThan(0);
    expect(screen.getAllByText('North District').length).toBeGreaterThan(0);
    expect(screen.getAllByText('Partner referral').length).toBeGreaterThan(0);
    expect(screen.getAllByText('Agent AGT-12').length).toBeGreaterThan(0);
    expect(screen.queryByText('internal-lead-id-1')).toBeNull();
    expect(screen.getAllByRole('link', { name: /Open lead/ })[0].getAttribute('href')).toBe('/operations/leads/internal-lead-id-1');
  });

  it('debounces normalized search and sends it to the server', async () => {
    state.data = page();
    renderPage();
    fireEvent.change(screen.getByRole('textbox', { name: 'Search leads' }), { target: { value: '  Mira  ' } });

    expect(screen.getByText('Applying search…')).toBeTruthy();
    await waitFor(() => expect(state.calls.at(-1)?.search).toBe('Mira'));
    expect(state.calls.at(-1)?.page).toBe(0);
  });

  it('uses the persisted status filter and server-supported sort fields', async () => {
    state.data = page();
    renderPage();
    await selectStatus('QUALIFIED');
    await waitFor(() => expect(state.calls.at(-1)?.status).toBe('QUALIFIED'));

    fireEvent.click(screen.getByRole('button', { name: 'Created date' }));
    await waitFor(() => expect(state.calls.at(-1)).toMatchObject({ status: 'QUALIFIED', sort: 'createdAt', direction: 'ASC', page: 0 }));
    fireEvent.click(screen.getByRole('button', { name: 'Location' }));
    await waitFor(() => expect(state.calls.at(-1)).toMatchObject({ status: 'QUALIFIED', sort: 'location', direction: 'ASC', page: 0 }));
  });

  it('moves between server pages and preserves the current status filter', async () => {
    state.data = { ...page(), totalElements: 51, totalPages: 3, last: false };
    renderPage();
    await selectStatus('QUALIFIED');
    await waitFor(() => expect(state.calls.at(-1)?.status).toBe('QUALIFIED'));
    fireEvent.click(screen.getByRole('button', { name: 'Go to next page' }));
    await waitFor(() => expect(state.calls.at(-1)).toMatchObject({ status: 'QUALIFIED', page: 1, size: 25 }));
  });

  it('shows distinct no-record and no-match states', async () => {
    state.data = page([], 0);
    const { rerender } = renderPage();
    expect(screen.getByText('No leads have been added yet.')).toBeTruthy();

    rerender(<MemoryRouter initialEntries={['/operations/leads']}><OperationsLeadQueuePage /></MemoryRouter>);
    await selectStatus('LOST');
    state.data = page([], 0);
    expect(screen.getByText('No leads match the current search or status filter.')).toBeTruthy();
  });

  it('keeps loading and retryable error states separate from empty results', () => {
    state.data = undefined;
    state.isLoading = true;
    const { rerender } = renderPage();
    expect(screen.getByRole('status', { name: 'Loading lead queue' })).toBeTruthy();

    state.isLoading = false;
    state.isError = true;
    state.error = new Error('Do not show server internals');
    rerender(<MemoryRouter initialEntries={['/operations/leads']}><OperationsLeadQueuePage /></MemoryRouter>);
    expect(screen.getByText('The lead queue could not be loaded. Retry to try again.')).toBeTruthy();
    expect(screen.queryByText('Do not show server internals')).toBeNull();
    fireEvent.click(screen.getByRole('button', { name: 'Retry' }));
    expect(state.refetch).toHaveBeenCalledOnce();
  });
});

async function selectStatus(status: LeadStatus) {
  fireEvent.mouseDown(screen.getByRole('combobox', { name: 'Lead status filter' }));
  fireEvent.click(await screen.findByRole('option', { name: status.replaceAll('_', ' ') }));
}
