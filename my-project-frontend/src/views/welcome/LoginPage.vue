<template>
  <div style="text-align: center;margin: 0 20px">
    <div style="margin-top: 150px">
      <div style="font-size: 25px;font-weight: bold">Login</div>
      <div style="font-size: 14px;color: grey">Please enter your username and password to log in before entering the system</div>
    </div>
    <div style="margin-top: 50px">
      <el-form :model="form" :rules="rules" ref="formRef">
        <el-form-item prop="username">
          <el-input v-model="form.username" maxlength="10" type="text" placeholder="Username/Email">
            <template #prefix>
              <el-icon>
                <User/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" maxlength="20" style="margin-top: 10px" placeholder="password">
            <template #prefix>
              <el-icon>
                <Lock/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-row style="margin-top: 5px">
          <el-col :span="12" style="text-align: left">
            <el-form-item prop="remember">
              <el-checkbox v-model="form.remember" label="Remember me"/>
            </el-form-item>
          </el-col>
          <el-col :span="12" style="text-align: right">
            <el-link @click="router.push('/forget')">Forgot your password?</el-link>
          </el-col>
        </el-row>
      </el-form>
    </div>
    <div style="margin-top: 40px">
      <el-button @click="userLogin()" style="width: 270px" type="success" plain>Log in now</el-button>
    </div>
    <el-divider>
      <span style="color: grey;font-size: 13px">No account</span>
    </el-divider>
    <div>
      <el-button style="width: 270px" @click="router.push('/register')" type="warning" plain>Sign up for an account</el-button>
    </div>
  </div>
</template>

<script setup>
import {User, Lock} from '@element-plus/icons-vue'
import router from "@/router";
import {reactive, ref} from "vue";
import {login} from '@/net'

const formRef = ref()
const form = reactive({
  username: '',
  password: '',
  remember: false
})

const rules = {
  username: [
    { required: true, message: 'Please enter a username' }
  ],
  password: [
    { required: true, message: 'Please enter your password'}
  ]
}

function userLogin() {
  formRef.value.validate((isValid) => {
    if(isValid) {
      const loadingInstance = ElLoading.service({
        lock: true,
        text: 'Processing, please wait...',
        background: 'rgba(0, 0, 0, 0.7)'
      });
      try {
        login(form.username, form.password, form.remember, () => router.push("/index"))
      }
      finally {
        loadingInstance.close();
      }
    }
  });
}
</script>

<style scoped>

</style>
