<template>
  <el-dialog
      v-model="dialogVisible"
      title="补助日历"
      width="1200px"
      :close-on-click-modal="false"
      @close="handleClose"
  >
    <div class="subsidy-calendar-container">
      <!-- 左侧出差信息 -->
      <div class="trip-info-panel">
        <div class="trip-info-header">
          <span class="info-label">出差类型</span>
          <span class="info-value">{{ businessTypeName }}</span>
        </div>

        <div class="trip-timeline">
          <div class="timeline-item">
            <div class="timeline-label">开始日期</div>
            <div class="timeline-dot"></div>
            <div class="timeline-date">{{ subsidyData?.startDate }}</div>
          </div>

          <div class="timeline-highlight">
            <div class="highlight-content">
              <span class="highlight-label">行程天数</span>
              <span class="highlight-route">{{ routeText }}</span>
              <span class="highlight-days">{{ subsidyDays }}天</span>
            </div>
          </div>

          <div class="timeline-item">
            <div class="timeline-label">结束日期</div>
            <div class="timeline-dot"></div>
            <div class="timeline-date">{{ subsidyData?.endDate }}</div>
          </div>
        </div>

        <div class="amount-summary">
          <div class="amount-item">
            <span class="amount-label">补助金额</span>
            <span class="amount-value highlight">CNY {{ formatMoney(totalSubsidyAmount) }}</span>
          </div>
          <div class="amount-item">
            <span class="amount-label">标准总额</span>
            <span class="amount-value">CNY {{ formatMoney(totalStandardAmount) }}</span>
          </div>
          <div class="amount-item">
            <span class="amount-label">补助金额</span>
            <span class="amount-value">CNY {{ formatMoney(totalSubsidyAmount) }}</span>
          </div>
        </div>
      </div>

      <!-- 右侧补助日历 -->
      <div class="calendar-panel">
        <div class="calendar-header">
          <span class="calendar-title">出差补助</span>
          <el-checkbox v-model="selectAll" @change="handleSelectAll">全选</el-checkbox>
        </div>

        <el-table
            :data="calendarData"
            border
            stripe
            class="calendar-table"
            :row-class-name="getRowClassName"
        >
          <el-table-column label="出差日期" width="140" align="center">
            <template #default="{ row, $index }">
              <div>
                <div class="date-checkbox">
                  <el-checkbox
                      v-model="row.dateChecked"
                      @change="() => handleDateCheck($index)"
                  />
                </div>
                <div>{{ row.date }}</div>
                <div class="week-day">{{ row.weekDay }}</div>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="补助城市" prop="city" width="120" align="center">
            <template #default="{ row }">
              <div v-if="row.isPlannedTrip" class="planned-trip">{{ row.city }}</div>
              <div v-else>{{ row.city }}</div>
            </template>
          </el-table-column>

          <el-table-column label="餐费补助" width="200" align="center">
            <template #header>
              <el-checkbox
                  v-model="selectAllMeal"
                  @change="handleSelectAllMeal"
              >
                餐费补助
              </el-checkbox>
            </template>
            <template #default="{ row, $index }">
              <div class="subsidy-cell">
                <el-checkbox
                    v-model="row.mealChecked"
                    @change="() => handleSubsidyChange($index, 'meal')"
                />
                <div class="subsidy-info">
                  <div class="subsidy-standard">CNY {{ row.mealStandard }} / 天</div>
                  <el-input-number
                      v-model="row.mealAmount"
                      :min="0"
                      :max="row.mealStandard"
                      :precision="2"
                      :controls="false"
                      :disabled="!row.mealChecked"
                      size="small"
                      class="subsidy-input"
                  />
                </div>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="交通补助" width="200" align="center">
            <template #header>
              <el-checkbox
                  v-model="selectAllTransport"
                  @change="handleSelectAllTransport"
              >
                交通补助
              </el-checkbox>
            </template>
            <template #default="{ row, $index }">
              <div class="subsidy-cell">
                <el-checkbox
                    v-model="row.transportChecked"
                    @change="() => handleSubsidyChange($index, 'transport')"
                />
                <div class="subsidy-info">
                  <div class="subsidy-standard">CNY {{ row.transportStandard }} / 天</div>
                  <el-input-number
                      v-model="row.transportAmount"
                      :min="0"
                      :max="row.transportStandard"
                      :precision="2"
                      :controls="false"
                      :disabled="!row.transportChecked"
                      size="small"
                      class="subsidy-input"
                  />
                </div>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="通讯补助" width="200" align="center">
            <template #header>
              <el-checkbox
                  v-model="selectAllCommunication"
                  @change="handleSelectAllCommunication"
              >
                通讯补助
              </el-checkbox>
            </template>
            <template #default="{ row, $index }">
              <div class="subsidy-cell">
                <el-checkbox
                    v-model="row.communicationChecked"
                    @change="() => handleSubsidyChange($index, 'communication')"
                />
                <div class="subsidy-info">
                  <div class="subsidy-standard">CNY {{ row.communicationStandard }} / 天</div>
                  <el-input-number
                      v-model="row.communicationAmount"
                      :min="0"
                      :max="row.communicationStandard"
                      :precision="2"
                      :controls="false"
                      :disabled="!row.communicationChecked"
                      size="small"
                      class="subsidy-input"
                  />
                </div>
              </div>
            </template>
          </el-table-column>
        </el-table>

        <div class="subsidy-tip">
          <el-icon><InfoFilled /></el-icon>
          <span>
            1、请根据实际出差日期选择补助<br/>
            2、出差期间当日有用餐安排的请自行核减当日餐补<br/>
            3、出差期间当日有用车的,请自行核减当日交补
          </span>
        </div>
      </div>
    </div>

    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSave">确认</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { InfoFilled } from '@element-plus/icons-vue'

interface CalendarRow {
  date: string
  weekDay: string
  city: string
  mealStandard: number
  transportStandard: number
  communicationStandard: number
  dateChecked: boolean
  mealChecked: boolean
  transportChecked: boolean
  communicationChecked: boolean
  mealAmount: number
  transportAmount: number
  communicationAmount: number
  isPlannedTrip?: boolean
}

type SubsidyType = 'meal' | 'transport' | 'communication'

const subsidyFieldMap: Record<SubsidyType, {
  checked: 'mealChecked' | 'transportChecked' | 'communicationChecked'
  amount: 'mealAmount' | 'transportAmount' | 'communicationAmount'
  standard: 'mealStandard' | 'transportStandard' | 'communicationStandard'
}> = {
  meal: {
    checked: 'mealChecked',
    amount: 'mealAmount',
    standard: 'mealStandard'
  },
  transport: {
    checked: 'transportChecked',
    amount: 'transportAmount',
    standard: 'transportStandard'
  },
  communication: {
    checked: 'communicationChecked',
    amount: 'communicationAmount',
    standard: 'communicationStandard'
  }
}

interface SubsidyData {
  startDate: string
  endDate: string
  startCityName?: string
  endCityName?: string
  calendarData?: CalendarRow[]
}

interface Props {
  visible: boolean
  subsidyData: SubsidyData
  businessType: string | string[]
}

interface Emits {
  (e: 'update:visible', value: boolean): void
  (e: 'save', data: any): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const dialogVisible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
})

// 城市类型对应的补助标准
const cityStandards = {
  '1': { meal: 100, transport: 40, communication: 40 },
  '2': { meal: 80, transport: 40, communication: 40 },
  '3': { meal: 50, transport: 40, communication: 40 }
}

// 业务类型名称
const businessTypeName = computed(() => {
  if (!props.businessType) return ''
  const types: Record<string, string> = {
    '1': '日常办公',
    '2': '项目出差'
  }
  return Array.isArray(props.businessType)
      ? types[props.businessType[0]] || ''
      : types[props.businessType] || ''
})

// 行程天数
const subsidyDays = computed(() => {
  if (!props.subsidyData?.startDate || !props.subsidyData?.endDate) return 0
  const start = new Date(props.subsidyData.startDate)
  const end = new Date(props.subsidyData.endDate)
  return Math.ceil((end.getTime() - start.getTime()) / (1000 * 60 * 60 * 24)) + 1
})

// 行程文本
const routeText = computed(() => {
  return `${props.subsidyData?.startCityName || ''} - ${props.subsidyData?.endCityName || ''}`
})

// 补助城市类型
const subsidyCityType = computed(() => {
  const cityMap: Record<string, string> = {
    '北京': '1', '上海': '1', '广州': '1', '深圳': '1',
    '武汉': '2', '西安': '2', '成都': '2'
  }
  return cityMap[props.subsidyData?.endCityName || ''] || '3'
})

// 补助标准
const subsidyStandard = computed(() => {
  return cityStandards[subsidyCityType.value as keyof typeof cityStandards] || cityStandards['3']
})

// 补助日历数据
const calendarData = ref<CalendarRow[]>([])

const generateCalendarData = () => {
  if (!props.subsidyData?.startDate || !props.subsidyData?.endDate) return

  const start = new Date(props.subsidyData.startDate)
  const end = new Date(props.subsidyData.endDate)
  const days: CalendarRow[] = []

  const weekDays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']

  let currentDate = new Date(start)
  while (currentDate <= end) {
    const dateStr = currentDate.toISOString().split('T')[0]
    const weekDay = weekDays[currentDate.getDay()]

    days.push({
      date: dateStr,
      weekDay,
      city: props.subsidyData.endCityName || '',
      mealStandard: subsidyStandard.value.meal,
      transportStandard: subsidyStandard.value.transport,
      communicationStandard: subsidyStandard.value.communication,
      dateChecked: false,
      mealChecked: false,
      transportChecked: false,
      communicationChecked: false,
      mealAmount: 0,
      transportAmount: 0,
      communicationAmount: 0,
      isPlannedTrip: false
    })

    currentDate.setDate(currentDate.getDate() + 1)
  }

  calendarData.value = days

  // 如果已有数据,回显
  if (props.subsidyData.calendarData) {
    props.subsidyData.calendarData.forEach((savedRow: CalendarRow, index: number) => {
      if (calendarData.value[index]) {
        calendarData.value[index] = {
          ...calendarData.value[index],
          ...savedRow
        }
      }
    })
  }

  updateSelectAllStatus()
}

// 全选状态
const selectAll = ref(false)
const selectAllMeal = ref(false)
const selectAllTransport = ref(false)
const selectAllCommunication = ref(false)

// 计算总额
const totalSubsidyAmount = computed(() => {
  return calendarData.value.reduce((sum, row) => {
    return sum + (row.mealAmount || 0) + (row.transportAmount || 0) + (row.communicationAmount || 0)
  }, 0)
})

const totalStandardAmount = computed(() => {
  return calendarData.value.reduce((sum, row) => {
    let rowStandard = 0
    if (row.mealChecked) rowStandard += row.mealStandard || 0
    if (row.transportChecked) rowStandard += row.transportStandard || 0
    if (row.communicationChecked) rowStandard += row.communicationStandard || 0
    return sum + rowStandard
  }, 0)
})

// 格式化金额
const formatMoney = (amount: number): string => {
  return (amount || 0).toFixed(2)
}

// 获取行样式
const getRowClassName = ({ row }: { row: CalendarRow }) => {
  if (row.dateChecked) {
    return 'selected-row'
  }
  return ''
}

// 日期复选框变化(横向选中)
const handleDateCheck = (index: number) => {
  const row = calendarData.value[index]
  if (row.dateChecked) {
    // 选中日期,则该行所有补助项都选中
    row.mealChecked = true
    row.transportChecked = true
    row.communicationChecked = true
    row.mealAmount = row.mealStandard
    row.transportAmount = row.transportStandard
    row.communicationAmount = row.communicationStandard
  } else {
    // 取消日期,则该行所有补助项都取消
    row.mealChecked = false
    row.transportChecked = false
    row.communicationChecked = false
    row.mealAmount = 0
    row.transportAmount = 0
    row.communicationAmount = 0
  }
  updateSelectAllStatus()
}

// 单项补助变化(纵向选中)
const handleSubsidyChange = (index: number, type: SubsidyType) => {
  const row = calendarData.value[index]
  const fields = subsidyFieldMap[type]
  const checked = row[fields.checked]

  if (checked) {
    row[fields.amount] = row[fields.standard]
  } else {
    row[fields.amount] = 0
  }

  // 检查该行是否所有补助项都选中
  const allSubsidyChecked = row.mealChecked && row.transportChecked && row.communicationChecked
  row.dateChecked = allSubsidyChecked

  updateSelectAllStatus()
}

// 更新全选状态
const updateSelectAllStatus = () => {
  if (calendarData.value.length === 0) return

  const allDateChecked = calendarData.value.every(row => row.dateChecked)
  const allMealChecked = calendarData.value.every(row => row.mealChecked)
  const allTransportChecked = calendarData.value.every(row => row.transportChecked)
  const allCommunicationChecked = calendarData.value.every(row => row.communicationChecked)

  selectAll.value = allDateChecked && allMealChecked && allTransportChecked && allCommunicationChecked
  selectAllMeal.value = allMealChecked
  selectAllTransport.value = allTransportChecked
  selectAllCommunication.value = allCommunicationChecked
}

// 全选
const handleSelectAll = (val: boolean) => {
  calendarData.value.forEach(row => {
    row.dateChecked = val
    row.mealChecked = val
    row.transportChecked = val
    row.communicationChecked = val
    row.mealAmount = val ? row.mealStandard : 0
    row.transportAmount = val ? row.transportStandard : 0
    row.communicationAmount = val ? row.communicationStandard : 0
  })
  selectAllMeal.value = val
  selectAllTransport.value = val
  selectAllCommunication.value = val
}

// 全选餐费
const handleSelectAllMeal = (val: boolean) => {
  calendarData.value.forEach(row => {
    row.mealChecked = val
    row.mealAmount = val ? row.mealStandard : 0

    // 更新日期选中状态
    row.dateChecked = row.mealChecked && row.transportChecked && row.communicationChecked
  })
  updateSelectAllStatus()
}

// 全选交通
const handleSelectAllTransport = (val: boolean) => {
  calendarData.value.forEach(row => {
    row.transportChecked = val
    row.transportAmount = val ? row.transportStandard : 0

    // 更新日期选中状态
    row.dateChecked = row.mealChecked && row.transportChecked && row.communicationChecked
  })
  updateSelectAllStatus()
}

// 全选通讯
const handleSelectAllCommunication = (val: boolean) => {
  calendarData.value.forEach(row => {
    row.communicationChecked = val
    row.communicationAmount = val ? row.communicationStandard : 0

    // 更新日期选中状态
    row.dateChecked = row.mealChecked && row.transportChecked && row.communicationChecked
  })
  updateSelectAllStatus()
}

// 保存
const handleSave = () => {
  const result = {
    ...props.subsidyData,
    applyAmount: totalStandardAmount.value,
    subsidyAmount: totalSubsidyAmount.value,
    mealAmount: calendarData.value.reduce((sum, row) => sum + (row.mealAmount || 0), 0),
    transportAmount: calendarData.value.reduce((sum, row) => sum + (row.transportAmount || 0), 0),
    communicationAmount: calendarData.value.reduce((sum, row) => sum + (row.communicationAmount || 0), 0),
    calendarData: calendarData.value
  }

  emit('save', result)
  dialogVisible.value = false
}

// 关闭
const handleClose = () => {
  dialogVisible.value = false
}

// 监听弹窗打开
watch(() => props.visible, (newVal) => {
  if (newVal) {
    generateCalendarData()
  }
}, { flush: 'post' })
</script>

<style scoped>
.subsidy-calendar-container {
  display: flex;
  gap: 24px;
}

/* 左侧出差信息 */
.trip-info-panel {
  width: 240px;
  flex-shrink: 0;
}

.trip-info-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background-color: #f5f7fa;
  border-radius: 4px;
  margin-bottom: 16px;
}

.info-label {
  font-size: 14px;
  color: #666;
}

.info-value {
  font-size: 14px;
  color: #ff6b35;
  font-weight: 500;
}

.trip-timeline {
  padding: 0;
  background-color: #fff;
  border: 1px solid #e8e8e8;
  border-radius: 4px;
  margin-bottom: 16px;
}

.timeline-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 16px;
}

.timeline-label {
  font-size: 12px;
  color: #666;
  min-width: 60px;
}

.timeline-dot {
  width: 8px;
  height: 8px;
  background-color: #409eff;
  border-radius: 50%;
  flex-shrink: 0;
  position: relative;
}

.timeline-dot::after {
  content: '';
  position: absolute;
  width: 2px;
  height: 16px;
  background-color: #409eff;
  top: 8px;
  left: 50%;
  transform: translateX(-50%);
}

.timeline-item:last-child .timeline-dot::after {
  display: none;
}

.timeline-date {
  font-size: 12px;
  color: #333;
  font-weight: 500;
}

.timeline-highlight {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  margin: 0 16px;
  border-radius: 4px;
  padding: 8px 12px;
}

.highlight-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  color: #fff;
  gap: 12px;
}

.highlight-label {
  font-size: 12px;
  font-weight: 500;
  white-space: nowrap;
}

.highlight-route {
  font-size: 12px;
  flex: 1;
  text-align: center;
}

.highlight-days {
  font-size: 12px;
  font-weight: bold;
  white-space: nowrap;
}

.amount-summary {
  background-color: #f5f7fa;
  padding: 12px;
  border-radius: 4px;
}

.amount-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #e8e8e8;
}

.amount-item:last-child {
  border-bottom: none;
}

.amount-label {
  font-size: 14px;
  color: #666;
}

.amount-value {
  font-size: 14px;
  color: #ff6b35;
  font-weight: 500;
}

.amount-value.highlight {
  color: #ff6b35;
  font-weight: bold;
  font-size: 16px;
}

/* 右侧补助日历 */
.calendar-panel {
  flex: 1;
}

.calendar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding: 12px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.calendar-title {
  font-size: 16px;
  color: #333;
  font-weight: 500;
}

.calendar-table {
  margin-bottom: 16px;
}

/* 缩小表格行高 */
.calendar-table :deep(.el-table__row) {
  height: 22px;
}

.calendar-table :deep(.el-table__row td) {
  padding: 1px 0;
}

.calendar-table :deep(.el-table__header) {
  background-color: #fafafa;
}

/* 表头样式 */
.calendar-table :deep(.el-table__header-wrapper th) {
  color: #606266;
  font-weight: 500;
  padding: 4px 0;
}

/* 选中行样式 */
.calendar-table :deep(.selected-row) {
  background-color: #e6f7ff !important;
}

.date-checkbox {
  margin-bottom: 4px;
}

.week-day {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.planned-trip {
  color: #409eff;
  font-weight: 500;
  text-decoration: underline;
}

.subsidy-cell {
  display: flex;
  align-items: center;
  gap: 4px;
}

.subsidy-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 0;
}

.subsidy-standard {
  font-size: 10px;
  color: #ff6b35;
  line-height: 1;
}

.subsidy-input {
  width: 100%;
}

/* 输入框样式 */
.subsidy-input :deep(.el-input__wrapper) {
  box-shadow: 0 0 0 1px #dcdfe6 inset;
}

/* 输入框中的数字颜色 */
.subsidy-input :deep(.el-input__inner) {
  color: #303133;
  font-weight: 500;
  font-size: 12px;
}

/* 未选中的输入框样式 */
.subsidy-input :deep(.el-input__wrapper.is-disabled) {
  background-color: #f5f7fa;
  box-shadow: 0 0 0 1px #e4e7ed inset;
}

.subsidy-input :deep(.el-input__wrapper.is-disabled .el-input__inner) {
  color: #c0c4cc;
}

.subsidy-tip {
  background-color: #fff7e6;
  border: 1px solid #ffe58f;
  padding: 12px;
  border-radius: 4px;
  display: flex;
  align-items: flex-start;
  gap: 8px;
  font-size: 14px;
  color: #d46b08;
  line-height: 1.8;
}

.subsidy-tip .el-icon {
  flex-shrink: 0;
  margin-top: 2px;
}

/* 对话框样式 */
:deep(.el-dialog__body) {
  padding: 20px;
}

:deep(.el-dialog__header) {
  padding: 20px 20px 10px;
}

:deep(.el-dialog__title) {
  font-size: 18px;
  color: #303133;
  font-weight: 500;
}
</style>
