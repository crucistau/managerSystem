<template>

  <div>
    <!--搜索框-->
    <div style="padding: 10px 0">
      <el-input style="width: 200px;margin-right: 5px" placeholder="请输入公告名字" suffix-icon="el-icon-search"
                v-model="search.name"></el-input>
      <el-input style="width: 200px;margin-right: 5px" placeholder="请输入时间" suffix-icon="el-icon-search"
                v-model="search.time"></el-input>
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
      <el-table-column prop="name" label="公告标题" width="260" align="center">
      </el-table-column>
      <el-table-column prop="content" label="公告内容" align="center">
        <template slot-scope="scope">
          <el-button @click="view(scope.row.content)" type="primary">查看内容</el-button>
        </template>
      </el-table-column>
      <el-table-column prop="time" label="发布时间" width="240" align="center">
      </el-table-column>
      <el-table-column label="操作" align="center">
        <template slot-scope="scope">
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
        title="文章信息"
        :visible.sync="dialogFormVisible"
        width="80%">
      <el-form :model="formA" label-width="80px">
        <el-form-item label="文章标题">
          <el-input v-model="formA.name" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="文章内容">
          <mavon-editor ref="md" v-model="formA.content" :ishljs="true" @imgAdd="imgAdd"/>
        </el-form-item>
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
          <el-form-item label="文章标题">
            <el-input v-model="form.name" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item label="文章内容">
            <mavon-editor ref="md" v-model="form.content" :ishljs="true" @imgAdd="imgAdd"/>
          </el-form-item>
        </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancel()">取消</el-button>
        <el-button type="primary" @click="handleEdi()">确定</el-button>
      </div>
    </el-dialog>

<!--预览-->
    <el-dialog title="公告内容" :visible.sync="viewDialogVis" width="60%" >
      <el-card>
        <div>
          <mavon-editor
              class="md"
              :value="content"
              :subfield="false"
              :defaultOpen="'preview'"
              :toolbarsFlag="false"
              :editable="false"
              :scrollStyle="true"
              :ishljs="true"
          />
        </div>
      </el-card>
    </el-dialog>

  </div>

</template>

<script>

import axios from "@/utils/request";

export default {
  name: "Article",
  data() {
    return {
      tableData: '',  //表格数
      dialogFormVisible: false,//新增学生表单显示
      dialogFormVisibleEdit: false,//编辑学生数据表单显示
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")):{},
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
        time: ''
      },
      content: '',
      viewDialogVis: false
    }
  },

  created() {
    this.getAll()
    console.log(this.user)
  },

  methods: {
    view(content) {
      this.content = content
      this.viewDialogVis = true
    },
    // 绑定@imgAdd event
    imgAdd: function (pos, $file) {
      let $vm = this.$refs.md
      // 第一步.将图片上传到服务器.
      const formData = new FormData();
      formData.append('file', $file);
      axios({
        url: 'http://localhost:8081/file/upload',
        method: 'post',
        data: formData,
        headers: {'Content-Type': 'multipart/form-data'},
      }).then((res) => {
        res = JSON.parse(res)
        // 第二步.将返回的url替换到文本原位置![...](./0) -> ![...](url)
        $vm.$img2Url(pos, res);
      })
    },

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
      if (this.user.faculty != null){
        this.search.faculty = this.user.faculty
      }
      this.axios.get("/article/" + this.pagination.currentPage + "/" + this.pagination.pageSize, {
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
      this.axios.post("/article", this.formA).then(resp => {
        if (resp.code === '200') {
          //增添成功 关闭弹窗
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
      this.axios.post("/article", this.form).then(resp => {
        if (resp.code === '200') {
          //增添成功 关闭弹窗
          this.dialogFormVisibleEdit = false
          this.dialogFormVisible = false
          this.$message.success("公告修改成功");
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
      axios.get("/article/" + row.id).then((resp) => {
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
        axios.delete("/article/" + row.id).then(resp => {
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
      axios.post("/article/del/batch", ids).then(resp => {
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
  },

}
</script>

<style scoped>

</style>