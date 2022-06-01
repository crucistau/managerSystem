<template>
  <div>
    <!--搜索框-->
    <div style="padding: 10px 0">
      <el-input style="width: 200px;margin-right: 5px" placeholder="请输入课题名字" suffix-icon="el-icon-search"
                v-model="search.name"></el-input>
      <el-input style="width: 200px;margin-right: 5px" placeholder="请输入学院名字" suffix-icon="el-icon-search"
                v-model="search.faculty"></el-input>
      <el-input style="width: 200px;margin-right: 5px" placeholder="请输入老师名称" suffix-icon="el-icon-search"
                v-model="search.teacherName"></el-input>
      <el-button class="ml-5" type="search" @click="getAll">搜索</el-button>
      <el-button class="ml-5" type="warning" @click="reset">重置</el-button>
      <el-button class="ml-5" type="info" @click="searchSub">查看选课</el-button>
    </div>

    <el-table :data="tableData" border strip :header-cell-style="{background:'#eee',color:'black' }"
    >
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
      <el-table-column prop="state" label="状态" width="120" align="center">
        <template slot-scope="scope">
          <el-button type="success" v-if="scope.row.state == true">已选完</el-button>
          <el-button type="danger" v-if="scope.row.state == false">未选</el-button>
        </template>
      </el-table-column>
      <el-table-column label="操作"   align="center">
        <template slot-scope="scope">
          <el-button type="primary" size="mini" v-if="scope.row.state == false && user.identity=='ROLE_STUDENT'" @click="handleChange(scope.row)">选择<i class="el-icon-check"></i></el-button>
          <el-button type="success" size="mini" v-if="user.identity!= 'ROLE_STUDENT'" @click="searchsss(scope.row)">查看学生</el-button>
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

    <el-dialog
        title="所选课题"
        :visible.sync="dialogFormVisible"
        width="40%"
        style="padding-bottom: 5px"
    >
      <el-table
          :data="tableSub"
          border strip
          :header-cell-style="{background:'#eee',color:'black' }"
          style="padding: 10px 10px"
      >
        <el-table-column
            prop="subjectName"
            label="课题名称" align="center">
        </el-table-column>
        <el-table-column
            prop="teacher"
            label="指导老师" align="center">
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>

</template>

<script>

export default {
  name: "Selsubject",
  data() {
    return {
      tableData: [],  //表格数据
      tableSub: [],
      tableStu: [], //选课学生列表
      dialogFormVisible: false,//新增学生表单显示
      menuVisible: false,
      menuData: [],
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")):{},

      multipleSelection: '',//复选框选中的数组

      pagination: {//分页相关模型数据
        currentPage: 1,//当前页码
        pageSize: 10,//每页显示的记录数
        total: 0 //总记录数
      },

      search: {//搜索框中的数据
        name: '',
        faculty: '',
        teacherName: ''
      },

      StuSub:{
        stuId:'',
        subjectNum:''
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
      this.axios.get("/subject/" + this.pagination.currentPage + "/" + this.pagination.pageSize, {
        params: {
          subjectName: this.search.name,
          faculty: this.search.faculty,
          teacherId: this.search.teacherName
        }
      }).then(resp => {
        if (resp.code === '200') {
          this.tableData = resp.data.records;
          this.pagination.total = resp.data.total;
        } else {
          this.$message.info(resp.msg)
        }
      })
    },

      //按钮重置
      reset(){
        this.search.name = ''
        this.search.faculty = ''
        this.search.teacherName = ''
      },
      //学生确定选课
      handleChange(row){
        let subId=row.id
        let stuId = parseInt(this.user.userId)
        this.$confirm('是否选中此课题?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'info'
        }).then(() => {
          this.axios.get("/subject/select/"+subId+"/"+stuId).then(res =>{
            if (res.code === '200'){
              this.$message.success(res.msg)
            }else{
              this.$message.error(res.msg)
            }
          })
        }).catch(() => {
          this.$message({
            type: 'info',
            message: '已取消选择'
          });
        });
      },

    //查看该课题对应的课程
    searchsss(row){
      this.axios.get('/subject/stu/'+row.id).then(res=>{
        if (res.code==='200'){
          this.tableStu = res.data
        }
      })
    },

    //根据学生Id查询课程信息
      searchSub(){
        this.axios.get('/subject/sub/'+this.user.userId).then(res=>{
          if (res.code==='200'){
            this.tableSub  = res.data
          }
        })
        this.dialogFormVisible = true
      },

    }
}
</script>

<style scoped>

</style>