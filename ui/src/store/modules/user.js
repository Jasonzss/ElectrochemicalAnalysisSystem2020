import { logout } from '@/api/user'

const state = {
  token: localStorage.getItem('token'),
  name: localStorage.getItem('name'),
  avatar: localStorage.getItem('avatar'),
  introduction: '',
  roles: localStorage.getItem('roles'),
  userEmail: localStorage.getItem('userEmail')
}

const mutations = {
  SET_TOKEN: (state, token) => {
    localStorage.setItem('token', token)
    state.token = token
  },
  SET_INTRODUCTION: (state, introduction) => {
    state.introduction = introduction
  },
  SET_NAME: (state, name) => {
    localStorage.setItem('name', name)
    state.name = name
  },
  SET_AVATAR: (state, avatar) => {
    localStorage.setItem('avatar', avatar)
    state.avatar = avatar
  },
  SET_ROLES: (state, roles) => {
    localStorage.setItem('roles', roles)
    state.roles = roles
  },
  SET_USER_EMAIL: (state, userEmail) => {
    localStorage.setItem('userEmail', userEmail)
    state.userEmail = userEmail
  }
}

const actions = {
  saveToken({ commit }, token) {
    commit('SET_TOKEN', token)
  },
  saveUserEmail({ commit }, userEmail) {
    commit('SET_USER_EMAIL', userEmail)
  },
  saveUserName({ commit }, username) {
    commit('SET_NAME', username)
  },
  saveRoles({ commit }, roles) {
    commit('SET_ROLES', roles)
  },
  saveAvatar({ commit }, avatar) {
    commit('SET_AVATAR', avatar)
  },
  logout() {
    logout().then(res => {
      localStorage.clear()
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
