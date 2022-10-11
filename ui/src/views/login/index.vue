<template>
  <div class="login-container">
    <div v-if="showBar && !showFindPassword">
      <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form" autocomplete="on" label-position="left">

        <div class="title-container">
          <h3 class="title">登 录 入 口</h3>
        </div>

        <el-form-item prop="userEmail">
          <span class="svg-container">
            <svg-icon icon-class="user" />
          </span>
          <el-input
            ref="userEmail"
            v-model="loginForm.userEmail"
            placeholder="用户邮箱"
            name="userEmail"
            type="text"
            tabindex="1"
            autocomplete="on"
          />
        </el-form-item>

        <el-tooltip v-model="capsTooltip" content="Caps lock is On" placement="right" manual>
          <el-form-item prop="userPassword">
            <span class="svg-container">
              <svg-icon icon-class="password" />
            </span>
            <el-input
              :key="passwordType"
              ref="userPassword"
              v-model="loginForm.userPassword"
              :type="passwordType"
              placeholder="密码"
              name="userPassword"
              tabindex="2"
              autocomplete="on"
              @keyup.native="checkCapslock"
              @blur="capsTooltip = false"
              @keyup.enter.native="handleLogin"
            />
            <span class="show-pwd" @click="showPwd">
              <svg-icon :icon-class="passwordType === 'password' ? 'eye' : 'eye-open'" />
            </span>
          </el-form-item>
        </el-tooltip>

        <el-image :src="authCodeSrc" style="height: 50px;width: 220px" />
        <span class="tip" @click="getAuthCode">看不清？点击换一张</span>

        <el-form-item prop="imgAuthCode">
          <span class="svg-container">
            <svg-icon icon-class="icon" />
          </span>
          <el-input
            ref="imgAuthCode"
            v-model="loginForm.imgAuthCode"
            placeholder="图片验证码"
            name="imgAuthCode"
            type="text"
            tabindex="1"
            autocomplete="on"
          />
        </el-form-item>

        <el-button :loading="loading" type="primary" style="width:100%;margin-bottom:15px;font-size: 15px" @click.native.prevent="handleLogin">登录</el-button>
        <el-button type="primary" style="width:30%;font-size: 15px" @click="showBar = false">邮箱注册</el-button>
        <el-button type="primary" style="width:30%;font-size: 15px;margin-left: 140px" @click="showFindPassword = true">找回密码</el-button>

      </el-form>

    </div>
    <registerBar v-if="!showBar && !showFindPassword" @BackLogin="BackLogin" />
    <findPassword v-if="showFindPassword" @BackLogin="BackLogin" />
  </div>
</template>

<script>
import { validEmail } from '@/utils/validate'
import registerBar from './components/registerBar'
import findPassword from './components/findPassword'
import { arrayBufferToBase64 } from '@/utils/tools'
import { getAuthCode, login, selectImage } from '@/api/user'

export default {
  name: 'Login',
  components: { registerBar, findPassword },
  data() {
    const validateUsername = (rule, value, callback) => {
      if (!validEmail(value)) {
        callback(new Error('邮箱号格式错误'))
      } else {
        callback()
      }
    }
    const validatePassword = (rule, value, callback) => {
      if (value.length < 6) {
        callback(new Error('密码不能少于6个字符'))
      } else {
        callback()
      }
    }
    const validateAuthCode = (rule, value, callback) => {
      if (value.length !== 6) {
        callback(new Error('验证码必须是6位字符'))
      } else {
        callback()
      }
    }
    return {
      loginForm: {
        userEmail: '',
        userPassword: '',
        imgAuthCode: ''
      },
      loginRules: {
        userEmail: [{ required: true, trigger: 'blur', validator: validateUsername }],
        userPassword: [{ required: true, trigger: 'blur', validator: validatePassword }],
        imgAuthCode: [{ required: true, trigger: 'blur', validator: validateAuthCode }]
      },
      passwordType: 'password',
      capsTooltip: false,
      loading: false,
      showDialog: false,
      redirect: undefined,
      otherQuery: {},
      showBar: true,
      showFindPassword: false,
      authCodeSrc: ''
    }
  },
  watch: {
    $route: {
      handler: function(route) {
        const query = route.query
        if (query) {
          this.redirect = query.redirect
          this.otherQuery = this.getOtherQuery(query)
        }
      },
      immediate: true
    }
  },
  async created() {
    await getAuthCode().then(res => {
      this.$data.authCodeSrc = 'data:image/png;base64,' + arrayBufferToBase64(res.data)
    })
    // window.addEventListener('storage', this.afterQRScan)
  },
  mounted() {
    if (this.loginForm.username === '') {
      this.$refs.userEmail.focus()
    } else if (this.loginForm.password === '') {
      this.$refs.userPassword.focus()
    }
  },
  destroyed() {
    // window.removeEventListener('storage', this.afterQRScan)
  },
  methods: {
    BackLogin() {
      this.showBar = true
      this.showFindPassword = false
    },
    checkCapslock(e) {
      const { key } = e
      this.capsTooltip = key && key.length === 1 && (key >= 'A' && key <= 'Z')
    },
    showPwd() {
      if (this.passwordType === 'password') {
        this.passwordType = ''
      } else {
        this.passwordType = 'password'
      }
      this.$nextTick(() => {
        this.$refs.userPassword.focus()
      })
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true
          login(this.loginForm).then(async res => {
            await selectImage(res.data.data.userEmail).then(res => {
              const avatar = 'data:image/jpeg;base64,' + arrayBufferToBase64(res.data)
              this.$store.dispatch('user/saveAvatar', avatar)
            })
            const roleIdList = res.data.data.roleIdList
            this.$store.dispatch('permission/generateRoutes', res.data.data.roleIdList)
            this.$store.dispatch('user/saveToken', res.data.data.token)
            this.$store.dispatch('user/saveUserEmail', res.data.data.userEmail)
            // this.$store.dispatch('user/saveRoles', 'editor')
            // this.$router.push({ path: this.redirect || '/usercenter', query: this.otherQuery })
            if (roleIdList[0] === 0) {
              this.$router.push('/usercenter')
            } else if (roleIdList[0] === 1) {
              this.$router.push('/datacontrol')
            } else if (roleIdList[0] === 2) {
              this.$router.push('/system')
            }
          })
          this.loading = false
        } else {
          return false
        }
      })
    },
    getOtherQuery(query) {
      return Object.keys(query).reduce((acc, cur) => {
        if (cur !== 'redirect') {
          acc[cur] = query[cur]
        }
        return acc
      }, {})
    },
    getAuthCode() {
      getAuthCode().then(res => {
        this.$data.authCodeSrc = 'data:image/jpeg;base64,' + arrayBufferToBase64(res.data)
      })
    }
    // afterQRScan() {
    //   if (e.key === 'x-admin-oauth-code') {
    //     const code = getQueryObject(e.newValue)
    //     const codeMap = {
    //       wechat: 'code',
    //       tencent: 'code'
    //     }
    //     const type = codeMap[this.auth_type]
    //     const codeName = code[type]
    //     if (codeName) {
    //       this.$store.dispatch('LoginByThirdparty', codeName).then(() => {
    //         this.$router.push({ path: this.redirect || '/' })
    //       })
    //     } else {
    //       alert('第三方登录失败')
    //     }
    //   }
    // }
  }
}
</script>

<style lang="scss">
/* 修复input 背景不协调 和光标变色 */
/* Detail see https://github.com/PanJiaChen/vue-element-admin/pull/927 */

$bg:#283443;
$light_gray:#fff;
$cursor: #fff;

@supports (-webkit-mask: none) and (not (cater-color: $cursor)) {
  .login-container .el-input input {
    color: $cursor;
  }
}

/* reset element-ui css */
.login-container {
  .el-input {
    display: inline-block;
    height: 47px;
    width: 85%;

    input {
      background: transparent;
      border: 0px;
      -webkit-appearance: none;
      border-radius: 0px;
      padding: 12px 5px 12px 15px;
      color: $light_gray;
      height: 47px;
      caret-color: $cursor;

      &:-webkit-autofill {
        box-shadow: 0 0 0px 1000px $bg inset !important;
        -webkit-text-fill-color: $cursor !important;
      }
    }
  }

  .el-form-item {
    border: 1px solid rgba(255, 255, 255, 0.1);
    background: rgba(0, 0, 0, 0.1);
    border-radius: 5px;
    color: #454545;
  }
}
</style>

<style lang="scss" scoped>
$bg:#2d3a4b;
$dark_gray:#889aa4;
$light_gray:#eee;

.login-container {
  min-height: 100%;
  width: 100%;
  background: url('../../../public/1.jpg') no-repeat;
  overflow: hidden;

  .login-form {
    position: relative;
    width: 520px;
    max-width: 100%;
    padding: 160px 35px 0;
    margin: 0 auto;
    overflow: hidden;
  }

  .tips {
    font-size: 14px;
    color: #fff;
    margin-bottom: 10px;

    span {
      &:first-of-type {
        margin-right: 16px;
      }
    }
  }

  .svg-container {
    padding: 6px 5px 6px 15px;
    color: $dark_gray;
    vertical-align: middle;
    width: 30px;
    display: inline-block;
  }

  .title-container {
    position: relative;

    .title {
      font-size: 26px;
      color: $light_gray;
      margin: 0px auto 40px auto;
      text-align: center;
      font-weight: bold;
    }
  }

  .show-pwd {
    position: absolute;
    right: 10px;
    top: 7px;
    font-size: 16px;
    color: $dark_gray;
    cursor: pointer;
    user-select: none;
  }

  .thirdparty-button {
    position: absolute;
    right: 0;
    bottom: 6px;
  }

  @media only screen and (max-width: 470px) {
    .thirdparty-button {
      display: none;
    }
  }
}

.tip{
  color: #b3450e;
  margin-left: 25px;
  font: 15px bold;
  text-align: center;
  align-items: center;
}
.tip:hover{
  color: #9971fe;
  cursor: pointer;
}
</style>
