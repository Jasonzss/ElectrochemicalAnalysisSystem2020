<template>
  <div class="body">
    <div class="top">
      <el-avatar class="logo" shape="circle" :size="50" fit="contain" src="https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg" />
      <h2>实验报告题头</h2>
      <div>
        <p>ID:<span>001</span></p>
        <p>Name:<span>敌敌畏</span></p>
      </div>
    </div>
    <div class="intro">
      <div class="model">
        <div>预处理算法:<span>1</span></div>
        <div>数据模型:<span>1</span></div>
      </div>
      <div class="expintro">
        实验说明:<span>1</span>
      </div>
      <el-divider>training series</el-divider>
      <el-table
        stripe
        border
        :cell-style="{ textAlign: 'center' }"
        :header-cell-style="{ textAlign: 'center' }"
        :default-sort="{prop: 'date', order: 'descending'}"
        :data="reportdata.traindata"
      >
        <el-table-column width="55" prop="name" />
        <el-table-column v-for="i in 7" :key="i" width="55" :label="i+''" :prop="i+''" />
        <el-table-column width="55" label="...">
          <template>
            <span>...</span>
          </template>
        </el-table-column>
      </el-table>
      <el-table
        stripe
        border
        :cell-style="{ textAlign: 'center' }"
        :header-cell-style="{ textAlign: 'center' }"
        :default-sort="{prop: 'date', order: 'descending'}"
      >
        <el-table-column width="165" label="Rp\^2" />
        <el-table-column width="165" label="RMSEc" />
        <el-table-column width="165" label="MAEc" />
      </el-table>
    </div>
    <div class="test">
      <el-divider>testing series</el-divider>
      <el-table
        stripe
        border
        :cell-style="{ textAlign: 'center' }"
        :header-cell-style="{ textAlign: 'center' }"
        :default-sort="{prop: 'date', order: 'descending'}"
        :data="reportdata.traindata"
      >
        <el-table-column width="55" prop="name" />
        <el-table-column v-for="i in 7" :key="i" width="55" :label="i+''" :prop="i+''" />
        <el-table-column width="55" label="...">
          <template>
            <span>...</span>
          </template>
        </el-table-column>
      </el-table>
      <el-table
        stripe
        border
        :cell-style="{ textAlign: 'center' }"
        :header-cell-style="{ textAlign: 'center' }"
        :default-sort="{prop: 'date', order: 'descending'}"
      >
        <el-table-column width="123" label="Rp\^2" />
        <el-table-column width="123" label="RMSEp" />
        <el-table-column width="123" label="MAEp" />
        <el-table-column width="125" label="RPD" />
      </el-table>
    </div>
    <div>
      <el-divider>result map</el-divider>
      <div class="charts">
        <div class="chart">
          <LineChart :chart-data="chartData" />
        </div>
        <div class="chart">
          <LineChart :chart-data="chartData" />
        </div>
      </div>
      <div> 结果模型 </div>
    </div>
    <el-divider>report time: 2022-01-01 17:55:53</el-divider>
  </div>
</template>

<script>
import LineChart from './LineChart.vue'
export default {
  components: {
    LineChart
  },
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
          name: '浓度', '1': 0.1, '2': 0.22, '3': 0.42, '4': 0.33, '5': 0.75, '6': 0.88, '7': 0.34 },
        {
          name: '实际浓度', '1': 0.1, '2': 0.22, '3': 0.42, '4': 0.33, '5': 0.75, '6': 0.88, '7': 0.34 },
        {
          name: '预测浓度', '1': 0.1, '2': 0.22, '3': 0.42, '4': 0.33, '5': 0.75, '6': 0.88, '7': 0.34 }
        ]
      },
      chartData: {
        expectedData: [1, 2, 3, 2, 3, 1, 6],
        actualData: [2, 3, 1, 1, 2, 3, 5]
      }
    }
  }

}
</script>

<style scoped>
/* 总报告容器 */
.body {
  width: 510px;
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
