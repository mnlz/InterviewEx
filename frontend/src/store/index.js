import Vue from 'vue'
import Vuex from 'vuex'
import axios from 'axios'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    interviews: [],
    currentPage: 1,
    pageSize: 20,
    totalItems: 0
  },
  mutations: {
    SET_INTERVIEWS(state, interviews) {
      state.interviews = interviews
    },
    SET_TOTAL_ITEMS(state, total) {
      state.totalItems = total
    },
    SET_CURRENT_PAGE(state, page) {
      state.currentPage = page
    }
  },
  actions: {
    async fetchInterviews({ commit, state }, { company = '' } = {}) {
      try {
        const response = await axios.get(`/api/interviews/search`, {
          params: {
            company,
            page: state.currentPage - 1,
            size: state.pageSize
          }
        })
        commit('SET_INTERVIEWS', response.data.content)
        commit('SET_TOTAL_ITEMS', response.data.totalElements)
      } catch (error) {
        console.error('Error fetching interviews:', error)
      }
    },
    async createInterview({ dispatch }, interview) {
      try {
        await axios.post('/api/interviews', interview)
        dispatch('fetchInterviews')
      } catch (error) {
        console.error('Error creating interview:', error)
      }
    },
    async updateInterview({ dispatch }, { id, interview }) {
      try {
        await axios.put(`/api/interviews/${id}`, interview)
        dispatch('fetchInterviews')
      } catch (error) {
        console.error('Error updating interview:', error)
      }
    },
    async deleteInterview({ dispatch }, id) {
      try {
        await axios.delete(`/api/interviews/${id}`)
        dispatch('fetchInterviews')
      } catch (error) {
        console.error('Error deleting interview:', error)
      }
    }
  }
})
