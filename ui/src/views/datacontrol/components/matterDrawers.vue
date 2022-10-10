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
          <span>物质类型名 :</span>
          <p>{{ data.materialTypeName }}</p>
        </div>
        <div class="inputBox">
          <span>物质数量 :</span>
          <p>{{ data.materialNumber }}</p>
        </div>
        <div class="inputBox">
          <span>物质类型说明 :</span>
          <p>{{ data.materialDesc }}</p>
        </div>
      </div>
      <div v-show="infos.title === '编辑'">
        <div class="inputBox">
          <span>物质类型名 :</span>
          <el-input v-model="data.materialTypeName" class="inputBar" />
        </div>
        <div class="inputBox">
          <span>物质数量 :</span>
          <el-input v-model="data.materialNumber" class="inputBar" />
        </div>
        <div class="inputBox">
          <span>物质类型说明 :</span>
          <p>{{ data.materialDesc }}</p>
        </div>
        <div class="buttonBox">
          <el-button @click="back">取消</el-button>
          <el-button type="primary">确认</el-button>
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
</style>
