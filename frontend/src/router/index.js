import Vue from 'vue'
import VueRouter from 'vue-router'
import InterviewList from '../views/InterviewList.vue'
import InterviewDetail from '../views/InterviewDetail.vue'
import QuestionList from '../views/QuestionList.vue'
import QuestionDetail from '../views/QuestionDetail.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'Interviews',
    component: InterviewList
  },
  {
    path: '/interview/:id',
    name: 'InterviewDetail',
    component: InterviewDetail
  },
  {
    path: '/questions',
    name: 'Questions',
    component: QuestionList
  },
  {
    path: '/question/:id',
    name: 'QuestionDetail',
    component: QuestionDetail
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
