<template>
  <div class="app-container">
    <div id="showUserMsg">
      <img id="autor" :src="userImg" alt="">
      <div id="userName"><span>{{ userInfo.name }}</span></div>
      <div v-show="!changepassword">
        <el-form label-width="20px">
          <div v-show="changeInfo" class="userInfo"><span>用户名</span>
            <el-input v-model="changeInfos.name" class="userinput" prop="name" />
          </div>
          <div class="userInfo"><span>手机号</span>
            <p v-show="!changeInfo">{{ userInfo.phoneNumber }}</p>
            <el-input v-show="changeInfo" v-model="changeInfos.phoneNumber" class="userinput" @blur="checkphone()" />
          </div>
          <div class="userInfo"><span>性  别</span>
            <p v-show="!changeInfo">{{ userInfo.sex }}</p>
            <el-select v-show="changeInfo" v-model="changeInfos.sex" class="userinput">
              <el-option value="男">男</el-option>
              <el-option value="女">女</el-option>
              <el-option value="保密">保密</el-option>
            </el-select>
          </div>
          <div class="userInfo"><span>年  龄</span>
            <p v-show="!changeInfo">{{ userInfo.age }}</p>
            <el-input-number v-show="changeInfo" v-model="changeInfos.age" class="userinput" prop="age" />
          </div>
          <div v-show="!changeInfo" class="userInfo"><span>邮  箱</span>
            <p>{{ userInfo.email }}</p>
          </div>
        </el-form>
      </div>
      <div v-show="changepassword">
        <el-form label-width="20px">
          <div v-show="!conpassword" class="userInfo"><span>原密码</span>
            <el-input v-model="password.lastpassword" class="userinput" show-password />
          </div>
          <div v-show="conpassword" class="passwordconfrim">
            <div class="userInfo"><span>密  码</span>
              <el-input v-model="password.newpassword" class="userinput" prop="email" show-password />
            </div>
            <div class="userInfo"><span>确认密码</span>
              <el-input v-model="password.confirmpassword" class="userinput" prop="email" show-password />
            </div>
          </div>
        </el-form>
      </div>
      <div v-show="changebtngroup" id="changeInfoButton1">
        <el-button type="primary" @click="changebtn('msg')">修改信息</el-button>
        <el-button type="primary" @click="changebtn('password')">修改密码</el-button>
      </div>
      <div v-show="!changebtngroup" id="changeInfoButton2">
        <el-button type="primary" @click="changeInfobtn()">确认</el-button>
        <el-button @click="changebtn('back')">返回</el-button>
      </div>
    </div>
    <div id="showSystemMsg">
      <div id="MsgBar">
        <span>你好，{{ userInfo.name }}</span>
        <a @click="deleteallmsg">清空所有通知</a>
      </div>
      <hr>
      <div
        v-for="message in showmessages.length === 0 && messagebars.length !== 0 ? firstmessages : showmessages"
        v-show="!isEmpty"
        id="SystemMsg"
        :key="message.id"
      >
        <!-- 这里是系统通知条组件 -->
        <SystemMsg :message="message" @deletemsg="deletemsg" />
      </div>
      <!-- 占位div -->
      <div v-show="isEmpty" class="empty">
        <i class="el-icon-chat-round" />
        <span>暂无通知</span>
      </div>
      <div class="pagination">
        <Pagination :datas="PaginationData" @setShowData="getshowdata" />
      </div>
    </div>
  </div>
</template>

<script>
// 引入系统通知条组件
import SystemMsg from './components/systemmsg.vue'
import Pagination from './components/Pagination.vue'
import { getUserInfo, selectImage, updateUserBaseInfo, updateUserPassword, verifyUserPassword } from '@/api/user'
import { deleteApplication, listUserApplication } from '@/api/application'
import { arrayBufferToBase64 } from '@/utils/tools'
export default {
  components: {
    SystemMsg,
    Pagination
  },
  data() {
    return {
      // 用户信息对象
      userInfo: {
        name: '',
        phoneNumber: '',
        sex: '',
        age: 0,
        email: ''
      },
      userImg: undefined,
      changeInfos: {
        name: '',
        phoneNumber: '',
        sex: '',
        age: 0
      },
      // 修改密码存储对象
      password: {
        lastpassword: '',
        newpassword: '',
        confirmpassword: '',
        passwordreg: `^123456$`
      },
      // 修改信息状态标记值
      changeInfo: false,
      // 修改密码装填标记值
      changepassword: false,
      // 表单验证通过标记
      checkrule: {
        phoneNumber: true
      },
      // 修改新密码标记值
      conpassword: false,
      // 修改按钮组标记值
      changebtngroup: true,
      // 信息条数组对象
      messagebars: [],
      // 展示信息条数组对象
      showmessages: [],
      // 空信息条展示标记
      isEmpty: false
    }
  },
  computed: {
    firstmessages() {
      return this.messagebars.length > 3 ? this.messagebars.slice(0, 3) : this.messagebars
    },
    PaginationData() {
      return [this.messagebars, 3]
    }
  },
  async created() {
    const userEmail = this.$store.getters.userEmail
    // 获取用户基本信息
    await getUserInfo(userEmail).then(res => {
      const data = res.data.data
      this.userInfo.sex = data.userSex
      this.userInfo.name = data.userName
      this.userInfo.age = data.userAge
      this.userInfo.email = data.userEmail
      this.userInfo.phoneNumber = data.userTel == null ? '无' : data.userTel
    })
    await selectImage(userEmail).then(res => {
      this.userImg = 'data:image/jpeg;base64,' + arrayBufferToBase64(res.data)
    })
    // 获取申请信息
    await listUserApplication(userEmail).then(res => {
      this.messagebars = res.data.data
    })
    this.changeInfos.name = this.userInfo.name
    this.changeInfos.age = this.userInfo.age
    this.changeInfos.sex = this.userInfo.sex
    this.changeInfos.phoneNumber = this.userInfo.phoneNumber
  },
  methods: {
    // 按钮组切换
    changebtn(str) {
      this.changebtngroup = !this.changebtngroup
      if (str === 'back') {
        if (this.changeInfo) {
          this.changeInfo = !this.changeInfo
        } else {
          this.conpassword = false
          this.password.lastpassword = ''
          this.changepassword = !this.changepassword
        }
      }
      if (str === 'msg') {
        this.changeInfo = !this.changeInfo
      }
      if (str === 'password') {
        this.changepassword = !this.changepassword
      }
    },
    // 信息提交按钮
    changeInfobtn() {
      if (this.changeInfo) {
      // 表单数据验证
        if (this.checkrule.phoneNumber) {
          const { name, age, sex, phoneNumber } = this.changeInfos
          // 修改用户信息
          updateUserBaseInfo(sex, age, name, phoneNumber, this.userInfo.email)
          this.userInfo = JSON.parse(JSON.stringify(this.changeInfos))
          this.changebtngroup = !this.changebtngroup
          this.changeInfo = !this.changeInfo
        } else {
          this.$notify.error({
            title: '错误',
            message: '表单有异常数据，无法提交',
            duration: 1000
          })
        }
      } else {
        if (this.conpassword === false) {
          verifyUserPassword(this.password.lastpassword)
          this.conpassword = true
          this.password.lastpassword = ''
        } else {
          if (this.password.newpassword === this.password.confirmpassword) {
            // 修改密码
            updateUserPassword(this.$store.getters.userEmail, this.password.newpassword, this.password.confirmpassword)
            this.password.newpassword = ''
            this.password.confirmpassword = ''
            this.conpassword = false
            this.changepassword = !this.changepassword
            this.changebtngroup = !this.changebtngroup
          }
        }
      }
    },
    // 检查手机号信息规则
    checkphone() {
      const reg = /^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$/
      const msg = '手机号为11位,请检查输入'
      this.checkRule(this.changeInfos.phoneNumber, reg, msg) > 0 ? this.checkrule.phoneNumber = true : this.checkrule.phoneNumber = false
    },
    checkRule(str, reg) {
      return !reg.test(str)
    },
    // 分页器更新展示数据
    getshowdata(data) {
      this.showmessages = data
    },
    // 子组件传来的删除方法
    deletemsg(id) {
      deleteApplication(id)
      this.messagebars = this.messagebars.filter((msg) => {
        return msg.id !== id
      })
      if (this.messagebars.length === 0) this.isEmpty = true
    },
    // 删除所有通知
    deleteallmsg() {
      const ids = this.messagebars.map(msg => msg.id)
      deleteApplication(ids)
      this.messagebars = []
      this.showmessages = []
      if (this.showmessages.length === 0) this.isEmpty = true
    }
  }

}
</script>

<style scoped>

/* 外包container */
.app-container {
  padding-top: 20px;
  display: flex;
}
/* 用户信息div */
#showUserMsg {
  width:30%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-content: center;
  position: relative;
}
/*  头像  */
#autor {
  width: 200px;
  height: 200px;
  margin: 0 55px;
  margin-top: 50px;
  border: 2px splid grey;
  border-radius: 100px;
  box-shadow: 2px 2px 18px #BBBBBB,-2px -2px 18px #BBBBBB;
}
/* 用户姓名 */
#userName {
  font-size: 48px;
  padding: 0 30px
}
#userName span{
  padding: 0 60px;
}
/* 用户信息清单 */
.userInfo{
  display: flex;
  margin: 0 30px;
}
.userInfo span{
  line-height: 50px;
  color: grey;
  width:70px;
  text-align: center
}
/* 用户修改信息表单 */
.userinput {
  width: 180px;
  margin-top: 5px;
  outline: none;
  border: none;
  transition: all .4s ease-in;
}
/* 按钮div */
#changeInfoButton1 {
  margin:30px 0;
  transition: all .4s ease-in;
}
#changeInfoButton2 {
  margin:30px 0;
  transition: all .4s ease-in;
}
#changeInfoButton1 button {
  margin: 0 25px;
  transition: all .4s ease-in;
}
#changeInfoButton2 button {
  margin: 0 45px;
  transition: all .4s ease-in;
}
/* 消息通知div */
#showSystemMsg {
  width: 70%;
}
/* 信息条展示外包div */
#SystemMsg {
  min-height: 100px;
}
/* 系统消息bar */
#MsgBar span {
  font-size: 25px;
}
#MsgBar a{
  float: right;
  margin: 0 20px;
  line-height: 32px;
  color: #328de9
}
/* 分割线 */
  hr {
  margin-top: 4px
}
/* 分页器 */
.pagination {
  display: flex;
  justify-content: center;
  position: absolute;
  top: 95%;
  left: 56%;
}
/* 空状态div */
.empty {
  display: flex;
  justify-content: center;
  align-content: center;
  width: 100%;
  height: 100%;
  flex-direction: column;
}
.empty i {
  font-size: 100px;
  margin: 0 auto;
  color: grey;
}
.empty span {
  font-size: 36px;
  margin: 0 auto;
  margin-bottom: 100px;
  color: grey;
}
</style>

