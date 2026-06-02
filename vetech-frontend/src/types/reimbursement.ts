export interface Result<T> {
  code: string
  message: string
  data: T
}

export interface BackendPage<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages?: number
}

export interface ReimbursementFilters {
  reimNo: string
  title: string
  reason: string
  companyId: string
  deptId: string
  employeeId: string
  businessTypeId: string
}

export interface ReimbursementItem {
  id: string
  reimNo?: string
  status: string
  reimburserName?: string
  reimburserNo?: string
  reimburserDeptName?: string
  reimburserDeptNo?: string
  companyName?: string
  businessType?: string
  title?: string
  reason?: string
  subsidyAmount: number
  createTime?: string
  showDropdown?: boolean
}

export interface BackendReimbursementListItem {
  id: string
  status?: number | string
  reimbursementTitle?: string
  businessTripReason?: string
  subsidyTotal?: number | string
  totalAmount?: number | string
  creationTime?: string
  reimburserId?: string
  reimburserName?: string
  reimburserNo?: string
  reimDepartmentId?: string
  reimDepartmentName?: string
  reimDepartmentNo?: string
  reimCompanyId?: string
  reimCompanyName?: string
  businessTypeId?: string
  businessTypeName?: string
}

export interface BackendReimMain {
  id?: string
  creationTime?: string | Date
  reimbursementTitle?: string
  reimburserId?: string
  reimDepartmentId?: string
  reimCompanyId?: string
  businessTypeId?: string
  businessTripReason?: string
  subsidyTotal?: number
  totalAmount?: number
  mealAllowance?: number
  transportationAllowance?: number
  phoneAllowance?: number
  remark?: string
  remarks?: string
  status?: number
}

export interface BackendTrip {
  id?: string
  mainId?: string
  travelerId?: string
  departureCityNo?: string
  arrivalCityNo?: string
  departureDate?: string | Date
  arrivalDate?: string | Date
  tripRemark?: string
  createTime?: string | Date
}

export interface BackendApportion {
  id?: string
  mainId?: string
  reimCompanyId?: string
  projectId?: string
  apportionRatio?: number
  apportionAmount?: number
  sortNo?: number
}

export interface BackendSubsidy {
  id?: string
  mainId?: string
  tripId?: string
  travelerId?: string
  travelDateRange?: string
  subsidyDays?: number
  tripRoute?: string
  subsidyCityNo?: string
  applyAmount?: number
  subsidyAmount?: number
  mealAllowance?: number
  transportationAllowance?: number
  phoneAllowance?: number
}

export interface BackendSubsidyDetail {
  id?: string
  subsidyId?: string
  detailDate?: string
  weekDay?: string
  cityNo?: string
  mealStd?: number
  mealAmount?: number
  transportStd?: number
  transportAmount?: number
  phoneStd?: number
  phoneAmount?: number
  selected?: number
  cityName?: string
}

export interface ReimbursementDetailPayload {
  main: BackendReimMain
  trips: BackendTrip[]
  apportion: BackendApportion[]
}

export interface ReimbursementDetailResponse extends ReimbursementDetailPayload {
  subsidies?: BackendSubsidy[]
  subsidyDetails?: BackendSubsidyDetail[]
}

export interface OptionItem {
  id: string
  name: string
}

export interface CityOption extends OptionItem {
  type?: string
}

export interface BusinessTypeTreeItem extends OptionItem {
  children: BusinessTypeTreeItem[]
}

export interface NormalizedLookups {
  companies: OptionItem[]
  departments: OptionItem[]
  employees: OptionItem[]
  businessTypes: BusinessTypeTreeItem[]
  projects: OptionItem[]
  cities: CityOption[]
}

export interface BackendLookups {
  companies?: Array<Record<string, string>>
  departments?: Array<Record<string, string>>
  employees?: Array<Record<string, string>>
  businessTypes?: Array<Record<string, string>>
  projects?: Array<Record<string, string>>
  cities?: Array<Record<string, string>>
}

export interface FormMainState {
  id: string
  title: string
  reason: string
  employeeId: string
  deptId: string
  companyId: string
  businessTypeId: string[]
  remark: string
  createTime: Date
}

export interface FeeSummaryState {
  totalAmount: number
  mealAmount: number
  transportAmount: number
  communicationAmount: number
}

export interface TravelState {
  id?: string
  employeeId: string
  employeeName?: string
  startCity: string
  endCity: string
  startCityName?: string
  endCityName?: string
  startDate: string
  endDate: string
  dateRange?: string
  route?: string
  description: string
}

export interface SubsidyState {
  employeeId?: string
  employeeName?: string
  startDate?: string
  endDate?: string
  dateRange?: string
  route?: string
  subsidyDays?: number
  startCityName?: string
  endCityName?: string
  subsidyCity?: string
  applyAmount?: number
  subsidyAmount?: number
  mealAmount?: number
  transportAmount?: number
  communicationAmount?: number
  calendarData?: BackendSubsidyDetail[]
}

export interface AllocationState {
  companyId: string
  projectId: string
  ratio: number
  amount: number
}

export interface FormStateForPayload {
  form: FormMainState
  feeSummary: FeeSummaryState
  travelList: TravelState[]
  allocationList: AllocationState[]
}

export interface DetailFormState extends FormStateForPayload {
  subsidyList: SubsidyState[]
}
