<template>
<!--展示个人信息页面-->
  <el-card style="width: 600px;padding: 20px;border: 1px solid #ccc">
    <el-form :model="studentForm" :rules="rules" ref="stuForm" label-width="80px">
      <el-row>
        <el-col :span="12">
          <el-form-item label="学生姓名" prop="stuName">
            <el-input v-model="studentForm.stuName"/>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="身份" prop="identity" >
            <el-input v-model="studentForm.identity" :disabled="true"/>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col>
          <el-form-item label="学号" prop="stuId">
            <el-input v-model.number="studentForm.stuId" :disabled="true"/>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="studentForm.email"/>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="学院" prop="faculty">
            <el-select v-model="studentForm.faculty" placeholder="请选择院系" :disabled="true">
              <el-option v-for="item in facultys" :key="item" v-bind:label="item" v-bind:value="item"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item >
        <el-button type="primary" @click="handleAdd('studentForm')">确定</el-button>
        <el-button type="success" @click="changePwd">修改密码</el-button>
      </el-form-item>
    </el-form>
  </el-card>

<!--  <el-dialog-->
<!--      title="修改密码"-->
<!--      :visible.sync="dialogFormVisibleEdit4"-->
<!--      width="40%"-->
<!--      :before-close="handleClose">-->
<!--    <el-form  :model="student" label-width="80px" status-icon :rules="rules" ref="ruleForm">-->
<!--      <el-row>-->
<!--        <el-col>-->
<!--          <el-form-item label="学生姓名" prop="stuName">-->
<!--            <el-input v-model="student.stuName" placeholder="请输入旧密码"/>-->
<!--          </el-form-item>-->
<!--        </el-col>-->
<!--      </el-row>-->
<!--      <el-col>-->
<!--        <el-form-item label="密码" prop="pass">-->
<!--          <el-input v-model="student.password" placeholder="请输入新密码"/>-->
<!--        </el-form-item>-->
<!--      </el-col>-->
<!--      <el-col>-->
<!--        <el-form-item label="密码" prop="checkPass">-->
<!--          <el-input v-model="student.checkPwd" placeholder="请再次输入新密码"/>-->
<!--        </el-form-item>-->
<!--      </el-col>-->
<!--    </el-form>-->
<!--    <div slot="footer" class="dialog-footer">-->
<!--      <el-button @click="cancel()">取消</el-button>-->
<!--      <el-button type="primary" @click="handleAdd()">确定</el-button>-->
<!--    </div>-->
<!--  </el-dialog>-->
</template>

<script>


export default {
  name: "Person",

  data(){
    // var validatePass = (rule, value, callback) => {
    //   if (value === '') {
    //     callback(new Error('请输入新密码'));
    //   } else {
    //     if (this.ruleForm.checkPass !== '') {
    //       this.$refs.ruleForm.validateField('checkPass');
    //     }
    //     callback();
    //   }
    // };
    // var validatePass2 = (rule, value, callback) => {
    //   if (value === '') {
    //     callback(new Error('请再次输入密码'));
    //   } else if (value !== this.ruleForm.pass) {
    //     callback(new Error('两次输入密码不一致!'));
    //   } else {
    //     callback();
    //   }
    // };
    return{
      studentForm: {},
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")):{},
      dialogFormVisibleEdit: false,
      dialogFormVisibleEdit4: false
      //检验表单规则
      // rules: {
      //   pass: [
      //     { validator: validatePass, trigger: 'blur' }
      //   ],
      //   checkPass: [
      //     { validator: validatePass2, trigger: 'blur' }
      //   ]
      // },

       //检验表单数据
      // ruleForm: {
      //   pass: '',
      //   checkPass: ''
      // }


    }
  },

  created() {
    this.studentForm = this.user.student
    console.log(this.user)
  },

  methods: {
    //修改数据
    handleAdd(){
      this.axios.put("/student", this.studentForm).then(resp => {
        if (resp.flag) {
          this.$message.success(resp.msg);
          //触发父级更新User的方法
          this.$emit("refreshUser");

          //更新浏览器缓存
          this.axios.post("/student/login", this.user).then(resp=>{
            resp.token = JSON.parse(localStorage.getItem("user")).token;
            localStorage.setItem("user",JSON.stringify(res.data))//存储用户信息到浏览器
          })
        } else {
          this.$message.error(resp.msg);
        }
      })
    },

    //修改密码
    changePwd(){
      this.dialogFormVisibleEdit = true;
    }
    },


}
</script>

<style scoped>

</style>