import api from '@/utils/request'
import { Backup, simpleUtil } from '@/api/apiConstant'

const SELECT = (param) => {
  return simpleUtil(Backup.viewName, Backup.operationType.SELECT, param)
}
const INSERT = (param) => {
  return simpleUtil(Backup.viewName, Backup.operationType.INSERT, param)
}
const DELETE = (param) => {
  return simpleUtil(Backup.viewName, Backup.operationType.DELETE, param)
}

export const hand = () => {
  const param = {
  }
  return api.simple(INSERT(param))
}

export const auto = () => {
  const param = {
    'timecycle': 3
  }
  return api.simple(INSERT(param))
}

export const listBackupFiles = () => {
  const param = {
    'pageSize': 8,
    'pageNo': 5
  }
  return api.simple(SELECT(param))
}

export const listTargetBackupFiles = () => {
  const param = {
    'pageSize': 8,
    'pageNo': 5,
    'backupType': 1,
    'startTime': '2022-08-13',
    'endTime': '2022-08-15'
  }
  return api.simple(SELECT(param))
}

export const deleteBackupFiles = () => {
  const param = {
    'bcakupDateIds': [1, 2, 3, 4]
  }
  return api.simple(DELETE(param))
}

export const listABackupFile = () => {
  const param = {
    'bcakupDateId': 1
  }
  return api.simple(DELETE(param))
}

export const restoreABackupFile = () => {
  const param = {
    'fileName': 'DataBase20220902165842.sql',
    'renewDate': [],
    'bcakupDateId': 1
  }
  return api.simple(DELETE(param))
}

export const restoreBackupFiles = () => {
  const param = {
    'fileName': 'DataBase20220902165842.sql',
    'renewDates': [],
    'bcakupDateIds': [1, 2, 3]
  }
  return api.simple(DELETE(param))
}

