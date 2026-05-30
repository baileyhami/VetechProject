import http from './http'
import type { LookupResponse } from '@/types/reimbursement'

export const fetchLookups = async () => {
  const { data } = await http.get<LookupResponse>('/lookups')
  return data
}
