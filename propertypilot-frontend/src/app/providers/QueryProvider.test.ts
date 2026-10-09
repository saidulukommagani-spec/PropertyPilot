import { describe, expect, it } from 'vitest';
import { shouldRetryQuery } from './QueryProvider';

describe('shouldRetryQuery', () => {
  it('retries a network failure once', () => {
    const networkError = { isAxiosError: true };

    expect(shouldRetryQuery(0, networkError)).toBe(true);
    expect(shouldRetryQuery(1, networkError)).toBe(false);
  });

  it('does not automatically retry an HTTP error', () => {
    const serverError = { isAxiosError: true, response: { status: 500 } };

    expect(shouldRetryQuery(0, serverError)).toBe(false);
  });

  it('does not retry non-Axios errors', () => {
    expect(shouldRetryQuery(0, new Error('Unexpected response'))).toBe(false);
  });

  it('does not retry an intentionally cancelled request', () => {
    expect(shouldRetryQuery(0, { isAxiosError: true, __CANCEL__: true })).toBe(false);
  });
});
