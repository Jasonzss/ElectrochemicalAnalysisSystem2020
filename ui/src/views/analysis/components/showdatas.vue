<template>
  <div class="body">
    <div class="tabletop">
      <div class="toptext"><span class="arrow">></span>实验对象</div>
      <div class="table">
        <table>
          <tr>
            <td>物质类型</td>
            <td>{{ currentData.type }}</td>
            <td>物质名称</td>
            <td>{{ currentData.name }}</td>
            <td>物质溶度</td>
            <td>{{ currentData.solu }}{{ currentData.soluunit }}</td>
            <td>PH</td>
            <td>{{ currentData.ph }}</td>
          </tr>
          <tr>
            <td>缓冲溶液</td>
            <td colspan="3">{{ currentData.buffer }}</td>
            <td>备注</td>
            <td colspan="3">{{ currentData.detail }}</td>
          </tr>
        </table>
      </div>
    </div>
    <div class="imgdata">
      <div class="imgbutton">
        <div class="toptext"><span class="arrow">></span>波形图</div>
        <el-button class="handle" icon="el-icon-edit" type="primary" @click="delData">处理数据</el-button>
      </div>
      <div class="imgshow">
        <LineChart :chart-data="chartData" :style="chartstyle" />
        <div class="datashow">
          <div class="olddata">
            <p class="datatext">旧数据</p>
            <el-card class="box-card">
              <div class="text item">
                {{ '原始电流 ' + expData.expOriginalCurrentAsScientificNotation }}
              </div>
              <div class="text item">
                {{ '原始电位 ' + expData.expOriginalPotential }}
              </div>
            </el-card>
          </div>
          <div class="newdata">
            <p class="datatext">新数据</p>
            <el-card class="box-card">
              <div class="text item">
                {{ '新电流 ' + expData.expNewestCurrentAsScientificNotation == null ? '' : expData.expNewestCurrentAsScientificNotation }}
              </div>
              <div class="text item">
                {{ '新电位 ' + expData.expNewestPotential == null ? '' : expData.expNewestPotential }}
              </div>
            </el-card>
          </div>
        </div>
      </div>
    </div>
    <el-dialog :visible.sync="ifShowDialog">
      <el-select v-model="selectValue" default-first-option>
        <el-option v-for="item in dataAlgorithms" :label="item.algorithmName" :value="item.algorithmName" />
      </el-select>
      <el-button type="primary" @click="submit">确定</el-button>
      <el-button type="mini" @click="ifShowDialog = false">返回</el-button>
      <el-link type="primary" @click="ifShowDialog = false">没有想要的算法?</el-link>
    </el-dialog>
  </div>
</template>

<script>
import LineChart from './LineChart.vue'
import { listAlgorithmTypeFillCondition } from '@/api/algorithm'
import { deal } from '@/api/analysis'
export default {
  components: { LineChart },
  props: {
    datas: {
      type: Object,
      required: true
    },
    expData: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      // chartData: {
      //   potential: this.expData.expPotentialPointDataAsDouble,
      //   current: this.expData.expOriginalCurrentPointDataAsDouble
      // },
      chartstyle: {
        width: '1150px',
        height: '500px'
      },
      dataAlgorithms: [],
      ifShowDialog: false,
      selectValue: undefined
    }
  },
  computed: {
    currentData() {
      const value = this.datas
      value.name = this.expData.expMaterialName
      value.type = this.expData.materialType == null ? '无' : this.expData.materialType.materialTypeName
      value.solu = this.expData.expMaterialSolubility
      value.buffer = this.expData.bufferSolution == null ? '无' : this.expData.bufferSolution.bufferSolutionName
      value.detail = this.expData.expDataDesc
      value.soluunit = 'um'
      value.ph = this.expData.expPh
      return value
    },
    chartData() {
      return {
        potential: this.expData.expPotentialPointDataAsDouble,
        current: this.expData.expOriginalCurrentPointDataAsDouble
      }
    }
  },
  methods: {
    // changeshowdata() {
    //   const flag = false
    //   this.$emit('getbackshow', flag)
    // }
    async delData() {
      const load = this.$loading({
        lock: true
      })
      listAlgorithmTypeFillCondition(undefined, 1).then(res => {
        this.dataAlgorithms = res.data.data
        load.close()
        this.ifShowDialog = !this.ifShowDialog
      })
      load.close()
    },
    async submit() {
      const id = this.dataAlgorithms.filter(k => {
        return k.algorithmName === this.selectValue
      })[0].algorithmId
      const load = this.$loading({
        lock: true
      })
      deal(id, this.expData.expPotentialPointData, this.expData.expOriginalCurrentPointData).then(res => {
        this.expData.expPotentialPointDataAsDouble = res.data.data.expPotentialPointDataAsDouble
        this.expData.expOriginalCurrentPointDataAsDouble = res.data.data.expOriginalCurrentPointDataAsDouble
        load.close()
      })
      load.close()
      this.ifShowDialog = !this.ifShowDialog
    }
  }
}
</script>

<style scoped>
/* 表格头 */
.tabletop {
  display: flex;
  align-content: center;
}
/* 头部文字 */
.toptext {
  font-size: 24px;
  font-style: italic;
  margin: 0 20px;
}
/* 箭头样式 */
.arrow {
  font-size: 30px;
  line-height: 30px;
  margin-right: 10px;
  color: rgb(159, 163, 163);
}
/* 修改表格样式 */
.table table{
  width: 60vw;
  margin: 0 30px;
}
table
{
    border-collapse:collapse;
}
tr:first-child td:nth-child(1),tr:first-child td:nth-child(3),tr:first-child td:nth-child(5),tr:first-child td:nth-child(7) {
  background-color: #EFF1F4;
  width: 100px;
}
tr:nth-child(2) td:nth-child(1),tr:nth-child(2) td:nth-child(3) {
  background-color: #EFF1F4;
  width: 100px;
}
table,th, td
{
    border: 1px solid #DFE6EC;
}
td {
  padding: 15px;
  text-align: center;
}
/* 修改按钮样式 */
.getback {
  height: 40px;
}
/* 波形图外包框 */
.imgdata {
  margin-top: 10px;
}
/* 按钮组 */
.imgbutton {
  display: flex;
}
.handle {
  margin: 0 20px;
}
.save {
  margin: 0 10px
}
/* 图标展示 */
.imgshow {
  display: flex;
  margin-top: 10px;
}
/* 图表展示 */
.chart {
  width: 80%;
  height: 350px;
}
/* 图表展示外包框 */
.echartsimg {
  width: 70vw;
  height: 490px;
  margin: 0 20px;
  background-color: rgb(119, 113, 113);
}
/* 展示数据 */
.datashow {
  display: flex;
  flex-direction: column;
}
</style>
