import { createRouter, createWebHistory,createWebHashHistory } from 'vue-router'
import Layout from '../components/layout/index.vue';
import AdminLayout from '../components/layout/admin/index.vue';

const router = createRouter({
  // history: createWebHistory(import.meta.env.BASE_URL),
  history: createWebHashHistory(import.meta.env.BASE_URL),
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
          path: 'search',
          name: 'search',
          meta: { title: '搜索' },
          component: () => import('@/views/search/index.vue'),
        },
        {
          path: '/course',
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
        {
          path: 'subject/subjectDetail',
          name: 'SubjectDetail',
          meta: { title: '拓展栏目通用' },
          component: () => import('@/views/subject/detail.vue'),
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
          path: 'column',
          name: 'Column',
          meta: { title: '栏目管理' },
          component: () => import('@/views/manage/column/index.vue'),
        },
        {
          path: 'users',
          name: 'Users',
          meta: { title: '用户管理' },
          component: () => import('@/views/manage/users/index.vue'),
        },
        {
          path: 'logs',
          name: 'Logs',
          meta: { title: '日志管理' },
          component: () => import('@/views/manage/logs/index.vue'),
        },
        {
          path: 'statistics',
          name: 'Statistics',
          meta: { title: '统计' },
          component: () => import('@/views/manage/statistics/index.vue'),
        },
        {
          path: 'dataUpdate',
          name: 'DataUpdate',
          meta: { title: '数据上传' },
          component: () => import('@/views/manage/data/index.vue'),
        },
        {
          path: 'dataGet',
          name: 'DataGet',
          meta: { title: '数据同步' },
          component: () => import('@/views/manage/getData/index.vue'),
        },
      ]
    }
  ],
})

export default router
