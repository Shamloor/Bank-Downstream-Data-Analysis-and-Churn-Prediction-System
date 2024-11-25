<template>
    <div>
        <div style="margin: 30px 20px">
            <el-steps :active="active" finish-status="success" align-center>
                <el-step title="Verify your email" />
                <el-step title="Reset your password" />
            </el-steps>
        </div>
        <transition name="el-fade-in-linear" mode="out-in">
            <div style="text-align: center;margin: 0 20px;height: 100%" v-if="active === 0">
                <div style="margin-top: 80px">
                    <div style="font-size: 25px;font-weight: bold">Reset your password</div>
                    <div style="font-size: 14px;color: grey">Please enter the email address where you want to reset your password</div>
                </div>
                <div style="margin-top: 50px">
                    <el-form :model="form" :rules="rules" @validate="onValidate" ref="formRef">
                        <el-form-item prop="email">
                            <el-input v-model="form.email" type="email" placeholder="Email address">
                                <template #prefix>
                                    <el-icon><Message /></el-icon>
                                </template>
                            </el-input>
                        </el-form-item>
                        <el-form-item prop="code">
                            <el-row :gutter="10" style="width: 100%">
                                <el-col :span="17">
                                    <el-input v-model="form.code" :maxlength="6" type="text" placeholder="Please enter a verification code">
                                        <template #prefix>
                                            <el-icon><EditPen /></el-icon>
                                        </template>
                                    </el-input>
                                </el-col>
                                <el-col :span="5">
                                    <el-button type="success" @click="validateEmail"
                                               :disabled="!isEmailValid || coldTime > 0">
                                        {{coldTime > 0 ? 'Please wait ' + coldTime + ' s' : 'Get a verification code'}}
                                    </el-button>
                                </el-col>
                            </el-row>
                        </el-form-item>
                    </el-form>
                </div>
                <div style="margin-top: 70px">
                    <el-button @click="confirmReset()" style="width: 270px;" type="danger" plain>Start resetting your password</el-button>
                </div>
            </div>
        </transition>
        <transition name="el-fade-in-linear" mode="out-in">
            <div style="text-align: center;margin: 0 20px;height: 100%" v-if="active === 1">
                <div style="margin-top: 80px">
                    <div style="font-size: 25px;font-weight: bold">Reset your password</div>
                    <div style="font-size: 14px;color: grey">Please fill in your new password and remember it to prevent it from being lost</div>
                </div>
                <div style="margin-top: 50px">
                    <el-form :model="form" :rules="rules" @validate="onValidate" ref="formRef">
                        <el-form-item prop="password">
                            <el-input v-model="form.password" :maxlength="16" type="password" placeholder="New passwords">
                                <template #prefix>
                                    <el-icon><Lock /></el-icon>
                                </template>
                            </el-input>
                        </el-form-item>
                        <el-form-item prop="password_repeat">
                            <el-input v-model="form.password_repeat" :maxlength="16" type="password" placeholder="Repeat the new password">
                                <template #prefix>
                                    <el-icon><Lock /></el-icon>
                                </template>
                            </el-input>
                        </el-form-item>
                    </el-form>
                </div>
                <div style="margin-top: 70px">
                    <el-button @click="doReset()" style="width: 270px;" type="danger" plain>Reset your password immediately</el-button>
                </div>
            </div>
        </transition>
    </div>
</template>

<script setup>
import {reactive, ref} from "vue";
import {EditPen, Lock, Message} from "@element-plus/icons-vue";
import {get, post} from "@/net";
import {ElMessage} from "element-plus";
import router from "@/router";

const active = ref(0)

const form = reactive({
    email: '',
    code: '',
    password: '',
    password_repeat: '',
})

const validatePassword = (rule, value, callback) => {
    if (value === '') {
        callback(new Error('Please enter your password again'))
    } else if (value !== form.password) {
        callback(new Error("The password entered twice is inconsistent"))
    } else {
        callback()
    }
}

const rules = {
    email: [
        { required: true, message: 'Please enter your e-mail address', trigger: 'blur' },
        {type: 'email', message: 'Please enter a valid email address', trigger: ['blur', 'change']}
    ],
    code: [
        { required: true, message: 'Please enter the verification code you obtained', trigger: 'blur' },
    ],
    password: [
        { required: true, message: 'Please enter your password', trigger: 'blur' },
        { min: 6, max: 16, message: 'The password must be between 6-16 characters long', trigger: ['blur'] }
    ],
    password_repeat: [
        { validator: validatePassword, trigger: ['blur', 'change'] },
    ],
}

const formRef = ref()
const isEmailValid = ref(false)
const coldTime = ref(0)

const onValidate = (prop, isValid) => {
    if(prop === 'email')
        isEmailValid.value = isValid
}

const validateEmail = () => {
    coldTime.value = 60
    get(`/api/auth/ask-code?email=${form.email}&type=reset`, () => {
        ElMessage.success(`A verification code has been sent to your email: ${form.email}, Please pay attention to check`)
        const handle = setInterval(() => {
          coldTime.value--
          if(coldTime.value === 0) {
            clearInterval(handle)
          }
        }, 1000)
    }, (message) => {
        ElMessage.warning(message)
        coldTime.value = 0
    })
}

const confirmReset = () => {
    formRef.value.validate((isValid) => {
        if(isValid) {
            post('/api/auth/reset-confirm', {
                email: form.email,
                code: form.code
            }, () => active.value++)
        }
    })
}

const doReset = () => {
    formRef.value.validate((isValid) => {
        if(isValid) {
            post('/api/auth/reset-password', {
                email: form.email,
                code: form.code,
                password: form.password
            }, () => {
                ElMessage.success('The password is successfully reset, please log in again')
                router.push('/')
            })
        }
    })
}

</script>

<style scoped>

</style>
