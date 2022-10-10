import { formatDate } from 'element-ui'

export const qs = require('qs')
export const formData = new FormData()
export const params = new URLSearchParams()

export const simpleUtil = (viewName, type, param) => {
  if (param == null || param === '') {
    param = {}
  }
  return {
    'viewName': viewName,
    'operation': type,
    'param': param
  }
}

export const User = {
  viewName: 'User',
  operationType: {
    'SELECT': 'select',
    'UPDATE': 'update',
    'INSERT': 'insert',
    'DELETE': 'delete',
    'LOGIN': 'login'
  }
}

export const MaterialType = {
  viewName: 'MaterialType',
  operationType: {
    'SELECT': 'select',
    'UPDATE': 'update',
    'INSERT': 'insert',
    'DELETE': 'delete'
  }
}

export const BufferSolution = {
  viewName: 'BufferSolution',
  operationType: {
    'SELECT': 'select',
    'UPDATE': 'update',
    'INSERT': 'insert',
    'DELETE': 'delete'
  }
}

export const Application = {
  viewName: 'Application',
  operationType: {
    'SELECT': 'select',
    'UPDATE': 'update',
    'INSERT': 'insert',
    'DELETE': 'delete'
  }
}

export const ExperimentalData = {
  viewName: 'ExperimentalData',
  operationType: {
    'SELECT': 'select',
    'UPDATE': 'update',
    'INSERT': 'insert',
    'DELETE': 'delete',
    'DOWNLOAD': 'download',
    'DISTINCT': 'distinct'
  }
}

export const ExperimentalReport = {
  viewName: 'ExperimentalReport',
  operationType: {
    'UPDATE': 'update',
    'SELECT': 'select',
    'DELETE': 'delete',
    'SELECT_IMAGE': 'selectImage'
  }
}

export const Algorithm = {
  viewName: 'Algorithm',
  operationType: {
    'SELECT': 'select',
    'UPDATE': 'update',
    'INSERT': 'insert',
    'DELETE': 'delete'
  }
}

export const Analysis = {
  viewName: 'Analysis',
  operationType: {
    'UPDATE': 'update',
    'INSERT': 'insert'
  }
}

export const Model = {
  viewName: 'Model',
  operationType: {
    'INSERT': 'insert'
  }
}

export const Log = {
  viewName: 'Log',
  operationType: {
    'INSERT': 'insert',
    'SELECT': 'select'
  }
}

export const Permission = {
  viewName: 'Permission',
  operationType: {
    'SELECT': 'select',
    'UPDATE': 'update',
    'INSERT': 'insert',
    'DELETE': 'delete'
  }
}

export const Backup = {
  viewName: 'Backup',
  operationType: {
    'SELECT': 'select',
    'INSERT': 'insert',
    'DELETE': 'delete'
  }
}

export const createDownloadData = (res, fileName, fileType = 'xls') => {
  const year = new Date().getFullYear()
  const month = new Date().getMonth() + 1
  const day = new Date().getDate()
  let hours = new Date().getHours()
  let minutes = new Date().getMinutes()
  let seconds = new Date().getSeconds()
  // 当小于 10 的是时候，在前面加 0
  if (hours < 10) {
    hours = '0' + hours
  }
  if (minutes < 10) {
    minutes = '0' + minutes
  }
  if (seconds < 10) {
    seconds = '0' + seconds
  }
  // 拼接格式化当前时间
  const time = year + '-' + month + '-' + day + ' ' + hours + ':' + minutes + ':' + seconds
  const fileTrueName = fileName + time
  const Mime = {
    txt: 'text/plain',
    doc: 'application/msword',
    docx: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
    xls: 'application/vnd.ms-excel',
    xlsx: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
  }
  const blob = new Blob([res], { type: Mime[fileType] })
  const objectUrl = URL.createObjectURL(blob)
  const a = document.createElement('a')
  document.body.appendChild(a)
  a.setAttribute('style', 'display:none')
  a.setAttribute('href', objectUrl)
  if (fileTrueName) { a.setAttribute('download', fileTrueName + '.' + fileType) }
  a.click()
  document.body.removeChild(a)
  URL.revokeObjectURL(objectUrl)
}
