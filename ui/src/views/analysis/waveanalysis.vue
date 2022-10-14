<template>
  <div class="app-container">
    <div v-show="!showdata">
      <div class="title"><p>添加数据以进行数据波形分析</p></div>
      <div class="formdiv">
        <el-form label-position="left" :rules="rules" :model="eddata" label-width="100px">
          <el-form-item label="数据文件">
            <el-upload
              ref="upload"
              class="upload-demo"
              action=""
              accept=".txt,.doc"
              :auto-upload="false"
              :before-upload="beforeUpload"
            >
              <el-button size="small" type="primary">点击上传</el-button>
            </el-upload>
          </el-form-item>
          <el-form-item label="物质类型名">
            <el-select v-model="eddata.type" placeholder="选择物质类型" class="mattertypename">
              <el-option v-for="(item) in inits.materialType" :label="item.materialTypeName" :value="item.materialTypeName" />
            </el-select>
            <el-button type="primary" class="newmattertype" @click="dialogtypeVisible = true">
              <i class="el-icon-plus el-icon--right" />
              新建物质类型
            </el-button>
          </el-form-item>
          <el-form-item label="物质名称" prop="name">
            <el-input v-model="eddata.name" class="mattername" />
          </el-form-item>
          <el-form-item label="物质溶度">
            <el-select v-model="eddata.solu" class="mattersolu" placeholder="0.1">
              <el-option label="0.01" value="0.01" />
              <el-option label="0.1" value="0.1" />
              <el-option label="0.11" value="0.11" />
            </el-select>
            <el-select v-model="eddata.soluunit" class="matterunit" placeholder="m">
              <el-option label="m" value="m" />
              <el-option label="um" value="um" />
            </el-select>
          </el-form-item>
          <el-form-item label="缓冲溶液">
            <el-select v-model="eddata.buffer" class="soluname" placeholder="选择缓冲溶液">
              <el-option v-for="(item) in inits.bufferSolution" :label="item.bufferSolutionName" :value="item.bufferSolutionName" />
            </el-select>
            <el-button type="primary" class="newsolu" @click="dialogsoluVisible = true">
              <i class="el-icon-plus el-icon--right" />
              新建缓冲溶液
            </el-button>
          </el-form-item>
          <el-form-item label="PH值" prop="ph">
            <el-input v-model="eddata.ph" class="ph">7.9</el-input>
          </el-form-item>
          <el-form-item label="备注">
            <el-input v-model="eddata.detail" class="detail" type="textarea" />
          </el-form-item>
          <el-form-item class="submitbutton">
            <el-button type="primary" @click="waveAnalysisUploadFile">提交</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
    <div v-show="showdata">
      <showdatas :datas="eddata" :exp-data="expData" @getbackshow="getbackshow(flag)" />
    </div>
    <!-- 新建物质类型对话框 -->
    <el-dialog title="新建物质类型" :visible.sync="dialogtypeVisible">
      <el-form :model="newtype">
        <el-form-item label="新建物质名" label-width="130px">
          <el-input v-model="newtype.name" autocomplete="off" />
        </el-form-item>
        <el-form-item label="新建物质说明" label-width="130px">
          <el-input v-model="newtype.info" type="textarea" autocomplete="off" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogtypeVisible = false">取 消</el-button>
        <el-button type="primary" @click="dialogtypeVisible = false">确 定</el-button>
      </div>
    </el-dialog>
    <!-- 新建物质类型对话框 -->
    <el-dialog title="新建缓冲溶液" :visible.sync="dialogsoluVisible">
      <el-form :model="newsolu">
        <el-form-item label="新建缓冲溶液名" label-width="180px">
          <el-input v-model="newsolu.name" autocomplete="off" />
        </el-form-item>
        <el-form-item label="新建缓冲溶液说明" label-width="180px">
          <el-input v-model="newsolu.info" type="textarea" autocomplete="off" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogsoluVisible = false">取 消</el-button>
        <el-button type="primary" @click="dialogsoluVisible = false">确 定</el-button>
      </div>
    </el-dialog>
  </div>

</template>

<script>
import showdatas from './components/showdatas.vue'
import { waveAnalysis } from '@/api/analysis'
import { listUserMaterialType } from '@/api/materialType'
import { listUserBufferSolution } from '@/api/bufferSolution'

export default {
  components: { showdatas },
  data() {
    var checkAge = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入ph'))
      } else {
        if (value > 0 && value < 14) {
          callback()
        }
        callback(new Error('请输入正确的ph'))
      }
    }
    return {
      showdata: false,
      eddata: {
        type: '',
        name: '',
        solu: '',
        soluunit: '',
        buffer: '',
        ph: '',
        detail: ''
      },
      eddatas: {},
      inits: {
        materialType: [],
        bufferSolution: []
      },
      rules: {
        name: [
          { required: true, message: '请输入物质名称', trigger: 'blur' },
          { min: 3, max: 5, message: '长度在 3 到 5 个字符', trigger: 'blur' }
        ],
        ph: [
          { validator: checkAge, trigger: 'blur' }
        ]
      },
      newtype: {
        name: '',
        info: ''
      },
      newsolu: {
        name: '',
        info: ''
      },
      dialogtypeVisible: false,
      dialogsoluVisible: false,
      expData: {}
    }
  },
  created() {
    listUserMaterialType().then(res => {
      this.inits.materialType = res.data.data
    })
    listUserBufferSolution().then(res => {
      this.inits.bufferSolution = res.data.data
    })
  },
  methods: {
    getbackshow(flag) {
      this.showdata = flag
    },
    // 对上传的文件进行波形分析
    waveAnalysisUploadFile() {
      this.$refs.upload.submit()
      this.showdata = !this.showdata
    },
    beforeUpload(file) {
      const extension = file.name.split('.')[1] === 'doc'
      const extension2 = file.name.split('.')[1] === 'txt'
      const isLt2M = file.size / 1024 / 1024 < 5
      if (!extension && !extension2) {
        this.$message.error('上传失败!  上传的文件格式只能是  .doc, .txt 格式!')
        return
      }
      if (!isLt2M) {
        this.$message.warning('上传的文件大小不能超过 5MB!')
        return
      }
      // fd.append('title', file.name)// 传其他参数
      // fd.append('file', file)// 传文件
      // 溶度单位拼接
      const unit = this.eddata.soluunit
      let value = this.eddata.solu
      if (unit === 'm') {
      } else if (unit === 'um') {
        value = value * 10E-6
      } else if (unit === 'nm') {
        value = value * 10E-9
      }
      // 转id
      const materialTypeId = this.inits.materialType.filter(k => {
        return k.materialTypeName === this.eddata.type
      })[0].materialTypeId
      const bufferSolutionId = this.inits.bufferSolution.filter(k => {
        return k.bufferSolutionName === this.eddata.buffer
      })[0].bufferSolutionId
      waveAnalysis(file, value, bufferSolutionId, this.eddata.ph, materialTypeId, this.eddata.name, this.eddata.detail).then(res => {
        this.expData = res.data.data
        // listExperimentalDataById(expId).then(res => {
        //   let expData = res.data.data
        //   if (expData.length === 1) {
        //     expData = expData[0]
        //   }
        //   this.expData = expData
        // })
      })
    }
  }
}
</script>

<style scoped>
/* 标题 */
.title {
  display: flex;
  justify-content: center;
  align-content: center;
}
.title p {
  font-size: 30px;
  margin-top: 0px;
  font-family: 'Franklin Gothic Medium', 'Arial Narrow', Arial, sans-serif;
  font-style: italic;
  font-weight: 900;
  z-index: 999;
}
/* 遮罩条 */
.title::before {
  content: '';
  background-color: rgb(22,132,252);
  width: 390px;
  position: absolute;
  top: 14%;
  left: 35%;
  transform: translateY(-50%);
  height: 10px;
  z-index: 1;
}
/* 上传文件条 */
.upload-demo {
  height: 33px;
  line-height: 43px;
  display: flex;
}
/* 表单 */
.formdiv {
  display: flex;
  justify-content: center;
  align-content: center;
  padding: 20px;
  width: 100%;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1)
}
/* 物质类型框 */
.mattertypename {
  width: 130px;
  margin-right:30px;
}
.newmattertype {
  border-radius: 50px;
}
/* 物质名称框 */
.mattername {
  width: 200px;
}
/* 物质溶度框 */
.mattersolu {
  width: 130px;
  margin-right:30px;
}
.matterunit {
  width: 80px;
  margin-right:30px;
}
/* 缓冲溶液框 */
.soluname {
  width: 230px;
  margin-right:30px;
}
.newsolu {
 border-radius: 50px;
}
/* ph */
.ph {
  width: 140px;
}
/* 提交按钮 */
.submitbutton {
  text-align: right;
}
</style>
