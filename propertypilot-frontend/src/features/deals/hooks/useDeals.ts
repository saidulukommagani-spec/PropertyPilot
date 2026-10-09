import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { dealApi } from '../services/dealApi';
import type { AddDealParticipantRequest, DealCreateRequest, DealStatusRequest } from '../types/deal';

export const dealKeys = { all: ['deals'] as const, list: ['deals','list'] as const, detail: (id: string) => ['deals','detail',id] as const, participants: (id: string) => ['deals','participants',id] as const };
export function useDeals() { return useQuery({ queryKey: dealKeys.list, queryFn: dealApi.list }); }
export function useDeal(id: string) { return useQuery({ queryKey: dealKeys.detail(id), queryFn: () => dealApi.get(id), enabled: Boolean(id) }); }
export function useDealParticipants(id: string) { return useQuery({ queryKey: dealKeys.participants(id), queryFn: () => dealApi.participants(id), enabled: Boolean(id) }); }
export function useAddDealParticipant(id: string) { const qc=useQueryClient(); return useMutation({ mutationFn:(request:AddDealParticipantRequest)=>dealApi.addParticipant(id,request), onSuccess:()=>{void qc.invalidateQueries({queryKey:dealKeys.participants(id)});} }); }
export function useSelectDealBuyer(id: string) { const qc=useQueryClient(); return useMutation({ mutationFn:(participantId:string)=>dealApi.selectBuyer(id,participantId), onSuccess:async()=>Promise.all([qc.invalidateQueries({queryKey:dealKeys.participants(id)}),qc.invalidateQueries({queryKey:dealKeys.detail(id)})]) }); }
export function useCreateDeal() { const qc=useQueryClient(); return useMutation({ mutationFn:(request:DealCreateRequest)=>dealApi.create(request), onSuccess:(deal)=>{void qc.invalidateQueries({queryKey:dealKeys.list});void qc.setQueryData(dealKeys.detail(deal.dealId),deal);} }); }
export function useUpdateDealStatus(id: string) { const qc=useQueryClient(); return useMutation({ mutationFn:(request:DealStatusRequest)=>dealApi.updateStatus(id,request), onSuccess:(deal)=>{void qc.invalidateQueries({queryKey:dealKeys.list});void qc.setQueryData(dealKeys.detail(id),deal);} }); }
export function useCancelDeal(id: string) { const qc=useQueryClient(); return useMutation({ mutationFn:(reason:string)=>dealApi.cancel(id,reason), onSuccess:(deal)=>{void qc.invalidateQueries({queryKey:dealKeys.list});void qc.setQueryData(dealKeys.detail(id),deal);} }); }
export function useFinalizeDeal(id: string) { const qc=useQueryClient(); return useMutation({ mutationFn:(input:{amount:number;remarks:string})=>dealApi.finalize(id,input.amount,input.remarks), onSuccess:(deal)=>{void qc.invalidateQueries({queryKey:dealKeys.list});void qc.setQueryData(dealKeys.detail(id),deal);} }); }
