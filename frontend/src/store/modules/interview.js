import axios from 'axios'

export default {
  namespaced: true,
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
    async fetchInterviews({ commit }, { page, pageSize, companyNames }) {
      try {
        const response = await axios.get('/api/interviews/search', {
          params: {
            companyNames: companyNames ? companyNames.join(',') : '',
            page: page - 1, // 后端页码从0开始
            size: pageSize
          },
          paramsSerializer: params => {
            return Object.entries(params)
              .filter(entry => entry[1] !== '')
              .map(entry => `${entry[0]}=${encodeURIComponent(entry[1])}`)
              .join('&')
          }
        })
        commit('SET_INTERVIEWS', response.data.content)
        commit('SET_TOTAL_ITEMS', response.data.totalElements)
        return {
          data: response.data.content,
          total: response.data.totalElements
        }
      } catch (error) {
        console.error('Error fetching interviews:', error)
        throw error
      }
    },

    async fetchInterviewById(_, id) {
      try {
        const response = await axios.get(`/api/interviews/${id}`)
        return response.data
      } catch (error) {
        console.error('Error fetching interview details:', error)
        throw error
      }
    }
  }
}
