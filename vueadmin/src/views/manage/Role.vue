<template>
  <div>
    <!--搜索框-->
    <div style="padding: 10px 0">
      <el-input style="width: 200px" placeholder="请输入名字" suffix-icon="el-icon-search"
                v-model="search.stuName"></el-input>
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
      <el-table-column prop="name" label="名称" width="240" align="center">
      </el-table-column>
      <el-table-column prop="flag" label="唯一标识" width="240" align="center">
      </el-table-column>
      <el-table-column prop="description" label="描述" width="240" align="center">
      </el-table-column>
      <el-table-column label="操作" align="center">
        <template slot-scope="scope">
          <el-button type="info" @click="selectMenu(scope.row)">分配菜单<i class="el-icon-menu"></i></el-button>
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
        title="新增角色数据"
        :visible.sync="dialogFormVisible"
        width="40%">
      <el-form :model="formA" label-width="80px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="角色名称" prop="name">
              <el-input v-model="formA.name"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="描述" prop="description">
              <el-input v-model="formA.description"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-form-item label="唯一标识" prop="flag">
            <el-select clearable v-model="formA.flag" placeholder="请选择角色" style="width: 100%">
              <el-option v-for="item in roles" :key="item.name" :label="item.name" :value="item.flag"></el-option>
            </el-select>
          </el-form-item>
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
      <el-form :model="form" label-width="80px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="角色名称" prop="name">
              <el-input v-model="form.name"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="描述" prop="description">
              <el-input v-model="form.description"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-form-item label="唯一标识" prop="flag">
            <el-select clearable v-model="form.flag" placeholder="请选择角色" style="width: 100%">
              <el-option v-for="item in roles" :key="item.name" :label="item.name" :value="item.flag"></el-option>
            </el-select>
          </el-form-item>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancel()">取消</el-button>
        <el-button type="primary" @click="handleAdd()">确定</el-button>
      </div>
    </el-dialog>

    <!--分配菜单-->
    <el-dialog title="菜单分配" :visible.sync="menuVisible" width="40%">
      <el-tree
          :data="menuData"
          show-checkbox
          node-key="id"
          ref="tree"
          :props="props"
          :default-expanded-keys="expends"
          :default-checked-keys="checks"
          >
        <span class="custom-tree-node" slot-scope="{ node, data }">
        <span><i :class="data.icon"></i> {{ data.name }}</span>
        </span>
     </el-tree>
      <div slot="footer" class="dialog-foot">
          <el-button @click="menuVisible = false">取消</el-button>
          <el-button @click="saveRoleMenu" type="primary">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import axios from "@/utils/request";

export default {
  name: "User",
  data() {
    return {
      tableData: '',  //表格数据
      dialogFormVisible: false,//新增学生表单显示
      dialogFormVisibleEdit: false,//编辑学生数据表单显示
      menuVisible: false,
      expends: [],
      checks: [],
      roleId: 0,
      roleFlag: '',
      menuData: [],
      roles: [],
      //增添表单时的校验规则
      rules: {
        stuName: [
          {required: true, message: '请输入学生名称', trigger: 'blur'},
          {min: 2, max: 5, message: '长度在 2 到 5 个字符', trigger: 'blur'}
        ],
        stuId: [
          {required: true, message: '请输入学号', trigger: 'change'},
          {len: 1, message: '长度为14个字符', trigger: 'blur'},
          {type: 'number', message: '学号必须为数字值'}
        ],
        faculty: [
          {required: true, message: '请选择院系', trigger: 'change'}
        ],
        identity: [{required: true, message: '请输入身份', trigger: 'change'}]
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
      this.axios.get("/role/" + this.pagination.currentPage + "/" + this.pagination.pageSize, {
        params: {
          name: this.search.name,
        }
      }).then(resp => {
        if (resp.code ==='200'){
          this.tableData = resp.data.records;
          this.pagination.total = resp.data.total;
        }else{
          this.$message.info(resp.msg)
        }
      })

      //获取到角色信息
      this.axios.get("/role").then(resp=>{
        if (resp.code ==='200'){
          this.roles = resp.data
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
      this.dialogFormVisible = true
    },


    //修改角色与菜单关系
    saveRoleMenu(){
      this.axios.post("/role/roleMenu/" + this.roleId, this.$refs.tree.getCheckedKeys()).then(resp=>{
        if (resp.code === '200'){
          this.$message.success("修改成功")
          this.menuVisible = false
          this.getAll()
          if(this.roleFlag ==="ROLE_ADMIN"){
            this.$store.commit('logout')
          }
        }else{
          this.$message.error(resp.msg)
        }
      })
    },

    //增添学生信息
    handleAdd() {
      this.axios.post("/role", this.form).then(resp => {
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
      axios.get("/role/" + row.id).then((resp) => {
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
        axios.delete("/role/" + row.id).then(resp => {
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
      console.log(ids);
      axios.post("/role/del/batch", ids).then(resp => {
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

    selectMenu(role){
      this.roleId = role.id;//选择的角色Id
      this.roleFlag = role.flag
      //将菜单信息展示到页面上
      this.axios.get("/menu", {
        params: {
          name: this.search.name,
        }
      }).then(resp => {
        this.menuData = resp.data

        //把后台返回的菜单处理成id数组
        this.expends = this.menuData.map(v => v.id)
        console.log(this.expends)
      })

      //查找对应角色所绑定的菜单
      this.axios.get("/role/roleMenu/"+ this.roleId).then(res => {

        this.menuVisible = true;

        this.checks = res.data;
        this.axios.get(("/menu/ids")).then(r =>{
          const ids = r.data
          ids.forEach(id =>{
            if (!this.checks.includes(id)){
              //此处的tree是弹窗中的
              this.$refs.tree.setChecked(id, false)
            }
          })
        })
      })
    },

  },


}
</script>

<style scoped>
.headerBgC {
  background: #eee !important;
}
</style>