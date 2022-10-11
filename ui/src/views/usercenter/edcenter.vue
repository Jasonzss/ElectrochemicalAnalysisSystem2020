<template>
  <div class="app-container">
    <div v-show="!showdata">
      <div class="top">
        <el-button
          size="medium"
          icon="el-icon-circle-plus-outline"
          type="primary"
          class="upload"
          @click="addAction"
        >添加</el-button>
        <el-dropdown :hide-on-click="false" @command="handleCommand">
          <span class="el-dropdown-link list">
            <span class="listtext">多选搜索</span><i class="el-icon-arrow-down el-icon--right" />
          </span>
          <el-dropdown-menu slot="dropdown" class="dropmenu">
            <el-dropdown-item id="defaultFrame" ref="name" command="name">
              <i class="el-icon-check el-icon--right" />
              物质名称</el-dropdown-item>
            <el-dropdown-item ref="type" command="type">
              <i v-show="!serachinput.type" class="el-icon-close el-icon--right" />
              <i v-show="serachinput.type" class="el-icon-check el-icon--right" />
              物质类型</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
        <div class="listofinput">
          <el-input v-model="searchCondition.materialName" class="search" placeholder="请输入物质名称">''</el-input>
          <el-input v-show="serachinput.type" v-model="searchCondition.materialTypeName" class="search" placeholder="请输入物质类型">''</el-input>
          <el-button size="mini" icon="el-icon-search" @click="searchByCondition" />
        </div>
        <el-button
          size="small"
          type="success"
          plain
          icon="el-icon-download"
          class="bachoper"
          @click="downloads"
        >批量下载</el-button>
        <el-button
          size="small"
          type="danger"
          plain
          icon="el-icon-delete"
          class="bachoper"
          @click="deleteRows"
        >批量删除</el-button>
        <el-button
          size="small"
          type="primary"
          plain
          icon="el-icon-s-open"
          class="bachoper"
        >批量查看</el-button>
      </div>
      <div class="tablediv">
        <el-table
          stripe
          border
          :cell-style="{ textAlign: 'center' }"
          :header-cell-style="{ textAlign: 'center' }"
          :data="showdatas.length === 0 && eddatas.length !== 0 ? firstdatas : showdatas"
          :default-sort="{prop: 'expCreateTime', order: 'descending'}"
          @selection-change="selectionChange"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column label="录入时间" width="160" prop="expCreateTime" sortable />
          <el-table-column label="物质类型" width="95" prop="materialType.materialTypeName" />
          <el-table-column label="物质名称" width="95" prop="expMaterialName" />
          <el-table-column label="物质溶度" width="95" prop="expMaterialSolubility" />
          <el-table-column label="原始电流" width="100" prop="expOriginalCurrentAsScientificNotation" />
          <el-table-column label="原始电位" width="85" prop="expOriginalPotential" />
          <el-table-column label="新电流" width="100" prop="expNewestCurrentAsScientificNotation" />
          <el-table-column label="新电位" width="75" prop="expNewestPotential" />
          <el-table-column label="缓冲溶液" width="85" prop="bufferSolution.bufferSolutionName" />
          <el-table-column label="操作">
            <template slot-scope="scope">
              <el-button
                size="mini"
                icon="el-icon-edit"
                @click="download(scope.row)"
              >下载</el-button>
              <el-button
                size="mini"
                type="primary"
                icon="el-icon-view"
                @click="wave(scope.row)"
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
    </div>
    <div v-show="showdata">
      <el-button style="margin-left: 500px;margin-bottom: 25px" type="primary" @click="showdata = !showdatas">返回实验数据管理页面</el-button>
      <showdatas :datas="eddata" :exp-data="expData" @getbackshow="getbackshow(flag)" />
    </div>
    <!-- 抽屉 -->
    <Drawers :infos="toDrawer" />
  </div>
</template>

<script>
import showdatas from '../analysis/components/showdatas'
import Drawers from './components/edDrawers.vue'
import Pagination from './components/Pagination'
import { deleteUserExperimentalData, downloadExperimentalData, listUserExperimentalData } from '@/api/experimentalData'
import { createDownloadData } from '@/api/apiConstant'
export default {
  components: {
    Drawers,
    Pagination,
    showdatas
  },
  data() {
    return {
      eddatas: [],
      showdata: false,
      serachinput: {
        type: false
      },
      eddata: {
        type: '',
        name: '',
        solu: '',
        soluunit: '',
        buffer: '',
        ph: '',
        detail: ''
      },
      expData: {},
      searchCondition: {
        materialName: '',
        materialTypeName: ''
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
      page: {
        size: undefined,
        current: 1,
        pageSize: 7
      }
    }
  },
  computed: {
    firstdatas() {
      return this.eddatas.length > this.page.pageSize ? this.eddatas.slice(0, this.page.pageSize) : this.eddatas
    },
    PaginationData() {
      return [this.eddatas, this.page.pageSize, this.page.size, this.page.current]
    }
  },
  async created() {
    const condition = {
      'expDeleteStatus': 0
    }
    await listUserExperimentalData(this.$store.getters.userEmail, this.page.current, this.page.pageSize, condition).then(res => {
      this.eddatas = res.data.data.dataList[0]
      this.page.current = res.data.data.currentPageNo
      this.page.size = res.data.data.totalPageSize
    })
  },
  methods: {
    wave(row) {
      this.expData = row
      this.showdata = !this.showdata
    },
    // 通过条件检索数据
    searchByCondition() {
      let id = ''
      let condition = {}
      if (this.searchCondition.materialTypeName !== '') {
        id = this.eddatas.filter(k => {
          return this.searchCondition.materialTypeName === k.materialType.materialTypeName
        })[0].materialType.materialTypeId
        condition = {
          'expMaterialName': this.searchCondition.materialName,
          'materialTypeId': id
        }
      } else {
        condition = {
          'expMaterialName': this.searchCondition.materialName
        }
      }
      listUserExperimentalData(this.$store.getters.userEmail, this.page.current, this.page.pageSize, condition).then(res => {
        this.eddatas = res.data.data.dataList[0]
      })
    },
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
    openDrawer(title, plugin, data) {
      this.toDrawer.title = title
      this.toDrawer.plugin = this.drawerShow[plugin] = true
      this.toDrawer.data = data
    },
    // 用来添加元素
    addAction() {
      this.$router.push('/analysis/waveanalysis')
    },
    // 用来删除单项数据
    deletedata(data) {
      const id = data.expDataId
      deleteUserExperimentalData(id, this.$store.getters.userEmail).then(res => {
        this.eddatas = this.eddatas.filter((ed) => ed.expDataId !== data.expDataId)
      })
    },
    // 用来记录被标记的行数
    selectionChange(rows) {
      this.selectdRow = rows
    },
    // 批量删除处理方法
    async deleteRows() {
      const ids = this.selectdRow.map(k => {
        return k.expDataId
      })
      await deleteUserExperimentalData(ids, this.$store.getters.userEmail).then(res => {
        for (const row of this.selectdRow) {
          this.deletedata(row)
        }
      })
    },
    // 分页器更新展示数据
    async getshowdata(currentPage) {
      const condition = {
        'expDeleteStatus': 0
      }
      await listUserExperimentalData(this.$store.getters.userEmail, currentPage, this.page.pageSize, condition).then(res => {
        this.eddatas = res.data.data.dataList[0]
        this.page.current = res.data.data.currentPageNo
        this.page.size = res.data.data.totalPageSize
      })
    },
    download(row) {
      downloadExperimentalData(row.expDataId, this.$store.getters.userEmail).then(res => {
        createDownloadData(res.data, row.expMaterialName, 'xls')
      })
    },
    downloads() {
      const ids = this.selectdRow.map(k => {
        return k.expDataId
      })
      downloadExperimentalData(ids, this.$store.getters.userEmail).then(res => {
        createDownloadData(res.data, '实验数据文件名', 'xls')
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
  width: 50vw;
  height: 36px;
  overflow: hidden;
}
/* 搜索框组  */
.search {
  transition: width 0.4s ease-in;
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
