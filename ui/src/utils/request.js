import { MessageBox, Message } from 'element-ui'
import store from '@/store'
import axios from 'axios'

const config = {
  baseURL: 'http://localhost:8080/ElectrochemicalAnalysisSystem2020',
  timeout: 1000,
  // withCredentials: true,
  headers: {
    'Content-Type': 'application/json'
  }
}

const api = axios.create(config)
axios.defaults.withCredentials = true
axios.interceptors.request.use(
  config => {
    if (config.method === 'get') {
      config.data = {
        unused: 0
      }
    }

    const token = store.getters.token
    if (token) {
      config.headers['Authorization'] = token
    }
    console.log(config)
    return config
  },
  error => {
    console.log(error) // for debug
    return Promise.reject(error)
  }
)

// response interceptor
axios.interceptors.response.use(
  response => {
    console.log(response)

    const res = response.data

    if (res.code !== 200 && res.code !== undefined) {
      Message({
        message: res.msg || '出现异常',
        type: 'error',
        duration: 5 * 1000
      })
      // if (res.code === 50008 || res.code === 50012 || res.code === 50014) {
      //   // to re-login
      //   MessageBox.confirm('You have been logged out, you can cancel to stay on this page, or log in again', 'Confirm logout', {
      //     confirmButtonText: 'Re-Login',
      //     cancelButtonText: 'Cancel',
      //     type: 'warning'
      //   }).then(() => {
      //     store.dispatch('/login').then(() => {
      //       location.reload()
      //     })
      //   })
      // }
      return Promise.reject(new Error(res.message || '出现异常'))
      // return response
    } else {
      if (res.msg !== '' && res.msg != null && res.msg.length !== 0) {
        Message({
          message: res.msg || '',
          type: 'success',
          duration: 3 * 1000
        })
      }
      return response
    }
  },
  error => {
    console.log('err' + error) // for debug
    Message({
      message: error.message,
      type: 'error',
      duration: 5 * 1000
    })
    return Promise.reject(error)
  }
)

// api.simpleGet = function(param) {
//   return new Promise((resolve, reject) => {
//     axios({
//       method: 'get',
//       url: config.baseURL,
//       params: param
//     }).then(response => {
//       resolve(response)
//     })
//   })
// }

api.simple = function(data) {
  return new Promise((resolve) => {
    axios({
      method: 'post',
      url: config.baseURL,
      data: data
    }).then(response => {
      resolve(response)
    })
  })
}

api.binary = function(data) {
  return new Promise((resolve) => {
    axios({
      method: 'post',
      url: config.baseURL,
      data: data,
      responseType: 'arraybuffer'
    }).then(response => {
      resolve(response)
    })
  })
}

api.download = function(data) {
  return new Promise((resolve, reject) => {
    axios({
      method: 'post',
      url: config.baseURL,
      data: data,
      responseType: 'blob'
    }).then(response => {
      resolve(response)
    })
  })
}

api.form = function(data) {
  return new Promise((resolve, reject) => {
    axios({
      method: 'post',
      url: config.baseURL,
      data: data,
      headers: {
        // 'Content-Type': 'multipart/form-data;boundary = ' + new Date().getTime()
        'Content-Type': 'multipart/form-data'
        // 'Content-Type': 'application/x-www-form-urlencoded'
      }
    }).then(response => {
      resolve(response)
    })
  })
}

export default api
