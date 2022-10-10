<template>
  <div>
    <el-drawer
      :title="infos.title"
      :visible.sync="infos.plugin"
      :direction="direction"
      :size="infos.title === '查看' ? '50%' : '30%'"
    >
      <div v-show="infos.title === '查看'" id="overflowAuto" class="app-container">
        <div class="inputBox">
          <span>训练集数据</span>
          <div>{{ data.trainingSetData }}</div>
        </div>
        <div class="inputBox">
          <span>测试集数据</span>
          <div>{{ data.testSetData }}</div>
        </div>
        <div class="inputBox">
          <span>训练集图片</span>
          <el-image :src="data.trainImg" style="width: 500px;height: 300px" />
        </div>
        <el-button type="primary" style="margin: 0 auto">下载图片</el-button>
        <div class="inputBox">
          <span>测试集图片</span>
          <el-image :src="data.testImg" style="width: 500px;height: 300px" />
        </div>
        <el-button type="primary" style="margin: 0 auto">下载图片</el-button>
        <div class="buttonBox" style="margin-bottom: 100px ">
          <el-button @click="back">返回</el-button>
        </div>
      </div>
      <div v-show="infos.title === '编辑'">
        <div class="inputBox">
          <span>实验报告标题 :</span>
          <el-input v-model="data.reportTitle" class="inputBar" />
        </div>
        <div class="inputBox">
          <span>实验报告描述 :</span>
          <el-input v-model="data.reportDesc" class="inputBar" />
        </div>
        <div class="buttonBox">
          <el-button @click="back">取消</el-button>
          <el-button type="primary" @click="back">确认</el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script>

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
    }
  },
  created() {
    console.log(this.infos.data)
  },
  methods: {
    backInfo() {
      this.$emit('sendInfo', this.infos)
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
