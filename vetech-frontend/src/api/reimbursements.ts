import { http, requestData } from './http'
import {
  mapDetailToFormState,
  mapFormStateToDetailPayload,
  mapListItem,
  mapListQuery
} from './reimbursementAdapters'
import type {
  BackendPage,
  BackendReimbursementListItem,
  DetailFormState,
  FormStateForPayload,
  NormalizedLookups,
  ReimbursementDetailResponse,
  ReimbursementFilters,
  ReimbursementItem
} from '../types/reimbursement'

export interface ReimbursementPage {
  records: ReimbursementItem[]
  total: number
}

export async function fetchReimbursements(
  filters: ReimbursementFilters,
  current: number,
  size: number
): Promise<ReimbursementPage> {
  const data = await requestData<BackendPage<BackendReimbursementListItem>>(
    http.get('/reimbursements', {
      params: mapListQuery(filters, current, size)
    })
  )

  return {
    records: (data.records ?? []).map(mapListItem),
    total: data.total ?? 0
  }
}

export async function fetchReimbursementDetail(
  id: string,
  lookups: NormalizedLookups
): Promise<DetailFormState> {
  const data = await requestData<ReimbursementDetailResponse>(http.get(`/reimbursements/${id}`))
  return mapDetailToFormState(data, lookups)
}

export async function saveReimbursement(state: FormStateForPayload): Promise<string> {
  const payload = mapFormStateToDetailPayload(state)
  const id = state.form.id
  if (id) {
    return requestData<string>(http.put(`/reimbursements/${id}`, payload))
  }
  return requestData<string>(http.post('/reimbursements', payload))
}

export async function submitReimbursement(id: string): Promise<boolean> {
  return requestData<boolean>(http.post(`/reimbursements/${id}/submit`))
}

export async function voidReimbursement(id: string): Promise<boolean> {
  return requestData<boolean>(http.post(`/reimbursements/${id}/void`))
}

export async function deleteReimbursement(id: string): Promise<boolean> {
  return requestData<boolean>(http.delete(`/reimbursements/${id}`))
}
