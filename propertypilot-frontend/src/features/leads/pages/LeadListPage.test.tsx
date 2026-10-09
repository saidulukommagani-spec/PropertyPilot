import { afterEach, describe, expect, it, vi } from 'vitest';
import { cleanup, fireEvent, render, screen } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { MemoryRouter } from 'react-router-dom';
import LeadListPage from './LeadListPage';

const state = vi.hoisted(() => ({ data: [] as Array<{ leadId: string; source: string; propertyType: string | null; location: string | null; status: 'NEW' | 'CONTACTED' | 'QUALIFIED' | 'CONVERTED' | 'LOST' | 'CLOSED'; budgetMin: number | null; budgetMax: number | null; notes: string | null; contactedAt: string | null; convertedAt: string | null; assignedAgentId: string | null; assignedAgentCode: string | null }>, loading: false }));
vi.mock('../hooks/useLeads', () => ({ useMyLeads: () => ({ data: state.data, isLoading: state.loading, isError: false, refetch: vi.fn() }) }));
const lead = (leadId: string, source: string) => ({ leadId, source, propertyType: 'Apartment', location: 'Pune', status: 'NEW' as const, budgetMin: null, budgetMax: null, notes: null, contactedAt: null, convertedAt: null, assignedAgentId: null, assignedAgentCode: null });
function renderPage(path = '/customer/leads') { const client = new QueryClient(); return render(<QueryClientProvider client={client}><MemoryRouter initialEntries={[path]}><LeadListPage /></MemoryRouter></QueryClientProvider>); }
afterEach(() => { cleanup(); state.data = []; state.loading = false; });

describe('LeadListPage', () => {
  it('renders leads and searches the returned data', () => {
    state.data = [lead('lead-100', 'Referral'), lead('lead-200', 'Portal')];
    renderPage();
    expect(screen.getByText('Lead from Referral')).toBeTruthy();
    expect(screen.getByText('Lead from Portal')).toBeTruthy();
    fireEvent.change(screen.getByLabelText('Search leads'), { target: { value: 'Portal' } });
    expect(screen.queryByText('Lead from Referral')).toBeNull();
    expect(screen.getByText('Lead from Portal')).toBeTruthy();
  });

  it('does not use the internal lead identifier as customer-facing search content', () => {
    state.data = [lead('lead-200', 'Portal')];
    renderPage();
    fireEvent.change(screen.getByLabelText('Search leads'), { target: { value: 'lead-200' } });
    expect(screen.getByText('No leads match these filters.')).toBeTruthy();
  });

  it('shows an empty state when the API returns no leads', () => {
    renderPage();
    expect(screen.getByText('You have not created any leads yet.')).toBeTruthy();
  });

  it('uses the assigned-lead experience for agents and links within the agent portal', () => {
    state.data = [lead('lead-300', 'Operations referral')];
    renderPage('/agent/leads');
    expect(screen.getByText('Assigned leads')).toBeTruthy();
    expect(screen.queryByRole('link', { name: 'Create lead' })).toBeNull();
    expect(screen.getByRole('link', { name: /Lead from Operations referral/ }).getAttribute('href')).toBe('/agent/leads/lead-300');
  });

  it('uses agent-specific copy when the assigned-lead queue is empty', () => {
    renderPage('/agent/leads');
    expect(screen.getByText('No leads are assigned to you.')).toBeTruthy();
  });
});
