<template>
  <div style="height: 100vh">
    <el-container style="height: 100%">
      <!--侧边栏-->
      <el-aside :width=" sideWidth+'px'"
                style="background-color: rgb(238, 241, 246); height: 100%;box-shadow: 2px 0 6px rgb(0 21 41 / 35%);">
        <!--将对应的属性修改-->
        <Aside :isCollapse="isCollapse" :logoTextShow="logoTextShow" :menus="user.menus"/>
      </el-aside>

      <el-container>
        <!--任务状态栏-->
        <el-header style=" border-bottom: 1px solid #ccc;">
          <!--引入组件 将user传过去-->
          <Header :collapse-btn-class="collapseBtnClass" :collapse="collapse" :user="user"  />
        </el-header>

        <!--页面主题数据-->
        <el-main>
          <!--表示当前页面的路由会在<router-view/>里展示-->
          <router-view  @refreshUser="getUser"/>
        </el-main>
      </el-container>
    </el-container>
  </div>

</template>


<style>
.el-aside {
  color: #333;
}


</style>


<script>

//引入组件
import Aside from "@/components/Aside";
import Header from "@/components/Header";

export default {
  name: 'HomeView',
  components: {Aside,Header},
  data() {
    return {

      collapseBtnClass: 'el-icon-s-fold',
      isCollapse: false,
      sideWidth: 200,//侧边栏的宽度（收缩前）
      logoTextShow: true,
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")):{}
    }
  },

  methods: {
    collapse() {  // 点击收缩按钮触发(侧边栏),
      this.isCollapse = !this.isCollapse
      if (this.isCollapse) {  // 收缩
        this.sideWidth = 64  //侧边栏的宽度（收缩后）
        this.collapseBtnClass = 'el-icon-s-unfold'
        this.logoTextShow = false
      } else {   // 展开
        this.sideWidth = 200
        this.collapseBtnClass = 'el-icon-s-fold'
        this.logoTextShow = true
      }
    },
    //从后台获取数据
    getUser(){
      this.axios.get("/student/"+this.user.student.id).then(res => {
        this.user = res.data
        this.user.userName = res.data.stuName;//重新赋值user最新的
      })
    }
  }
}
</script>
