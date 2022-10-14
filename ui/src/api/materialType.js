import api from '@/utils/request'
import { MaterialType, simpleUtil } from '@/api/apiConstant'

const UPDATE = (param) => {
  return simpleUtil(MaterialType.viewName, MaterialType.operationType.UPDATE, param)
}
const SELECT = (param) => {
  return simpleUtil(MaterialType.viewName, MaterialType.operationType.SELECT, param)
}
const INSERT = (param) => {
  return simpleUtil(MaterialType.viewName, MaterialType.operationType.INSERT, param)
}
const DELETE = (param) => {
  return simpleUtil(MaterialType.viewName, MaterialType.operationType.DELETE, param)
}

export const insertMaterialType = (typeName, desc) => {
  const param = {
    'materialTypeName': typeName,
    'materialDesc': desc
  }
  return api.simple(INSERT(param))
}

export const deleteMaterialType = (id) => {
  const param = {
    'materialTypeId': id
  }
  return api.simple(DELETE(param))
}

export const updateMaterialType = (id, typeName, desc) => {
  const param = {
    'materialTypeId': id,
    'materialTypeName': typeName,
    'materialDesc': desc
  }
  return api.simple(UPDATE(param))
}

export const listUserMaterialType = (pageNo, pageSize, typeName) => {
  const param = {
    'pageNo': pageNo,
    'pageSize': pageSize,
    'materialTypeName': typeName
  }
  return api.simple(SELECT(param))
}


