import { createRouter, createWebHistory } from 'vue-router'
import ReimbursementView from '../views/ReimbursementView.vue'
import ReimbursementForm from '../views/ReimbursementForm.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: ReimbursementView
    },
    {
      path: '/reimbursement/add',
      name: 'reimbursementAdd',
      component: ReimbursementForm
    },
    {
      path: '/reimbursement/edit/:id',
      name: 'reimbursementEdit',
      component: ReimbursementForm
    },
    {
      path: '/reimbursement/detail/:id',
      name: 'reimbursementDetail',
      component: ReimbursementForm
    }
  ]
})

export default router
