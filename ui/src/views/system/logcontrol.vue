<template>
  <div class="app-container">
    <div class="top">
      <div class="listofinput">
        <el-select v-model="searchValue.type" default-first-option>
          <el-option v-for="item in options" :value="item" :label="item" />
        </el-select>
        <el-input v-show="!sys" v-model="searchValue.userEmail" placeholder="请输入用户邮箱" />
        <el-button v-show="sys" style="margin-left: 20px" size="mini" icon="el-icon-search" @click="searchSys" />
        <el-button v-show="!sys" style="margin-left: 20px" size="mini" icon="el-icon-search" @click="searchUser" />
        <el-button v-show="sys" style="margin-left: 500px" size="primary" @click="getUserLog">点击切换用户日志</el-button>
        <el-button v-show="!sys" size="primary" @click="getSystemLog">点击切换系统日志</el-button>
      </div>
    </div>
    <div class="tablediv">
      <el-table
        v-show="sys"
        stripe
        border
        :cell-style="{ textAlign: 'center' }"
        :header-cell-style="{ textAlign: 'center' }"
        :data="showdatas.length === 0 && eddatas.length !== 0 ? firstdatas : showdatas"
        :default-sort="{prop: 'systemLogTime', order: 'descending'}"
      >
        <el-table-column label="日志级别" width="250" prop="systemLogLevel" sortable />
        <el-table-column label="日志详细信息" width="728" prop="systemLogDetails" />
        <el-table-column label="记录日志时间" width="250" prop="systemLogTime" sortable />
      </el-table>
      <el-table
        v-show="!sys"
        stripe
        border
        :cell-style="{ textAlign: 'center' }"
        :header-cell-style="{ textAlign: 'center' }"
        :data="showdatas.length === 0 && eddatas.length !== 0 ? firstdatas : showdatas"
        :default-sort="{prop: 'userLogOperateTime', order: 'descending'}"
      >
        <el-table-column label="用户邮箱号" width="200" prop="user.userEmail" sortable />
        <el-table-column label="IP" width="100" prop="userIp" />
        <el-table-column label="级别" width="70" prop="userLogLevel" />
        <el-table-column label="类名方法名" width="400" prop="userLogClassMethodName" />
        <!--        <el-table-column label="参数" width="200" prop="userLogParameter" />-->
        <!--        <el-table-column label="返回值" width="200" prop="userLogMethodReturnValue" />-->
        <el-table-column label="记录时间" width="200" prop="userLogOperateTime" sortable />
        <el-table-column label="悬浮查看详情信息" width="250">
          <template slot-scope="scope">
            <el-popover
              placement="top-start"
              title="日志详细信息"
              width="1000"
              trigger="hover"
              :content="scope.row == null ? '无' : scope.row.userLogParameter "
            >
              <p style="color: #1890ff">全类名方法名: {{ scope.row.userLogClassMethodName }}</p>
              <p style="color: #4AB7BD">方法参数: {{ scope.row.userLogParameter }}</p>
              <p style="color: #99a9bf">返回结果: {{ scope.row.userLogMethodReturnValue }}</p>
              <div slot="reference" class="name-wrapper">
                <el-tag size="medium">{{ scope.row.userLogDetail }}</el-tag>
              </div>
            </el-popover>
          </template>
        </el-table-column>

      </el-table>
    </div>
    <div class="bottompage">
      <!-- 分页器 -->
      <Pagination :datas="PaginationData" @setShowData="getshowdata" />
    </div>
  </div>
</template>

<script>
import Pagination from './components/Pagination.vue'
import { listUserLog, systemLog } from '@/api/Log'
export default {
  components: {
    Pagination
  },
  data() {
    return {
      eddatas: [],
      showdatas: [],
      direction: 'rtl',
      searchValue: {
        type: undefined,
        userEmail: undefined
      },
      options: [
        'error',
        'warn',
        'debug',
        'info'
      ],
      sys: true
    }
  },
  computed: {
    firstdatas() {
      return this.eddatas.length > 11 ? this.eddatas.slice(0, 11) : this.eddatas
    },
    PaginationData() {
      return [this.eddatas, 11]
    }
  },
  created() {
    systemLog(1, 11).then(res => {
      this.eddatas = res.data.data.dataList
    })
  },
  methods: {
    // 分页器更新展示数据
    getshowdata(data) {
      this.showdatas = data
    },
    searchSys() {
      systemLog(1, 11, this.searchValue.type).then(res => {
        this.eddatas = res.data.data.dataList
      })
    },
    searchUser() {
      listUserLog(1, 11, this.searchValue.type, this.searchValue.userEmail).then(res => {
        this.eddatas = res.data.data.dataList
      })
    },
    async getUserLog() {
      await listUserLog(1, 11).then(res => {
        this.eddatas = res.data.data.dataList
      })
      this.sys = false
    },
    async getSystemLog() {
      await systemLog(1, 11).then(res => {
        this.eddatas = res.data.data.dataList
      })
      this.sys = true
    }
  }
}
</script>

<style scoped>
* {
  box-sizing: border-box;
}
.top {
  display: flex;
  height: 35px;
  margin: 0 auto;
  margin-bottom: 20px;
  align-content: center;
  justify-content: center;
  width: 80vw;
}
/* 下拉框 */
.dropmenu {
  margin-right: 10px;
}
/* 默认框背景色 */
#defaultFrame {
  background-color: #e8f4ff;
}
/* 下拉菜单文字 */
.listtext {
  cursor: pointer;
}
/* 下拉菜单标题 */
.list {
  display: block;
  width: 102px;
  border: 1px solid #ecf5ff;
  background-color: #ecf5ff;
  font-size: 15px;
  border-radius: 5px;
  color: #409eff;
  line-height: 36px;
  padding: 0 10px;
  margin: 0 20px;
}
/* 搜索框的外包div */
.listofinput {
  display: flex;
  width: 60vw;
  height: 36px;
  overflow: hidden;
}
/* 搜索框组  */
.search {
  transition: width 0.4s ease-in;
}
/* 按钮组 */
.bachoper {
  margin-left: 20px;
}
/* 表格外包div */
.tablediv {
  width: 80vw;
  height: 547px;
  display: flex;
  margin: 0 auto;
  justify-content: center;
  align-content: center;
}

/* 分页器外包div */
.bottompage {
  width: 90%;
  margin-top: 20px;
  margin-left: 85px;
  text-align: right;
}
</style>
