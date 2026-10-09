import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { EvidenceService } from '../services/EvidenceService';
import type { ReviewEvidenceRequest, UploadEvidenceRequest } from '../types/evidence';
import { serviceRequestApi } from '../../../api/serviceRequestApi';

export const evidenceKeys = {
  all: ['evidence'] as const,
  list: ['evidence','list'] as const,
  detail: (id:string) => ['evidence','detail',id] as const,
  types: ['evidence','types'] as const,
  validation: ['evidence','validation'] as const,
  requestContext: (id: string) => ['evidence','request-context',id] as const,
  forVisit: (id:string) => ['evidence','visit',id] as const,
  visitSummary: (id:string) => ['evidence','visit-summary',id] as const,
};

export function useEvidenceList() { return useQuery({queryKey:evidenceKeys.list,queryFn:EvidenceService.list}); }
export function useEvidenceDashboard() { return useQuery({queryKey:['evidence','dashboard'],queryFn:EvidenceService.getDashboard}); }
export function useEvidenceDetails(id:string) { return useQuery({queryKey:evidenceKeys.detail(id),queryFn:()=>EvidenceService.get(id),enabled:Boolean(id)}); }
export function useEvidenceTypes() { return useQuery({queryKey:evidenceKeys.types,queryFn:EvidenceService.getTypes,staleTime:300_000}); }
export function useEvidenceValidationQueue() { return useQuery({queryKey:evidenceKeys.validation,queryFn:EvidenceService.getValidationQueue}); }
export function useEvidenceRequestContext(serviceRequestId: string) {
  return useQuery({ queryKey: evidenceKeys.requestContext(serviceRequestId), queryFn: () => serviceRequestApi.getDetails(serviceRequestId), enabled: Boolean(serviceRequestId) });
}
export function useVisitEvidence(visitId:string) { return useQuery({queryKey:evidenceKeys.forVisit(visitId),queryFn:()=>EvidenceService.getForVisit(visitId),enabled:Boolean(visitId)}); }
export function useVisitEvidenceSummary(visitId:string) { return useQuery({queryKey:evidenceKeys.visitSummary(visitId),queryFn:()=>EvidenceService.getVisitSummary(visitId),enabled:Boolean(visitId)}); }

export function useUploadEvidence() {
  const client=useQueryClient();
  return useMutation({mutationFn:({request,onProgress}:{request:UploadEvidenceRequest;onProgress?:(percentage:number)=>void})=>EvidenceService.upload(request,onProgress),onSuccess:async(evidence)=>{
    await Promise.all([client.invalidateQueries({queryKey:evidenceKeys.list}),client.invalidateQueries({queryKey:['evidence','dashboard']}),client.invalidateQueries({queryKey:evidenceKeys.validation}),client.invalidateQueries({queryKey:evidenceKeys.forVisit(evidence.visitId??'')}),client.invalidateQueries({queryKey:evidenceKeys.visitSummary(evidence.visitId??'')}),client.invalidateQueries({queryKey:['service-request-evidence',evidence.serviceRequestId]})]);
  }});
}

export function useReviewEvidence() {
  const client=useQueryClient();
  return useMutation({mutationFn:({evidenceId,request}:{evidenceId:string;request:ReviewEvidenceRequest})=>EvidenceService.review(evidenceId,request),onSuccess:async(evidence)=>{
    await Promise.all([client.invalidateQueries({queryKey:evidenceKeys.list}),client.invalidateQueries({queryKey:['evidence','dashboard']}),client.invalidateQueries({queryKey:evidenceKeys.detail(evidence.evidenceId)}),client.invalidateQueries({queryKey:evidenceKeys.validation}),client.invalidateQueries({queryKey:evidenceKeys.forVisit(evidence.visitId??'')}),client.invalidateQueries({queryKey:evidenceKeys.visitSummary(evidence.visitId??'')}),client.invalidateQueries({queryKey:evidenceKeys.requestContext(evidence.serviceRequestId)})]);
  }});
}
