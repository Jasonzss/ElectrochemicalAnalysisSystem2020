import api from '@/utils/request'
import { User, simpleUtil } from '@/api/apiConstant'
const qs = require('qs')
const formData = new FormData()
const params = new URLSearchParams()

const LOGIN = (param) => {
  return simpleUtil(User.viewName, User.operationType.LOGIN, param)
}
const UPDATE = (param) => {
  return simpleUtil(User.viewName, User.operationType.UPDATE, param)
}
const SELECT = (param) => {
  return simpleUtil(User.viewName, User.operationType.SELECT, param)
}
const INSERT = (param) => {
  return simpleUtil(User.viewName, User.operationType.INSERT, param)
}
const DELETE = (param) => {
  return simpleUtil(User.viewName, User.operationType.DELETE, param)
}
/**
 * 登录
 * "param": {
 * 		"userEmail": "2538506575@qq.com",
 * 		"userPassword": "123456",
 * 		"imgAuthCode": "UUKmDS"
 * 	}
 * 	"res":{
 * 	  token: "",
 * 	}
 */
export const login = (param) => {
  return api.simple(LOGIN(param))
}
// 获取图片验证码
export const getAuthCode = () => {
  return api.binary(LOGIN())
}
// 获取邮箱验证码
export const getEmailCode = (userEmail) => {
  const param = {
    'userEmail': userEmail
  }
  return api.simple(LOGIN(param))
}
// 验证邮箱验证码
export const verifyEmailCode = (authCode) => {
  const param = {
    'authCode': authCode
  }
  return api.simple(LOGIN(param))
}
// 注册
export const register = (userEmail, userPassword, userConfirmPassword) => {
  const param = {
    'userEmail': userEmail,
    'userPassword': userPassword,
    'userConfirmPassword': userConfirmPassword
  }
  return api.simple(LOGIN(param))
}
/**
 * 获取本人基本信息
 */
export const getUserInfo = (userEmail) => {
  const param = {
    'userEmail': userEmail
  }
  return api.simple(SELECT(param))
}
/**
 * 修改本人基本信息
 */
export const updateUserBaseInfo = (userSex, userAge, userName, userTel, userEmail, userImg) => {
  formData.append('viewName', User.viewName)
  formData.append('operation', User.operationType.UPDATE)
  formData.append('userEmail', userEmail)
  formData.append('userSex', userSex)
  formData.append('userAge', userAge)
  formData.append('userName', userName)
  formData.append('userTel', userTel)
  formData.append('userImg', userImg)
  return api.form(formData)
}
/**
 * 修改本人密码前，查询原密码是否正确
 */
export const verifyUserPassword = (userPassword) => {
  const param = {
    'oldPassword': userPassword
  }
  return api.simple(SELECT(param))
}
/**
 * 修改本人密码
 */
export const updateUserPassword = (userEmail, userPassword, userConfirmPassword) => {
  formData.append('viewName', User.viewName)
  formData.append('operation', User.operationType.UPDATE)
  formData.append('userEmail', userEmail)
  formData.append('userPassword', userPassword)
  formData.append('userConfirmPassword', userConfirmPassword)
  return api.form(formData)
}

// 登出
export const logout = () => {
  const param = {

  }
  return api.simple(DELETE(param))
}

// 删除用户
export const deleteUser = (userEmails) => {
  const emails = []
  for (let i = 0; i < userEmails.length; i++) {
    emails[i] = {
      'userEmail': userEmails[i]
    }
  }
  const param = {
    'user': emails
  }
  return api.simple(DELETE(param))
}

// 查看头像
export const selectImage = (userEmail) => {
  const param = {
    'userEmail': userEmail,
    'userImg': ''
  }
  return api.binary(SELECT(param))
}

// 查询用户
export const listUser = (pageNo, pageSize) => {
  const param = {
    'pageSize': pageSize,
    'pageNo': pageNo
  }
  return api.simple(SELECT(param))
}
