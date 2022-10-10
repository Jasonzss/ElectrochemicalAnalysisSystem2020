import api from '@/utils/request'
import { Model, simpleUtil } from '@/api/apiConstant'

const INSERT = (param) => {
  return simpleUtil(Model.viewName, Model.operationType.INSERT, param)
}

export const model = (ids, prepareAlgorithmId, modelAlgorithmId, expMaterialName) => {
  const expDataIds = []
  for (let i = 0; i < ids.length; i++) {
    expDataIds[i] = {
      'expDataId': ids[i]
    }
  }
  const param = {
    'expData': expDataIds,
    'pretreatmentAlgorithmId': prepareAlgorithmId,
    'reportDataModelId': modelAlgorithmId,
    'expMaterialName': expMaterialName
  }

  return api.simple(INSERT(param))
}
