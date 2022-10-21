<template>
  <div id="overflowAuto">
    <div v-show="!ifShow" class="getreport">
      <h2 class="title2">新建报告数据项选择</h2>
      <div class="reportform">
        <el-form>
          <el-form-item label="物质名称">
            <el-select v-model="selectValue.materialName">
              <el-option v-for="item in materialNames" :label="item" :value="item" />
            </el-select>
            <el-button type="primary" class="newsolu" @click="selectExpDatas('请选择需要进行建模分析的实验数据', '70%')">
              <i class="el-icon-plus el-icon--right" />
              选择数据
            </el-button>
          </el-form-item>
          <el-form-item label="预处理方法">
            <el-select v-model="selectValue.prepare">
              <el-option v-for="item in prepareAlgorithmNames" :label="item" :value="item" />
            </el-select>
            <el-button type="primary" class="newsolu">
              <i class="el-icon-plus el-icon--right" />
              添加算法
            </el-button>
          </el-form-item>
          <el-form-item label="数据建模方法">
            <el-select v-model="selectValue.model">
              <el-option v-for="item in modelAlgorithmNames" :label="item" :value="item" />
            </el-select>
            <el-button type="primary" class="newsolu">
              <i class="el-icon-plus el-icon--right" />
              添加算法
            </el-button>
          </el-form-item>
          <el-form-item class="submitbutton">
            <el-button type="success" icon="el-icon-check" @click="createReport">提交</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
    <div :class="reportClass"><expreport :report-data="reportData" /></div>
    <SimpleDialog :dialog="dialog" @finishSelectExpDatas="finishSelectExpDatas" />
  </div>
</template>

<script>
import expreport from './components/expreport.vue'
import { listMaterialNameFillCondition, listUserExperimentalData } from '@/api/experimentalData'
import { listAlgorithmTypeFillCondition } from '@/api/algorithm'
import { model } from '@/api/model'
import SimpleDialog from '@/views/analysis/components/SimpleDialog'
import {listReport, selectImage, selectReportInfo} from "@/api/experimentalReport";
import {arrayBufferToBase64} from "@/utils/tools";
export default {
  components: {
    SimpleDialog,
    expreport
  },
  data() {
    return {
      ruleForm: { region: false },
      ifShow: false,
      materialNames: [],
      prepareAlgorithms: [],
      modelAlgorithms: [],
      reportData: {},
      selectValue: {
        prepare: '',
        model: '',
        materialName: ''
      },
      ifSelectExpData: false,
      dialog: {
        visible: false,
        data: undefined,
        title: undefined,
        width: '50%'
      },
      selectExpDataIds: []
    }
  },
  computed: {
    reportClass() {
      return this.ifShow ? 'report_main' : 'report'
    },
    prepareAlgorithmNames() {
      return this.prepareAlgorithms.map(k => {
        return k.algorithmName
      })
    },
    modelAlgorithmNames() {
      return this.modelAlgorithms.map(k => {
        return k.algorithmName
      })
    }
  },
  async created() {
    if (this.materialNames !== null) {
      // 物质名称
      await listMaterialNameFillCondition(this.$store.getters.userEmail).then(res => {
        this.materialNames = res.data.data
      })
    }
    if (this.prepareAlgorithms !== null) {
      // 预处理方法
      await listAlgorithmTypeFillCondition(undefined, 0).then(res => {
        this.prepareAlgorithms = res.data.data
      })
    }
    if (this.modelAlgorithms != null) {
      // 建模方法
      await listAlgorithmTypeFillCondition(undefined, 2).then(res => {
        this.modelAlgorithms = res.data.data
      })
    }
  },
  methods: {
    async createReport() {
      if (this.ifSelectExpData !== true || this.selectExpDataIds == null) {
        this.$message.warning('请选择需要进行建模分析的实验数据!')
        return
      }
      if (this.selectValue.materialName === '') {
        this.$message.warning('请选择实验物质')
        return
      }
      if (this.selectValue.prepare === '') {
        this.$message.warning('请选择预处理算法')
        return
      }
      if (this.selectValue.model === '') {
        this.$message.warning('请选择建模算法')
        return
      }
      const pid = this.prepareAlgorithms.filter(k => {
        return this.selectValue.prepare === k.algorithmName
      })[0].algorithmId

      const mid = this.modelAlgorithms.filter(k => {
        return this.selectValue.model ===
          k.algorithmName
      })[0].algorithmId

      const loading = this.$loading({
        lock: true,
        text: '正在获取数据',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      })

      await model(this.selectExpDataIds, pid, mid, this.selectValue.materialName).then(async res => {
        const id = res.data.data[0]
        await selectReportInfo(id).then(res => {
          this.reportData = res.data.data[0]
        })
        // await selectImage(id, 'train').then(res => {
        //   this.reportData.trainImg = 'data:image/jpeg;base64,' + arrayBufferToBase64(res.data)
        // })
        // await selectImage(id, 'test').then(res => {
        //   this.reportData.testImg = 'data:image/jpeg;base64,' + arrayBufferToBase64(res.data)
        // })
        loading.close()
      })
      this.ifShow = !this.ifShow
    },
    async selectExpDatas(title, width) {
      if (this.selectValue.materialName === '' || this.selectValue.materialName == null) {
        this.$message.warning('请选择实验物质名称')
        return
      }
      this.dialog.title = title
      this.dialog.width = width
      if (this.dialog.data == null || this.selectValue.materialName !== this.dialog.data[0].expMaterialName) {
        const loading = this.$loading({
          lock: true,
          text: '正在获取数据',
          spinner: 'el-icon-loading',
          background: 'rgba(0, 0, 0, 0.7)'
        })
        const condition = {
          'expDeleteStatus': 0,
          'expMaterialName': this.selectValue.materialName
        }
        await listUserExperimentalData(this.$store.getters.userEmail, 1, 10, condition).then(res => {
          this.dialog.data = res.data.data.dataList[0]
        })
        loading.close()
      }
      this.dialog.visible = true
    },
    finishSelectExpDatas(ids) {
      this.selectExpDataIds = ids
      this.ifSelectExpData = true
    }
  }
}
</script>

<style scoped>
.getreport {
  width: 500px;
  height: 300px;
  position: absolute;
  background-color: #fff;
  top: 190px;
  left: 260px;
  z-index: 2;
  border-radius: 30px;
  box-shadow: 2px 2px 8px #BBBBBB,-2px -2px 8px #BBBBBB;
}
.report {
  width: 600px;
  position: absolute;
  top: 70px;
  left: 650px;
  filter: blur(1.5px);
}
.overflow {
  height: 450px;
}
.report_main {
  width: 600px;
  position: absolute;
  top: 70px;
  left: 300px;
  /*filter: blur(1.5px)*/
}
.title2 {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}
.reportform {
  margin: 10px 30px;
}
.newsolu {
  margin-left: 10px;
}
.submitbutton {
  text-align: right;
}
#overflowAuto {
  overflow-y: auto;
  position: absolute;
  width: 100%;
  height: 100%;
}
#overflowAuto::-webkit-scrollbar {
  height: 6px;
  width: 6px;
}
#overflowAuto::-webkit-scrollbar-thumb {
  background: rgb(110, 86, 138);
}
</style>
