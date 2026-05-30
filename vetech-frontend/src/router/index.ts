import { createRouter, createWebHistory } from 'vue-router'
import ReimbursementListView from '../views/ReimbursementListView.vue'
import ReimbursementFormView from '../views/ReimbursementFormView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'reimbursements',
      component: ReimbursementListView
    },
    {
      path: '/reimbursements',
      redirect: '/'
    },
    {
      path: '/reimbursements/new',
      name: 'reimbursement-create',
      component: ReimbursementFormView
    },
    {
      path: '/reimbursements/:id',
      name: 'reimbursement-detail',
      component: ReimbursementFormView
    }
  ]
})

export default router
