import api from '@/utils/request'
import { Application, simpleUtil } from '@/api/apiConstant'

const UPDATE = (param) => {
  return simpleUtil(Application.viewName, Application.operationType.UPDATE, param)
}
const SELECT = (param) => {
  return simpleUtil(Application.viewName, Application.operationType.SELECT, param)
}
const INSERT = (param) => {
  return simpleUtil(Application.viewName, Application.operationType.INSERT, param)
}
const DELETE = (param) => {
  return simpleUtil(Application.viewName, Application.operationType.DELETE, param)
}

export const unlockApplication = () => {
  const param = {
    'userEmail': '2538506575@qq.com',
    'applicationType': 0,
    'applicationContent': '咕噜咕噜'
  }
  return api.simple(INSERT(param))
}

export const noUnlockApplication = () => {
  const param = {
    'userEmail': '2538506575@qq.com',
    'applicationType': 2, // 1:物质类型申请 //算法申请 //3.缓冲溶液申请
    'applicationContent': {
      // 分别放入对应的填充的数据
      // 如果是appicationType=1,物质类型数据
      // "materialTypeName":"化学",
      // "materialDesc":"描述"
      // 如果是applicationType=2,算法数据
      'algorithmId': 1
      // 如果是appicationType=3,缓冲溶液数据
      // "bufferSolutionName":"缓冲溶液1",
      // "bufferSolutionDesc":"描述"
    }
  }
}

export const deleteApplication = (id) => {
  const param = {
    'applicationId': id
  }
  return api.simple(DELETE(param))
}

export const updateUnlockApplicationInfo = () => {
  const param = {
    'userEmail': '2538506575@qq.com',
    'applicationId': 1,
    'applicationContent': '修改解封申请成功!'
  }
  return api.simple(UPDATE(param))
}

export const updateNoUnlockApplication = () => {
  const param = {
    'userEmail': '2538506575@qq.com',
    'applicationId': 1,
    'applicationContent': {
      // 分别放入对应的填充的数据
      // 如果是appicationType=1,物质类型数据
      // "materialTypeName":"化学",
      // "materialTypeDesc":"描述"
      // 如果是applicationType=2,算法数据
      // "algorithmId":1,
      // 如果是appicationType=3,缓冲溶液数据
      'bufferSolutionName': '缓冲溶液1',
      'bufferSolutionDesc': '修改非解封申请成功'
    }
  }
  return api.simple(UPDATE(param))
}

export const approveApplication = () => {
  const param = {
    'applicationId': 6,
    'applicationStatus': 1
  }
  return api.simple(UPDATE(param))
}

export const rejectApplication = () => {
  const param = {
    'applicationId': 1,
    'applicationStatus': 2,
    'applicationRejectReason': '拒绝原因'
  }
  return api.simple(UPDATE(param))
}

// 用户的申请通知信息
export const listUserApplication = (userEmail, pageNo, pageSize, status, type) => {
  const param = {
    'userEmail': userEmail,
    'pageNo': pageNo,
    'pageSize': pageSize,
    'applicationType': type,
    'applicationStatus': status
    // "beginTime": "2022-8-13",
    // "endTime": "2022-8-14"
  }
  return api.simple(SELECT(param))
}

