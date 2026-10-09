import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { invoiceApi } from '../services/invoiceApi';

export const invoiceKeys = { all: ['invoices'] as const, list: ['invoices','list'] as const, detail: (id:string)=>['invoices','detail',id] as const, payments:(id:string)=>['invoices','payments',id] as const, revenue:['invoices','revenue'] as const, deals:['invoices','deals'] as const };
export function useInvoices(){return useQuery({queryKey:invoiceKeys.list,queryFn:invoiceApi.list});}
export function useInvoice(id:string){return useQuery({queryKey:invoiceKeys.detail(id),queryFn:()=>invoiceApi.get(id),enabled:Boolean(id)});}
export function useInvoicePayments(id:string, enabled=true){return useQuery({queryKey:invoiceKeys.payments(id),queryFn:()=>invoiceApi.payments(id),enabled:Boolean(id)&&enabled});}
function useInvoiceWorkflowAction(id:string, action:'send'|'cancel') { const qc=useQueryClient(); return useMutation({mutationFn:()=>invoiceApi[action](id),onSuccess:()=>{void qc.invalidateQueries({queryKey:invoiceKeys.all});void qc.invalidateQueries({queryKey:invoiceKeys.detail(id)});void qc.invalidateQueries({queryKey:invoiceKeys.revenue});}}); }
export function useSendInvoice(id:string){return useInvoiceWorkflowAction(id,'send');}
export function useCancelInvoice(id:string){return useInvoiceWorkflowAction(id,'cancel');}
export function useInvoiceRevenue(){return useQuery({queryKey:invoiceKeys.revenue,queryFn:invoiceApi.revenue});}
export function useInvoiceDeals(){return useQuery({queryKey:invoiceKeys.deals,queryFn:invoiceApi.deals});}
export function useRecordInvoicePayment(id:string){const qc=useQueryClient();return useMutation({mutationFn:(request:Parameters<typeof invoiceApi.recordPayment>[1])=>invoiceApi.recordPayment(id,request),onSuccess:()=>{void qc.invalidateQueries({queryKey:invoiceKeys.all});void qc.invalidateQueries({queryKey:invoiceKeys.detail(id)});void qc.invalidateQueries({queryKey:invoiceKeys.payments(id)});void qc.invalidateQueries({queryKey:invoiceKeys.revenue});}});}
export function useCompleteDealForInvoice(){const qc=useQueryClient();return useMutation({mutationFn:(input:{dealId:string;finalDealAmount:number})=>invoiceApi.completeDeal(input.dealId,input.finalDealAmount),onSuccess:()=>{void qc.invalidateQueries({queryKey:invoiceKeys.all});void qc.invalidateQueries({queryKey:invoiceKeys.revenue});void qc.invalidateQueries({queryKey:['deals']});}});}
