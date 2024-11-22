import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from '../views/Home.vue'
import InterviewList from '../views/InterviewList.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/interviews',
    name: 'Interviews',
    component: InterviewList
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
