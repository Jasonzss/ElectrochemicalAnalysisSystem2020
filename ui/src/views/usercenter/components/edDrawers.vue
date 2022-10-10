<template>
  <div>
    <el-drawer
      :title="infos.title"
      :visible.sync="infos.plugin"
      :direction="direction"
      size="30%"
    >
      <div v-show="infos.title === '查看'" class="app-container">
        <div class="inputBox">
          <span>物质名称 :</span>
          <p>{{ data.expMaterialName }}</p>
        </div>
        <div class="inputBox">
          <span>物质类型 :</span>
          <p>{{ materialType }}</p>
        </div>
        <div class="inputBox">
          <span>物质溶度 :</span>
          <p>{{ data.expMaterialSolubility }}</p>
        </div>
        <div class="showVA">
          <div class="inputBox">
            <span>原始电流 :</span>
            <p>{{ data.expOriginalCurrentAsScientificNotation }}</p>
          </div>
          <div class="inputBox">
            <span>原始电位 :</span>
            <p>{{ data.expOriginalPotential }}</p>
          </div>
        </div>
        <div class="showVA">
          <div class="inputBox">
            <span>新电流 :</span>
            <p>{{ data.expNewestCurrentAsScientificNotation }}</p>
          </div>
          <div class="inputBox">
            <span>新电位 :</span>
            <p>{{ data.expNewestPotential }}</p>
          </div>
        </div>
        <div class="inputBox">
          <span>缓冲溶液 :</span>
          <p>{{ bufferSolutionName }}</p>
        </div>
        <div class="inputBox">
          <span>录入时间 :</span>
          <p>{{ data.expCreateTime }}</p>
        </div>
      </div>
      <div v-show="infos.title === '编辑'">
        <div class="inputBox">
          <span>物质名称 :</span>
          <el-input v-model="data.expMaterialName" class="inputBar" />
        </div>
        <div class="inputBox">
          <span>物质类型 :</span>
          <el-input v-model="materialType" class="inputBar" />
        </div>
        <div class="inputBox">
          <span>物质溶度 :</span>
          <el-input v-model="data.expMaterialSolubility" class="inputBar" />
        </div>
        <div class="showVA">
          <div class="inputBox">
            <span>原始电流 :</span>
            <p>{{ data.expOriginalCurrentAsScientificNotation }}</p>
          </div>
          <div class="inputBox">
            <span>原始电位 :</span>
            <p>{{ data.expOriginalPotential }}</p>
          </div>
        </div>
        <div class="showVA">
          <div class="inputBox">
            <span>新电流 :</span>
            <p>{{ data.expNewestCurrentAsScientificNotation }}</p>
          </div>
          <div class="inputBox">
            <span>新电位 :</span>
            <p>{{ data.expNewestPotential }}</p>
          </div>
        </div>
        <div class="inputBox">
          <span>缓冲溶液 :</span>
          <p>{{ bufferSolutionName }}</p>
        </div>
        <div class="inputBox">
          <span>录入时间 :</span>
          <p>{{ data.expCreateTime }}</p>
        </div>
        <div class="buttonBox">
          <el-button @click="back">取消</el-button>
          <el-button type="primary" @click="confirmUpdate">确认</el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import { updateUserExperimentalData } from '@/api/experimentalData'

export default {
  props: {
    infos: {
      type: Object,
      default: function() {
        return {}
      }
    }
  },
  data() {
    return {
      direction: 'rtl'
    }
  },
  computed: {
    data() {
      return this.infos.data
    },
    materialType() {
      if (this.infos.data.materialType === undefined || this.infos.data.materialType === '') {
        return '暂无'
      } else {
        return this.infos.data.materialType.materialTypeName
      }
    },
    bufferSolutionName() {
      if (this.infos.data.bufferSolution === undefined || this.infos.data.bufferSolution === '') {
        return '暂无'
      } else {
        return this.infos.data.bufferSolution.bufferSolutionName
      }
    }
  },
  methods: {
    confirmUpdate() {
      // this.$emit('getNewInfo', this.infos)
      const { expDataId,
        expMaterialName,
        expMaterialSolubility,
        expOriginalCurrentAsScientificNotation,
        expOriginalPotential,
        expNewestCurrentAsScientificNotation,
        expNewestPotential
      } = this.infos.data
      updateUserExperimentalData(expDataId, expMaterialName, expMaterialSolubility,
        expOriginalCurrentAsScientificNotation, expOriginalPotential, expNewestCurrentAsScientificNotation, expNewestPotential)
        .then(res => {
          this.infos.plugin = false
        })
    },
    back() {
      this.infos.plugin = false
    }
  }
}
</script>

<style scoped>
/* 抽屉全局box */
/* 外包div */
.app-container {
    display: flex;
    margin: 0 auto;
    flex-direction: column;
}
/* 展示电流电位盒子 */
.showVA {
    display: flex;
}
/* 输入框盒子 */
.inputBox {
    display: flex;
    flex-direction: row;
    margin: 10px 5px;
}
.inputBar {
    width: 200px;
    line-height: 50px;
}
.inputBox p {
    font-size: 17px;
}
.inputBox p::after {
    content:'';
    display: block;
    position: relative;
    bottom: 0;
    left: 0;
    width: 100%;
    height: 2px;
    background-color: rgb(150, 195, 212);
}
.inputBox span {
    line-height: 50px;
    margin: 0 20px;
    font-size: 20px;
}
/* 按钮盒子 */
.buttonBox {
    text-align: right;
    margin: 10px 20px;
}
</style>
