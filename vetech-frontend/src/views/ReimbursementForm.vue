<template>
  <div class="reimbursement-form-wrapper">
    <!-- 固定表头 -->
    <div class="form-header">
      <div class="header-content">
        <div class="header-title">差旅费用报销单</div>
        <div class="header-date">
          单据日期：{{ formatDate(form.createTime || new Date()) }}
        </div>
      </div>
    </div>

    <!-- 表单主体内容 -->
    <div class="form-body">
      <div class="form-content">
        <!-- 基本信息分区 -->
        <div class="section">
          <div class="section-header" @click="sections.basicInfo = !sections.basicInfo">
            <span class="section-title">基本信息</span>
            <el-icon class="section-icon" :class="{ 'is-expanded': sections.basicInfo }">
              <ArrowRight />
            </el-icon>
          </div>
          <div class="section-content" v-show="sections.basicInfo">
            <el-form :model="form" label-width="120px" class="form-grid">
              <el-form-item label="报销标题">
                <el-input
                    v-model="form.title"
                    placeholder="请输入"
                    maxlength="500"
                    show-word-limit
                    :disabled="isReadonly"
                />
              </el-form-item>
              <el-form-item label="出差事由">
                <el-input
                    v-model="form.reason"
                    type="textarea"
                    :rows="3"
                    placeholder="请输入"
                    maxlength="500"
                    show-word-limit
                    :disabled="isReadonly"
                />
              </el-form-item>
              <el-form-item label="报销人">
                <el-select v-model="form.employeeId" placeholder="请选择" :disabled="isReadonly">
                  <el-option
                      v-for="emp in employees"
                      :key="emp.id"
                      :label="emp.name"
                      :value="emp.id"
                  />
                </el-select>
              </el-form-item>
              <el-form-item label="报销部门">
                <el-select v-model="form.deptId" placeholder="请选择" :disabled="isReadonly">
                  <el-option
                      v-for="dept in departments"
                      :key="dept.id"
                      :label="dept.name"
                      :value="dept.id"
                  />
                </el-select>
              </el-form-item>
              <el-form-item label="费用归属公司">
                <el-select
                    v-model="form.companyId"
                    placeholder="请选择"
                    :disabled="isReadonly"
                    @change="handleMainCompanyChange"
                >
                  <el-option
                      v-for="company in companies"
                      :key="company.id"
                      :label="company.name"
                      :value="company.id"
                  />
                </el-select>
              </el-form-item>
              <el-form-item label="业务类型">
                <el-cascader
                    v-model="form.businessTypeId"
                    :options="businessTypes"
                    placeholder="请选择"
                    :props="{ label: 'name', value: 'id', children: 'children' }"
                    clearable
                    :disabled="isReadonly"
                />
              </el-form-item>
            </el-form>
          </div>
        </div>

        <!-- 补录行程分区 -->
        <div class="section">
          <div class="section-header">
            <span class="section-title" @click="sections.travel = !sections.travel" style="cursor: pointer;">
              补录行程
            </span>
            <div class="section-header-actions">
              <el-button v-if="!isReadonly" type="primary" size="small" @click="openTravelDialog()">
                补录行程
              </el-button>
              <el-icon
                  class="section-icon"
                  :class="{ 'is-expanded': sections.travel }"
                  @click="sections.travel = !sections.travel"
              >
                <ArrowRight />
              </el-icon>
            </div>
          </div>
          <div class="section-content" v-show="sections.travel">
            <el-table :data="travelList" border stripe>
              <el-table-column label="序号" type="index" width="60" align="center" />
              <el-table-column label="出行人员" prop="employeeName" min-width="120" />
              <el-table-column label="出差日期" prop="dateRange" min-width="180" />
              <el-table-column label="行程" prop="route" min-width="150" />
              <el-table-column label="行程说明" prop="description" min-width="200" show-overflow-tooltip />
              <el-table-column v-if="!isReadonly" label="操作" width="150" align="center">
                <template #default="{ row, $index }">
                  <el-button type="primary" link size="small" @click="editTravel(row)">编辑</el-button>
                  <el-button type="primary" link size="small" @click="copyTravel(row)">复制</el-button>
                  <el-button type="danger" link size="small" @click="deleteTravel($index)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>

        <!-- 补助信息分区 -->
        <div class="section">
          <div class="section-header" @click="sections.subsidy = !sections.subsidy">
            <span class="section-title">补助信息</span>
            <el-icon class="section-icon" :class="{ 'is-expanded': sections.subsidy }">
              <ArrowRight />
            </el-icon>
          </div>
          <div class="section-content" v-show="sections.subsidy">
            <div class="subsidy-tip">
              <el-icon><InfoFilled /></el-icon>
              <span>
                1、请根据实际出差日期选择餐补；出差期间当日有用餐安排的请自行核减当日餐补；出差期间当日有用车的，请自行核减当日交补
              </span>
            </div>
            <el-table :data="subsidyList" border stripe>
              <el-table-column label="序号" type="index" width="60" align="center" />
              <el-table-column label="出行人" prop="employeeName" min-width="100" />
              <el-table-column label="出差日期" prop="dateRange" min-width="180" />
              <el-table-column label="补助天数" prop="subsidyDays" width="100" align="center" />
              <el-table-column label="行程" prop="route" min-width="150" />
              <el-table-column label="补助城市" prop="subsidyCity" width="120" />
              <el-table-column label="申请金额" prop="applyAmount" width="120" align="right">
                <template #default="{ row }">
                  {{ formatMoney(row.applyAmount) }}
                </template>
              </el-table-column>
              <el-table-column label="补助金额" prop="subsidyAmount" width="120" align="right">
                <template #default="{ row }">
                  {{ formatMoney(row.subsidyAmount) }}
                </template>
              </el-table-column>
              <el-table-column v-if="!isReadonly" label="操作" width="80" align="center">
                <template #default="{ row }">
                  <el-button type="primary" link size="small" @click="openSubsidyDialog(row)">
                    编辑
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>

        <!-- 费用合计分区 -->
        <div class="section">
          <div class="section-header" @click="sections.total = !sections.total">
            <span class="section-title">费用合计</span>
            <el-icon class="section-icon" :class="{ 'is-expanded': sections.total }">
              <ArrowRight />
            </el-icon>
          </div>
          <div class="section-content" v-show="sections.total">
            <div class="fee-summary">
              <div class="fee-item">
                <span class="fee-label">补助总金额：</span>
                <span class="fee-value">{{ formatMoney(feeSummary.totalAmount) }}</span>
              </div>
              <div class="fee-item">
                <span class="fee-label">餐费补助：</span>
                <span class="fee-value">{{ formatMoney(feeSummary.mealAmount) }}</span>
              </div>
              <div class="fee-item">
                <span class="fee-label">交通补助：</span>
                <span class="fee-value">{{ formatMoney(feeSummary.transportAmount) }}</span>
              </div>
              <div class="fee-item">
                <span class="fee-label">通讯补助：</span>
                <span class="fee-value">{{ formatMoney(feeSummary.communicationAmount) }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 费用归属及分摊分区 -->
        <div class="section">
          <div class="section-header" @click="sections.allocation = !sections.allocation">
            <span class="section-title">
              费用归属及分摊
              <span class="allocation-amount">(分摊金额：{{ formatMoney(allocationSummary.totalAmount) }})</span>
            </span>
            <el-icon class="section-icon" :class="{ 'is-expanded': sections.allocation }">
              <ArrowRight />
            </el-icon>
          </div>
          <div class="section-content" v-show="sections.allocation">
            <el-table :data="allocationList" border stripe>
              <el-table-column label="序号" type="index" width="60" align="center" />
              <el-table-column label="费用归属公司" min-width="200">
                <template #default="{ row, $index }">
                  <el-select v-model="row.companyId" placeholder="请选择" :disabled="isReadonly">
                    <el-option
                        v-for="company in companies"
                        :key="company.id"
                        :label="company.name"
                        :value="company.id"
                    />
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column label="项目" min-width="200">
                <template #default="{ row, $index }">
                  <el-select v-model="row.projectId" placeholder="请选择" :disabled="isReadonly">
                    <el-option
                        v-for="project in projects"
                        :key="project.id"
                        :label="project.name"
                        :value="project.id"
                    />
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column width="150" align="right">
                <template #header>
                  <span class="ratio-header">
                    <span>分摊比例</span>
                    <el-button
                        v-if="!isReadonly"
                        class="average-button"
                        type="primary"
                        link
                        :icon="Refresh"
                        @click.stop="handleAverageAllocation"
                    />
                    <span class="required-mark">*</span>
                  </span>
                </template>
                <template #default="{ row, $index }">
                  <el-input-number
                      v-model="row.ratio"
                      :min="0"
                      :max="100"
                      :precision="2"
                      :controls="false"
                      :disabled="isReadonly || $index === 0"
                      @change="handleRatioChange($index)"
                      style="width: 100%"
                  />
                  <span class="percent-sign">%</span>
                </template>
              </el-table-column>
              <el-table-column label="分摊金额" width="150" align="right">
                <template #default="{ row, $index }">
                  <span :class="{ 'first-row-readonly': $index === 0 }">
                    {{ formatMoney(row.amount) }}
                  </span>
                </template>
              </el-table-column>
              <el-table-column v-if="!isReadonly" label="操作" width="80" align="center">
                <template #default="{ $index }">
                  <el-button
                      type="danger"
                      link
                      size="small"
                      @click="deleteAllocation($index)"
                  >
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
            <div class="allocation-add">
              <el-button v-if="!isReadonly" type="primary" link size="small" @click="addAllocation">
                添加一行
              </el-button>
            </div>
            <div class="allocation-footer">
              <div class="allocation-summary">
                <span class="summary-label">合计：</span>
                <span class="summary-ratio">{{ formatPercent(allocationSummary.totalRatio) }}</span>
                <span class="summary-amount">CNY {{ formatMoney(allocationSummary.totalAmount) }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 备注信息分区 -->
        <div class="section">
          <div class="section-header">
            <span class="section-title" @click="sections.remark = !sections.remark" style="cursor: pointer;">
              备注信息
            </span>
            <div class="section-header-actions">
              <el-button v-if="!isReadonly" type="danger" link size="small" @click="deleteRemark">删除备注</el-button>
              <el-icon
                  class="section-icon"
                  :class="{ 'is-expanded': sections.remark }"
                  @click="sections.remark = !sections.remark"
              >
                <ArrowRight />
              </el-icon>
            </div>
          </div>
          <div class="section-content" v-show="sections.remark">
            <el-input
                v-model="form.remark"
                type="textarea"
                :rows="4"
                placeholder="请输入"
                maxlength="1000"
                show-word-limit
                :disabled="isReadonly"
            />
          </div>
        </div>
      </div>
    </div>

    <!-- 固定底部按钮 -->
    <div class="form-footer">
      <el-button @click="handleClose">关闭</el-button>
      <el-button v-if="!isReadonly" type="primary" @click="handleSubmit">提交</el-button>
    </div>

    <!-- 补录行程弹窗 -->
    <TravelItineraryDialog
        v-model:visible="travelDialogVisible"
        :form-data="travelFormData || {}"
        :employees="employees"
        :cities="cities"
        :is-copy="isTravelCopy"
        @save="handleSaveTravel"
    />

    <!-- 补助日历弹窗 -->
    <SubsidyCalendarDialog
        v-model:visible="subsidyDialogVisible"
        :subsidy-data="currentSubsidyData || {}"
        :business-type="form.businessTypeId"
        @save="handleSaveSubsidy"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowRight, InfoFilled, Refresh } from '@element-plus/icons-vue'
import TravelItineraryDialog from '../components/TravelItineraryDialog.vue'
import SubsidyCalendarDialog from '../components/SubsidyCalendarDialog.vue'
import { fetchLookups } from '../api/lookups'
import {
  fetchReimbursementDetail,
  saveReimbursement,
  submitReimbursement
} from '../api/reimbursements'
import type { BusinessTypeTreeItem, CityOption, NormalizedLookups, OptionItem } from '../types/reimbursement'

const router = useRouter()
const route = useRoute()
const lookupData = ref<NormalizedLookups | null>(null)
const isReadonly = computed(() => route.name === 'reimbursementDetail')

// 分区展开状态
const sections = reactive({
  basicInfo: true,
  travel: true,
  subsidy: true,
  total: true,
  allocation: true,
  remark: true
})

// 表单数据
const form = reactive({
  id: '',
  title: '',
  reason: '',
  employeeId: '',
  deptId: '',
  companyId: '',
  businessTypeId: [],
  remark: '',
  createTime: new Date()
})

// 下拉数据
const employees = ref<OptionItem[]>([
  { id: '1', name: '徐年年' },
  { id: '2', name: '张三' }
])

const departments = ref<OptionItem[]>([
  { id: '1', name: '测试部' },
  { id: '2', name: '开发部' }
])

const companies = ref<OptionItem[]>([
  { id: '1', name: '胜意科技有限公司' }
])

const businessTypes = ref<BusinessTypeTreeItem[]>([
  {
    id: '1',
    name: '日常办公',
    children: []
  },
  {
    id: '2',
    name: '项目出差',
    children: []
  }
])

const projects = ref<OptionItem[]>([
  { id: '1', name: '项目A' },
  { id: '2', name: '项目B' }
])

const cities = ref<CityOption[]>([
  { id: '1', name: '北京', type: '1' },
  { id: '2', name: '上海', type: '1' },
  { id: '3', name: '广州', type: '1' },
  { id: '4', name: '深圳', type: '1' },
  { id: '5', name: '武汉', type: '2' },
  { id: '6', name: '西安', type: '2' },
  { id: '7', name: '成都', type: '2' },
  { id: '8', name: '其他', type: '3' }
])

// 补录行程列表
const travelList = ref<any[]>([])

// 补助信息列表
const subsidyList = ref<any[]>([])

// 费用合计
const feeSummary = reactive({
  totalAmount: 0,
  mealAmount: 0,
  transportAmount: 0,
  communicationAmount: 0
})

// 费用分摊列表
const allocationList = ref<any[]>([
  {
    companyId: '',
    projectId: '',
    ratio: 100,
    amount: 0
  }
])

// 分摊合计
const allocationSummary = computed(() => {
  const totalRatio = allocationList.value.reduce((sum, item) => sum + (item.ratio || 0), 0)
  const totalAmount = allocationList.value.reduce((sum, item) => sum + (item.amount || 0), 0)
  return { totalRatio, totalAmount }
})

const applyLookups = async (): Promise<void> => {
  const data = await fetchLookups()
  lookupData.value = data
  employees.value = data.employees
  departments.value = data.departments
  companies.value = data.companies
  businessTypes.value = data.businessTypes
  projects.value = data.projects
  cities.value = data.cities
}

const applyDetailState = (state: Awaited<ReturnType<typeof fetchReimbursementDetail>>, clearId = false): void => {
  Object.assign(form, state.form)
  if (clearId) {
    form.id = ''
    form.createTime = new Date()
  }
  travelList.value = clearId
      ? state.travelList.map(item => ({ ...item, id: undefined }))
      : state.travelList
  subsidyList.value = state.subsidyList
  allocationList.value = state.allocationList
  Object.assign(feeSummary, state.feeSummary)
  calculateAllocationAmount()
}

const saveCurrentForm = async (): Promise<string> => {
  return saveReimbursement({
    form,
    feeSummary,
    travelList: travelList.value,
    allocationList: allocationList.value
  })
}

const hasDraftContent = (): boolean => {
  return Boolean(
      form.id ||
      form.title.trim() ||
      form.reason.trim() ||
      form.employeeId ||
      form.deptId ||
      form.companyId ||
      form.businessTypeId.length > 0 ||
      form.remark.trim() ||
      travelList.value.length > 0 ||
      subsidyList.value.length > 0 ||
      allocationList.value.some(item =>
          item.companyId ||
          item.projectId ||
          item.ratio !== 100 ||
          item.amount
      )
  )
}

const handleMainCompanyChange = (value: string): void => {
  if (!allocationList.value[0]) {
    allocationList.value.push({
      companyId: value,
      projectId: '',
      ratio: 100,
      amount: 0
    })
    return
  }
  allocationList.value[0].companyId = value
}

// 补录行程弹窗
const travelDialogVisible = ref(false)
const travelFormData = ref<any>(null)
const isTravelCopy = ref(false)
const editingTravelIndex = ref(-1)

// 补助日历弹窗
const subsidyDialogVisible = ref(false)
const currentSubsidyData = ref<any>(null)
const editingSubsidyIndex = ref(-1)

// 格式化日期
const formatDate = (date: any): string => {
  if (!date) return ''
  const d = new Date(date)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

// 格式化金额
const formatMoney = (amount: number): string => {
  return (amount || 0).toFixed(2)
}

// 格式化百分比
const formatPercent = (value: number): string => {
  return value.toFixed(2) + '%'
}

// 打开补录行程弹窗
const openTravelDialog = (data?: any, isCopy = false) => {
  travelFormData.value = data ? { ...data } : {
    employeeId: '',
    startDate: '',
    endDate: '',
    startCity: '',
    endCity: '',
    description: ''
  }
  isTravelCopy.value = isCopy
  editingTravelIndex.value = -1
  travelDialogVisible.value = true
}

// 编辑行程
const editTravel = (row: any) => {
  const index = travelList.value.findIndex(item => item === row)
  editingTravelIndex.value = index
  openTravelDialog(row)
}

// 复制行程
const copyTravel = (row: any) => {
  openTravelDialog(row, true)
}

// 删除行程
const deleteTravel = async (index: number) => {
  try {
    await ElMessageBox.confirm('确认删除此行程？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    travelList.value.splice(index, 1)
    // 同时删除对应的补助信息
    subsidyList.value.splice(index, 1)
    calculateFeeSummary()
    ElMessage.success('删除成功')
  } catch {
    // 取消删除
  }
}

// 保存行程
const handleSaveTravel = (data: any) => {
  // 校验人员+日期是否重复
  const isDuplicate = travelList.value.some((item, index) => {
    if (editingTravelIndex.value !== -1 && index === editingTravelIndex.value) {
      return false
    }
    return item.employeeId === data.employeeId &&
        item.startDate === data.startDate &&
        item.endDate === data.endDate
  })

  if (isDuplicate) {
    ElMessage.error('同一人员在相同日期范围内已有行程，请勿重复添加')
    return
  }

  const employee = employees.value.find(e => e.id === data.employeeId)
  const startCity = cities.value.find(c => c.id === data.startCity)
  const endCity = cities.value.find(c => c.id === data.endCity)

  const travelData = {
    ...data,
    employeeName: employee?.name || '',
    startCityName: startCity?.name || '',
    endCityName: endCity?.name || '',
    dateRange: `${data.startDate} 至 ${data.endDate}`,
    route: `${startCity?.name || ''} - ${endCity?.name || ''}`
  }

  if (editingTravelIndex.value !== -1 && !isTravelCopy.value) {
    // 编辑模式
    travelList.value[editingTravelIndex.value] = travelData
    // 更新对应的补助信息
    if (subsidyList.value[editingTravelIndex.value]) {
      subsidyList.value[editingTravelIndex.value] = {
        ...subsidyList.value[editingTravelIndex.value],
        ...travelData,
        subsidyDays: calculateDays(data.startDate, data.endDate),
        subsidyCity: endCity?.name || ''
      }
    }
  } else {
    // 新增或复制模式
    travelList.value.push(travelData)
    // 生成补助信息
    generateSubsidyInfo(travelData)
  }

  calculateFeeSummary()
  calculateAllocationAmount()
  ElMessage.success('保存成功')
}

// 计算天数
const calculateDays = (startDate: string, endDate: string): number => {
  const start = new Date(startDate)
  const end = new Date(endDate)
  const diffTime = end.getTime() - start.getTime()
  return Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1
}

// 生成补助信息
const generateSubsidyInfo = (travelData: any) => {
  const days = calculateDays(travelData.startDate, travelData.endDate)
  const startCity = cities.value.find(c => c.id === travelData.startCity)
  const endCity = cities.value.find(c => c.id === travelData.endCity)

  subsidyList.value.push({
    employeeId: travelData.employeeId,
    employeeName: travelData.employeeName,
    startDate: travelData.startDate,
    endDate: travelData.endDate,
    dateRange: travelData.dateRange,
    route: travelData.route,
    subsidyDays: days,
    startCityName: startCity?.name || '',
    endCityName: endCity?.name || '',
    subsidyCity: endCity?.name || '',
    applyAmount: 0,
    subsidyAmount: 0,
    mealAmount: 0,
    transportAmount: 0,
    communicationAmount: 0
  })
}

// 打开补助日历弹窗
const openSubsidyDialog = async (row: any) => {
  const index = subsidyList.value.findIndex(item => item === row)
  editingSubsidyIndex.value = index
  currentSubsidyData.value = { ...row }
  await nextTick()
  subsidyDialogVisible.value = true
}

// 保存补助信息
const handleSaveSubsidy = (data: any) => {
  if (editingSubsidyIndex.value !== -1) {
    subsidyList.value[editingSubsidyIndex.value] = {
      ...subsidyList.value[editingSubsidyIndex.value],
      ...data
    }
  }
  calculateFeeSummary()
  calculateAllocationAmount()
  ElMessage.success('保存成功')
}

// 计算费用合计
const calculateFeeSummary = () => {
  feeSummary.totalAmount = subsidyList.value.reduce((sum, item) => sum + (item.subsidyAmount || 0), 0)
  feeSummary.mealAmount = subsidyList.value.reduce((sum, item) => sum + (item.mealAmount || 0), 0)
  feeSummary.transportAmount = subsidyList.value.reduce((sum, item) => sum + (item.transportAmount || 0), 0)
  feeSummary.communicationAmount = subsidyList.value.reduce((sum, item) => sum + (item.communicationAmount || 0), 0)
}

// 计算分摊金额
const calculateAllocationAmount = () => {
  const totalAmount = feeSummary.totalAmount
  allocationList.value.forEach(item => {
    item.amount = totalAmount * (item.ratio || 0) / 100
  })
}

// 均摊
const handleAverageAllocation = () => {
  const count = allocationList.value.length
  if (count === 0) return

  const totalAmount = feeSummary.totalAmount
  const baseRatio = Math.floor(100 / count * 100) / 100
  const remainder = 100 - baseRatio * count

  allocationList.value.forEach((item, index) => {
    item.ratio = index === 0 ? baseRatio + remainder : baseRatio
    item.amount = totalAmount * item.ratio / 100
  })
}

// 分摊比例变化
const handleRatioChange = (index: number) => {
  if (index === 0) return

  const otherTotal = allocationList.value
      .filter((_, i) => i !== 0 && i !== index)
      .reduce((sum, item) => sum + (item.ratio || 0), 0)

  const currentRatio = allocationList.value[index].ratio || 0

  if (otherTotal + currentRatio > 100) {
    allocationList.value[index].ratio = 0
    ElMessage.warning('分摊比例总和不能超过100%')
    return
  }

  allocationList.value[0].ratio = 100 - otherTotal - currentRatio
  calculateAllocationAmount()
}

// 添加分摊行
const addAllocation = () => {
  allocationList.value.push({
    companyId: '',
    projectId: '',
    ratio: 0,
    amount: 0
  })
}

// 删除分摊行
const deleteAllocation = async (index: number) => {
  if (allocationList.value.length === 1) {
    ElMessage.warning('至少保留一条分摊信息')
    return
  }

  try {
    await ElMessageBox.confirm('确认删除此分摊信息？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    allocationList.value.splice(index, 1)

    // 重新计算第一行的比例
    if (allocationList.value.length > 0) {
      const otherTotal = allocationList.value
          .filter((_, i) => i !== 0)
          .reduce((sum, item) => sum + (item.ratio || 0), 0)
      allocationList.value[0].ratio = 100 - otherTotal
    }

    calculateAllocationAmount()
    ElMessage.success('删除成功')
  } catch {
    // 取消删除
  }
}

// 删除备注
const deleteRemark = async () => {
  try {
    await ElMessageBox.confirm('确认清空备注信息？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    form.remark = ''
    ElMessage.success('已清空备注')
  } catch {
    // 取消
  }
}

// 关闭
const handleClose = async () => {
  try {
    const message = isReadonly.value || !hasDraftContent()
        ? '确认关闭当前页面？'
        : '确认关闭当前页面？当前内容将保存为草稿'
    await ElMessageBox.confirm(message, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    if (!isReadonly.value && hasDraftContent()) {
      await saveCurrentForm()
      ElMessage.success('草稿已保存')
    }
    router.back()
  } catch {
    // 取消
  }
}

// 提交
const handleSubmit = async () => {
  // 必填校验
  if (!form.title) {
    ElMessage.error('请输入报销标题')
    return
  }
  if (!form.reason) {
    ElMessage.error('请输入出差事由')
    return
  }
  if (!form.employeeId) {
    ElMessage.error('请选择报销人')
    return
  }
  if (!form.deptId) {
    ElMessage.error('请选择报销部门')
    return
  }
  if (!form.companyId) {
    ElMessage.error('请选择费用归属公司')
    return
  }
  if (!form.businessTypeId || form.businessTypeId.length === 0) {
    ElMessage.error('请选择业务类型')
    return
  }

  // 校验补录行程
  if (travelList.value.length === 0) {
    ElMessage.error('请至少添加一条行程信息')
    return
  }

  // 校验人员+日期重复
  const duplicateCheck = travelList.value.some((item, index) => {
    return travelList.value.some((other, otherIndex) => {
      if (index === otherIndex) return false
      return item.employeeId === other.employeeId &&
          item.startDate === other.startDate &&
          item.endDate === other.endDate
    })
  })

  if (duplicateCheck) {
    ElMessage.error('存在重复的人员+日期行程，请检查')
    return
  }

  // 校验分摊比例
  const totalRatio = allocationList.value.reduce((sum, item) => sum + (item.ratio || 0), 0)
  if (Math.abs(totalRatio - 100) > 0.01) {
    ElMessage.error(`分摊比例合计为${totalRatio.toFixed(2)}%，不等于100%`)
    return
  }

  // 校验分摊金额
  const totalAllocationAmount = allocationList.value.reduce((sum, item) => sum + (item.amount || 0), 0)
  if (Math.abs(totalAllocationAmount - feeSummary.totalAmount) > 0.01) {
    ElMessage.error(`分摊金额合计为${totalAllocationAmount.toFixed(2)}，不等于补助总金额${feeSummary.totalAmount.toFixed(2)}`)
    return
  }

  const savedId = await saveCurrentForm()
  await submitReimbursement(savedId)
  ElMessage.success('提交成功')
  router.back()
}

onMounted(async () => {
  await applyLookups()
  const id = route.params.id as string
  const copyFrom = route.query.copyFrom as string | undefined
  if (copyFrom && lookupData.value) {
    const detail = await fetchReimbursementDetail(copyFrom, lookupData.value)
    applyDetailState(detail, true)
    return
  }
  if (id && id !== 'add') {
    if (lookupData.value) {
      const detail = await fetchReimbursementDetail(id, lookupData.value)
      applyDetailState(detail)
    }
  }
})
</script>

<style scoped>
.reimbursement-form-wrapper {
  width: 100vw;
  height: 100vh;
  background-color: #f0f2f5;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 固定表头 */
.form-header {
  background-color: #fff;
  padding: 16px 0;
  border-bottom: 1px solid #e8e8e8;
  flex-shrink: 0;
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
}

.header-title {
  font-size: 20px;
  font-weight: bold;
  color: #333;
}

.header-date {
  position: absolute;
  right: 0;
  font-size: 14px;
  color: #666;
}

/* 表单主体 */
.form-body {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  padding: 16px 0;
}

.form-content {
  width: 1200px;
  margin: 0 auto;
}

/* 分区样式 */
.section {
  background-color: #fff;
  margin-bottom: 12px;
  border-radius: 4px;
}

.section-header {
  height: 36px;
  background-color: #f5f7fa;
  padding: 0 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
  border-radius: 4px 4px 0 0;
}

.section-title {
  font-size: 16px;
  color: #333;
  font-weight: 500;
}

.section-header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.section-icon {
  font-size: 16px;
  color: #999;
  transition: transform 0.3s;
}

.section-icon.is-expanded {
  transform: rotate(90deg);
}

.section-content {
  padding: 16px;
}


.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.form-grid :deep(.el-form-item) {
  margin-bottom: 0;
}

.form-grid :deep(.el-input),
.form-grid :deep(.el-select),
.form-grid :deep(.el-cascader) {
  width: 100%;
}


.subsidy-tip {
  background-color: #fff7e6;
  border: 1px solid #ffe58f;
  padding: 8px 12px;
  margin-bottom: 12px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #d46b08;
}

.subsidy-tip .el-icon {
  flex-shrink: 0;
}

/* 费用合计 */
.fee-summary {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.fee-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px;
  background-color: #f9f9f9;
  border-radius: 4px;
}

.fee-label {
  font-size: 14px;
  color: #666;
}

.fee-value {
  font-size: 16px;
  color: #333;
  font-weight: 500;
}

.allocation-amount {
  font-size: 14px;
  color: #999;
  font-weight: normal;
  margin-left: 8px;
}

.ratio-header {
  display: inline-flex;
  align-items: center;
  justify-content: flex-end;
  gap: 4px;
  width: 100%;
}

.average-button {
  width: 20px;
  height: 20px;
  min-height: 20px;
  padding: 0;
  border-radius: 50%;
  background-color: #ecf5ff;
}

.average-button :deep(.el-icon) {
  font-size: 16px;
}

.required-mark {
  color: #f56c6c;
  font-weight: 600;
}

.percent-sign {
  margin-left: 4px;
  color: #999;
}

.first-row-readonly {
  color: #999;
  background-color: #f5f5f5;
  padding: 4px 8px;
  display: inline-block;
  border-radius: 4px;
}

.allocation-add {
  margin-top: 12px;
}

.allocation-footer {
  margin-top: 12px;
  padding: 12px;
  background-color: #fff7e6;
  border-radius: 4px;
}

.allocation-summary {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 24px;
  font-size: 14px;
}

.summary-label {
  color: #666;
}

.summary-ratio {
  color: #333;
  font-weight: 500;
}

.summary-amount {
  color: #fa8c16;
  font-weight: 500;
}


.form-footer {
  background-color: #fff;
  padding: 16px 0;
  border-top: 1px solid #e8e8e8;
  flex-shrink: 0;
  display: flex;
  justify-content: center;
  gap: 16px;
}


:deep(.el-table) {
  font-size: 14px;
}

:deep(.el-table th) {
  background-color: #fafafa !important;
  color: #606266;
  font-weight: normal;
}

:deep(.el-table td) {
  font-size: 14px;
}
</style>
