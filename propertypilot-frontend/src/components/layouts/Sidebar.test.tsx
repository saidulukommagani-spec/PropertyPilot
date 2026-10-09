import { afterEach, describe, expect, it, vi } from 'vitest';
import { cleanup, render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';

const auth = vi.hoisted(() => ({ roles: ['OPERATIONS'] as string[] }));

vi.mock('../../features/auth/AuthContext', () => ({
  useAuth: () => ({ user: { roles: auth.roles } }),
}));

import Sidebar from './Sidebar';

afterEach(() => {
  cleanup();
  auth.roles = ['OPERATIONS'];
});

describe('Sidebar role navigation', () => {
  it('links operations staff to their authorized overview and operational screens', () => {
    render(<MemoryRouter><Sidebar mobileOpen={false} onClose={() => undefined} /></MemoryRouter>);

    expect(screen.getAllByRole('link', { name: 'Overview' })[0].getAttribute('href')).toBe('/staff');
    expect(screen.getAllByRole('link', { name: 'Service requests' })[0].getAttribute('href')).toBe('/service-requests');
    expect(screen.getAllByRole('link', { name: 'Agent assignments' })[0].getAttribute('href')).toBe('/operations/assignments');
    expect(screen.getByRole('link', { name: 'Lead queue' }).getAttribute('href')).toBe('/operations/leads');
    expect(screen.getAllByRole('img', { name: 'PropertyPilot' })[0].getAttribute('src')).toBe('/brand/propertypilot-mark-light.svg');
  });

  it('links field agents to their assigned lead queue', () => {
    auth.roles = ['AGENT'];
    render(<MemoryRouter><Sidebar mobileOpen={false} onClose={() => undefined} /></MemoryRouter>);

    expect(screen.getByRole('link', { name: 'Assigned leads' }).getAttribute('href')).toBe('/agent/leads');
    expect(screen.queryByRole('link', { name: 'Lead queue' })).toBeNull();
  });

  it('shows the Admin lead queue entry point', () => {
    auth.roles = ['ADMIN'];
    render(<MemoryRouter><Sidebar mobileOpen={false} onClose={() => undefined} /></MemoryRouter>);
    expect(screen.getByRole('link', { name: 'Lead queue' }).getAttribute('href')).toBe('/operations/leads');
  });
});
