<template>
  <div style="font-size: 20px; line-height: 60px; display: flex">
    <!--加入收缩标志，可以将状态栏收缩-->
    <div style="flex: 1">
      <span :class="collapseBtnClass" style="cursor: pointer;" @click="collapse"></span>
      <el-breadcrumb separator-class="el-icon-arrow-right"  style="display: inline-block;margin-left: 10px">
        <el-breadcrumb-item v-for="brandCrumbItem in brandCrumbList" :key="brandCrumbItem.path" >
          <router-link  v-if="brandCrumbItem.meta.url" :to="brandCrumbItem.meta.url">{{brandCrumbItem.meta.title}}</router-link>
          <a v-else>
            {{brandCrumbItem.meta.title}}
          </a>
        </el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <el-dropdown style="width: 150px;cursor: pointer;text-align: right">
      <div style="display: inline-block">
        <span>{{user.userName}}</span><i class="el-icon-arrow-down" style="margin-left: 5px"></i>
      </div>
      <el-dropdown-menu slot="dropdown" style="width: 100px; text-align: center;">
        <el-dropdown-item style="font-size: 14px; padding: 5px 0">
          <router-link to="/resetPwd" style="text-decoration: none;color: black">修改密码</router-link>
        </el-dropdown-item>
        <el-dropdown-item>
          <span style="text-decoration: none;color: black" @click="logout">退出</span>
        </el-dropdown-item>
      </el-dropdown-menu>
    </el-dropdown>
  </div>
</template>

<script>
export default {
  name: "Header",
  props: {
    collapseBtnClass: String,
    collapse: '',
    user: Object
  },
  data(){
    return{

    }
  },
  created() {
  },
  methods:{
  logout(){
    this.$store.commit("logout")
  }
  },
  computed:{
    brandCrumbList(){
      return this.$route.matched;
    }
  }
}
</script>

<style scoped>

</style>