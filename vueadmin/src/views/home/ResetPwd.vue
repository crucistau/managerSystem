<template>
  <div>
  <el-card style="width: 600px;padding: 20px;border: 1px solid #ccc">
    <el-form :model="userForm" status-icon :rules="rules" ref="userForm" label-width="100px" class="demo-ruleForm">
      <el-form-item label="旧密码" prop="oldpass">
        <el-input  show-password v-model.number="userForm.oldpass" ></el-input>
      </el-form-item>
      <el-form-item label="密码" prop="pass">
        <el-input type="password" show-password v-model="userForm.pass" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="确认密码" prop="checkPass">
        <el-input type="password"  show-password v-model="userForm.checkPass" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitForm()">提交</el-button>
        <el-button @click="resetForm('userForm')">重置</el-button>
        <el-button type="info" @click="dialogFormVisible = true">忘记密码</el-button>
      </el-form-item>
    </el-form>
  </el-card>
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
      <el-form-item label="密码:" prop="pass">
        <el-input type="password" show-password v-model="form.pass" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="resetPwd()">提交</el-button>
        <el-button @click="resetForm('form')">重置</el-button>
      </el-form-item>
    </el-form>
  </el-dialog>
  </div>
</template>

<script>
import { hex_md5 } from '@/utils/md5.js'
import user from "@/views/manage/User";
export default {
  name: "ResetPwd",
  data() {
    const validatePass = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入密码'));
      } else {
        if (this.userForm.checkPass !== '') {
          this.$refs.userForm.validateField('checkPass');
        }
        callback();
      }
    };
    const validatePass2 = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'));
      } else if (value !== this.userForm.pass ) {
        callback(new Error('两次输入密码不一致!'));
      } else {
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
      disabled: true,
      timer: 60,
      mainCode:'',
      formLabelWidth: '120px',
      dialogFormVisible: false,
      form:{
        email: '',
        checkCode:'' ,
        pass:'',
      },
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {},
      userForm: {
        oldpass:'',
        pass: '',
        checkPass: '',
      },
      rules: {
        oldpass:[
            { required: true, message: '请输入旧密码', trigger: 'blur' }
        ],
        pass: [
          { required: true, validator: validatePass, trigger: 'blur' }
        ],
        checkPass: [
          { required: true,validator: validatePass2, trigger: 'blur' }
        ]
      },
      rules2:{
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
    };

  },
  mounted() {
    console.log(this.mainCode)
    this.form.email = this.user.mail
  },
  methods: {
    submitForm: function () {
      this.$refs['userForm'].validate((valid) => {
        if (valid) {
          //旧密码判断
          let oldpwd = hex_md5(this.userForm.oldpass)
          if (oldpwd != this.user.password){
            this.$message.error("密码错误")
            this.resetForm('userForm')
            return false
          }else {
            //密码正确，进行修改
            switch (this.user.identity) {
                case "ROLE_ADMIN":
                  this.axios.put("/manage/changePwd",{
                      userId: this.user.userId,
                      password: this.userForm.pass
                    }
                  ).then(res=>{
                    if (res.code==='200'){
                      this.$store.commit("logout")
                      this.$message.success("请重新登录")
                    }
                }); break;
                case "ROLE_TEACHER":
                  this.axios.put("/teacher/changePwd",{
                        userId: this.user.userId,
                        password: this.userForm.pass
                      }
                  ).then(res=>{
                    if (res.code==='200'){
                      this.$store.commit("logout")
                      this.$message.success("请重新登录")
                    }
                  }); break;
                  case "ROLE_STUDENT":
                    this.axios.put("/student/changePwd",{
                          userId: this.user.userId,
                          password: this.userForm.pass
                        }
                    ).then(res=>{
                      if (res.code==='200'){
                        this.$store.commit("logout")
                        this.$message.success("请重新登录")
                      }
                    }); break;
            }
          }

        } else {
          console.log('error submit!!');
          return false;
        }
      });
    },
    resetForm(formName) {
      this.$refs[formName].resetFields();
    },

    resetPwd (){
      this.$refs['form'].validate((valid) =>{
        if (valid){
          if (this.form.checkCode == this.mainCode){
            switch (this.user.identity) {
              case "ROLE_ADMIN":
                this.axios.put("/manage/changePwd",{
                      userId: this.user.userId,
                      password: this.form.pass
                    }
                ).then(res=>{
                  if (res.code==='200'){
                    this.$store.commit("logout")
                    this.$message.success("请重新登录")
                  }
                }); break;
              case "ROLE_TEACHER":
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
              case "ROLE_STUDENT":
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
    //获取验证码
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
        }
    })
    },

    beforeClose(){
      this.mainCode =''
    }
  }
}
</script>

<style scoped>

</style>