import { afterEach, describe, expect, it, vi } from 'vitest';
import { cleanup, render, screen } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';

const auth = vi.hoisted(() => ({ roles: [] as string[] }));

vi.mock('../../features/auth/AuthContext', () => ({
  useAuth: () => ({ user: { roles: auth.roles } }),
}));

import RoleGuard from './RoleGuard';

afterEach(() => {
  cleanup();
  auth.roles = [];
});

function renderProtectedPage(allowedRoles = ['ADMIN', 'OPERATIONS']) {
  return render(
    <MemoryRouter initialEntries={['/protected']}>
      <Routes>
        <Route element={<RoleGuard allowedRoles={allowedRoles} />}>
          <Route path="/protected" element={<h1>Authorized workspace</h1>} />
        </Route>
      </Routes>
    </MemoryRouter>,
  );
}

describe('RoleGuard', () => {
  it('normalizes backend ROLE_ADMIN authorities and grants authorized routes', () => {
    auth.roles = ['ROLE_ADMIN'];
    renderProtectedPage();

    expect(screen.getByRole('heading', { name: 'Authorized workspace' })).toBeTruthy();
  });

  it('denies roles outside the route policy and explains missing roles', () => {
    auth.roles = ['ROLE_CUSTOMER'];
    renderProtectedPage();

    expect(screen.getByRole('alert').textContent).toContain('does not have an authorized role');
    expect(screen.queryByRole('heading', { name: 'Authorized workspace' })).toBeNull();
  });

  it('explains when the authenticated account has no assigned roles', () => {
    renderProtectedPage();

    expect(screen.getByRole('alert').textContent).toContain('No roles are assigned');
  });

  it('keeps Admin-only pages restricted and offers Operations users a route to their workspace', () => {
    auth.roles = ['ROLE_OPERATIONS'];
    renderProtectedPage(['ADMIN']);

    expect(screen.getByRole('alert').textContent).toContain('does not have an authorized role');
    expect(screen.queryByRole('heading', { name: 'Authorized workspace' })).toBeNull();
    expect(screen.getByRole('link', { name: 'Go to Operations workspace' }).getAttribute('href')).toBe('/staff');
  });
});
