<template>
  <div class="app-container">
    <div class="top">
      <el-button
        size="medium"
        icon="el-icon-download"
        type="primary"
        class="upload"
        @click="openAddDrawer('添加', 'add')"
      >新建报告</el-button>
      <el-dropdown :hide-on-click="false" @command="handleCommand">
        <span class="el-dropdown-link list">
          <span class="listtext">多选搜索</span><i class="el-icon-arrow-down el-icon--right" />
        </span>
        <el-dropdown-menu slot="dropdown" class="dropmenu">
          <el-dropdown-item id="defaultFrame" ref="name" command="name">
            <i class="el-icon-check el-icon--right" />
            物质名称</el-dropdown-item>
          <el-dropdown-item ref="title" command="title">
            <i v-show="!serachinput.title" class="el-icon-close el-icon--right" />
            <i v-show="serachinput.title" class="el-icon-check el-icon--right" />
            实验报告标题</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
      <div class="listofinput">
        <el-input v-model="searchValue.name" class="search" placeholder="请输入物质名称">''</el-input>
        <el-input v-show="serachinput.title" v-model="searchValue.title" class="search" placeholder="请输入实验报告标题">''</el-input>
        <el-button size="mini" icon="el-icon-search" @click="search" />
      </div>

      <el-button
        size="small"
        type="success"
        plain
        icon="el-icon-download"
        class="bachoper"
      >批量下载</el-button>
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
        :data="showdatas.length === 0 && eddatas.length !== 0 ? firstdatas : showdatas"
        :default-sort="{prop: 'eddatas', order: 'descending'}"
        @selection-change="selectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column label="物质名称" width="120" prop="reportMaterialName" />
        <el-table-column label="标题" width="125" prop="reportTitle" />
        <el-table-column label="预处理算法" width="125" prop="pretreatmentAlgorithmName" />
        <el-table-column label="建模算法" width="125" prop="reportDataModelName" />
        <el-table-column label="建模方程" width="125" prop="reportResultModel" />
        <el-table-column label="描述" width="150" prop="reportDesc" />
        <el-table-column label="录入时间" width="180" prop="reportCreateTime" sortable />
        <el-table-column label="操作">
          <template slot-scope="scope">
            <el-button
              size="mini"
              icon="el-icon-edit"
              @click="openDrawer('编辑', 'edit', scope.row)"
            >编辑</el-button>
            <el-button
              size="mini"
              type="primary"
              icon="el-icon-view"
              @click="openDrawer('查看', 'view', scope.row)"
            >查看</el-button>
            <el-button
              size="mini"
              type="danger"
              icon="el-icon-delete"
              @click="deletedata(scope.row)"
            >删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div class="bottompage">
      <!-- 分页器 -->
      <Pagination :datas="PaginationData" @setShowData="getshowdata" />
    </div>
    <!-- 抽屉 -->
    <Drawers :infos="toDrawer" @sendInfo="getNewInfo" />
  </div>
</template>

<script>
import Drawers from './components/reportDrawers.vue'
import Pagination from './components/Pagination.vue'
import { listReport, selectImage, selectReportInfo } from '@/api/experimentalReport'
import { arrayBufferToBase64 } from '@/utils/tools'
export default {
  components: {
    Drawers,
    Pagination
  },
  data() {
    return {
      eddatas: [],
      serachinput: {
        title: false
        // solu: false
      },
      showdatas: [],
      direction: 'rtl',
      drawerShow: {
        edit: false,
        add: false,
        view: false
      },
      toDrawer: {
        title: '',
        plugin: false,
        data: {}
      },
      selectdRow: {},
      searchValue: {
        name: undefined,
        title: undefined
      }
    }
  },
  computed: {
    firstdatas() {
      return this.eddatas.length > 10 ? this.eddatas.slice(0, 10) : this.eddatas
    },
    PaginationData() {
      return [this.eddatas, 10]
    }
  },
  created() {
    listReport(this.$store.getters.userEmail, 1, 10).then(res => {
      this.eddatas = res.data.data
    })
  },
  methods: {
    // 用来判断搜索框搜索类目
    handleCommand(command) {
      this.serachinput[command] = !this.serachinput[command]
    },
    // 用来开启抽屉并传值
    openEdit(plugin, index, row) {
      this.drawerShow[plugin] = true
      console.log(this.edit)
      console.log(index, row)
    },
    async openDrawer(title, plugin, data) {
      if (title === '查看') {
        await selectImage(data.reportId, 'train').then(res => {
          data.trainImg = 'data:image/jpeg;base64,' + arrayBufferToBase64(res.data)
        })
        await selectImage(data.reportId, 'test').then(res => {
          data.testImg = 'data:image/jpeg;base64,' + arrayBufferToBase64(res.data)
        })
        await selectReportInfo(data.reportId).then(res => {
          data.testSetData = res.data.data[0].testSetData
          data.trainingSetData = res.data.data[0].trainingSetData
        })
      }
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
      this.eddatas = this.eddatas.map((ed) => {
        if (ed.id === info.id) return info
      })
    },
    // 用来删除单项数据
    deletedata(data) {
      // console.log(data)
      this.eddatas = this.eddatas.filter((ed) => ed.id !== data.id)
    },
    // 用来记录被标记的行数
    selectionChange(rows) {
      // console.log(rows)
      this.selectdRow = rows
    },
    // 批量删除处理方法
    deleteRows() {
      for (const row of this.selectdRow) {
        this.deletedata(row)
      }
    },
    // 分页器更新展示数据
    getshowdata(data) {
      this.showdatas = data
    },
    search() {
      if (this.searchValue.title === '') this.searchValue.title = undefined
      if (this.searchValue.name === '') this.searchValue.name = undefined
      listReport(this.$store.getters.userEmail, 1, 10, this.searchValue.title, this.searchValue.name).then(res => {
        this.eddatas = res.data.data
      })
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
/* 新建报告按钮 */
.upload {
  margin-right: 50px;
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
/* 表格外包div */
.carddiv {
  width: 80vw;
  height: 547px;
  margin: 0 auto;
}

/* 分页器外包div */
.bottompage {
  width: 90%;
  margin-top: 20px;
  margin-left: 85px;
  text-align: right;
}
</style>
