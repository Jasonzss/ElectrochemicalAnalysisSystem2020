import api from '@/utils/request'
import { createDownloadData, ExperimentalData, simpleUtil } from '@/api/apiConstant'

const UPDATE = (param) => {
  return simpleUtil(ExperimentalData.viewName, ExperimentalData.operationType.UPDATE, param)
}
const SELECT = (param) => {
  return simpleUtil(ExperimentalData.viewName, ExperimentalData.operationType.SELECT, param)
}
const INSERT = (param) => {
  return simpleUtil(ExperimentalData.viewName, ExperimentalData.operationType.INSERT, param)
}
const DELETE = (param) => {
  return simpleUtil(ExperimentalData.viewName, ExperimentalData.operationType.DELETE, param)
}
const DISTINCT = (param) => {
  return simpleUtil(ExperimentalData.viewName, ExperimentalData.operationType.DISTINCT, param)
}
const DOWNLOAD = (param) => {
  return simpleUtil(ExperimentalData.viewName, ExperimentalData.operationType.DOWNLOAD, param)
}

export const insertExperimentalData = (param) => {
  return api.simple(INSERT(param))
}

export const deleteUserExperimentalData = (id, userEmail) => {
  const param = {
    'expData': [
      {
        'expDataId': id,
        'userEmail': userEmail
      }
    ]
  }
  return api.simple(DELETE(param))
}

export const deleteExperimentalData = (id) => {
  const param = {
    'expData': [
      {
        'expDataId': id
      }
    ]
  }
  return api.simple(DELETE(param))
}

// 用户的修改实验报告是否是回收站
export const updateUserExperimentalDataStatus = (id, status) => {
  // status: 0 回收站
  const expDatas = []
  for (let i = 0; i < id.length; i++) {
    expDatas[i] = {
      'expDataId': id[i],
      'expDeleteStatus': status
    }
  }
  const param = {
    'expData': expDatas
  }
  return api.simple(UPDATE(param))
}

// 用户的单条实验数据修改
export const updateUserExperimentalData = (expDataId, materialTypeId, expMaterialName
  , expMaterialSolubility, expOrginalCurrent, expOrginalPotential, expNewestCurrent, expNewestPotential, bufferSolutionId) => {
  const param = {
    'expDataId': expDataId,
    // 'materialTypeId': materialTypeId,
    'expMaterialName': expMaterialName,
    'expMaterialSolubility': expMaterialSolubility,
    'expOrginalCurrent': expOrginalCurrent,
    'expOrginalPotential': expOrginalPotential,
    'expNewestCurrent': expNewestCurrent,
    'expNewestPotential': expNewestPotential,
    'bufferSolutionId': bufferSolutionId
  }
  return api.simple(UPDATE(param))
}

// 用户的实验数据列表接口
export const listUserExperimentalData = (userEmail, pageNo, pageSize, condition) => {
  const param = {
    'userEmail': userEmail,
    'pageNo': pageNo,
    'pageSize': pageSize,
    'expDeleteStatus': condition.expDeleteStatus,
    'expLastUpdateTimeStart': condition.expLastUpdateTimeStart,
    'expLastUpdateTimeEnd': condition.expLastUpdateTimeEnd,
    'expMaterialName': condition.expMaterialName,
    'materialTypeId': condition.materialTypeId
  }
  return api.simple(SELECT(param))
}

export const listMaterialNameFillCondition = (userEmail) => {
  const param = {
    'userEmail': userEmail,
    'expMaterialName': ''
  }
  return api.simple(DISTINCT(param))
}

export const listExperimentalDataById = (id) => {
  const expDataIds = []
  for (let i = 0; i < id.length; i++) {
    expDataIds[i] = {
      'expDataId': id[i]
    }
  }
  const param = {
    'expData': expDataIds
  }
  return api.simple(SELECT(param))
}

// 用户的实验数据下载
export const downloadExperimentalData = (ids, userEmail) => {
  const datas = []
  const len = ids.length
  if (len === undefined) {
    datas[0] = {
      'userEmail': userEmail,
      'expDataId': ids
    }
  } else {
    for (let i = 0; i < len; i++) {
      datas[i] = {
        'userEmail': userEmail,
        'expDataId': ids[i]
      }
    }
  }

  const param = {
    'expData': datas
  }
  return api.download(DOWNLOAD(param))
}
