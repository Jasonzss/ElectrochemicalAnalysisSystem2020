<template>
  <div class="app-container">
    <div class="top">
      <el-button
        size="small"
        type="primary"
        plain
        icon="el-icon-check"
        class="bachoper"
      >批量通过</el-button>
      <el-button
        size="small"
        type="danger"
        plain
        icon="el-icon-error"
        class="bachoper"
      >批量驳回</el-button>
    </div>
    <div class="tablediv">
      <el-table
        stripe
        border
        :cell-style="{ textAlign: 'center' }"
        :header-cell-style="{ textAlign: 'center' }"
        :data="showdatas.length === 0 && desdatas.length !== 0 ? firstdatas : showdatas"
        :default-sort="{prop: 'date', order: 'descending'}"
        @selection-change="selectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column label="用户昵称" width="135" prop="user.userName" sortable />
        <el-table-column label="用户邮箱" width="175" prop="user.userEmail" sortable />
        <el-table-column label="审核类型" width="100" prop="applicationType" sortable />
        <el-table-column label="审核状态" width="100" prop="applicationStatus" />
        <el-table-column label="审核时间" width="200" prop="applicationTime" />
<!--        <el-table-column label="申请内容" width="150" prop="applicationContent" />-->
<!--        <el-table-column label="拒绝原因" width="200" prop="applicationRejectReason" />-->
        <el-table-column label="操作">
          <template slot-scope="scope">
            <el-button
              size="mini"
              icon="el-icon-s-check"
              @click="openDrawer('编辑', 'edit', scope.row)"
            >审批</el-button>
            <el-button
              size="mini"
              type="primary"
              icon="el-icon-view"
              @click="openDrawer('查看', 'view', scope.row)"
            >查看</el-button>
            <el-button
              size="mini"
              type="danger"
              icon="el-icon-error"
              @click="deletedata(scope.row)"
            >驳回</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div class="bottompage">
      <Pagination :datas="PaginationData" @setShowData="getshowdata" />
    </div>
    <!-- 抽屉 -->
  </div>
</template>

<script>
import Pagination from './components/Pagination.vue'
import { listUserApplication } from '@/api/application'
export default {
  components: {
    Pagination
  },
  data() {
    return {
      desdatas: [],
      serachinput: {
        name: false,
        count: false,
        solu: false
      },
      // 抽屉展示参数和标记值
      direction: 'rtl',
      drawerShow: {
        edit: false,
        add: false,
        view: false
      },
      // 分页器展示数据
      showdatas: [],
      // 传给抽屉的数据
      toDrawer: {
        title: '',
        plugin: false,
        data: {}
      },
      // 被选择的行
      selectdRow: {},
      page: {
        size: undefined,
        current: 1,
        pageSize: 10
      }
    }
  },
  computed: {
    PaginationData() {
      return [this.desdatas, this.page.pageSize]
    },
    firstdatas() {
      return this.desdatas.length > this.page.pageSize ? this.desdatas.slice(0, this.page.pageSize) : this.desdatas
    }
  },
  async created() {
    await listUserApplication(this.$store.getters.userEmail, this.page.current, this.page.pageSize).then(res => {
      this.desdatas = res.data.data
    })
  },
  methods: {
    // 处理搜索框显示
    handleCommand(command) {
      this.serachinput[command] = !this.serachinput[command]
    },
    // 分页器更新展示数据
    getshowdata(data) {
      this.showdatas = data
    },
    // 用来开启抽屉并传值
    openEdit(plugin, index, row) {
      this.drawerShow[plugin] = true
      console.log(this.edit)
      console.log(index, row)
    },
    openDrawer(title, plugin, data) {
      this.toDrawer.title = title
      this.toDrawer.plugin = this.drawerShow[plugin] = true
      this.toDrawer.data = data
    },
    // 用来添加元素
    openAddDrawer(title, plugin) {
      this.toDrawer.title = title
      this.toDrawer.plugin = this.drawerShow[plugin] = true
    },
    // 用来更新抽屉数据
    getNewInfo(info) {
      this.desdatas = this.desdatas.map((ed) => {
        if (ed.id === info.id) return info
      })
    },
    // 用来删除单项数据
    deletedata(data) {
      this.desdatas = this.desdatas.filter((ed) => ed.id !== data.id)
    },
    // 用来记录被标记的行数
    selectionChange(rows) {
      this.selectdRow = rows
    },
    // 批量删除处理方法
    deleteRows() {
      for (const row of this.selectdRow) {
        this.deletedata(row)
      }
    }
  }
}
</script>

<style scoped>
* {
  box-sizing: border-box;
}
.top {
  height: 35px;
  margin: 0 auto;
  margin-bottom: 20px;
  width: 80vw;
  text-align: right;
}
/* 按钮组 */
.bachoper {
  margin-left: 10px;
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
