import api from '@/utils/request'
import { Log, simpleUtil } from '@/api/apiConstant'

const SELECT = (param) => {
  return simpleUtil(Log.viewName, Log.operationType.SELECT, param)
}

export const listUserLog = (pageNo, pageSize, level, userEmail, startTime, endTime) => {
  const param = {
    'userLog': [],
    'pageSize': pageSize,
    'pageNo': pageNo,
    'userEmail': userEmail,
    'startTime': startTime,
    'endTime': endTime,
    'ulogLevel': level
  }
  return api.simple(SELECT(param))
}

export const systemLog = (pageNo, pageSize, level, startTime, endTime) => {
  const param = {
    'systemLog': [],
    'pageSize': pageSize,
    'pageNo': pageNo,
    'slogLevel': level,
    'startTime': startTime,
    'endTime': endTime
  }
  return api.simple(SELECT(param))
}
