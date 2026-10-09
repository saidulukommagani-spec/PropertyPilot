import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { quoteApi } from '../services/quoteApi';

export const quoteKeys = { all: ['quotes'] as const, list: (operations: boolean) => ['quotes', 'list', operations] as const, detail: (id: string) => ['quotes', 'detail', id] as const };
export function useQuotes(operations: boolean) { return useQuery({ queryKey: quoteKeys.list(operations), queryFn: () => quoteApi.list(operations) }); }
export function useQuote(id: string) { return useQuery({ queryKey: quoteKeys.detail(id), queryFn: () => quoteApi.get(id), enabled: Boolean(id) }); }
function useDecision(action: 'accept' | 'reject') {
  const client = useQueryClient();
  return useMutation({ mutationFn: ({ id, reason }: { id: string; reason?: string }) => action === 'accept' ? quoteApi.accept(id) : quoteApi.reject(id, reason ?? ''), onSuccess: (quote) => { void client.invalidateQueries({ queryKey: quoteKeys.all }); void client.setQueryData(quoteKeys.detail(quote.quoteId), quote); } });
}
export function useAcceptQuote() { return useDecision('accept'); }
export function useRejectQuote() { return useDecision('reject'); }
export function useCalculateQuote() { const client = useQueryClient(); return useMutation({ mutationFn: quoteApi.calculate, onSuccess: () => { void client.invalidateQueries({ queryKey: quoteKeys.all }); } }); }
