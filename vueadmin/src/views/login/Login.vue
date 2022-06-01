<template>
<div>


    <div class="wrapper">
    <div style="margin: 200px auto; background-color: #fff; width: 350px; height: 400px; padding: 20px; border-radius: 10px">
      <div style="margin: 20px 0; text-align: center; font-size: 24px"><b>登 录</b></div>
      <el-form :model="user" :rules="rules" ref="userForm">
        <el-form-item prop="userId">
          <el-input size="medium" style="margin: 10px 0" prefix-icon="el-icon-user" v-model="user.userId"></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input size="medium" style="margin: 10px 0" prefix-icon="el-icon-lock" show-password v-model="user.password"></el-input>
        </el-form-item>
        <el-form-item >
          <div style="display: flex">
            <el-input prefix-icon="el-icon-key" size="medium" v-model="user.validCode" style="width: 50%;" placeholder="请输入验证码"></el-input>
            <ValidCode @input="createValidCode" ref="valid"/>
          </div>
        </el-form-item>
        <el-radio-group v-model="radio">
          <el-radio :label="1">学生</el-radio>
          <el-radio :label="2">教师</el-radio>
          <el-radio :label="3">管理员</el-radio>
        </el-radio-group>
        <el-form-item style="margin: 10px 0; text-align: right">
          <el-button type="primary" size="small"  autocomplete="off" @click="login">登录</el-button>
          <el-button type="warning" size="small"  autocomplete="off" @click="dialogFormVisible = true" >忘记密码</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>

  <el-dialog title="重置密码" :visible.sync="dialogFormVisible" width="40%"
             @close="beforeClose">
    <el-form :model="form" :rules="rules2" ref="form" status-icon label-width="100px" class="demo-Form" >
      <el-form-item label="Email:" prop="email" required>
        <el-row>
          <el-col :span="18" style="margin-right: 2px">
            <el-input v-model="form.email"></el-input>
          </el-col>
          <el-col :span="4">
            <el-button v-if="disabled" type="primary" @click="sendCode">发送验证码</el-button>
            <el-button v-show="!disabled"><span>{{timer}}</span>秒后重新获取</el-button>
          </el-col>
        </el-row>
      </el-form-item>
      <el-form-item label="验证码:" prop="checkCode">
        <el-input v-model="form.checkCode"></el-input>
      </el-form-item>
      <el-form-item label="账号:" prop="userId">
        <el-input  v-model="form.userId" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="密码:" prop="pass">
        <el-input type="password" show-password v-model="form.pass" autocomplete="off"></el-input>
      </el-form-item>
      <el-radio-group v-model="radio" style="margin-left: 100px;margin-bottom: 10px">
        <el-radio :label="1">学生</el-radio>
        <el-radio :label="2">教师</el-radio>
        <el-radio :label="3">管理员</el-radio>
      </el-radio-group>
      <el-form-item>
        <el-button type="primary" @click="resetPwd()">提交</el-button>
        <el-button @click="resetForm('form')">重置</el-button>
      </el-form-item>
    </el-form>
  </el-dialog>

</div>
</template>
<script>

import {setRoutes} from "@/router";
import { hex_md5 } from '@/utils/md5.js'
import ValidCode from "@/components/ValidCode";

export default {
  name: "Login",
  components: {
    ValidCode
  },
  data() {
    const validatePass = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入密码'));
      } else {
        if (this.form.checkPass !== '') {
          this.$refs.form.validateField('checkPass');
        }
        callback();
      }
    };
    const checkEmail = (rule, value, callback) => {
      const mailReg = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+/
      if (!value) {
        return callback(new Error('邮箱不能为空'))
      }
      setTimeout(() => {
        if (mailReg.test(value)) {
          callback()
        } else {
          callback(new Error('请输入正确的邮箱格式'))
        }
      }, 100)
    }
    return {
      validC: '',
      disabled: true,
      timer: 60,
      dialogFormVisible: false,
      radio: 1,
      form:{
        userId:'',
        email: '',
        checkCode:'' ,
        pass:'',
      },
      user: {
        userId: '20181506210209',
        password: 'admin',
        validCode:''
      },
      rules: {
        userId: [
          { required: true, message: '请输入学号', trigger: 'blur' },
          { max: 20, message: '长度为14个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 5  , message: '密码长度最少为6个', trigger: 'blur' }
        ],
      },
    rules2:{
        userId: [
          {required: true, message: "请输入账号", rigger: 'blur'}
          ],
        email:[
        {  validator: checkEmail, trigger: 'blur' },
      ],
          pass: [
        { required: true, validator: validatePass, trigger: 'blur' }
      ],
          checkCode:[
        {required: true, message: "请输入验证码", trigger: 'blur'}
      ]
    }
    }
  },
  mounted() {
  },
  methods: {

     login: function () {
      this.$refs['userForm'].validate((valid) => {
        if (valid) {  // 表单校验合法
          if (!this.user.validCode) {
            this.$message.error("请填写验证码")
            return
          }
          if(this.user.validCode.toLowerCase() != this.validC.toLowerCase()) {
            this.$message.error("验证码错误")
            this.$refs.valid.refreshCode()
            return
          }
          this.user.password = hex_md5(this.user.password);
          if (this.radio===1){
            this.axios.post("/student/login", this.user).then(res => {
              if (res.code === '200') {
                //登录成功，保存数据
                localStorage.setItem("menus", JSON.stringify(res.data.menus))
                localStorage.setItem("user", JSON.stringify(res.data))//存储用户信息到浏览器
                //动态设置当前用户的路由
                setRoutes()
                this.$router.push("/")
                this.$message.success(res.msg)
              } else {
                this.$message.error(res.msg)
              }
            })
          }else if (this.radio===2){
            //教师登录
            this.axios.post("/teacher/login", this.user).then(res =>{
              if (res.code==='200'){
                //登录成功，保存数据
                localStorage.setItem("menus", JSON.stringify(res.data.menus))
                localStorage.setItem("user", JSON.stringify(res.data))//存储用户信息到浏览器
                //动态设置当前用户的路由
                setRoutes()
                this.$router.push("/")
                this.$message.success(res.msg)
              } else {
                this.$message.error(res.msg)
              }
            })

          }else{
            //管理员登录
            this.axios.post("/manage/login", this.user).then(res =>{
              if (res.code==='200'){
                //登录成功，保存数据
                localStorage.setItem("menus", JSON.stringify(res.data.menus))
                localStorage.setItem("user", JSON.stringify(res.data))//存储用户信息到浏览器
                //动态设置当前用户的路由
                setRoutes()
                this.$router.push("/")
                this.$message.success(res.msg)
              } else {
                this.$message.error(res.msg)
              }
            })

          }
        } else {
          return false;
        }
      });
    },

    createValidCode(data){
       this.validC = data
    },

    resetForm(formName) {
      this.$refs[formName].resetFields();
    },


    resetPwd (){
      this.$refs['form'].validate((valid) =>{
        if (valid){
          //验证码通过
          if (this.form.checkCode == this.mainCode){
            console.log(this.radio)
            switch (this.radio) {
              case 1:
                this.axios.put("/manage/changePwd",{
                      userId: this.form.userId,
                      password: this.form.pass
                    }
                ).then(res=>{
                  if (res.code==='200'){
                    this.$store.commit("logout")
                    this.$message.success("请重新登录")
                  }
                }); break;
              case 2:
                this.axios.put("/teacher/changePwd",{
                      userId: this.user.userId,
                      password: this.form.pass
                    }
                ).then(res=>{
                  if (res.code==='200'){
                    this.$store.commit("logout")
                    this.$message.success("请重新登录")
                  }
                }); break;
              case 3:
                this.axios.put("/student/changePwd",{
                      userId: this.user.userId,
                      password: this.form.pass
                    }
                ).then(res=>{
                  if (res.code==='200'){
                    this.$store.commit("logout")
                    this.$message.success("请重新登录")
                  }
                }); break;
            }
          }else {
            this.$message.error("验证码错误")
            this.resetForm('form')
            return false
          }
        }else {
          return false
        }
      })
    },

    sendCode(){
      this.axios.put("/student/rePassword",{
        mail: this.form.email
      }).then(res=> {
        if (res.code == '200') {
          this.disabled = false
          const authTimer = setInterval(() => {
            this.timer--
            if (this.timer <= 0) {
              this.disabled = true
              this.timer = 60
              clearInterval(authTimer)
            }
          }, 1000)
          this.mainCode = res.data
          console.log(this.mainCode)
        }
      })
    },

    beforeClose(){
      this.mainCode =''
    }
    }
}
</script>

<style>
.wrapper {
  height: 100vh;
  background-image: linear-gradient(to bottom right, #FC466B , #3F5EFB);
  overflow: hidden;
}

</style>