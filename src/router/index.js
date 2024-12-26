import { createRouter, createWebHistory } from 'vue-router'
import Layout from '../components/layout/index.vue';
import AdminLayout from '../components/layout/admin/index.vue';

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
          component: () => import('@/views/home/index.vue'),
        },
        {
          path: 'course',
          name: 'course',
          meta: { title: '课程教学' },
          component: () => import('@/views/course/index.vue'),
        },
        {
          path: 'course/detail',
          name: 'courseDetails',
          meta: { title: '学科课程详情', pid: 'course' },
          component: () => import('@/views/course/detail.vue'),
        },
        {
          path: 'course/extendDetail',
          name: 'courseExtendDetail',
          meta: { title: '拓展课程详情', pid: 'course' },
          component: () => import('@/views/course/extendDetail.vue'),
        },
        {
          path: 'subject',
          name: 'subject',
          meta: { title: '专题教育' },
          component: () => import('@/views/subject/index.vue'),
        },
      ],
    },
    {
      path: '/login',
      name: 'login',
      meta: { title: '登录' },
      component: () => import('@/views/login/index.vue'),
    },

    {
      path: '/manage',
      name: 'manage',
      redirect: '/manage/contents',
      component: AdminLayout,
      meta: { title: '后台' },
      children: [
        {
          path: 'contents',
          name: 'Contents',
          meta: { title: '内容管理' },
          component: () => import('@/views/manage/content/index.vue'),
        },
        {
          path: 'users',
          name: 'Users',
          meta: { title: '用户管理' },
          component: () => import('@/views/manage/user/index.vue'),
        },
        {
          path: 'dataUpdate',
          name: 'DataUpdate',
          meta: { title: '数据同步' },
          component: () => import('@/views/manage/data/index.vue'),
        },
      ]
    }
  ],
})

export default router
