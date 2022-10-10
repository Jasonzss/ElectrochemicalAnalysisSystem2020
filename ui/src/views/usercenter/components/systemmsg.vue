<template>
  <div class="container">
    <div id="SystemMsgBar">
      <div id="Msgtop">
        <!--eslint-disable-next-line vue/html-self-closing -->
        <span id="Msgicon"><i class="el-icon-bell"></i></span>
        <el-tag style="font-size: 23px" size="medium" type="success">{{ type }}</el-tag>
        <div id="Timeicon">
          <svg-icon id="time" icon-class="jiankangjihuayusuifang" />
          <span> {{ year }} </span>
          <span> {{ time }} </span>
        </div>
      </div>
      <div id="MsgBody">
        <div :style="statusColor">{{ statusInfo }} 具体详细内容如下：</div>
        <span>{{ info }}</span>
      </div>
      <div id="MsgBottom">
        <!--eslint-disable-next-line vue/html-self-closing -->
        <el-button type="danger" @click="deletemsg"><i class="el-icon-delete-solid"></i>删除</el-button>
      </div>
    </div>
  </div>
</template>

<script>
import { listAlgorithmBaseInfo } from '@/api/algorithm'

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
    },
    type() {
      let type
      switch (this.message.applicationType) {
        case 0:
          type = '账号解封申请'
          break
        case 1:
          type = '新增物质类型申请'
          break
        case 2:
          type = '算法公开申请'
          break
        case 3:
          type = '新增缓冲溶液申请'
          break
      }
      return type
    },
    statusInfo() {
      let status
      switch (this.message.applicationStatus) {
        case 0:
          status = '您的申请正在等待审核中!'
          break
        case 1:
          status = '您的申请已经审核通过了!'
          break
        case 2:
          status = '您的申请被驳回了!'
          break
      }
      return status
    },
    statusColor() {
      let color = 'color: '
      switch (this.message.applicationStatus) {
        case 0:
          color += '#d7e117'
          break
        case 1:
          color += '#20a0ff'
          break
        case 2:
          color += '#f60d56'
          break
      }
      return color
    },
    bufferSolution() {
      const val = JSON.parse(this.message.applicationContent)
      const name = val.bufferSolutionName
      const desc = val.bufferSolutionDesc
      return '缓冲溶液名:' + name + '   缓冲溶液描述:' + desc
    },
    materialType() {
      const val = JSON.parse(this.message.applicationContent)
      const name = val.materialTypeName
      const desc = val.materialTypeDesc
      return '物质类型名:' + name + '   物质类型描述描述:' + desc
    },
    algorithm() {
      const val = JSON.parse(this.message.applicationContent)
      const id = val.applicationId
      this.selectAlgorithm(id)
      // return '算法名:' + name + '   缓冲溶液描述:' + desc
    },
    // user() {
    //
    // },
    info() {
      switch (this.message.applicationType) {
        case 0:
          return this.user
        case 1:
          return this.materialType
        case 2:
          return this.algorithm
        case 3:
          return this.bufferSolution
      }
    }
  },
  methods: {
    deletemsg() {
      this.$emit('deletemsg', this.message.id)
    },
    async selectAlgorithm(id) {
      await listAlgorithmBaseInfo(id).then(res => {

      })
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
