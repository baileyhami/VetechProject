<template>
  <div class="reimbursement-wrapper">
    <div class="reimbursement-container">
      <div class="toolbar">
        <el-form :model="filters" class="filter-form" label-width="88px">
          <el-form-item label="报销单号">
            <el-input v-model="filters.reimNo" clearable placeholder="请输入" />
          </el-form-item>
          <el-form-item label="报销标题">
            <el-input v-model="filters.title" clearable placeholder="请输入" />
          </el-form-item>
          <el-form-item label="报销事由">
            <el-input v-model="filters.reason" clearable placeholder="请输入" />
          </el-form-item>
          <el-form-item label="归属公司">
            <el-select v-model="filters.companyId" clearable filterable placeholder="请选择">
              <el-option
                v-for="item in companies"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="报销部门">
            <el-select v-model="filters.deptId" clearable filterable placeholder="请选择">
              <el-option
                v-for="item in departments"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="报销人">
            <el-select v-model="filters.employeeId" clearable filterable placeholder="请选择">
              <el-option
                v-for="item in employees"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="业务类型">
            <el-select v-model="filters.businessTypeId" clearable filterable placeholder="请选择">
              <el-option
                v-for="item in flatBusinessTypes"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
        </el-form>

        <div class="actions">
          <el-button @click="handleAdd">新增</el-button>
          <el-button @click="resetFilters">清除</el-button>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
        </div>
      </div>

      <el-table
        v-loading="loading"
        :data="rows"
        stripe
        border
        class="reimbursement-table"
      >
        <el-table-column label="序号" width="64" align="center" fixed="left">
          <template #default="{ $index }">
            {{ (currentPage - 1) * pageSize + $index + 1 }}
          </template>
        </el-table-column>

        <el-table-column label="操作" width="180" align="center" fixed="left">
          <template #default="{ row }">
            <el-button type="primary" link :icon="View" @click="handleView(row)">查看</el-button>
            <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-dropdown>
              <el-button type="primary" link :icon="MoreFilled" />
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="handleSubmit(row)">提交</el-dropdown-item>
                  <el-dropdown-item @click="handleVoid(row)">作废</el-dropdown-item>
                  <el-dropdown-item @click="handleCopy(row)">复制</el-dropdown-item>
                  <el-dropdown-item divided @click="handleDelete(row)">删除</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>

        <el-table-column label="报销单号" prop="reimNo" min-width="150" />
        <el-table-column label="状态" prop="status" width="100" align="center">
          <template #default="{ row }">
            <span class="status-tag" :class="getStatusClass(row.status)">
              {{ row.status }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="报销人" min-width="140">
          <template #default="{ row }">
            <span>{{ formatNameWithNo(row.reimburserName, row.reimburserNo) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="报销部门" min-width="150">
          <template #default="{ row }">
            <span>{{ formatNameWithNo(row.reimburserDeptName, row.reimburserDeptNo) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="归属公司" prop="companyName" min-width="180" />
        <el-table-column label="业务类型" prop="businessType" min-width="140" />
        <el-table-column label="报销标题" prop="title" min-width="220" show-overflow-tooltip />
        <el-table-column label="报销事由" prop="reason" min-width="220" show-overflow-tooltip />
        <el-table-column label="补助金额" width="120" align="right">
          <template #default="{ row }">
            {{ formatAmount(row.subsidyAmount) }}
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime" width="120" align="center" />
      </el-table>

      <div class="pagination-wrapper">
        <div class="pagination-info">共{{ totalRows }}条 &nbsp; {{ pageSize }}条/页</div>
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="totalRows"
          layout="prev, pager, next, jumper"
          @current-change="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { Edit, MoreFilled, View } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { fetchLookups } from '../api/lookups'
import {
  deleteReimbursement,
  fetchReimbursements,
  submitReimbursement,
  voidReimbursement
} from '../api/reimbursements'
import type {
  BusinessTypeTreeItem,
  OptionItem,
  ReimbursementFilters,
  ReimbursementItem
} from '../types/reimbursement'

const router = useRouter()

const filters = reactive<ReimbursementFilters>({
  reimNo: '',
  title: '',
  reason: '',
  companyId: '',
  deptId: '',
  employeeId: '',
  businessTypeId: ''
})

const companies = ref<OptionItem[]>([])
const departments = ref<OptionItem[]>([])
const employees = ref<OptionItem[]>([])
const businessTypes = ref<BusinessTypeTreeItem[]>([])
const rows = ref<ReimbursementItem[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const totalRows = ref(0)
const loading = ref(false)

const flatBusinessTypes = computed(() => flattenBusinessTypes(businessTypes.value))

const loadLookups = async (): Promise<void> => {
  const data = await fetchLookups()
  companies.value = data.companies
  departments.value = data.departments
  employees.value = data.employees
  businessTypes.value = data.businessTypes
}

const loadList = async (): Promise<void> => {
  loading.value = true
  try {
    const data = await fetchReimbursements(filters, currentPage.value, pageSize.value)
    rows.value = data.records
    totalRows.value = data.total
  } finally {
    loading.value = false
  }
}

const handleSearch = async (): Promise<void> => {
  currentPage.value = 1
  await loadList()
}

const resetFilters = (): void => {
  filters.reimNo = ''
  filters.title = ''
  filters.reason = ''
  filters.companyId = ''
  filters.deptId = ''
  filters.employeeId = ''
  filters.businessTypeId = ''
  currentPage.value = 1
  void loadList()
}

const handleAdd = (): void => {
  router.push('/reimbursement/add')
}

const handleView = (row: ReimbursementItem): void => {
  router.push(`/reimbursement/detail/${row.id}`)
}

const handleEdit = (row: ReimbursementItem): void => {
  router.push(`/reimbursement/edit/${row.id}`)
}

const handleCopy = (row: ReimbursementItem): void => {
  router.push(`/reimbursement/add?copyFrom=${row.id}`)
}

const handleSubmit = async (row: ReimbursementItem): Promise<void> => {
  await ElMessageBox.confirm('确认提交此报销单？', '提示', { type: 'warning' })
  await submitReimbursement(row.id)
  ElMessage.success('提交成功')
  await loadList()
}

const handleVoid = async (row: ReimbursementItem): Promise<void> => {
  await ElMessageBox.confirm('确认作废此报销单？', '提示', { type: 'warning' })
  await voidReimbursement(row.id)
  ElMessage.success('作废成功')
  await loadList()
}

const handleDelete = async (row: ReimbursementItem): Promise<void> => {
  await ElMessageBox.confirm('确认删除此报销单？', '提示', { type: 'warning' })
  await deleteReimbursement(row.id)
  ElMessage.success('删除成功')
  await loadList()
}

const handlePageChange = (page: number): void => {
  currentPage.value = page
  void loadList()
}

const formatAmount = (amount: number): string => `${Number(amount || 0).toFixed(2)} CNY`

const formatNameWithNo = (name?: string, no?: string): string => {
  if (name && no) return `${name}[${no}]`
  return name || no || ''
}

const getStatusClass = (status: string): string => {
  const map: Record<string, string> = {
    草稿: 'status-draft',
    审批中: 'status-pending',
    审批通过: 'status-approved',
    已作废: 'status-void'
  }
  return map[status] || ''
}

const flattenBusinessTypes = (items: BusinessTypeTreeItem[]): OptionItem[] => {
  return items.flatMap(item => [
    { id: item.id, name: item.name },
    ...flattenBusinessTypes(item.children ?? [])
  ])
}

onMounted(async () => {
  await loadLookups()
  await loadList()
})
</script>

<style scoped>
.reimbursement-wrapper {
  width: 100vw;
  height: 100vh;
  background: #f3f5f8;
  padding: 24px;
  box-sizing: border-box;
}

.reimbursement-container {
  height: 100%;
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  padding: 18px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.toolbar {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  border-bottom: 1px solid #eef0f3;
  padding-bottom: 12px;
}

.filter-form {
  flex: 1;
  display: grid;
  grid-template-columns: repeat(4, minmax(220px, 1fr));
  gap: 12px 16px;
}

.filter-form :deep(.el-form-item) {
  margin-bottom: 0;
}

.filter-form :deep(.el-select),
.filter-form :deep(.el-input) {
  width: 100%;
}

.actions {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}

.reimbursement-table {
  flex: 1;
  min-height: 0;
}

.status-tag {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 64px;
  height: 24px;
  border-radius: 4px;
  font-size: 12px;
  background: #f4f4f5;
  color: #606266;
}

.status-draft {
  background: #f4f4f5;
  color: #606266;
}

.status-pending {
  background: #ecf5ff;
  color: #337ecc;
}

.status-approved {
  background: #f0f9eb;
  color: #529b2e;
}

.status-void {
  background: #fef0f0;
  color: #c45656;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 16px;
}

.pagination-info {
  font-size: 13px;
  color: #606266;
}

@media (max-width: 1100px) {
  .toolbar {
    flex-direction: column;
  }

  .filter-form {
    width: 100%;
    grid-template-columns: repeat(2, minmax(220px, 1fr));
  }
}
</style>
