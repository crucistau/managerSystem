<template>
<div>
<!--  <div id="main" style="height: 400px; width: 500px">-->
<!--  </div>-->

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
      <div style="padding: 10px 0; border-bottom: 1px dashed #ccc" v-for="item in tableData" :key="item.id">
        <div class="pd-10" style="font-size: 20px; color: #3F5EFB; cursor: pointer" @click="$router.push('/articleDetail?id=' + item.id)">{{ item.name }}</div>
        <div style="font-size: 14px; margin-top: 10px">
          <i class="el-icon-time" style="margin-left: 10px"></i> <span>{{ item.time }}</span>
        </div>
      </div>
    </div>

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

  </div>
</div>
</template>

<script>
import * as echarts from 'echarts';
import axios from "@/utils/request";
export default {
  name: "Home",
  data() {
    return {
      tableData: '',  //表格数
      dialogFormVisible: false,//新增学生表单显示
      dialogFormVisibleEdit: false,//编辑学生数据表单显示
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {},
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

  //页面元素渲染后在触发
  mounted() {
    var chartDom = document.getElementById('main');
    var myChart = echarts.init(chartDom);
    var option = {
      tooltip: {
        trigger: 'item'
      },
      legend: {
        top: '5%',
        left: 'center'
      },
      series: [
        {
          name: 'Access From',
          type: 'pie',
          radius: ['40%', '70%'],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 10,
            borderColor: '#fff',
            borderWidth: 2
          },
          label: {
            show: false,
            position: 'center'
          },
          emphasis: {
            label: {
              show: true,
              fontSize: '40',
              fontWeight: 'bold'
            }
          },
          labelLine: {
            show: false
          },
          data: [
            {value: 1048, name: 'Search Engine'},
            {value: 735, name: 'Direct'},
            {value: 580, name: 'Email'},
            {value: 484, name: 'Union Ads'},
            {value: 300, name: 'Video Ads'}
          ]
        }
      ]
    };
    myChart.setOption(option)
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
  }
}
</script>

<style scoped>

</style>