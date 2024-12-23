import { createRouter, createWebHistory } from 'vue-router'
import Layout from '../components/layout/index.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      component: Layout,
      name: 'Home',
      redirect: '/home',
      meta: { title: '首页' },
      children: [
        {
          path: 'home',
          name: 'home',
          meta: { title: '首页' },
          component: () => import('../views/home/index.vue'),
        },
        {
          path: 'course',
          name: 'course',
          meta: { title: '课程教学' },
          component: () => import('../views/course/index.vue'),
        },
        {
          path: 'course/detail',
          name: 'courseDetails',
          meta: { title: '基础课程', pid: 'course' },
          component: () => import('../views/course/detail.vue'),
        },
        {
          path: 'subject',
          name: 'subject',
          meta: { title: '专题教育' },
          component: () => import('../views/subject/index.vue'),
        },
        // {
        //   path: 'practice',
        //   name: 'practice',
        //   meta: { title: '课后服务' },
        //   component: () => import('../views/practice/index.vue'),
        // },
      ],
    },
    {
      path: '/login',
      name: 'login',
      meta: { title: '登录' },
      component: () => import('../views/login/index.vue'),
    },
  ],
})

export default router
