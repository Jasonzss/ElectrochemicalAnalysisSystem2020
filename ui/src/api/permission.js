import api from '@/utils/request'
import { Permission, simpleUtil } from '@/api/apiConstant'

const UPDATE = (param) => {
  return simpleUtil(Permission.viewName, Permission.operationType.UPDATE, param)
}
const SELECT = (param) => {
  return simpleUtil(Permission.viewName, Permission.operationType.SELECT, param)
}
const INSERT = (param) => {
  return simpleUtil(Permission.viewName, Permission.operationType.INSERT, param)
}

export const listUserRole = (pageNo, pageSize) => {
  const param = {
    'user': [],
    'pageSize': pageSize,
    'pageNo': pageNo
  }
  return api.simple(SELECT(param))
}

export const listRoles = () => {
  const param = {
    'roles': []
  }
  return api.simple(SELECT(param))
}

export const listRolePermission = () => {
  const param = {
    'role': []
  }
  return api.simple(SELECT(param))
}

// 查询所有权限
export const listPermission = () => {
  const param = {
    'permission': []
  }
  return api.simple(SELECT(param))
}

export const updateUserRole = () => {
  const param = {
    'userEmail': '123@qq.com',
    'roleIds': [1, 2]
  }
  return api.simple(UPDATE(param))
}

export const updateRolePermission = () => {
  const param = {
    'roleId': 26,
    'permissionIds': [1, 2, 3]
  }
  return api.simple(UPDATE(param))
}

export const insertRole = () => {
  const param = {
    'roleName': '权限管理员',
    'roleDesc': '专门管理权限的角色',
    'permissionIds': [1, 2, 3]
  }
  return api.simple(INSERT(param))
}
