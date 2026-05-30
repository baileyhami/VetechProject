<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { fetchLookups } from '@/api/lookups'
import {
  createReimbursement,
  fetchReimbursementDetail,
  submitReimbursement,
  updateReimbursement
} from '@/api/reimbursements'
import type {
  ApportionForm,
  LookupResponse,
  MainForm,
  ReimbursementDetail,
  SubsidyDetailForm,
  TripForm
} from '@/types/reimbursement'

interface ApportionRow extends ApportionForm {
  ratioPercent?: number
}

const route = useRoute()
const router = useRouter()

const lookups = ref<LookupResponse>()
const loading = ref(false)
const isCopyMode = computed(() => route.query.mode === 'copy')

const form = reactive<MainForm>({
  reimbursementTitle: '',
  reimburserId: '',
  reimDepartmentId: '',
  reimCompanyId: '',
  businessTypeId: '',
  businessTripReason: '',
  remarks: ''
})

const trips = ref<TripForm[]>([])
const apportion = ref<ApportionRow[]>([])

const tripDialogVisible = ref(false)
const tripDialogIndex = ref(-1)
const tripDialogForm = reactive<TripForm>({
  travelerId: '',
  departureCityNo: '',
  arrivalCityNo: '',
  departureDate: '',
  arrivalDate: '',
  tripRemark: ''
})

const subsidyDialogVisible = ref(false)
const subsidyDialogTrip = ref<TripForm | null>(null)
const subsidyDialogDetails = ref<SubsidyDetailForm[]>([])

const subsidySummaries = computed(() => {
  return trips.value.map((trip) => buildSubsidySummary(trip))
})

const totalSubsidy = computed(() => {
  return subsidySummaries.value.reduce((sum, item) => sum + item.subsidyAmount, 0)
})

const mealTotal = computed(() => {
  return subsidySummaries.value.reduce((sum, item) => sum + item.mealAmount, 0)
})

const transportTotal = computed(() => {
  return subsidySummaries.value.reduce((sum, item) => sum + item.transportAmount, 0)
})

const phoneTotal = computed(() => {
  return subsidySummaries.value.reduce((sum, item) => sum + item.phoneAmount, 0)
})

const loadDetail = async () => {
  const id = route.params.id as string | undefined
  if (!id || id === 'new') {
    return
  }

  loading.value = true
  try {
    const detail = await fetchReimbursementDetail(id)
    form.id = detail.main.id
    form.creationTime = detail.main.creationTime
    form.status = detail.main.status
    form.reimbursementTitle = detail.main.reimbursementTitle || ''
    form.reimburserId = detail.main.reimburserId || ''
    form.reimDepartmentId = detail.main.reimDepartmentId || ''
    form.reimCompanyId = detail.main.reimCompanyId || ''
    form.businessTypeId = detail.main.businessTypeId || ''
    form.businessTripReason = detail.main.businessTripReason || ''
    form.remarks = detail.main.remarks || ''

    const subsidyDetailsBySubsidyId = new Map<string, SubsidyDetailForm[]>()
    for (const detailRow of detail.subsidyDetails || []) {
      const list = subsidyDetailsBySubsidyId.get(detailRow.subsidyId || '') || []
      list.push(detailRow)
      subsidyDetailsBySubsidyId.set(detailRow.subsidyId || '', list)
    }

    trips.value = (detail.trips || []).map((trip) => {
      const subsidy = detail.subsidies?.find((item) => item.tripId === trip.id)
      const detailList = subsidy ? subsidyDetailsBySubsidyId.get(subsidy.id) : []
      return {
        ...trip,
        departureDate: trip.departureDate,
        arrivalDate: trip.arrivalDate,
        subsidyDetails: detailList && detailList.length > 0 ? detailList : undefined
      }
    })

    if (isCopyMode.value) {
      form.id = undefined
      form.status = 0
      form.creationTime = undefined
    }

    apportion.value = (detail.apportion || []).map((row, index) => ({
      ...row,
      sortNo: row.sortNo ?? index + 1,
      ratioPercent: row.apportionRatio ? Number((row.apportionRatio * 100).toFixed(2)) : 0
    }))
  } finally {
    loading.value = false
  }
}

const resetTripDialog = () => {
  tripDialogForm.travelerId = ''
  tripDialogForm.departureCityNo = ''
  tripDialogForm.arrivalCityNo = ''
  tripDialogForm.departureDate = ''
  tripDialogForm.arrivalDate = ''
  tripDialogForm.tripRemark = ''
  tripDialogForm.subsidyDetails = undefined
}

const openTripDialog = (index?: number) => {
  resetTripDialog()
  tripDialogIndex.value = typeof index === 'number' ? index : -1
  if (typeof index === 'number') {
    const current = trips.value[index]
    Object.assign(tripDialogForm, JSON.parse(JSON.stringify(current)))
  }
  tripDialogVisible.value = true
}

const copyTrip = (index: number) => {
  const current = trips.value[index]
  const copy = JSON.parse(JSON.stringify(current)) as TripForm
  copy.id = undefined
  trips.value.push(copy)
}

const deleteTrip = async (index: number) => {
  await ElMessageBox.confirm('确认删除该行程信息吗？', '提示', { type: 'warning' })
  trips.value.splice(index, 1)
}

const saveTrip = () => {
  if (!tripDialogForm.travelerId || !tripDialogForm.departureCityNo || !tripDialogForm.arrivalCityNo) {
    ElMessage.warning('请完整填写行程信息')
    return
  }
  if (!tripDialogForm.departureDate || !tripDialogForm.arrivalDate) {
    ElMessage.warning('请选择出发和到达日期')
    return
  }

  if (new Date(tripDialogForm.arrivalDate) < new Date(tripDialogForm.departureDate)) {
    ElMessage.warning('到达日期不可早于出发日期')
    return
  }

  const target = JSON.parse(JSON.stringify(tripDialogForm)) as TripForm
  target.subsidyDetails = buildDetails(target)

  if (tripDialogIndex.value >= 0) {
    trips.value.splice(tripDialogIndex.value, 1, target)
  } else {
    trips.value.push(target)
  }

  if (hasTripOverlap()) {
    ElMessage.warning('同一出行人行程日期重叠')
    return
  }

  tripDialogVisible.value = false
}

const openSubsidyDialog = (trip: TripForm) => {
  subsidyDialogTrip.value = trip
  subsidyDialogDetails.value = (trip.subsidyDetails && trip.subsidyDetails.length > 0)
    ? JSON.parse(JSON.stringify(trip.subsidyDetails))
    : buildDetails(trip)
  subsidyDialogVisible.value = true
}

const saveSubsidyDetails = () => {
  if (subsidyDialogTrip.value) {
    subsidyDialogTrip.value.subsidyDetails = subsidyDialogDetails.value
  }
  subsidyDialogVisible.value = false
}

const addApportionRow = () => {
  apportion.value.push({
    reimCompanyId: '',
    projectId: '',
    apportionRatio: 0,
    apportionAmount: 0,
    sortNo: apportion.value.length + 1,
    ratioPercent: 0
  })
}

const removeApportionRow = async (index: number) => {
  if (apportion.value.length === 1) {
    ElMessage.warning('至少保留一条分摊信息')
    return
  }
  await ElMessageBox.confirm('确认删除当前分摊信息吗？', '提示', { type: 'warning' })
  apportion.value.splice(index, 1)
  normalizeApportion()
}

const normalizeApportion = () => {
  if (apportion.value.length === 0) {
    return
  }
  const totalPercent = apportion.value.slice(1).reduce((sum, item) => sum + (item.ratioPercent || 0), 0)
  apportion.value[0].ratioPercent = Math.max(0, 100 - totalPercent)
  apportion.value[0].apportionRatio = Number(((apportion.value[0].ratioPercent || 0) / 100).toFixed(4))

  const remainingAmount = totalSubsidy.value - apportion.value.slice(1).reduce((sum, item) => sum + (item.apportionAmount || 0), 0)
  apportion.value[0].apportionAmount = Number(remainingAmount.toFixed(2))
}

watch(
  () => totalSubsidy.value,
  () => {
    if (apportion.value.length === 0) {
      addApportionRow()
    }
    normalizeApportion()
  }
)

const updateRatio = (row: ApportionRow, value: number | undefined) => {
  row.ratioPercent = value || 0
  row.apportionRatio = Number(((row.ratioPercent || 0) / 100).toFixed(4))
  const totalPercent = apportion.value.slice(1).reduce((sum, item) => sum + (item.ratioPercent || 0), 0)
  if (totalPercent > 100) {
    row.ratioPercent = 0
    row.apportionRatio = 0
    ElMessage.warning('分摊比例合计不可超过100%')
  }
  normalizeApportion()
}

const handleRatioChange = (row: ApportionRow, value: unknown) => {
  updateRatio(row, typeof value === 'number' ? value : undefined)
}

const updateAmount = () => {
  normalizeApportion()
}

const buildPayload = (): ReimbursementDetail => {
  form.subsidyTotal = totalSubsidy.value.toFixed(2)
  form.mealAllowance = mealTotal.value.toFixed(2)
  form.transportationAllowance = transportTotal.value.toFixed(2)
  form.phoneAllowance = phoneTotal.value.toFixed(2)

  return {
    main: form,
    trips: trips.value.map((trip) => ({
      ...trip,
      subsidyDetails: trip.subsidyDetails
    })),
    apportion: apportion.value.map((row) => ({
      id: row.id,
      reimCompanyId: row.reimCompanyId,
      projectId: row.projectId,
      apportionRatio: row.apportionRatio,
      apportionAmount: row.apportionAmount,
      sortNo: row.sortNo
    })),
    subsidies: [],
    subsidyDetails: []
  }
}

const saveDraft = async () => {
  if (hasTripOverlap()) {
    ElMessage.warning('同一出行人行程日期重叠')
    return
  }
  const payload = buildPayload()
  if (form.id && !isCopyMode.value) {
    await updateReimbursement(form.id, payload)
    ElMessage.success('保存成功')
  } else {
    const result = await createReimbursement(payload)
    ElMessage.success('保存成功')
    await router.replace(`/reimbursements/${result.id}`)
  }
}

const submitDocument = async () => {
  await saveDraft()
  if (form.id) {
    await submitReimbursement(form.id)
    ElMessage.success('提交成功')
  }
}

const closePage = () => {
  router.push('/')
}

const hasTripOverlap = () => {
  const rangesByTraveler = new Map<string, { start: Date; end: Date }[]>()
  for (const trip of trips.value) {
    const start = new Date(trip.departureDate)
    const end = new Date(trip.arrivalDate)
    const list = rangesByTraveler.get(trip.travelerId) || []
    list.push({ start, end })
    rangesByTraveler.set(trip.travelerId, list)
  }
  for (const ranges of rangesByTraveler.values()) {
    ranges.sort((a, b) => a.start.getTime() - b.start.getTime())
    for (let i = 1; i < ranges.length; i++) {
      if (ranges[i].start <= ranges[i - 1].end) {
        return true
      }
    }
  }
  return false
}

const buildDetails = (trip: TripForm) => {
  const city = lookups.value?.cities.find((item) => item.no === trip.arrivalCityNo)
  const mealStd = city?.type === '1' ? 100 : city?.type === '2' ? 80 : 50
  const details: SubsidyDetailForm[] = []
  const start = new Date(trip.departureDate)
  const end = new Date(trip.arrivalDate)
  const dayMs = 24 * 60 * 60 * 1000

  for (let time = start.getTime(); time <= end.getTime(); time += dayMs) {
    const date = new Date(time)
    details.push({
      detailDate: date.toISOString().slice(0, 10),
      weekDay: ['周日', '周一', '周二', '周三', '周四', '周五', '周六'][date.getDay()],
      cityNo: trip.arrivalCityNo,
      mealStd: mealStd.toFixed(2),
      mealAmount: mealStd.toFixed(2),
      transportStd: '40.00',
      transportAmount: '40.00',
      phoneStd: '40.00',
      phoneAmount: '40.00',
      selected: true
    })
  }
  return details
}

const buildSubsidySummary = (trip: TripForm) => {
  const details = trip.subsidyDetails && trip.subsidyDetails.length > 0 ? trip.subsidyDetails : buildDetails(trip)
  let applyAmount = 0
  let subsidyAmount = 0
  let mealAmount = 0
  let transportAmount = 0
  let phoneAmount = 0

  for (const item of details) {
    if (item.selected === false) {
      continue
    }
    const mealStd = Number(item.mealStd || 0)
    const transportStd = Number(item.transportStd || 0)
    const phoneStd = Number(item.phoneStd || 0)
    applyAmount += mealStd + transportStd + phoneStd

    mealAmount += Number(item.mealAmount || 0)
    transportAmount += Number(item.transportAmount || 0)
    phoneAmount += Number(item.phoneAmount || 0)
    subsidyAmount += Number(item.mealAmount || 0) + Number(item.transportAmount || 0) + Number(item.phoneAmount || 0)
  }

  return {
    travelDateRange: `${trip.departureDate}~${trip.arrivalDate}`,
    subsidyDays: details.length,
    tripRoute: `${lookupCityName(trip.departureCityNo)}-${lookupCityName(trip.arrivalCityNo)}`,
    subsidyCityNo: trip.arrivalCityNo,
    applyAmount,
    subsidyAmount,
    mealAmount,
    transportAmount,
    phoneAmount
  }
}

const lookupCityName = (cityNo: string) => {
  const city = lookups.value?.cities.find((item) => item.no === cityNo)
  return city?.name || cityNo
}

onMounted(async () => {
  lookups.value = await fetchLookups()
  await loadDetail()
  if (apportion.value.length === 0) {
    addApportionRow()
  }
})
</script>

<template>
  <div class="detail-page" v-loading="loading">
    <div class="detail-header">
      <div class="detail-title">差旅报销单</div>
      <div class="detail-date">{{ form.creationTime || new Date().toISOString().slice(0, 10) }}</div>
    </div>

    <el-card class="section-card">
      <template #header>
        <div class="section-title">基础信息</div>
      </template>
      <el-form label-width="100px">
        <el-form-item label="报销标题" required>
          <el-input v-model="form.reimbursementTitle" maxlength="500" show-word-limit />
        </el-form-item>
        <el-form-item label="出差事由" required>
          <el-input v-model="form.businessTripReason" maxlength="500" show-word-limit />
        </el-form-item>
        <el-form-item label="报销人" required>
          <el-select v-model="form.reimburserId" filterable placeholder="请选择">
            <el-option v-for="item in lookups?.employees" :key="item.id" :label="`${item.name}(${item.no})`" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="报销部门" required>
          <el-select v-model="form.reimDepartmentId" filterable placeholder="请选择">
            <el-option v-for="item in lookups?.departments" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="费用归属公司" required>
          <el-select v-model="form.reimCompanyId" filterable placeholder="请选择">
            <el-option v-for="item in lookups?.companies" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="业务类型" required>
          <el-select v-model="form.businessTypeId" filterable placeholder="请选择">
            <el-option v-for="item in lookups?.businessTypes" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="section-card">
      <template #header>
        <div class="section-title">
          <span>补录行程</span>
          <el-button type="primary" size="small" @click="openTripDialog()">补录行程</el-button>
        </div>
      </template>
      <div class="section-tip">仅可补录未从申请单带入或未产生费用的行程信息。跨天跨城行程填写说明：出发城市-到达城市：武汉-北京；出发日期-到达日期：1号-5号；1号~5号补助按北京匹配。</div>
      <el-table :data="trips" border>
        <el-table-column prop="travelerId" label="出行人员" min-width="140">
          <template #default="scope">
            {{ lookups?.employees.find((item) => item.id === scope.row.travelerId)?.name || scope.row.travelerId }}
          </template>
        </el-table-column>
        <el-table-column label="出差日期" min-width="160">
          <template #default="scope">
            {{ scope.row.departureDate }} ~ {{ scope.row.arrivalDate }}
          </template>
        </el-table-column>
        <el-table-column label="行程" min-width="180">
          <template #default="scope">
            {{ lookupCityName(scope.row.departureCityNo) }} - {{ lookupCityName(scope.row.arrivalCityNo) }}
          </template>
        </el-table-column>
        <el-table-column prop="tripRemark" label="行程说明" min-width="200" />
        <el-table-column label="操作" min-width="180">
          <template #default="scope">
            <el-button size="small" type="primary" link @click="openTripDialog(scope.$index)">编辑</el-button>
            <el-button size="small" type="primary" link @click="copyTrip(scope.$index)">复制</el-button>
            <el-button size="small" type="primary" link @click="deleteTrip(scope.$index)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card class="section-card">
      <template #header>
        <div class="section-title">补助信息</div>
      </template>
      <div class="section-tip">1、请根据实际出差日期选择补助 2、出差期间当日有用餐安排的请自行核减当日餐补 3、出差期间当日有用车的，请自行核减当日交补</div>
      <el-table :data="subsidySummaries" border>
        <el-table-column label="出行人" min-width="120">
          <template #default="scope">
            {{ lookups?.employees.find((item) => item.id === trips[scope.$index]?.travelerId)?.name || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="travelDateRange" label="出差日期" min-width="180" />
        <el-table-column prop="subsidyDays" label="补助天数" min-width="100" />
        <el-table-column prop="tripRoute" label="行程" min-width="180" />
        <el-table-column label="补助城市" min-width="120">
          <template #default="scope">
            {{ lookupCityName(scope.row.subsidyCityNo) }}
          </template>
        </el-table-column>
        <el-table-column prop="applyAmount" label="申请金额" min-width="120" align="right" />
        <el-table-column prop="subsidyAmount" label="补助金额" min-width="120" align="right" />
        <el-table-column label="操作" min-width="100">
          <template #default="scope">
            <el-button size="small" type="primary" link @click="openSubsidyDialog(trips[scope.$index])">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card class="section-card">
      <template #header>
        <div class="section-title">费用合计</div>
      </template>
      <el-row :gutter="20">
        <el-col :span="6">
          <el-statistic title="补助总金额" :value="totalSubsidy.toFixed(2)" />
        </el-col>
        <el-col :span="6">
          <el-statistic title="餐费补助" :value="mealTotal.toFixed(2)" />
        </el-col>
        <el-col :span="6">
          <el-statistic title="交通补助" :value="transportTotal.toFixed(2)" />
        </el-col>
        <el-col :span="6">
          <el-statistic title="通讯补助" :value="phoneTotal.toFixed(2)" />
        </el-col>
      </el-row>
    </el-card>

    <el-card class="section-card">
      <template #header>
        <div class="section-title">费用归属及分摊</div>
      </template>
      <div class="section-tip">分摊金额：{{ totalSubsidy.toFixed(2) }}</div>
      <el-table :data="apportion" border>
        <el-table-column label="费用归属" min-width="200">
          <template #default="scope">
            <el-select v-model="scope.row.reimCompanyId" placeholder="请选择" filterable>
              <el-option v-for="item in lookups?.companies" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="项目" min-width="200">
          <template #default="scope">
            <el-select v-model="scope.row.projectId" placeholder="请选择" filterable>
              <el-option v-for="item in lookups?.projects" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="分摊比例(%)" min-width="160" align="right">
          <template #default="scope">
            <el-input-number
              v-if="scope.$index > 0"
              v-model="scope.row.ratioPercent"
              :min="0"
              :max="100"
              :step="0.01"
              @change="handleRatioChange(scope.row, $event)"
            />
            <span v-else>{{ scope.row.ratioPercent?.toFixed(2) }}%</span>
          </template>
        </el-table-column>
        <el-table-column label="分摊金额" min-width="160" align="right">
          <template #default="scope">
            <el-input-number
              v-if="scope.$index > 0"
              v-model="scope.row.apportionAmount"
              :min="0"
              :step="0.01"
              @change="updateAmount"
            />
            <span v-else>{{ Number(scope.row.apportionAmount || 0).toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="120">
          <template #default="scope">
            <el-button size="small" type="primary" link @click="removeApportionRow(scope.$index)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="apportion-actions">
        <el-button size="small" @click="addApportionRow">添加一行</el-button>
      </div>
    </el-card>

    <el-card class="section-card">
      <template #header>
        <div class="section-title">备注信息</div>
      </template>
      <el-input v-model="form.remarks" type="textarea" :rows="4" maxlength="1000" show-word-limit />
    </el-card>

    <div class="action-bar">
      <el-button @click="closePage">关闭</el-button>
      <el-button type="primary" @click="saveDraft">保存</el-button>
      <el-button type="success" @click="submitDocument">提交</el-button>
    </div>

    <el-dialog v-model="tripDialogVisible" title="补录行程" width="600px">
      <el-form label-width="90px">
        <el-form-item label="出行人" required>
          <el-select v-model="tripDialogForm.travelerId" filterable placeholder="请选择">
            <el-option v-for="item in lookups?.employees" :key="item.id" :label="`${item.name}(${item.no})`" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="出发城市" required>
          <el-select v-model="tripDialogForm.departureCityNo" filterable placeholder="请选择">
            <el-option v-for="item in lookups?.cities" :key="item.no" :label="item.name" :value="item.no" />
          </el-select>
        </el-form-item>
        <el-form-item label="到达城市" required>
          <el-select v-model="tripDialogForm.arrivalCityNo" filterable placeholder="请选择">
            <el-option v-for="item in lookups?.cities" :key="item.no" :label="item.name" :value="item.no" />
          </el-select>
        </el-form-item>
        <el-form-item label="出发日期" required>
          <el-date-picker v-model="tripDialogForm.departureDate" type="date" placeholder="请选择" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="到达日期" required>
          <el-date-picker v-model="tripDialogForm.arrivalDate" type="date" placeholder="请选择" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="行程说明">
          <el-input v-model="tripDialogForm.tripRemark" maxlength="500" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="tripDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveTrip">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="subsidyDialogVisible" title="补助日历" width="760px">
      <el-table :data="subsidyDialogDetails" border height="360">
        <el-table-column label="出差日期" min-width="160">
          <template #default="scope">
            <el-checkbox v-model="scope.row.selected" />
            <span>{{ scope.row.detailDate }} ({{ scope.row.weekDay }})</span>
          </template>
        </el-table-column>
        <el-table-column label="餐费补助" min-width="150" align="right">
          <template #default="scope">
            <el-input-number
              v-model="scope.row.mealAmount"
              :min="0"
              :max="Number(scope.row.mealStd || 0)"
              :disabled="scope.row.selected === false"
              :step="1"
            />
          </template>
        </el-table-column>
        <el-table-column label="交通补助" min-width="150" align="right">
          <template #default="scope">
            <el-input-number
              v-model="scope.row.transportAmount"
              :min="0"
              :max="Number(scope.row.transportStd || 0)"
              :disabled="scope.row.selected === false"
              :step="1"
            />
          </template>
        </el-table-column>
        <el-table-column label="通讯补助" min-width="150" align="right">
          <template #default="scope">
            <el-input-number
              v-model="scope.row.phoneAmount"
              :min="0"
              :max="Number(scope.row.phoneStd || 0)"
              :disabled="scope.row.selected === false"
              :step="1"
            />
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <el-button @click="subsidyDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveSubsidyDetails">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.detail-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.detail-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 4px;
}

.detail-title {
  font-size: 20px;
  font-weight: 600;
}

.detail-date {
  font-size: 14px;
  color: #6b7280;
}

.section-card {
  border-radius: 12px;
}

.section-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
  font-weight: 600;
}

.section-tip {
  font-size: 12px;
  color: #6b7280;
  margin-bottom: 12px;
}

.apportion-actions {
  margin-top: 12px;
}

.action-bar {
  position: sticky;
  bottom: 0;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  background: #f5f7fb;
  padding: 12px 0;
}
</style>

