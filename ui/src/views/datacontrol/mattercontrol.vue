<template>
  <div class="app-container">
    <div class="top">
      <el-button
        size="medium"
        icon="el-icon-plus"
        type="primary"
        class="upload"
      >新添物质类型</el-button>
      <!--      <el-dropdown :hide-on-click="false" @command="handleCommand">-->
      <!--        <span class="el-dropdown-link list">-->
      <!--          <span class="listtext">多选搜索</span><i class="el-icon-arrow-down el-icon&#45;&#45;right" />-->
      <!--        </span>-->
      <!--        <el-dropdown-menu slot="dropdown" class="dropmenu">-->
      <!--          <el-dropdown-item id="defaultFrame">-->
      <!--            <i class="el-icon-check el-icon&#45;&#45;right" />-->
      <!--            物质类型名</el-dropdown-item>-->
      <!--          <el-dropdown-item ref="count" command="count">-->
      <!--            <i v-show="!serachinput.count" class="el-icon-close el-icon&#45;&#45;right" />-->
      <!--            <i v-show="serachinput.count" class="el-icon-check el-icon&#45;&#45;right" />-->
      <!--            物质数量</el-dropdown-item>-->
      <!--          <el-dropdown-item ref="intro" command="intro">-->
      <!--            <i v-show="!serachinput.intro" class="el-icon-close el-icon&#45;&#45;right" />-->
      <!--            <i v-show="serachinput.intro" class="el-icon-check el-icon&#45;&#45;right" />-->
      <!--            物质类型说明</el-dropdown-item>-->
      <!--        </el-dropdown-menu>-->
      <!--      </el-dropdown>-->
      <div class="listofinput" style="margin-left: 10px">
        <el-input placeholder="请输入物质类型名" v-model="searchValue">'' </el-input>
        <!--        <el-input v-show="serachinput.count" placeholder="请输入物质数量">'' </el-input>-->
        <!--        <el-input v-show="serachinput.intro" placeholder="请输入物质类型说明">'' </el-input>-->
        <el-button size="mini" icon="el-icon-search" @click="search" />
      </div>
      <!--      <el-button-->
      <!--        size="small"-->
      <!--        type="success"-->
      <!--        plain-->
      <!--        icon="el-icon-download"-->
      <!--        class="bachoper"-->
      <!--      >批量下载</el-button>-->
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
        <el-table-column label="物质类型名" width="200" prop="materialTypeName" sortable />
        <el-table-column label="物质数量" width="200" prop="materialNumber" />
        <el-table-column label="物质类型说明" width="235" prop="materialDesc" />
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
      <Pagination :datas="PaginationData" @setShowData="getshowdata" />
    </div>
    <!-- 抽屉 -->
    <Drawer :infos="toDrawer" @sendInfo="getNewInfo" />
  </div>
</template>

<script>
import Pagination from './components/Pagination.vue'
import Drawer from './components/matterDrawers.vue'
import { listUserMaterialType } from '@/api/materialType'
export default {
  components: {
    Pagination,
    Drawer
  },
  data() {
    return {
      desdatas: [],
      // serachinput: {
      //   count: false,
      //   intro: false
      // },
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
      searchValue: ''
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
    listUserMaterialType(1, 10).then(res => {
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
      // console.log(rows)
      this.selectdRow = rows
    },
    // 批量删除处理方法
    deleteRows() {
      for (const row of this.selectdRow) {
        this.deletedata(row)
      }
    },
    search() {
      listUserMaterialType(1, 10, this.searchValue).then(res => {
        this.desdatas = res.data.data
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
/* 下拉框 */
.dropmenu {
  margin-right: 10px;
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
  width: 50vw;
  height: 36px;
  overflow: hidden;
}
/* 默认搜索框背景色 */
#defaultFrame {
  background-color: #e8f4ff;
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

