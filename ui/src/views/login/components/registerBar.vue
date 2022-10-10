<template>
  <div class="login-container">
    <el-form ref="loginForm" class="login-form" autocomplete="on" label-position="left">

      <div class="title-container">
        <h3 class="title">用 户 注 册</h3>
      </div>

      <el-form-item v-if="!ifVerifySendEmail">
        <span class="svg-container">
          <svg-icon icon-class="email" />
        </span>
        <el-input
          ref="userEmail"
          v-model="userEmail"
          placeholder="用户邮箱号"
          type="text"
          tabindex="1"
          autocomplete="on"
        />
      </el-form-item>

      <el-form-item v-if="ifSendEmail && !ifVerifySendEmail">
        <span class="svg-container">
          <i class="el-icon-message-solid" />
        </span>
        <el-input
          ref="authCode"
          v-model="authCode"
          placeholder="验证码"
          type="text"
          tabindex="1"
          autocomplete="on"
        />
      </el-form-item>

      <el-button v-if="!ifVerifySendEmail" type="primary" style="width:30%;margin-bottom: 15px;margin-left: 300px" @click="getEmailCode">发送验证码</el-button>

      <el-tooltip v-if="ifVerifySendEmail" content="Caps lock is On" placement="right" manual>
        <el-form-item>
          <span class="svg-container">
            <svg-icon icon-class="password" />
          </span>
          <el-input
            ref="userPassword"
            v-model="userPassword"
            placeholder="密码"
            tabindex="2"
            autocomplete="on"
          />
          <span class="show-pwd" @click="showPwd">
            <svg-icon :icon-class="passwordType === 'password' ? 'eye' : 'eye-open'" />
          </span>
        </el-form-item>
      </el-tooltip>

      <el-form-item v-if="ifVerifySendEmail">
        <span class="svg-container">
          <svg-icon icon-class="password" />
        </span>
        <el-input
          ref="userConfirmPassword"
          v-model="userConfirmPassword"
          placeholder="确认密码"
          type="text"
          tabindex="1"
          autocomplete="on"
        />
        <span class="show-pwd" @click="showConfirmPwd">
          <svg-icon :icon-class="passwordType === 'password' ? 'eye' : 'eye-open'" />
        </span>
      </el-form-item>

      <el-button v-if="!ifVerifySendEmail && ifSendEmail" :loading="loading" type="primary" style="width:100%;margin-bottom:30px;" @click="verifySendEmail">下一步</el-button>
      <el-button v-if="ifVerifySendEmail" :loading="loading" type="primary" style="width:100%;margin-bottom:30px;" @click="register">注册</el-button>
      <el-button type="primary" style="width:30%;margin-left: 300px" @click="BackLogin">返回登录界面</el-button>

    </el-form>
  </div>
</template>

<script>
import { validEmail } from '@/utils/validate'
import { register, getEmailCode, verifyEmailCode } from '@/api/user'

export default {
  name: 'Login',
  data() {
    return {
      authCode: '',
      userEmail: '',
      userPassword: '',
      userConfirmPassword: '',
      passwordType: 'password',
      capsTooltip: false,
      loading: false,
      redirect: undefined,
      otherQuery: {},
      ifSendEmail: false,
      ifVerifySendEmail: false
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
  created() {
    // window.addEventListener('storage', this.afterQRScan)
  },
  mounted() {
  },
  destroyed() {
    // window.removeEventListener('storage', this.afterQRScan)
  },
  methods: {
    BackLogin() {
      this.$emit('BackLogin')
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
    showConfirmPwd() {
      if (this.passwordType === 'password') {
        this.passwordType = ''
      } else {
        this.passwordType = 'password'
      }
      this.$nextTick(() => {
        this.$refs.userConfirmPassword.focus()
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
    getEmailCode() {
      if (validEmail(this.userEmail)) {
        getEmailCode(this.userEmail).then(res => {
          if (res.data.code === 200) {
            this.ifSendEmail = true
          }
        })
      } else {
        this.$message.error('请输入正确的邮箱号!')
        this.$refs.userEmail.focus()
      }
    },
    verifySendEmail() {
      if (this.authCode.length === 6 && this.userEmail.length !== 0) {
        verifyEmailCode(this.authCode).then(res => {
          if (res.data.code === 200) {
            this.ifVerifySendEmail = true
          }
        })
      } else {
        this.$message.error('请输入正确的验证码!')
        this.$refs.authCode.focus()
      }
    },
    register() {
      if (this.userPassword.length === 6 && this.userPassword === this.userConfirmPassword) {
        this.loading = true
        register(this.userEmail, this.userPassword, this.userConfirmPassword).then(res => {
          if (res.data.code === 200) {
            this.loading = false
            this.$message.success('注册成功,开始登录')
            this.BackLogin()
          }
        })
      } else if (this.userPassword === '') {
        this.$message.error('输入的密码长度不小于6位!')
        this.$refs.userPassword.focus()
      } else {
        this.$message.error('两次输入的密码不一致!')
        this.$refs.userConfirmPassword.focus()
      }
    }
  }
}
</script>

<style lang="scss">
$bg:#283443;
$light_gray:#fff;
$cursor: #fff;
@supports (-webkit-mask: none) and (not (cater-color: $cursor)) {
  .login-container .el-input input {
    color: $cursor;
  }
}
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
  background: url('../../../../public/1.jpg') no-repeat;
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
</style>

