<template>
  <el-dialog
    v-model="dialogVisible"
    :title="dialogTitle"
    width="700px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <div class="dialog-tip">
      <el-icon><InfoFilled /></el-icon>
      <span>仅可补录未从申请单带入或未产生费用的行程信息。</span>
    </div>

    <el-form ref="formRef" :model="draft" :rules="rules" label-width="120px" class="travel-form">
      <el-form-item label="出行人员" prop="employeeId">
        <el-select v-model="draft.employeeId" placeholder="请选择出行人员" filterable>
          <el-option v-for="emp in employees" :key="emp.id" :label="emp.name" :value="emp.id" />
        </el-select>
      </el-form-item>

      <el-form-item label="出发城市" prop="startCity">
        <el-select v-model="draft.startCity" placeholder="请选择出发城市" filterable>
          <el-option v-for="city in cities" :key="city.id" :label="city.name" :value="city.id" />
        </el-select>
      </el-form-item>

      <el-form-item label="到达城市" prop="endCity">
        <el-select v-model="draft.endCity" placeholder="请选择到达城市" filterable>
          <el-option v-for="city in cities" :key="city.id" :label="city.name" :value="city.id" />
        </el-select>
      </el-form-item>

      <el-form-item label="出发日期" prop="startDate">
        <el-date-picker
          v-model="draft.startDate"
          type="date"
          value-format="YYYY-MM-DD"
          placeholder="请选择出发日期"
          :disabled-date="disabledStartDate"
        />
      </el-form-item>

      <el-form-item label="到达日期" prop="endDate">
        <el-date-picker
          v-model="draft.endDate"
          type="date"
          value-format="YYYY-MM-DD"
          placeholder="请选择到达日期"
          :disabled-date="disabledEndDate"
        />
      </el-form-item>

      <el-form-item label="行程说明" prop="description">
        <el-input
          v-model="draft.description"
          type="textarea"
          :rows="3"
          maxlength="500"
          show-word-limit
          placeholder="请输入行程说明"
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSave">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import { InfoFilled } from '@element-plus/icons-vue'

interface TravelFormData {
  id?: string
  employeeId: string
  startCity: string
  endCity: string
  startDate: string
  endDate: string
  description: string
  employeeName?: string
  startCityName?: string
  endCityName?: string
  dateRange?: string
  route?: string
}

interface Props {
  visible: boolean
  formData: TravelFormData | null
  employees: { id: string; name: string }[]
  cities: { id: string; name: string; type?: string }[]
  isCopy: boolean
}

interface Emits {
  (e: 'update:visible', value: boolean): void
  (e: 'save', data: TravelFormData): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()
const formRef = ref<FormInstance>()

const emptyDraft = (): TravelFormData => ({
  employeeId: '',
  startCity: '',
  endCity: '',
  startDate: '',
  endDate: '',
  description: ''
})

const draft = reactive<TravelFormData>(emptyDraft())

const dialogVisible = computed({
  get: () => props.visible,
  set: value => emit('update:visible', value)
})

const dialogTitle = computed(() => {
  if (props.isCopy) return '复制行程'
  return props.formData?.id ? '编辑行程' : '补录行程'
})

const rules: FormRules = {
  employeeId: [{ required: true, message: '请选择出行人员', trigger: 'change' }],
  startCity: [{ required: true, message: '请选择出发城市', trigger: 'change' }],
  endCity: [{ required: true, message: '请选择到达城市', trigger: 'change' }],
  startDate: [{ required: true, message: '请选择出发日期', trigger: 'change' }],
  endDate: [
    { required: true, message: '请选择到达日期', trigger: 'change' },
    {
      validator: (_rule, value, callback) => {
        if (value && draft.startDate && value < draft.startDate) {
          callback(new Error('到达日期不可早于出发日期'))
          return
        }
        callback()
      },
      trigger: 'change'
    }
  ],
  description: [{ required: true, message: '请输入行程说明', trigger: 'blur' }]
}

const disabledStartDate = (time: Date): boolean => {
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  return time.getTime() > today.getTime()
}

const disabledEndDate = (time: Date): boolean => {
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  if (!draft.startDate) return time.getTime() > today.getTime()
  const startDate = new Date(draft.startDate)
  startDate.setHours(0, 0, 0, 0)
  return time.getTime() < startDate.getTime() || time.getTime() > today.getTime()
}

const applyFormData = (): void => {
  Object.assign(draft, emptyDraft(), props.formData ?? {})
  if (props.isCopy) {
    draft.id = undefined
  }
}

const handleSave = async (): Promise<void> => {
  try {
    await formRef.value?.validate()
    emit('save', { ...draft })
    dialogVisible.value = false
  } catch {
    ElMessage.error('请完善必填信息')
  }
}

const handleClose = (): void => {
  formRef.value?.resetFields()
  dialogVisible.value = false
}

watch(() => props.visible, value => {
  if (value) {
    applyFormData()
  }
})
</script>

<style scoped>
.dialog-tip {
  background: #fff7e6;
  border: 1px solid #ffe58f;
  padding: 12px;
  margin-bottom: 16px;
  border-radius: 4px;
  display: flex;
  align-items: flex-start;
  gap: 8px;
  font-size: 14px;
  color: #ad6800;
  line-height: 1.6;
}

.travel-form :deep(.el-select),
.travel-form :deep(.el-date-editor) {
  width: 100%;
}
</style>
