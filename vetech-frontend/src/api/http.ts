import axios from 'axios'
import { ElMessage } from 'element-plus'
import type { Result } from '../types/reimbursement'

export const http = axios.create({
  baseURL: '/api',
  timeout: 10000
})

export async function requestData<T>(promise: Promise<{ data: Result<T> }>): Promise<T> {
  const response = await promise
  const result = response.data

  if (result.code !== '200') {
    const dataMessage = typeof result.data === 'string' ? result.data : ''
    const message = dataMessage || result.message || '请求失败'
    ElMessage.error(message)
    throw new Error(message)
  }

  return result.data
}
