import { http, requestData } from './http'
import { mapLookups } from './reimbursementAdapters'
import type { BackendLookups, NormalizedLookups } from '../types/reimbursement'

export async function fetchLookups(): Promise<NormalizedLookups> {
  const [companies, departments, employees, businessTypes, projects, cities] = await Promise.all([
    requestData(http.get('/lookup/companies')),
    requestData(http.get('/lookup/departments')),
    requestData(http.get('/lookup/employees')),
    requestData(http.get('/lookup/business-types')),
    requestData(http.get('/lookup/projects')),
    requestData(http.get('/lookup/cities'))
  ])

  return mapLookups({
    companies,
    departments,
    employees,
    businessTypes,
    projects,
    cities
  } as BackendLookups)
}
