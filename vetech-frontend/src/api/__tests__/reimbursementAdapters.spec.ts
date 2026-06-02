import * as assert from 'node:assert/strict'
import {
  mapDetailToFormState,
  mapFormStateToDetailPayload,
  mapListItem,
  mapListQuery,
  mapLookups
} from '../reimbursementAdapters'

const query = mapListQuery({
  reimNo: 'BX202606010001',
  title: '差旅',
  reason: '客户拜访',
  companyId: 'company-1',
  deptId: 'dept-1',
  employeeId: 'emp-1',
  businessTypeId: 'biz-1'
}, 2, 20)

assert.deepEqual(query, {
  reimbursementNo: 'BX202606010001',
  reimbursementTitle: '差旅',
  businessTripReason: '客户拜访',
  reimCompanyId: 'company-1',
  reimDepartmentId: 'dept-1',
  reimburserId: 'emp-1',
  businessTypeId: 'biz-1',
  current: 2,
  size: 20
})

assert.deepEqual(mapListItem({
  id: 'BX202606010001',
  status: 1,
  reimbursementTitle: '差旅报销',
  businessTripReason: '客户拜访',
  subsidyTotal: 128.5,
  creationTime: '2026-06-01T10:20:30',
  reimburserName: '张三',
  reimburserNo: 'E001',
  reimDepartmentName: '销售部',
  reimDepartmentNo: 'D001',
  reimCompanyName: '北京分公司',
  businessTypeName: '项目出差'
}), {
  id: 'BX202606010001',
  reimNo: 'BX202606010001',
  status: '已完成',
  reimburserName: '张三',
  reimburserNo: 'E001',
  reimburserDeptName: '销售部',
  reimburserDeptNo: 'D001',
  companyName: '北京分公司',
  businessType: '项目出差',
  title: '差旅报销',
  reason: '客户拜访',
  subsidyAmount: 128.5,
  createTime: '2026-06-01',
  showDropdown: false
})

assert.deepEqual(mapLookups({
  companies: [{ reimCompanyId: 'company-1', reimCompanyName: '北京分公司' }],
  departments: [{ reimDepartmentId: 'dept-1', reimDepartmentName: '销售部' }],
  employees: [{ reimburserId: 'emp-1', reimburserName: '张三' }],
  businessTypes: [
    { businessTypeId: 'root', businessTypeName: '差旅', superiorId: 'none' },
    { businessTypeId: 'child', businessTypeName: '项目出差', superiorId: 'root' }
  ],
  projects: [{ projectId: 'project-1', projectName: '研发项目' }],
  cities: [{ cityNo: '10119', cityName: '北京', cityType: '1' }]
}), {
  companies: [{ id: 'company-1', name: '北京分公司' }],
  departments: [{ id: 'dept-1', name: '销售部' }],
  employees: [{ id: 'emp-1', name: '张三' }],
  businessTypes: [{
    id: 'root',
    name: '差旅',
    children: [{ id: 'child', name: '项目出差', children: [] }]
  }],
  projects: [{ id: 'project-1', name: '研发项目' }],
  cities: [{ id: '10119', name: '北京', type: '1' }]
})

const payload = mapFormStateToDetailPayload({
  form: {
    id: '',
    title: '差旅报销',
    reason: '客户拜访',
    employeeId: 'emp-1',
    deptId: 'dept-1',
    companyId: 'company-1',
    businessTypeId: ['root', 'child'],
    remark: '备注',
    createTime: new Date('2026-06-01T08:00:00')
  },
  feeSummary: {
    totalAmount: 100,
    mealAmount: 50,
    transportAmount: 30,
    communicationAmount: 20
  },
  travelList: [{
    id: 'trip-1',
    employeeId: 'emp-1',
    startCity: '10119',
    endCity: '10621',
    startDate: '2026-05-30',
    endDate: '2026-06-01',
    description: '客户拜访'
  }],
  allocationList: [{
    companyId: 'company-1',
    projectId: 'project-1',
    ratio: 100,
    amount: 100
  }]
})

assert.equal(payload.main.reimbursementTitle, '差旅报销')
assert.equal(payload.main.businessTripReason, '客户拜访')
assert.equal(payload.main.businessTypeId, 'child')
assert.equal(payload.trips[0].travelerId, 'emp-1')
assert.equal(payload.trips[0].departureCityNo, '10119')
assert.equal(payload.apportion[0].apportionRatio, 100)
assert.equal(payload.apportion[0].apportionAmount, 100)

const detailState = mapDetailToFormState({
  main: payload.main,
  trips: payload.trips,
  apportion: payload.apportion,
  subsidies: [{
    travelerId: 'emp-1',
    travelDateRange: '2026-05-30 至 2026-06-01',
    subsidyDays: 3,
    tripRoute: '北京 - 上海',
    subsidyCityNo: '10621',
    applyAmount: 100,
    subsidyAmount: 100,
    mealAllowance: 50,
    transportationAllowance: 30,
    phoneAllowance: 20
  }],
  subsidyDetails: []
}, mapLookups({
  companies: [{ reimCompanyId: 'company-1', reimCompanyName: '北京分公司' }],
  departments: [{ reimDepartmentId: 'dept-1', reimDepartmentName: '销售部' }],
  employees: [{ reimburserId: 'emp-1', reimburserName: '张三' }],
  businessTypes: [
    { businessTypeId: 'root', businessTypeName: '差旅', superiorId: 'none' },
    { businessTypeId: 'child', businessTypeName: '项目出差', superiorId: 'root' }
  ],
  projects: [{ projectId: 'project-1', projectName: '研发项目' }],
  cities: [
    { cityNo: '10119', cityName: '北京', cityType: '1' },
    { cityNo: '10621', cityName: '上海', cityType: '1' }
  ]
}))

assert.equal(detailState.form.title, '差旅报销')
assert.deepEqual(detailState.form.businessTypeId, ['root', 'child'])
assert.equal(detailState.travelList[0].employeeName, '张三')
assert.equal(detailState.travelList[0].route, '北京 - 上海')
assert.equal(detailState.allocationList[0].ratio, 100)
assert.equal(detailState.subsidyList[0].mealAmount, 50)

const detailStateWithBusinessTypeName = mapDetailToFormState({
  main: {
    ...payload.main,
    businessTypeId: '',
    businessTypeName: '项目出差'
  },
  trips: [],
  apportion: [],
  subsidies: []
}, mapLookups({
  companies: [],
  departments: [],
  employees: [],
  businessTypes: [
    { businessTypeId: 'root', businessTypeName: '差旅', superiorId: 'none' },
    { businessTypeId: 'child', businessTypeName: '项目出差', superiorId: 'root' }
  ],
  projects: [],
  cities: []
}))

assert.deepEqual(detailStateWithBusinessTypeName.form.businessTypeId, ['root', 'child'])
