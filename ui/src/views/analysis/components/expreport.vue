<template>
  <div class="body">
    <div class="top">
      <el-avatar class="logo" shape="circle" :size="50" fit="contain" src="https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg" />
      <h2>实验报告题头</h2>
      <div>
        <p>ID:<span>{{ report.reportId }}</span></p>
        <p>Name:<span>{{ report.reportTitle }}</span></p>
      </div>
    </div>
    <div class="intro">
      <div class="model">
        <div>预处理算法:<span>{{ report.pretreatmentAlgorithmId }}</span></div>
        <div>数据模型:<span>{{ report.reportDataModelId }}</span></div>
      </div>
      <div class="expintro">
        实验说明:<span>{{ report.reportDesc }}</span>
      </div>
      <el-divider>训练集数据</el-divider>
      <el-table
        stripe
        border
        :cell-style="{ textAlign: 'center' }"
        :header-cell-style="{ textAlign: 'center' }"
        :default-sort="{prop: 'date', order: 'descending'}"
        :data="trainData"
      >
        <el-table-column width="55" prop="name" />
        <el-table-column v-for="i in trainLens" :key="i" width="55" :label="i+''" :prop="i+''" />
        <el-table-column width="90" label="...">
          <template>
            <span>...</span>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top: 10px">
        <span style="margin-left: 25px;border: 1px solid black;padding: 5px">Rc2: {{ report.trainSetIndicatorAsDouble === undefined ? '无' : report.trainSetIndicatorAsDouble.rc2 }}</span>
        <span style="margin-left: 100px;border: 1px solid black;padding: 5px">RMSEc: {{ report.trainSetIndicatorAsDouble === undefined ? '无' : report.trainSetIndicatorAsDouble.rmsec }}</span>
        <span style="margin-left: 100px;border: 1px solid black;padding: 5px">MAEc: {{ report.trainSetIndicatorAsDouble === undefined ? '无' : report.trainSetIndicatorAsDouble.maec }}</span>
      </div>
    </div>
    <div class="test">
      <el-divider>测试集数据</el-divider>
      <el-table
        stripe
        border
        :cell-style="{ textAlign: 'center' }"
        :header-cell-style="{ textAlign: 'center' }"
        :default-sort="{prop: 'date', order: 'descending'}"
        :data="testData"
      >
        <el-table-column width="55" prop="name" />
        <el-table-column v-for="i in testLens" :key="i" width="55" :label="i+''" :prop="i+''" />
        <el-table-column width="55" label="...">
          <template>
            <span>...</span>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top: 10px">
        <span style="margin-left: 25px;border: 1px solid black;padding: 5px">rp2: {{ report.testSetIndicatorAsDouble === undefined ? '无' : report.testSetIndicatorAsDouble.rp2 }}</span>
        <span style="margin-left: 70px;border: 1px solid black;padding: 5px">rmsep: {{ report.testSetIndicatorAsDouble === undefined ? '无' :report.testSetIndicatorAsDouble.rmsep }}</span>
        <span style="margin-left: 70px;border: 1px solid black;padding: 5px">maep: {{ report.testSetIndicatorAsDouble === undefined ? '无' :report.testSetIndicatorAsDouble.maep }}</span>
        <span style="margin-left: 70px;border: 1px solid black;padding: 5px">rpd: {{report.testSetIndicatorAsDouble === undefined ? '无' : report.testSetIndicatorAsDouble.rpd }}</span>
      </div>
    </div>
    <div>
      <el-divider>结果数据</el-divider>
      <div class="charts">
        <div class="chart">
          <img src="../../../../public/fig_train.jpg" style="width: 320px" alt=""/>
        </div>
        <div class="chart">
          <img src="../../../../public/fig_test.jpg" style="width: 320px;margin-left: 70px;" alt=""/>
        </div>
      </div>
    </div>
    <el-divider>实验报告创建时间: {{ report.reportCreateTime }}</el-divider>
  </div>
</template>

<script>
export default {
  props: {
    reportData: {
      type: Object,
      default: function() {
        return {}
      }
    }
  },
  data() {
    return {
      reportdata: {
        traindata: [{
          name: '浓度', '1': 0.1, '2': 0.22, '3': 0.42, '4': 0.33, '5': 0.75, '6': 0.88, '7': 0.34, '8': 0.35, '9': 0.36},
        {
          name: '实际浓度', '1': 0.1, '2': 0.22, '3': 0.42, '4': 0.33, '5': 0.75, '6': 0.88, '7': 0.34, '8': 0.35, '9': 0.36 },
        {
          name: '预测浓度', '1': 0.1, '2': 0.22, '3': 0.42, '4': 0.33, '5': 0.75, '6': 0.88, '7': 0.34, '8': 0.35, '9': 0.36 }
        ],
        testdata: [{
          name: '浓度', '1': 0.1, '2': 0.22, '3': 0.42, '4': 0.33, '5': 0.75, '6': 0.88, '7': 0.34, '8': 0.35, '9': 0.36},
          {
            name: '实际浓度', '1': 0.1, '2': 0.22, '3': 0.42, '4': 0.33, '5': 0.75, '6': 0.88, '7': 0.34, '8': 0.35, '9': 0.36 },
          {
            name: '预测浓度', '1': 0.1, '2': 0.22, '3': 0.42, '4': 0.33, '5': 0.75, '6': 0.88, '7': 0.34, '8': 0.35, '9': 0.36 }
        ]
      },
      trainLen: undefined,
      testLen: undefined,
    }
  },
  computed: {
    report() {
      return this.reportData
    },
    trainLens() {
      return this.trainLen === undefined ? 0 : this.trainLen
    },
    testLens() {
      return this.testLen === undefined ? 0 : this.testLen
    },
    trainData() {
      const train = this.reportData.trainingSetDataAsDoubles
      if (train !== undefined) {
        const data = this.reportdata.traindata
        this.trainLen = train.length
        data[0] = {
          name: '浓度',
          '1': train[0][0],
          '2': train[1][0],
          '3': train[2][0],
          '4': train[3][0],
          '5': train[4][0],
          '6': train[5][0],
          '7': train[6][0],
          '8': train[7][0]
          // '9': train[8][0]
        }
        data[1] = {
          name: '实际浓度',
          '1': train[0][1],
          '2': train[1][1],
          '3': train[2][1],
          '4': train[3][1],
          '5': train[4][1],
          '6': train[5][1],
          '7': train[6][1],
          '8': train[7][1],
          // '9': train[8][1]
        }
        data[2] = {
          name: '预测浓度',
          '1': train[0][2],
          '2': train[1][2],
          '3': train[2][2],
          '4': train[3][2],
          '5': train[4][2],
          '6': train[5][2],
          '7': train[6][2],
          '8': train[7][2],
          // '9': train[8][2]
        }
        return data
      } else {
        return this.reportdata.traindata
      }
    },
    testData() {
      const train = this.reportData.testSetDataAsDoubles
      if (train !== undefined) {
        const data = this.reportdata.testdata
        this.testLen = train.length
        data[0] = {
          name: '浓度',
          '1': train[0][0],
          '2': train[1][0],
          // '3': train[2][0],
          // '4': train[3][0],
          // '5': train[4][0],
          // '6': train[5][0],
          // '7': train[6][0],
          // '8': train[7][0],
          // '9': train[8][0]
        }
        data[1] = {
          name: '实际浓度',
          '1': train[0][1],
          '2': train[1][1],
          // '3': train[2][1],
          // '4': train[3][1],
          // '5': train[4][1],
          // '6': train[5][1],
          // '7': train[6][1],
          // '8': train[7][1],
          // '9': train[8][1]
        }
        data[2] = {
          name: '预测浓度',
          '1': train[0][2],
          '2': train[1][2],
          // '3': train[2][2],
          // '4': train[3][2],
          // '5': train[4][2],
          // '6': train[5][2],
          // '7': train[6][2],
          // '8': train[7][2],
          // '9': train[8][2]
        }
        return data
      } else {
        return this.reportdata.testdata
      }
    }
  }

}
</script>

<style scoped>
/* 总报告容器 */
.body {
  width: 650px;
  box-sizing: border-box;
  border: 3px solid rgb(148, 145, 145);
}
/* 顶部栏 */
.top {
  display: flex;
  justify-content: center;
  align-content: center;
}
.logo {
  width: 25%;
  text-align: left;
  margin-top: 10px;
  margin-left: 5px;
}
/* 题头 */
.top h2 {
  width: 65%;
  line-height: 80px;
  text-align: center;
  padding: 0 0;
  margin: 0 0;
}
.top div {
  width: 20%;
  text-align: right;
}
.model {
  display: flex;
  justify-content: center;
  align-content: center;
}
.model div {
  margin: 0 10px;
  width: 50%;
  text-align: left;
}
.expintro {
  margin: 10px 10px;
}
.charts {
  display: flex;
}
.chart {
  width: 240px;
}
</style>
