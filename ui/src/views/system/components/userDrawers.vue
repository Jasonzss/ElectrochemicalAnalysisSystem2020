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
          <span>用户邮箱 :</span>
          <p>{{ data.name }}</p>
        </div>
        <div class="inputBox">
          <span>日志级别 :</span>
          <p>{{ data.type }}</p>
        </div>
        <div class="inputBox">
          <span>算法语言 :</span>
          <p>{{ data.lang }}</p>
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
