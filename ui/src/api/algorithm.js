import api from '@/utils/request'
import { Algorithm, formData, simpleUtil } from '@/api/apiConstant'

const UPDATE = (param) => {
  return simpleUtil(Algorithm.viewName, Algorithm.operationType.UPDATE, param)
}
const SELECT = (param) => {
  return simpleUtil(Algorithm.viewName, Algorithm.operationType.SELECT, param)
}
const INSERT = (param) => {
  return simpleUtil(Algorithm.viewName, Algorithm.operationType.INSERT, param)
}
const DELETE = (param) => {
  return simpleUtil(Algorithm.viewName, Algorithm.operationType.DELETE, param)
}

export const listAlgorithm = (userEmail, pageNo, pageSize, algorithmName, algorithmType, algorithmLanguage) => {
  const param = {
    'pageSize': pageSize,
    'pageNo': pageNo,
    'userEmail': userEmail,
    'algorithmName': algorithmName,
    'algorithmType': algorithmType,
    'algorithmLanguage': algorithmLanguage
  }
  return api.simple(SELECT(param))
}

export const selectAlgorithmInfo = (pageSize, pageNo, algorithmName, algorithmType, algorithmLanguage) => {
  const param = {
    'pageSize': pageSize,
    'pageNo': pageNo,
    'algorithmName': algorithmName,
    'algorithmType': algorithmType,
    'algorithmLanguage': algorithmLanguage
  }
  return api.simple(SELECT(param))
}

export const listAlgorithmTypeFillCondition = (typeName, value) => {
  let intValue
  switch (typeName) {
    case '预处理算法':
      intValue = 0
      break
    case '数据处理算法':
      intValue = 1
      break
    case '数据模型算法':
      intValue = 2
      break
    default:
      intValue = value
      break
  }
  const param = {
    'algorithmType': intValue
  }
  return api.simple(SELECT(param))
}

export const listAlgorithmBaseInfo = (id) => {
  const param = {
    'algorithmId': id
  }
  return api.simple(SELECT(param))
}

export const insertAlgorithm = () => {
  formData.append('viewName', Algorithm.viewName)
  formData.append('operation', Algorithm.operationType.INSERT)
  formData.append('algorithmName')
  formData.append('algorithmFile')
  formData.append('algorithmLanguage')
  formData.append('algorithmType')
  formData.append('algorithmDesc')
  formData.append('algorithmStatus')
  return api.form(formData)
}

export const updateAlgorithm = () => {
  const param = {
    'userEmail': '1378799690@qq.com',
    'algorithmId': 1,
    'algorithmName': '滑动平均法',
    'algorithmDesc': '滑动平均法的简介',
    'algorithmType': 1,
    'algorithmStatus': ''
  }
  return api.simple(UPDATE(param))
}

export const deleteAlgorithm = () => {
  const param = {
    'userEmail': '1378799690@qq.com',
    'algorithm': [
      { 'algorithmId': 4 },
      { 'algorithmId': 5 }
    ]
  }
  return api.simple(DELETE(param))
}
