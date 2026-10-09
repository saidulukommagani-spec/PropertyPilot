import { cleanup, fireEvent, render, screen, waitFor } from '@testing-library/react';
import { afterEach, describe, expect, it, vi } from 'vitest';
import { authApi, type CurrentUserResponse } from '../../api/auth';
import { AuthProvider, useAuth } from './AuthContext';

const sessionKey = 'propertypilot.session.v1';

const currentUser: CurrentUserResponse = {
  userId: 'user-id',
  fullName: 'PropertyPilot Admin',
  email: 'admin@propertypilot.com',
  mobileNumber: '9999999999',
  status: 'ACTIVE',
  roles: ['ROLE_ADMIN'],
};

function AuthProbe() {
  const auth = useAuth();
  return (
    <>
      <output data-testid="auth-state">
        {auth.isAuthenticated ? 'authenticated' : auth.isInitializing ? 'initializing' : 'anonymous'}
      </output>
      <button onClick={() => void auth.signIn({ email: currentUser.email, password: 'secret' })}>
        Sign in
      </button>
    </>
  );
}

afterEach(() => {
  cleanup();
  window.sessionStorage.clear();
  vi.restoreAllMocks();
});

describe('AuthProvider session persistence', () => {
  it('persists server-provided expiry seconds as an absolute expiry timestamp', async () => {
    vi.spyOn(authApi, 'login').mockResolvedValue({
      accessToken: 'signed-token',
      tokenType: 'Bearer',
      expiresIn: 1_800,
    });
    vi.spyOn(authApi, 'currentUser').mockResolvedValue(currentUser);

    render(<AuthProvider><AuthProbe /></AuthProvider>);
    const start = Date.now();
    fireEvent.click(screen.getByRole('button', { name: 'Sign in' }));

    await waitFor(() => expect(screen.getByTestId('auth-state').textContent).toBe('authenticated'));

    const session = JSON.parse(window.sessionStorage.getItem(sessionKey) ?? '{}') as {
      accessToken?: string;
      expiresAt?: number;
    };
    expect(session.accessToken).toBe('signed-token');
    expect(session.expiresAt).toBeGreaterThanOrEqual(start + 1_800_000);
    expect(session.expiresAt).toBeLessThanOrEqual(Date.now() + 1_800_000);
  });

  it('removes an expired persisted session without validating its token', async () => {
    window.sessionStorage.setItem(sessionKey, JSON.stringify({ accessToken: 'expired-token', expiresAt: Date.now() - 1 }));
    const currentUserRequest = vi.spyOn(authApi, 'currentUser').mockResolvedValue(currentUser);

    render(<AuthProvider><AuthProbe /></AuthProvider>);

    await waitFor(() => expect(screen.getByTestId('auth-state').textContent).toBe('anonymous'));
    expect(window.sessionStorage.getItem(sessionKey)).toBeNull();
    expect(currentUserRequest).not.toHaveBeenCalled();
  });
});
