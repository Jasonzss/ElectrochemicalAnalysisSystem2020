import { constantRoutes, userRoutes, adminRoutes } from '@/router'

const state = {
  routes: JSON.parse(localStorage.getItem('routes'))
  // addRoutes: JSON.parse(localStorage.getItem('addRoutes'))
}

const mutations = {
  SET_ROUTES: (state, routes) => {
    // localStorage.setItem('addRoutes', JSON.stringify(routes))
    localStorage.setItem('routes', JSON.stringify(routes))
    // state.addRoutes = routes
    state.routes = routes
    // state.routes = routes
  }
}

const actions = {
  generateRoutes({ commit }, roleIdList) {
    let route
    const len = roleIdList.length
    if (roleIdList[0] === 0) {
      switch (len) {
        case 1:
          route = userRoutes
          break
        case 2:
          route = adminRoutes
          break
        case 3:
          route = constantRoutes
          break
      }
    } else if (roleIdList[0] === 1) {
      switch (len) {
        case 1:
          route = adminRoutes
          break
        case 2:
          route = constantRoutes
          break
      }
    } else if (roleIdList[0] === 2) {
      route = constantRoutes
    }
    commit('SET_ROUTES', route)
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
