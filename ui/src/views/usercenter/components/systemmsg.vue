<template>
  <div class="container">
    <div id="SystemMsgBar">
      <div id="Msgtop">
        <!--eslint-disable-next-line vue/html-self-closing -->
        <span id="Msgicon"><i class="el-icon-bell"></i></span>
        <div id="Timeicon">
          <svg-icon id="time" icon-class="jiankangjihuayusuifang" />
          <span> {{ year }} </span>
          <span> {{ time }} </span>
        </div>
      </div>
      <div id="MsgBody">{{ message.applicationContent }}</div>
      <div id="MsgBottom">
        <!--eslint-disable-next-line vue/html-self-closing -->
        <el-button type="danger" @click="deletemsg"><i class="el-icon-delete-solid"></i>删除</el-button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    message: {
      type: Object,
      default: function() {
        return {}
      }
    }
  },
  data() {
    return {
    }
  },
  computed: {
    year() {
      return this.message.applicationTime.split(' ')[0]
    },
    time() {
      return this.message.applicationTime.split(' ')[1]
    }
  },
  methods: {
    deletemsg() {
      this.$emit('deletemsg', this.message.id)
    }
  }
}
</script>

<style scoped>

/* 主体容器 */
.container {
  padding: 10px 20px;
  margin: 30px 30px;
  border-radius: 30px;
  box-shadow: 2px 2px 8px #BBBBBB,-2px -2px 8px #BBBBBB;
}

/* 信息条头部 */
#Msgtop {
  padding-bottom: 20px
}
/* 信息条提示图标 */
#Msgicon {
  display: inline-block;
  background-color: #8cbef0;
  width: 40px;
  height: 40px;
  border-radius: 50px;
}
.el-icon-bell {
  font-size: 30px;
  padding-top: 5px;
  padding-left: 5px;
}
/* 信息条头部时间 */
#Timeicon {
  float: right;
  line-height: 40px
}
#Timeicon span {
  margin: 0 5px;
}
#time {
  font-size: 20px;
}
/* 信息条主体 */
#MsgBody {
  padding: 0 50px;
  font-size: 20px;
}
/* 信息条按钮 */
#MsgBottom {
  text-align: right;
  padding: 10px 0;
}
</style>
