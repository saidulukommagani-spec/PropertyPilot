import { afterEach, describe, expect, it, vi } from 'vitest';
import { cleanup, fireEvent, render, screen, waitFor } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { MemoryRouter } from 'react-router-dom';
import CustomerRegistrationPage from './CustomerRegistrationPage';

const registerCustomer = vi.hoisted(() => vi.fn());
vi.mock('../../../api/auth', () => ({ authApi: { registerCustomer } }));

function renderPage() {
  return render(<QueryClientProvider client={new QueryClient()}><MemoryRouter><CustomerRegistrationPage /></MemoryRouter></QueryClientProvider>);
}

afterEach(() => { cleanup(); registerCustomer.mockReset(); });

describe('CustomerRegistrationPage', () => {
  it('validates matching passwords and submits the existing registration contract', async () => {
    registerCustomer.mockResolvedValue(undefined);
    renderPage();
    fireEvent.change(screen.getByLabelText(/Full name/), { target: { value: '  Alex Customer  ' } });
    fireEvent.change(screen.getByLabelText(/Email address/), { target: { value: ' ALEX@example.com ' } });
    fireEvent.change(screen.getByLabelText(/Phone number/), { target: { value: ' 5551234567 ' } });
    fireEvent.change(screen.getByLabelText(/^Password/), { target: { value: 'secure-password' } });
    fireEvent.change(screen.getByLabelText(/Confirm password/), { target: { value: 'secure-password' } });
    fireEvent.click(screen.getByRole('button', { name: 'Create account' }));
    await waitFor(() => expect(registerCustomer).toHaveBeenCalledWith({ fullName: 'Alex Customer', email: 'alex@example.com', mobileNumber: '5551234567', password: 'secure-password' }));
  });

  it('rejects a password confirmation mismatch', async () => {
    renderPage();
    fireEvent.change(screen.getByLabelText(/^Password/), { target: { value: 'secure-password' } });
    fireEvent.change(screen.getByLabelText(/Confirm password/), { target: { value: 'different-password' } });
    fireEvent.click(screen.getByRole('button', { name: 'Create account' }));
    expect(await screen.findByText('Passwords do not match.')).toBeTruthy();
    expect(registerCustomer).not.toHaveBeenCalled();
  });
});
