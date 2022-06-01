<template>

  <div>
    <!--搜索框-->
    <div style="padding: 10px 0">
      <el-input style="width: 200px;margin-right: 5px" placeholder="请输入课题名字" suffix-icon="el-icon-search"
                v-model="search.name"></el-input>
      <el-input style="width: 200px;margin-right: 5px" placeholder="请输入学院名字" suffix-icon="el-icon-search"
                v-model="search.faculty" v-if=" user.identity == 'ROLE_ADMIN' && user.faculty == ''  "></el-input>
      <el-input style="width: 200px;margin-right: 5px" placeholder="请输入老师名称" suffix-icon="el-icon-search"
                v-model="search.teacherName"></el-input>
      <el-button class="ml-5" type="search" @click="getAll">搜索</el-button>
      <el-button class="ml-5" type="warning" @click="reset">重置</el-button>
    </div>
    <div style="margin: 10px 0">
      <el-button type="primary" @click="handleCreate()">新建 <i class="el-icon-circle-plus-outline"></i></el-button>
      <el-popconfirm
          class="ml-5"
          confirm-button-text='确定'
          cancel-button-text='我再想想'
          icon="el-icon-info"
          icon-color="red"
          title="您确定批量删除这些数据吗？"
          @confirm="del"
      >
        <el-button type="danger" slot="reference">批量删除 <i class="el-icon-remove-outline"></i></el-button>
      </el-popconfirm>
      <el-upload action="http://localhost:8081/student/import" :show-file-list="false" accept="xlsx" :on-success="handleExcelImportSuccess" style="display: inline-block">
        <el-button type="primary" class="ml-5">导入 <i class="el-icon-bottom"></i></el-button>
      </el-upload>
      <el-button type="primary" @click="exp" class="ml-5">导出<i class="el-icon-top"></i></el-button>
    </div>

    <el-table :data="tableData" border strip :header-cell-style="{background:'#eee',color:'black' }"
              @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center"></el-table-column>
      <el-table-column
          type="index"
          label="序号"
          width="70"
          align="center">
      </el-table-column>
      <el-table-column prop="subjectName" label="课题名称" width="260" align="center">
      </el-table-column>
      <el-table-column prop="faculty" label="所属学院" width="240" align="center">
      </el-table-column>
      <el-table-column prop="teacher" label="所属老师" width="120" align="center">
      </el-table-column>
      <el-table-column prop="secretary" label="教学秘书" width="120" align="center">
      </el-table-column>
      <el-table-column prop="state" label="状态" width="90" align="center">
        <template slot-scope="scope">
          <el-tag type="success" v-if="scope.row.state === true">已选中</el-tag>
          <el-tag type="danger" v-else>未选中</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center">
        <template slot-scope="scope">
          <el-button type="primary" v-if="user.identity != 'ROLE_STUDENT' " @click="handleStu(scope.row.id)">查看学生</el-button>
          <el-button type="success" size="mini" @click="handleUpdate(scope.row)">编辑 <i class="el-icon-edit"></i>
          </el-button>
          <el-button type="danger" size="mini" @click="handleDelete(scope.row)">删除 <i class="el-icon-remove-outline"></i></el-button>
        </template>

      </el-table-column>
    </el-table>
    <!--分页工具条-->
    <div class="block" style="padding: 10px 0">
      <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pagination.currentPage"
          :page-sizes="[5, 10, 15, 20]"
          :page-size="pagination.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total">
      </el-pagination>
    </div>
    <!--新增数据-->
    <el-dialog
        title="新增课题"
        :visible.sync="dialogFormVisible"
        width="40%">
      <el-form :model="formA" label-width="80px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="课题名称" prop="name">
              <el-input v-model="formA.subjectName"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属学院" prop="faculty">
              <el-input v-model="formA.faculty"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="教师名称" prop="name">
              <el-input v-model="formA.teacher"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="教学秘书" prop="faculty">
              <el-input v-model="formA.secretary"/>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancel()">取消</el-button>
        <el-button type="primary" @click="handleAdd()">确定</el-button>
      </div>
    </el-dialog>

    <!--编辑数据-->
    <el-dialog
        title="编辑角色数据"
        :visible.sync="dialogFormVisibleEdit"
        width="40%"
    >
        <el-form :model="form" label-width="80px">
          <el-row>
            <el-col :span="12">
              <el-form-item label="课题名称" prop="name">
                <el-input v-model="form.subjectName"/>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="所属学院" prop="faculty">
                <el-input v-model="form.faculty"/>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12">
              <el-form-item label="教师名称" prop="name">
                <el-input v-model="form.teacher"/>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="教学秘书" prop="faculty">
                <el-input v-model="form.secretary"/>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancel()">取消</el-button>
        <el-button type="primary" @click="handleEdi()">确定</el-button>
      </div>
    </el-dialog>

    <!--选中学生-->
    <el-dialog
        title="所选学生"
        :visible.sync="dialogFormVisibleEdit2"
        width="40%"
        style="padding-bottom: 5px"
    >
      <el-table
          :data="tableStu"
          border strip
          :header-cell-style="{background:'#eee',color:'black' }"
          style="padding: 10px 10px"
          >
        <el-table-column
            prop="stuName"
            label="学生姓名">
        </el-table-column>
        <el-table-column
            prop="stuId"
            label="学号">
        </el-table-column>
        <el-table-column label="操作">
          <template slot-scope="scope">
          <el-button type="primary" @click="selectStu(scope.row)"  >选中</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>

</template>

<script>

import axios from "@/utils/request";

export default {
  name: "Subject",
  data() {
    return {
      tableData: '',  //表格数据
      tableStu: [], //选课学生列表
      dialogFormVisible: false,//新增学生表单显示
      dialogFormVisibleEdit: false,//编辑学生数据表单显示
      dialogFormVisibleEdit2: false,
      menuVisible: false,
      subjectId:'',
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")):{},
      //增添表单时的校验规则
      // rules: {
      //   stuName: [
      //     {required: true, message: '请输入学生名称', trigger: 'blur'},
      //     {min: 2, max: 5, message: '长度在 2 到 5 个字符', trigger: 'blur'}
      //   ],
      //   stuId: [
      //     {required: true, message: '请输入学号', trigger: 'change'},
      //     {len: 1, message: '长度为14个字符', trigger: 'blur'},
      //     {type: 'number', message: '学号必须为数字值'}
      //   ],
      //   faculty: [
      //     {required: true, message: '请选择院系', trigger: 'change'}
      //   ],
      //   identity: [{required: true, message: '请输入身份', trigger: 'change'}]
      // },

      multipleSelection: '',//复选框选中的数组

      pagination: {//分页相关模型数据
        currentPage: 1,//当前页码
        pageSize: 10,//每页显示的记录数
        total: 0 //总记录数
      },

      form: {},//编辑窗口数据
      formA: {},//新增窗口数据

      search: {//搜索框中的数据
        name: '',
        faculty: '',
        teacherName: ''
      },

      //教师相关信息
      teacher: {
        id: '',//教师主键
        faculty: '',
        subjectId:'',
        teacherName: ''
      },
      props: {
        label: "name"
      }
    }
  },

  created() {
    this.getAll()
    console.log(this.user)
  },

  methods: {
    //改变页的大小
    handleSizeChange(val) {
      this.pagination.pageSize = val;
      this.getAll()
    },

    //改变当前页
    handleCurrentChange(val) {
      this.pagination.currentPage = val;
      this.getAll()
    },

    //分页+条件查询
    getAll() {
      //管理员为学院管理员
      if (this.user.faculty != ''){
        console.log("==================")
        console.log(this.user.faculty)
        this.search.faculty = this.user.faculty
      }
      this.axios.get("/subject/" + this.pagination.currentPage + "/" + this.pagination.pageSize, {
        params: {
          subjectName: this.search.name,
          faculty: this.search.faculty,
          teacherId: this.search.teacherName
        }
      }).then(resp => {
        if (resp.code ==='200'){
          this.tableData = resp.data.records;
          this.pagination.total = resp.data.total;
        }else{
          this.$message.info(resp.msg)
        }
      })
    },

    //取消功能
    cancel() {
      this.dialogFormVisibleEdit = false;
      this.dialogFormVisible = false;
      this.$message.info("当前操作取消");
    },

    //新增学生表单
    handleCreate() {
      var user = JSON.parse(localStorage.getItem("user"))
      if (user.identity == "ROLE_TEACHER"){
        this.formA.faculty =  user.faculty
        this.formA.teacherName = user.userName
      }
      this.dialogFormVisible = true
    },


    //增添学生信息
    handleAdd() {
      this.axios.post("/subject", this.formA).then(resp => {
        if (resp.code === '200') {
          //增添成功 关闭弹窗
          this.dialogFormVisibleEdit = false
          this.dialogFormVisible = false
          this.$message.success("保存成功");
          this.formA = {}
        } else {
          this.$message.error("保存失败");
        }
      }).finally(() => {
        //刷新页面
        this.getAll();
      });
    },

    //增添学生信息
    handleEdi() {
      this.axios.post("/subject", this.form).then(resp => {
        if (resp.code === '200') {
          //增添成功 关闭弹窗
          this.dialogFormVisibleEdit = false
          this.dialogFormVisible = false
          this.$message.success("保存成功");
          this.form = ''
        } else {
          this.$message.error("保存失败");
        }
      }).finally(() => {
        //刷新页面
        this.getAll();
      });
    },
    //弹出编辑窗口
    handleUpdate(row) {
      //将数据表示在编辑窗口上
      axios.get("/subject/" + row.id).then((resp) => {
        console.log(resp)
        if (resp.code === '200' && resp.data != null) {
          this.dialogFormVisibleEdit = true;
          this.form = resp.data;//显示数据
        } else {
          this.$message.error("数据同步失败，自动刷新");
        }
      }).finally(() => {
        //刷新页面
        this.getAll();
      });
    },

    // 删除
    handleDelete(row) {
      this.$confirm("此操作将永久删除当前信息，是否继续？", "提示", {type: "info"}).then(() => {
        axios.delete("/subject/" + row.id).then(resp => {
          if (resp.code === '200') {
            this.$message.success("删除成功");
          } else {
            this.$message.error(resp.msg);
          }
        }).finally(() => {
          //刷新页面
          this.getAll();
        });
      }).catch(() => {
        this.$message.info("取消操作");
      });
    },


    handleSelectionChange(val) {
      this.multipleSelection = val;
    },

    //批量删除
    del() {
      let ids = this.multipleSelection.map(v => v.id); //[{},{},{}]=>[1,2,4]
      console.log(ids);
      axios.post("/subject/del/batch", ids).then(resp => {
        if (resp.code === '200') {
          this.$message.success("删除成功");
        } else {
          this.$message.error(resp.msg);
        }
      }).finally(() => {
        this.getAll();
      })
    },

    //按钮重置
    reset() {
      this.search.name = ''
      this.search.faculty = ''
      this.search.teacherName = ''
    },

    //查看对应课程所选的学生
    handleStu(id){
      this.subjectId = id;//将该课程号传给subjectId
      this.dialogFormVisibleEdit2 = true;
      this.axios.get("/subject/stu/"+id).then(res=>{
        if (res.code==='200'){
          this.tableStu = res.data
        }
      })
    },

    //设置该学生为该课程唯一选中
    selectStu(row){
      //将学生Id传入后台然后清除该学生选择的其他课程，最后设置该课题被stuId选中  （stuId,subjectId）
      this.axios.put("/subject/setStuAndSub",{
          studentId: row.stuId,
          subjectId: this.subjectId
      }).then(res=>{
        if(res.code==='200'){
          this.dialogFormVisibleEdit2 = false;
          this.$message.success("成功选中")
          this.getAll()//刷新全局
        }else{
          this.$message.error(res.msg)
        }
      })
    },

    //导出
    exp(){
      window.open("http://localhost:8081/subject/export")
    },

    handleExcelImportSuccess(){
      this.$message.success("导入成功")
      this.getAll()
    }



  },

}
</script>

<style scoped>

</style>