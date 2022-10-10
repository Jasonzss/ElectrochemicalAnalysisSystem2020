<template>
  <div class="app-container">
    <div class="top">
      <el-button
        size="medium"
        icon="el-icon-plus"
        type="primary"
        class="upload"
      >添加新的角色</el-button>
      <el-button
        size="small"
        type="danger"
        plain
        icon="el-icon-delete"
        class="bachoper"
        @click="deleteRows"
      >批量删除</el-button>
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
        <el-table-column label="角色" width="135" prop="role.roleName" sortable />
        <el-table-column label="角色数量" width="135" prop="count" sortable />
        <el-table-column label="角色描述" prop="role.roleDesc" />
        <el-table-column label="操作" width="265">
          <template slot-scope="scope">
            <el-button
              size="mini"
              icon="el-icon-edit"
              @click="openDrawer('编辑', 'edit', scope.row)"
            >编辑</el-button>
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
import { listRoles } from '@/api/permission'
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
      selectdRow: {}
    }
  },
  computed: {
    PaginationData() {
      return [this.desdatas, 10]
    },
    firstdatas() {
      return this.desdatas.length > 10 ? this.desdatas.slice(0, 10) : this.desdatas
    }
  },
  created() {
    listRoles().then(res => {
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
      // console.log(data)
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
  display: flex;
  height: 35px;
  margin: 0 auto;
  margin-bottom: 20px;
  width: 80vw;
}
.upload {
  margin-right: 830px;
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
