<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { fetchLookups } from '@/api/lookups'
import { fetchReimbursements, voidReimbursement } from '@/api/reimbursements'
import type { LookupResponse, ReimbursementListItem } from '@/types/reimbursement'

const router = useRouter()
const loading = ref(false)
const list = ref<ReimbursementListItem[]>([])
const lookups = ref<LookupResponse>()

const totalCount = computed(() => list.value.length)

const filters = reactive({
  id: '',
  title: '',
  reason: '',
  companyId: '',
  departmentId: '',
  reimburserId: '',
  businessTypeId: ''
})

const statusLabel = (status: number) => {
  switch (status) {
    case 0:
      return '草稿'
    case 1:
      return '已完成'
    case 2:
      return '已作废'
    default:
      return '-'
  }
}

const loadList = async () => {
  loading.value = true
  try {
    list.value = await fetchReimbursements({
      id: filters.id || undefined,
      title: filters.title || undefined,
      reason: filters.reason || undefined,
      companyId: filters.companyId || undefined,
      departmentId: filters.departmentId || undefined,
      reimburserId: filters.reimburserId || undefined,
      businessTypeId: filters.businessTypeId || undefined
    })
  } finally {
    loading.value = false
  }
}

const resetFilters = () => {
  filters.id = ''
  filters.title = ''
  filters.reason = ''
  filters.companyId = ''
  filters.departmentId = ''
  filters.reimburserId = ''
  filters.businessTypeId = ''
  loadList()
}

const openDetail = (id: string) => {
  router.push(`/reimbursements/${id}`)
}

const openCreate = () => {
  router.push('/reimbursements/new')
}

const openCopy = (id: string) => {
  router.push(`/reimbursements/${id}?mode=copy`)
}

const handleVoid = async (id: string) => {
  await voidReimbursement(id)
  ElMessage.success('单据已作废')
  await loadList()
}

onMounted(async () => {
  lookups.value = await fetchLookups()
  await loadList()
})
</script>

<template>
  <div class="page-shell">
    <el-card class="page-card">
      <div class="page-header">
        <div class="page-title-group">
          <div class="page-title">报销单列表</div>
          <div class="page-subtitle">支持按条件搜索和快速查看单据状态</div>
        </div>
        <div class="page-actions">
          <div class="page-count">共 {{ totalCount }} 条</div>
          <el-button type="primary" @click="openCreate">新建报销单</el-button>
        </div>
      </div>

      <div class="filter-panel">
        <el-form class="filter-form" label-width="90px">
          <el-form-item label="报销单号">
            <el-input v-model="filters.id" placeholder="请输入报销单号" clearable />
          </el-form-item>
          <el-form-item label="标题">
            <el-input v-model="filters.title" placeholder="请输入标题" clearable />
          </el-form-item>
          <el-form-item label="事由">
            <el-input v-model="filters.reason" placeholder="请输入事由" clearable />
          </el-form-item>
          <el-form-item label="费用归属公司">
            <el-select v-model="filters.companyId" placeholder="请选择" clearable filterable>
              <el-option
                v-for="item in lookups?.companies"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="报销部门">
            <el-select v-model="filters.departmentId" placeholder="请选择" clearable filterable>
              <el-option
                v-for="item in lookups?.departments"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="报销人">
            <el-select v-model="filters.reimburserId" placeholder="请选择" clearable filterable>
              <el-option
                v-for="item in lookups?.employees"
                :key="item.id"
                :label="`${item.name}(${item.no})`"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="业务类型">
            <el-select v-model="filters.businessTypeId" placeholder="请选择" clearable filterable>
              <el-option
                v-for="item in lookups?.businessTypes"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item class="filter-actions">
            <el-button type="primary" @click="loadList">搜索</el-button>
            <el-button @click="resetFilters">清除</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table :data="list" border stripe v-loading="loading" class="list-table">
        <el-table-column label="操作" width="160" fixed="left">
          <template #default="scope">
            <el-button size="small" type="primary" link @click="openDetail(scope.row.id)">编辑</el-button>
            <el-button size="small" type="primary" link @click="openCopy(scope.row.id)">复制</el-button>
            <el-dropdown>
              <span class="dropdown-trigger">更多</span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="handleVoid(scope.row.id)">作废</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
        <el-table-column label="报销单号" min-width="180">
          <template #default="scope">
            <el-link type="primary" @click="openDetail(scope.row.id)">{{ scope.row.id }}</el-link>
          </template>
        </el-table-column>
        <el-table-column label="单据状态" min-width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : scope.row.status === 2 ? 'info' : 'warning'">
              {{ statusLabel(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="报销人" min-width="150">
          <template #default="scope">
            {{ scope.row.reimburserName }}({{ scope.row.reimburserNo }})
          </template>
        </el-table-column>
        <el-table-column label="报销部门" min-width="180">
          <template #default="scope">
            {{ scope.row.departmentName }}({{ scope.row.departmentNo }})
          </template>
        </el-table-column>
        <el-table-column prop="companyName" label="费用归属公司" min-width="160" />
        <el-table-column prop="businessTypeName" label="业务类型" min-width="140" />
        <el-table-column label="报销标题" min-width="200">
          <template #default="scope">
            <el-link type="primary" @click="openDetail(scope.row.id)">{{ scope.row.title }}</el-link>
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="报销事由" min-width="200" />
        <el-table-column prop="subsidyTotal" label="补助金额" min-width="120" align="right" />
        <el-table-column prop="creationTime" label="创建时间" min-width="180" />
      </el-table>
    </el-card>
  </div>
</template>

<style scoped>
.page-shell {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.page-card {
  border-radius: 16px;
  box-shadow: 0 12px 32px rgba(17, 24, 39, 0.08);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.page-title-group {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
}

.page-subtitle {
  font-size: 12px;
  color: #6b7280;
}

.page-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.page-count {
  font-size: 12px;
  color: #6b7280;
}

.filter-panel {
  background: #f8fafc;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 16px;
}

.filter-form {
  display: grid;
  grid-template-columns: repeat(4, minmax(180px, 1fr));
  gap: 12px 16px;
}

.filter-form :deep(.el-form-item) {
  margin-bottom: 0;
}

.filter-actions {
  display: flex;
  justify-content: flex-end;
  align-items: flex-end;
}

.list-table :deep(.el-table__header th) {
  background: #f3f4f6;
  color: #374151;
}

.dropdown-trigger {
  font-size: 12px;
  color: #409eff;
  cursor: pointer;
  margin-left: 8px;
}

@media (max-width: 1200px) {
  .filter-form {
    grid-template-columns: repeat(2, minmax(180px, 1fr));
  }
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .page-actions {
    width: 100%;
    justify-content: space-between;
  }

  .filter-form {
    grid-template-columns: 1fr;
  }
}
</style>
