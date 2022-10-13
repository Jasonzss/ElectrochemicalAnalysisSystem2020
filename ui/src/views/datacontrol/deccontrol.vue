<template>
  <div class="app-container">
    <div class="top">
      <el-button size="medium" icon="el-icon-upload2" type="primary" class="upload"
        @click="upload.dialog.visible = true">上传算法</el-button>
      <el-dropdown :hide-on-click="false" @command="handleCommand">
        <span class="el-dropdown-link list">
          <span class="listtext">多选搜索</span><i class="el-icon-arrow-down el-icon--right" />
        </span>
        <el-dropdown-menu slot="dropdown" class="dropmenu">
          <el-dropdown-item id="defaultFrame" ref="name" command="name">
            <i class="el-icon-check el-icon--right" />
            算法名称
          </el-dropdown-item>
          <el-dropdown-item ref="type" command="type">
            <i v-show="!serachinput.type" class="el-icon-close el-icon--right" />
            <i v-show="serachinput.type" class="el-icon-check el-icon--right" />
            算法类型
          </el-dropdown-item>
          <el-dropdown-item ref="lang" command="lang">
            <i v-show="!serachinput.lang" class="el-icon-close el-icon--right" />
            <i v-show="serachinput.lang" class="el-icon-check el-icon--right" />
            算法语言
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
      <div class="listofinput">
        <el-input v-model="searchValue.name" class="search" placeholder="请输入算法名称">'' </el-input>
        <el-input v-show="serachinput.type" v-model="searchValue.type" class="search" placeholder="请输入算法类型">''
        </el-input>
        <el-input v-show="serachinput.lang" v-model="searchValue.lang" class="search" placeholder="请输入算法语言">''
        </el-input>
        <el-button size="mini" icon="el-icon-search" @click="search" />
      </div>
      <el-button size="small" type="success" plain icon="el-icon-download" class="bachoper">批量下载</el-button>
      <el-button size="small" type="danger" plain icon="el-icon-delete" class="bachoper" @click="deleteRows">批量删除
      </el-button>
    </div>
    <div class="tablediv">
      <el-table stripe border :cell-style="{ textAlign: 'center' }" :header-cell-style="{ textAlign: 'center' }"
        :data="showdatas.length === 0 && desdatas.length !== 0 ? firstdatas : showdatas"
        :default-sort="{prop: 'date', order: 'descending'}" @selection-change="selectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column label="用户邮箱" width="175" prop="user.userEmail" />
        <el-table-column label="算法审核状态" width="100" sortable>
          <template slot-scope="scope">
            <el-tag :type="scope.row.algorithmStatus.label === '公开' ? 'sucess' : 'warning'">{{
            scope.row.algorithmStatus.label }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="算法名称" width="135" prop="algorithmName" />
        <el-table-column label="算法类型" width="135" prop="algorithmType.label" />
        <el-table-column label="算法语言" width="100" prop="algorithmLanguage.label" />
        <el-table-column label="算法介绍" width="235" prop="algorithmDesc" />
        <el-table-column label="操作">
          <template slot-scope="scope">
            <el-button size="mini" icon="el-icon-edit" @click="openDrawer('编辑', 'edit', scope.row)">编辑</el-button>
            <el-button size="mini" type="primary" icon="el-icon-view" @click="openDrawer('查看', 'view', scope.row)">查看
            </el-button>
            <el-button size="mini" type="danger" icon="el-icon-delete" @click="deletedata(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div class="bottompage">
      <Pagination :datas="PaginationData" @setShowData="getshowdata" />
    </div>
    <!-- 抽屉 -->
    <Drawer :infos="toDrawer" @sendInfo="getNewInfo" />
    <!-- 弹窗 -->
    <!-- 上传算法的弹窗 -->
    <el-dialog title="上传算法" :visible.sync="upload.dialog.visible">
      <el-form :model="upload.form" :rules="upload.rules" ref="uploadForm">
        <el-form-item required label="算法名称" prop="algorithmName">
          <el-input v-model="upload.form.algorithmName" placeholder="算法名称，唯一" maxlength="20" show-word-limit></el-input>
        </el-form-item>
        <el-form-item required label="算法语言" prop="algorithmLanguage">
          <el-select v-model="upload.form.algorithmLanguage" placeholder="请选择">
            <el-option v-for="e in upload.algoLangList" :key="e.value" :label="e.label" :value="e.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item required label="算法类型" prop="algorithmType">
          <el-select v-model="upload.form.algorithmType" placeholder="请选择">
            <el-option v-for="e in upload.algoTypeList" :key="e.value" :label="e.label" :value="e.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item required label="算法描述" prop="algorithmDesc">
          <el-input v-model="upload.form.algorithmDesc  " placeholder="对上传算法的描述" type="textarea" maxlength="50"
            show-word-limit></el-input>
        </el-form-item>
        <el-form-item required label="算法文件" prop="algorithmFile">
          <el-upload action="" :auto-upload="false" accept=".java,.python,.txt" :before-upload="getUploadFile"
            ref="uploadAlgo">
            <el-button size="small" type="primary">选取文件</el-button>
          </el-upload>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="upload.dialog.visible = false">取 消</el-button>
        <el-button type="primary" @click="uploadAlgo">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import Pagination from './components/Pagination.vue'
import Drawer from './components/decmDrawers.vue'
import { listAlgorithm, insertAlgorithm } from '@/api/algorithm'
export default {
  components: {
    Pagination,
    Drawer
  },
  data() {
    return {
      upload: {
        dialog: {
          visible: false,
        },
        form: {
          algorithmName: undefined,
          algorithmFile: undefined,
          algorithmLanguage: undefined,
          algorithmType: undefined,
          algorithmDesc: undefined,
        },
        algoLangList: [
          { label: "Java", value: 0 },
          { label: "Python", value: 2 }
        ],
        algoTypeList: [
          { label: "预处理算法", value: 0 },
          { label: "数据处理算法", value: 1 },
          { label: "数据模型算法", value: 2 },
        ],
        rules: {
          algorithmName: [
            { required: true, message: '请输入算法名称', trigger: 'blur' },
          ],
          algorithmFile: [
            { required: true, message: '请选择算法文件', trigger: 'blur' },
          ],
          algorithmLanguage: [
            { required: true, message: '请选择算法语言', trigger: 'blur' },
          ],
          algorithmType: [
            { required: true, message: '请选择算法类型', trigger: 'blur' },
          ],
          algorithmDesc: [
            { required: true, message: '请输入算法简介', trigger: 'blur' },
          ],
        },
      },
      desdatas: [],
      serachinput: {
        type: false,
        lang: false
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
      searchValue: {
        name: undefined,
        type: undefined,
        lang: undefined
      }
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
    listAlgorithm(undefined, 0, 10).then(res => {
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
      listAlgorithm(undefined, 0, 10, this.searchValue.name, this.searchValue.type, this.searchValue.lang).then(res => {
        this.desdatas = res.data.data
      })
    },
        // 在文件上传前将文件保存到变量中，然后不要上传，通过表单和其他参数一起上传。
        getUploadFile(file) {
      this.upload.form.algorithmFile = file
      return false;
    },
    //上传算法文件
    uploadAlgo() {

      // 先获取上传文件
      this.$refs.uploadAlgo.submit()
      //判断是否为空
      this.$refs.uploadForm.validate((valid) => {
        if (valid) {
          
          insertAlgorithm(this.upload.form).then((res) => {
            this.search()
            this.upload.dialog.visible = false
          })
        } else {
          console.log("error submit");
          return false;
        }
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

/* 默认框背景色 */
#defaultFrame {
  background-color: #e8f4ff;
}

/* 搜索框组 */
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

