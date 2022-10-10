import api from '@/utils/request'
import { Analysis, formData, simpleUtil } from '@/api/apiConstant'

const UPDATE = (param) => {
  return simpleUtil(Analysis.viewName, Analysis.operationType.UPDATE, param)
}

/**
 * 波形分析
 */
export const waveAnalysis = (file, expMaterialSolubility, bufferSolutionId, expPh, materialTypeId, expMaterialName, expDataDesc) => {
  formData.append('viewName', Analysis.viewName)
  formData.append('operation', Analysis.operationType.INSERT)
  formData.append('file', file)
  formData.append('expMaterialSolubility', expMaterialSolubility)
  formData.append('bufferSolutionId', bufferSolutionId)
  formData.append('expPh', expPh)
  formData.append('materialTypeId', materialTypeId)
  formData.append('expMaterialName', expMaterialName)
  formData.append('expDataDesc', expDataDesc)
  return api.form(formData)
}

// 数据处理
export const deal = (id, potential, current) => {
  const param = {
    'algorithmId': id,
    'expPotentialPointData': potential,
    'expOriginalCurrentPointData': current
  }
  return api.simple(UPDATE(param))
}

// 调点
export const point = (potential, current) => {
  const param = {
    'expPotentialPointData  ': potential,
    'expNewestCurrentPointData': current
  }
  return api.simple(UPDATE(param))
}

