import api from '@/utils/request'
import { ExperimentalReport, simpleUtil } from '@/api/apiConstant'

const UPDATE = (param) => {
  return simpleUtil(ExperimentalReport.viewName, ExperimentalReport.operationType.UPDATE, param)
}
const SELECT = (param) => {
  return simpleUtil(ExperimentalReport.viewName, ExperimentalReport.operationType.SELECT, param)
}
// const INSERT = (param) => {
//   return simpleUtil(ExperimentalReport.viewName, ExperimentalReport.operationType.INSERT, param)
// }
const DELETE = (param) => {
  return simpleUtil(ExperimentalReport.viewName, ExperimentalReport.operationType.DELETE, param)
}
const SELECT_IMAGE = (param) => {
  return simpleUtil(ExperimentalReport.viewName, ExperimentalReport.operationType.SELECT_IMAGE, param)
}

export const deleteReport = (id) => {
  const param = {
    'reportId': id
  }
  return api.simple(DELETE(param))
}

export const updateReportInfo = (id, title, desc) => {
  const param = {
    'reportId': id,
    'reportTitle': title,
    'reportDesc': desc
  }
  return api.simple(UPDATE(param))
}

export const listReport = (userEmail, pageNo, pageSize, reportTitle, reportMaterialName) => {
  const param = {
    'userEmail': userEmail,
    'pageNo': pageNo,
    'pageSize': pageSize,
    'reportTitle': reportTitle,
    'reportMaterialName': reportMaterialName
  }
  return api.simple(SELECT(param))
}

export const selectReportInfo = (id) => {
  const param = {
    'reportId': id
  }
  return api.simple(SELECT(param))
}

export const selectImage = (id, type) => {
  const param = {
    'reportId': id,
    'imageType': type // test或train
  }
  return api.binary(SELECT_IMAGE(param))
}
