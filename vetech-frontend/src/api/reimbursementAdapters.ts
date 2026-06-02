import type {
  AllocationState,
  BackendApportion,
  BackendLookups,
  BackendReimbursementListItem,
  BackendSubsidy,
  BackendTrip,
  BusinessTypeTreeItem,
  DetailFormState,
  FormStateForPayload,
  NormalizedLookups,
  ReimbursementDetailPayload,
  ReimbursementDetailResponse,
  ReimbursementFilters,
  ReimbursementItem
} from '../types/reimbursement'

const statusText: Record<string, string> = {
  '0': '草稿',
  '1': '已完成',
  '2': '已作废'
}

export function mapStatus(value: number | string | undefined): string {
  if (value === undefined || value === null || value === '') return '草稿'
  const key = String(value)
  return statusText[key] ?? key
}

export function mapListQuery(
  filters: ReimbursementFilters,
  current: number,
  size: number
): Record<string, string | number> {
  const query: Record<string, string | number> = {
    current,
    size
  }

  addIfPresent(query, 'reimbursementNo', filters.reimNo)
  addIfPresent(query, 'reimbursementTitle', filters.title)
  addIfPresent(query, 'businessTripReason', filters.reason)
  addIfPresent(query, 'reimCompanyId', filters.companyId)
  addIfPresent(query, 'reimDepartmentId', filters.deptId)
  addIfPresent(query, 'reimburserId', filters.employeeId)
  addIfPresent(query, 'businessTypeId', filters.businessTypeId)

  return query
}

export function mapListItem(item: BackendReimbursementListItem): ReimbursementItem {
  return {
    id: item.id,
    reimNo: item.id,
    status: mapStatus(item.status),
    reimburserName: item.reimburserName,
    reimburserNo: item.reimburserNo,
    reimburserDeptName: item.reimDepartmentName,
    reimburserDeptNo: item.reimDepartmentNo,
    companyName: item.reimCompanyName,
    businessType: item.businessTypeName,
    title: item.reimbursementTitle,
    reason: item.businessTripReason,
    subsidyAmount: toNumber(item.subsidyTotal ?? item.totalAmount),
    createTime: formatDateOnly(item.creationTime),
    showDropdown: false
  }
}

export function mapLookups(data: BackendLookups): NormalizedLookups {
  const businessTypes = (data.businessTypes ?? []).map(item => ({
    id: pick(item, 'businessTypeId', 'reimBusinessTypeId', 'id'),
    name: pick(item, 'businessTypeName', 'reimBusinessTypeName', 'name'),
    parentId: pick(item, 'superiorId', 'parentId'),
    children: []
  }))

  return {
    companies: (data.companies ?? []).map(item => ({
      id: pick(item, 'reimCompanyId', 'companyId', 'id'),
      name: pick(item, 'reimCompanyName', 'companyName', 'name')
    })),
    departments: (data.departments ?? []).map(item => ({
      id: pick(item, 'reimDepartmentId', 'reimDeptId', 'deptId', 'id'),
      name: pick(item, 'reimDepartmentName', 'reimDeptName', 'deptName', 'name')
    })),
    employees: (data.employees ?? []).map(item => ({
      id: pick(item, 'reimburserId', 'employeeId', 'id'),
      name: pick(item, 'reimburserName', 'employeeName', 'name')
    })),
    businessTypes: buildBusinessTypeTree(businessTypes),
    projects: (data.projects ?? []).map(item => ({
      id: pick(item, 'projectId', 'id'),
      name: pick(item, 'projectName', 'name')
    })),
    cities: (data.cities ?? []).map(item => ({
      id: pick(item, 'cityNo', 'cityId', 'id'),
      name: pick(item, 'cityName', 'name'),
      type: pick(item, 'cityType', 'type')
    }))
  }
}

export function mapFormStateToDetailPayload(state: FormStateForPayload): ReimbursementDetailPayload {
  return {
    main: {
      id: state.form.id || undefined,
      creationTime: toBackendDateTime(state.form.createTime),
      reimbursementTitle: state.form.title,
      businessTripReason: state.form.reason,
      reimburserId: state.form.employeeId,
      reimDepartmentId: state.form.deptId,
      reimCompanyId: state.form.companyId,
      businessTypeId: lastValue(state.form.businessTypeId),
      subsidyTotal: state.feeSummary.totalAmount,
      totalAmount: state.feeSummary.totalAmount,
      mealAllowance: state.feeSummary.mealAmount,
      transportationAllowance: state.feeSummary.transportAmount,
      phoneAllowance: state.feeSummary.communicationAmount,
      remark: state.form.remark,
      remarks: state.form.remark,
      status: 0
    },
    trips: state.travelList.map((item): BackendTrip => ({
      id: item.id || undefined,
      travelerId: item.employeeId,
      departureCityNo: item.startCity,
      arrivalCityNo: item.endCity,
      departureDate: item.startDate,
      arrivalDate: item.endDate,
      tripRemark: item.description
    })),
    apportion: state.allocationList.map((item, index): BackendApportion => ({
      reimCompanyId: item.companyId,
      projectId: item.projectId,
      apportionRatio: item.ratio,
      apportionAmount: item.amount,
      sortNo: index + 1
    }))
  }
}

export function mapDetailToFormState(
  detail: ReimbursementDetailResponse,
  lookups: NormalizedLookups
): DetailFormState {
  const main = detail.main ?? {}
  const trips = detail.trips ?? []
  const apportion = detail.apportion ?? []
  const subsidies = detail.subsidies ?? []

  const travelList = trips.map(trip => {
    const employee = lookups.employees.find(item => item.id === trip.travelerId)
    const startCity = lookups.cities.find(item => item.id === trip.departureCityNo)
    const endCity = lookups.cities.find(item => item.id === trip.arrivalCityNo)
    const startDate = formatDateOnly(trip.departureDate)
    const endDate = formatDateOnly(trip.arrivalDate)

    return {
      id: trip.id,
      employeeId: trip.travelerId ?? '',
      employeeName: employee?.name ?? '',
      startCity: trip.departureCityNo ?? '',
      endCity: trip.arrivalCityNo ?? '',
      startCityName: startCity?.name ?? '',
      endCityName: endCity?.name ?? '',
      startDate,
      endDate,
      dateRange: `${startDate} 至 ${endDate}`,
      route: `${startCity?.name ?? ''} - ${endCity?.name ?? ''}`,
      description: trip.tripRemark ?? ''
    }
  })

  const allocationList = apportion.length > 0
    ? apportion.map((item): AllocationState => ({
      companyId: item.reimCompanyId ?? '',
      projectId: item.projectId ?? '',
      ratio: toNumber(item.apportionRatio),
      amount: toNumber(item.apportionAmount)
    }))
    : [{
      companyId: main.reimCompanyId ?? '',
      projectId: '',
      ratio: 100,
      amount: toNumber(main.subsidyTotal ?? main.totalAmount)
    }]

  const subsidyList = subsidies.map((item: BackendSubsidy) => {
    const employee = lookups.employees.find(option => option.id === item.travelerId)
    const city = lookups.cities.find(option => option.id === item.subsidyCityNo)
    return {
      employeeId: item.travelerId,
      employeeName: employee?.name ?? '',
      dateRange: item.travelDateRange,
      route: item.tripRoute,
      subsidyDays: item.subsidyDays,
      subsidyCity: city?.name ?? item.subsidyCityNo ?? '',
      applyAmount: toNumber(item.applyAmount),
      subsidyAmount: toNumber(item.subsidyAmount),
      mealAmount: toNumber(item.mealAllowance),
      transportAmount: toNumber(item.transportationAllowance),
      communicationAmount: toNumber(item.phoneAllowance),
      calendarData: detail.subsidyDetails ?? []
    }
  })

  return {
    form: {
      id: main.id ?? '',
      title: main.reimbursementTitle ?? '',
      reason: main.businessTripReason ?? '',
      employeeId: main.reimburserId ?? '',
      deptId: main.reimDepartmentId ?? '',
      companyId: main.reimCompanyId ?? '',
      businessTypeId: main.businessTypeId ? [main.businessTypeId] : [],
      remark: main.remark ?? main.remarks ?? '',
      createTime: toDate(main.creationTime)
    },
    feeSummary: {
      totalAmount: toNumber(main.subsidyTotal ?? main.totalAmount),
      mealAmount: toNumber(main.mealAllowance),
      transportAmount: toNumber(main.transportationAllowance),
      communicationAmount: toNumber(main.phoneAllowance)
    },
    travelList,
    subsidyList,
    allocationList
  }
}

function addIfPresent(target: Record<string, string | number>, key: string, value: string): void {
  if (value !== undefined && value !== null && value !== '') {
    target[key] = value
  }
}

function pick(item: Record<string, string>, ...keys: string[]): string {
  for (const key of keys) {
    const value = item[key]
    if (value !== undefined && value !== null && value !== '') return value
  }
  return ''
}

function buildBusinessTypeTree(
  flatItems: Array<{ id: string; name: string; parentId: string; children: Array<any> }>
): BusinessTypeTreeItem[] {
  const nodes: BusinessTypeTreeItem[] = flatItems.map(item => ({
    id: item.id,
    name: item.name,
    children: [] as BusinessTypeTreeItem[]
  }))
  const nodeById = new Map(nodes.map(node => [node.id, node]))
  const roots: typeof nodes = []

  flatItems.forEach((item, index) => {
    const node = nodes[index]
    const parentId = item.parentId
    const parent = parentId && parentId !== 'none' ? nodeById.get(parentId) : undefined
    if (parent) {
      parent.children.push(node)
    } else {
      roots.push(node)
    }
  })

  return roots
}

function toNumber(value: unknown): number {
  if (value === undefined || value === null || value === '') return 0
  const parsed = Number(value)
  return Number.isFinite(parsed) ? parsed : 0
}

function formatDateOnly(value: string | Date | undefined): string {
  if (!value) return ''
  if (value instanceof Date) return value.toISOString().slice(0, 10)
  return String(value).slice(0, 10)
}

function toDate(value: string | Date | undefined): Date {
  if (value instanceof Date) return value
  if (value) {
    const parsed = new Date(value)
    if (!Number.isNaN(parsed.getTime())) return parsed
  }
  return new Date()
}

function toBackendDateTime(value: Date): string {
  return value.toISOString()
}

function lastValue(value: string[]): string {
  return value.length > 0 ? value[value.length - 1] : ''
}
