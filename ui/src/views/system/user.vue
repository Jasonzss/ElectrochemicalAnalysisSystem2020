<template>
  <div class="app-container">
    <div class="top">
      <el-button
        size="medium"
        icon="el-icon-circle-plus-outline"
        type="primary"
        class="upload"
        @click="openAddDrawer('添加', 'add')"
      >添加新的用户</el-button>
      <el-dropdown :hide-on-click="false" @command="handleCommand">
        <span class="el-dropdown-link list">
          <span class="listtext">多选搜索</span><i class="el-icon-arrow-down el-icon--right" />
        </span>
        <el-dropdown-menu slot="dropdown" class="dropmenu">
          <el-dropdown-item id="defaultFrame" ref="name" command="name">
            <i class="el-icon-check el-icon--right" />
            用户昵称</el-dropdown-item>
          <el-dropdown-item ref="type" command="type">
            <i v-show="!serachinput.type" class="el-icon-close el-icon--right" />
            <i v-show="serachinput.type" class="el-icon-check el-icon--right" />
            电话号码</el-dropdown-item>
          <el-dropdown-item ref="solu" command="solu">
            <i v-show="!serachinput.solu" class="el-icon-close el-icon--right" />
            <i v-show="serachinput.solu" class="el-icon-check el-icon--right" />
            用户邮箱</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
      <div class="listofinput">
        <el-input class="search" placeholder="请输入用户昵称">''</el-input>
        <el-input v-show="serachinput.type" class="search" placeholder="请输入电话号码">''</el-input>
        <el-input v-show="serachinput.solu" class="search" placeholder="请输入用户邮箱">''</el-input>
        <el-button size="mini" icon="el-icon-search" />
      </div>
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
        :default-sort="{prop: 'name', order: 'descending'}"
        @selection-change="selectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column label="昵称" width="85" prop="userName" />
        <el-table-column label="年龄" width="75" prop="userAge" sortable />
        <el-table-column label="性别" width="75" prop="userSex" sortable />
        <el-table-column label="电话号码" width="115" prop="userTel" />
        <el-table-column label="邮箱" width="185" prop="userEmail" />
        <el-table-column label="封禁状态" width="185" prop="userStatus" />
        <el-table-column label="头像" width="85">
          <template slot-scope="scope">
            <el-avatar shape="circle" :size="50" fit="contain" :src="scope.row.userImg" />
          </template>
        </el-table-column>
        <el-table-column label="角色" width="75" prop="user" />
        <el-table-column label="操作">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="danger"
              icon="el-icon-delete"
              @click="deletedata(scope.row)"
            >注销</el-button>
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
import { listUser, selectImage } from '@/api/user'
import { arrayBufferToBase64 } from '@/utils/tools'
export default {
  components: {
    Pagination
  },
  data() {
    return {
      eddatas: [],
      serachinput: {
        type: false,
        solu: false
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
      selectdRow: {}
    }
  },
  computed: {
    firstdatas() {
      return this.eddatas.length > 6 ? this.eddatas.slice(0, 6) : this.eddatas
    },
    PaginationData() {
      return [this.eddatas, 6]
    }
  },
  async created() {
    await listUser(1, 6).then(res => {
      this.eddatas = res.data.data.dataList
      for (let i = 0; i < this.eddatas.length; i++) {
        const sex = this.eddatas[i].userSex === 0 ? '男' : '女'
        const status = this.eddatas[i].userStatus === 0 ? '正常' : '封禁中'
        const tel = this.eddatas[i].userTel === '' ? '无' : this.eddatas[i].userTel
        this.eddatas[i].userSex = sex
        this.eddatas[i].userStatus = status
        this.eddatas[i].userTel = tel
      }
    })
    const emails = this.eddatas.map(k => {
      return k.userEmail
    })
    for (let i = 0; i < emails.length; i++) {
      await selectImage(emails[i]).then(res => {
        this.eddatas[i].userImg = 'data:image/jpeg;base64,' + arrayBufferToBase64(res.data)
      })
    }
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
/* 展示密码 */
.passwordeye {
  font-size: 20px;
  cursor: pointer;
}
/* 分页器外包div */
.bottompage {
  width: 90%;
  margin-top: 20px;
  margin-left: 85px;
  text-align: right;
}
</style>

