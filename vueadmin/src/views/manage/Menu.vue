<template>
  <div>
    <!--搜索框-->
    <div style="padding: 10px 0">
      <el-input style="width: 200px" placeholder="请输入菜单名称" suffix-icon="el-icon-search"
                v-model="search.name"></el-input>
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
        <el-button type="danger" slot="reference" >批量删除 <i class="el-icon-remove-outline"></i></el-button>
      </el-popconfirm>
    </div>

    <el-table :data="tableData" border strip :header-cell-style="{background:'#eee',color:'black' }"
              @selection-change="handleSelectionChange"
              row-key="id"

    >
      <el-table-column type="selection" width="55" align="center" ></el-table-column>
      <el-table-column
          type="index"
          label="序号"
          width="70"
          align="center">
      </el-table-column>
      <el-table-column prop="id" label="id" width="120" align="center"></el-table-column>
      <el-table-column prop="name" label="名称" width="120" align="center"></el-table-column>
      <el-table-column prop="pagePath" label="页面路径" width="120" align="center"></el-table-column>
      <el-table-column prop="path" label="路径" width="120" align="center"></el-table-column>
      <el-table-column label="图标" width="120" align="center">
        <template slot-scope="scope">
          <i :class="scope.row.icon" style="font-size: 20px"/>
        </template>
      </el-table-column>
      <el-table-column prop="description" label="描述" width="160" align="center"></el-table-column>

      <el-table-column label="操作" align="center">
        <template slot-scope="scope">
          <el-button type="primary" size="mini" @click="handleCreate(scope.row)" v-if="!scope.row.pid && !scope.row.path">新增子菜单 <i class="el-icon-plus"></i></el-button>
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
          :page-sizes="[1, 2, 15, 20]"
          :page-size="pagination.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total">
      </el-pagination>
    </div>


    <!--新增数据-->
    <el-dialog
        title="新增菜单数据"
        :visible.sync="dialogFormVisible"
        width="40%"
        >
      <el-form :model="form"  label-width="80px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="菜单名称" >
              <el-input v-model="form.name"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="路径" >
              <el-input v-model="form.path"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="菜单图标" >
              <el-select v-model="form.icon" placeholder="请选择" style="width: 80%">
                <el-option v-for="item in options" :key="item.name"  :label="item.name" :value="item.value">
                  <i :class="item.value"/> {{item.name}}
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="菜单描述" >
              <el-input v-model="form.description"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-col >
          <el-form-item label="页面路径" >
            <el-input v-model="form.pagePath"/>
          </el-form-item>
        </el-col>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancel()">取消</el-button>
        <el-button type="primary" @click="handleAdd()">确定</el-button>
      </div>
    </el-dialog>

    <!--编辑数据-->
    <el-dialog
        title="编辑菜单数据"
        :visible.sync="dialogFormVisibleEdit"
        width="40%"
    >
      <el-form :model="form"  label-width="80px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="菜单名称" >
              <el-input v-model="form.name"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="路径" >
              <el-input v-model="form.path"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="菜单图标" >
              <el-select v-model="form.icon" placeholder="请选择" style="width: 80%">
                <el-option v-for="item in options" :key="item.name"  :label="item.name" :value="item.value">
                  <i :class="item.value"/> {{item.name}}
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="菜单描述" >
              <el-input v-model="form.description"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-col >
          <el-form-item label="页面路径" >
            <el-input v-model="form.pagePath"/>
          </el-form-item>
        </el-col>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancel()">取消</el-button>
        <el-button type="primary" @click="handleAdd()">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>


export default {
  name: "Menu",
  data(){
    return {
      tableData: [],  //表格数据
      dialogFormVisible: false,//新增学生表单显示
      dialogFormVisibleEdit: false,//编辑学生数据表单显示
      options: [],
      //增添表单时的校验规则
      rules: {
        stuName: [
          { required: true, message: '请输入学生名称', trigger: 'blur' },
          { min: 2, max: 5, message: '长度在 2 到 5 个字符', trigger: 'blur' }
        ],
        stuId: [
          { required: true, message: '请输入学号', trigger: 'change' },
          {  len: 1, message: '长度为14个字符', trigger: 'blur' },
          { type: 'number', message: '学号必须为数字值'}
        ],
        faculty: [
          { required: true, message: '请选择院系', trigger: 'change' }
        ],
        identity:[{ required: true, message: '请输入身份', trigger: 'change' }]
      },

      multipleSelection:'',//复选框选中的数组

      pagination: {//分页相关模型数据
        currentPage: 1,//当前页码
        pageSize: 1,//每页显示的记录数
        total: 0 //总记录数
      },

      form: {},

      search: {//搜索框中的数据
        name: ''
      }

    }
  },

  created() {
    this.getAll()
    this.selectDirIcon()
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
      this.axios.get("/menu",{
        params: {
          name: this.search.name
        }
      }).then(resp => {
        if (resp.code === '200'){
          this.tableData = resp.data;
        }else {
          this.$message.info(resp.msg)
        }


      })
    },

    //取消功能
    cancel() {
      this.dialogFormVisibleEdit = false;
      this.dialogFormVisible = false;
      this.form = {};
      this.$message.info("当前操作取消");

    },

    //新增表单
    handleCreate(row) {
      this.dialogFormVisible = true
      let pid = row.id
      if (pid){
        this.form.pid = pid;
      }
    },


    //增添信息
    handleAdd() {
      //搜索图标中的数据
      this.selectDirIcon();

      this.axios.post("/menu", this.form).then(resp => {
        if (resp.code === '200') {
          //增添成功 关闭弹窗
          this.dialogFormVisibleEdit = false
          this.dialogFormVisible = false
          this.$message.success("保存成功");

        } else {
          this.$message.error("保存失败");
        }
      }).finally(() => {
        //刷新页面
        this.getAll();
      });
    },

    //获取图标信息
    selectDirIcon(){
      this.axios.get("/dict/icons").then((resp)=>{
        this.options = resp.data;
      })
    },


    //弹出编辑窗口
    handleUpdate(row) {
      //搜索图标中的数据
      this.selectDirIcon();
      //将数据表示在编辑窗口上
      this.axios.get("/menu/" + row.id).then((resp) => {
        if (resp.code ==='200' && resp.data != null) {
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
        this.axios.delete("/menu/" + row.id).then(resp => {
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


    handleSelectionChange(val){
      this.multipleSelection = val;
    },

    //批量删除
    del(){
      let ids = this.multipleSelection.map(v=> v.id); //[{},{},{}]=>[1,2,4]
      this.axios.post("/menu/del/batch", ids).then(resp=>{
        if (resp.code === '200') {
          this.$message.success("删除成功");
        } else {
          this.$message.error("删除失败");
        }
      }).finally(()=>{
        this.getAll();
      })
    },

    //按钮重置
    reset() {
      this.search.stuName = ''
      this.search.stuId = ''
      this.search.faculty = ''
    }

  },



}
</script>

<style scoped>
.headerBgC {
  background: #eee !important;
}
</style>