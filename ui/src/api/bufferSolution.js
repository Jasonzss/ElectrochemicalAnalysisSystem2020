import api from '@/utils/request'
import { BufferSolution, simpleUtil } from '@/api/apiConstant'

const UPDATE = (param) => {
  return simpleUtil(BufferSolution.viewName, BufferSolution.operationType.UPDATE, param)
}
const SELECT = (param) => {
  return simpleUtil(BufferSolution.viewName, BufferSolution.operationType.SELECT, param)
}
const INSERT = (param) => {
  return simpleUtil(BufferSolution.viewName, BufferSolution.operationType.INSERT, param)
}
const DELETE = (param) => {
  return simpleUtil(BufferSolution.viewName, BufferSolution.operationType.DELETE, param)
}

export const insertBufferSolution = () => {
  const param = {
    'bufferSolutionName': '卡西尼亚',
    'bufferSolutionDesc': '缓冲溶液描述'
  }
  return api.simple(INSERT(param))
}

export const deleteBufferSolution = (id) => {
  const param = {
    'bufferSolutionId': id
  }
  return api.simple(DELETE(param))
}

export const updateBufferSolution = (id, typeName, desc) => {
  const param = {
    'bufferSolutionId': id,
    'bufferSolutionName': typeName,
    'bufferSolutionDesc': desc
  }
  return api.simple(UPDATE(param))
}

export const listUserBufferSolution = (pageNo, pageSize, typeName) => {
  const param = {
    'pageNo': pageNo,
    'pageSize': pageSize,
    'bufferSolutionName': typeName
  }
  return api.simple(SELECT(param))
}

