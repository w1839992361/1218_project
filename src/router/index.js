import { createRouter, createWebHistory } from 'vue-router'
import Layout from '../components/layout/index.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      component: Layout,
      redirect:'/home',
      children: [
        {
          path: 'home',
          name:'home',
          component: () => import('../views/home/index.vue'),
        },
        {
          path: 'course',
          name: 'course',
          component: () => import('../views/course/index.vue'),
        },
        {
          path: 'subject',
          name: 'subject',
          component: () => import('../views/subject/index.vue'),
        },
        {
          path: 'practice',
          name: 'practice',
          component: () => import('../views/practice/index.vue'),
        },
      ],
    },
    {
      path: '/about',
      name: 'about',
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../views/AboutView.vue'),
    },
  ],
})

export default router
