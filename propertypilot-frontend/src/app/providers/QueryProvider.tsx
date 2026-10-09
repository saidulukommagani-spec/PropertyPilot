import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { useState, type ReactNode } from 'react';
import axios from 'axios';

export function shouldRetryQuery(failureCount: number, error: unknown): boolean {
  // HTTP responses are authoritative failures (including 5xx) and need an
  // explicit user retry. Only retry once when the request had no response.
  return failureCount < 1
    && axios.isAxiosError(error)
    && !axios.isCancel(error)
    && error.response == null;
}

export default function QueryProvider({ children }: { children: ReactNode }) {
  const [client] = useState(() => new QueryClient({
    defaultOptions: { queries: { retry: shouldRetryQuery, staleTime: 30_000, refetchOnWindowFocus: false } },
  }));
  return <QueryClientProvider client={client}>{children}</QueryClientProvider>;
}
