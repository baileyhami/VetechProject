import http from './http'
import type { ReimbursementDetail, ReimbursementListItem } from '@/types/reimbursement'

export interface ReimbursementQuery {
  id?: string
  title?: string
  reason?: string
  companyId?: string
  departmentId?: string
  reimburserId?: string
  businessTypeId?: string
}

export const fetchReimbursements = async (query: ReimbursementQuery) => {
  const { data } = await http.get<ReimbursementListItem[]>('/reimbursements', { params: query })
  return data
}

export const fetchReimbursementDetail = async (id: string) => {
  const { data } = await http.get<ReimbursementDetail>(`/reimbursements/${id}`)
  return data
}

export const createReimbursement = async (payload: ReimbursementDetail) => {
  const { data } = await http.post<{ id: string }>('/reimbursements', payload)
  return data
}

export const updateReimbursement = async (id: string, payload: ReimbursementDetail) => {
  const { data } = await http.put<{ id: string }>(`/reimbursements/${id}`, payload)
  return data
}

export const submitReimbursement = async (id: string) => {
  const { data } = await http.post<{ id: string; status: number }>(`/reimbursements/${id}/submit`)
  return data
}

export const voidReimbursement = async (id: string) => {
  const { data } = await http.post<{ id: string; status: number }>(`/reimbursements/${id}/void`)
  return data
}
