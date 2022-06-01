<template>
  <div>
    <!--搜索框-->
    <div style="padding: 10px 0">
      <el-input style="width: 200px" placeholder="请输入名字" suffix-icon="el-icon-search"
                v-model="search.stuName"></el-input>
      <el-input style="width: 200px" placeholder="请输入院系" suffix-icon="el-icon-position" class="ml-5"
                v-model="search.faculty" v-if=" user.identity == 'ROLE_ADMIN' && user.faculty ==''"></el-input>
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
      <el-table-column prop="teaName" label="名字" width="240" align="center">
      </el-table-column>
      <el-table-column prop="teaId" label="工号" width="240" align="center">
      </el-table-column>
      <el-table-column prop="email" label="电子邮件" width="240" align="center">
      </el-table-column>
      <el-table-column prop="faculty" label="学院" width="240" align="center">
      </el-table-column>
      <el-table-column label="操作" align="center">
        <template slot-scope="scope">
          <el-button type="success" size="mini" @click="handleUpdate(scope.row)">编辑 <i class="el-icon-edit"></i>
          </el-button>
          <el-button type="danger" size="mini" @click="handleDelete(scope.row)">删除 <i
              class="el-icon-remove-outline"></i></el-button>
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

    <!--新增学生数据-->
    <el-dialog
        title="新增教师数据"
        :visible.sync="dialogFormVisible"
        width="40%">
      <el-form :model="formA"  :rules="rules"  ref="formA" label-width="80px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="教师名称" prop="teaName">
              <el-input v-model="formA.teaName"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工号" prop="teaId">
              <el-input v-model="formA.teaId"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="formA.email"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="学院" prop="faculty">
              <el-input v-model="formA.faculty"/>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancel()">取消</el-button>
        <el-button type="primary" @click="handleAdd('form')">确定</el-button>
      </div>
    </el-dialog>

    <!--编辑数据-->
    <el-dialog
        title="编辑角色数据"
        :visible.sync="dialogFormVisibleEdit"
        width="40%"
    >
      <el-form :model="form"   label-width="80px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="教师名称" prop="teaName">
              <el-input v-model="form.teaName"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工号" prop="teaId">
              <el-input v-model="form.teaId"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="学院" prop="faculty">
              <el-input v-model="form.faculty"/>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancel()">取消</el-button>
        <el-button type="primary" @click="handleEdite()">确定</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>

export default {
  name: "User",
  data() {
    return {
      tableData: '',  //表格数据
      dialogFormVisible: false,//新增学生表单显示
      dialogFormVisibleEdit: false,//编辑学生数据表单显示
      menuVisible: false,
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")):{},
      //增添表单时的校验规则
      rules: {
        teaName: [
          {required: true, message: '请输入教师名称', trigger: 'blur'},
          {min: 2, max: 5, message: '长度在 2 到 5 个字符', trigger: 'blur'}
        ],
        teaId: [
          {required: true, message: '请输入工号', trigger: 'change'},
          {len: 14, message: '长度为14个字符', trigger: 'blur'},
          {type: 'number', message: '工号必须为数字值'}
        ],
        faculty: [
          {required: true, message: '请选择院系', trigger: 'change'}
        ]
      },
      multipleSelection: '',//复选框选中的数组

      pagination: {//分页相关模型数据
        currentPage: 1,//当前页码
        pageSize: 10,//每页显示的记录数
        total: 0 //总记录数
      },
      form: {},//编辑窗口数据
      formA: {},//新增窗口数据
      search: {//搜索框中的数据
        name: ''
      },

      props: {
        label: "name"
      }
    }
  },

  created() {
    this.getAll()
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
      if (this.user.faculty != null || this.user.identity=='ROLE_TEACHER'){
        //总管理员都不满足条件  分管理员与老师满足条件
        this.search.faculty = this.user.faculty
      }
      this.axios.get("/teacher/" + this.pagination.currentPage + "/" + this.pagination.pageSize, {
        params: {
          teaName: this.search.name,
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
      this.formA={}
      this.dialogFormVisibleEdit = false;
      this.dialogFormVisible = false;
      this.$message.info("当前操作取消");
    },

    //新增学生表单
    handleCreate() {
      this.dialogFormVisible = true
    },


    //增添学生信息
    handleAdd() {
      this.$refs['formA'].validate((valid) => {
        if (valid) {  // 表单校验合法
          this.axios.post("/teacher", this.formA).then(resp => {
            if (resp.code === '200') {
              //增添成功 关闭弹窗
              this.dialogFormVisible = false
              this.$message.success("保存成功");
              this.formA = ''
            } else {
              this.$message.error("保存失败");
            }
          }).finally(() => {
            //刷新页面
            this.getAll();
          });
        } else {//验证不通过
          return false;
        }
      })
    },

    handleEdite(){
      this.axios.put("/teacher", this.form).then(resp => {
        if (resp.code === '200') {
          //增添成功 关闭弹窗
          this.dialogFormVisibleEdit = false
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
      this.axios.get("/teacher/" + row.id).then((resp) => {
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
        this.axios.delete("/teacher/" + row.id).then(resp => {
          if (resp.code === '200') {
            this.$message.success(resp.msg);
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
      this.axios.post("/teacher/del/batch", ids).then(resp => {
        if (resp.code === '200') {
          this.$message.success(resp.msg);
        } else {
          this.$message.error(resp.msg);
        }
      }).finally(() => {
        this.getAll();
      })
    },

    //按钮重置
    reset() {
      this.search.stuName = ''
      this.search.stuId = ''
      this.search.faculty = ''
    },

  },


}
</script>

<style scoped>
.headerBgC {
  background: #eee !important;
}
</style>