export interface Company {
  id: string
  no: string
  name: string
}

export interface Department {
  id: string
  no: string
  name: string
}

export interface Employee {
  id: string
  no: string
  name: string
}

export interface BusinessType {
  id: string
  no: string
  name: string
  superiorId: string
  hasChildren: string
}

export interface City {
  no: string
  name: string
  type: string
}

export interface Project {
  id: string
  no: string
  name: string
}

export interface LookupResponse {
  companies: Company[]
  departments: Department[]
  employees: Employee[]
  businessTypes: BusinessType[]
  cities: City[]
  projects: Project[]
}

export interface ReimbursementListItem {
  id: string
  status: number
  reimburserName: string
  reimburserNo: string
  departmentName: string
  departmentNo: string
  companyName: string
  businessTypeName: string
  title: string
  reason: string
  subsidyTotal: string
  creationTime: string
}

export interface MainForm {
  id?: string
  reimbursementTitle: string
  reimburserId: string
  reimDepartmentId: string
  reimCompanyId: string
  businessTypeId: string
  businessTripReason: string
  remarks: string
  status?: number
  creationTime?: string
  subsidyTotal?: string
  mealAllowance?: string
  transportationAllowance?: string
  phoneAllowance?: string
}

export interface TripForm {
  id?: string
  travelerId: string
  departureCityNo: string
  arrivalCityNo: string
  departureDate: string
  arrivalDate: string
  tripRemark: string
  subsidyDetails?: SubsidyDetailForm[]
}

export interface SubsidyDetailForm {
  subsidyId?: string
  detailDate: string
  weekDay?: string
  cityNo?: string
  mealStd?: string
  mealAmount?: string
  transportStd?: string
  transportAmount?: string
  phoneStd?: string
  phoneAmount?: string
  selected?: boolean
}

export interface SubsidySummary {
  id: string
  mainId: string
  tripId: string
  travelerId: string
  travelDateRange: string
  subsidyDays: number
  tripRoute: string
  subsidyCityNo: string
  applyAmount: string
  subsidyAmount: string
  mealAllowance: string
  transportationAllowance: string
  phoneAllowance: string
}

export interface ApportionForm {
  id?: string
  reimCompanyId: string
  projectId: string
  apportionRatio: number
  apportionAmount: number
  sortNo?: number
}

export interface ReimbursementDetail {
  main: MainForm
  trips: TripForm[]
  subsidies: SubsidySummary[]
  subsidyDetails: SubsidyDetailForm[]
  apportion: ApportionForm[]
}
