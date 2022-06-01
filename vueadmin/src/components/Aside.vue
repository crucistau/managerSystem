<template>
  <!--导航栏-->
  <el-menu style="min-height:100vh; overflow-x: hidden"
           background-color="rgb(48, 65, 86)"
           text-color="#fff"
           active-text-color="blue"
           :collapse-transition="false"
           :collapse="isCollapse"
           router
  >
    <div style="height: 60px; line-height: 60px; text-align: center">
      <img src="../assets/s.png" style="width: 40px; position: relative; top: 8px; margin-right: 3px">
      <b style="color: #fff" v-show="logoTextShow">后台管理系统</b>
    </div>

    <div v-for="item in menus" :key="item.id">
      <!--一级菜单-->
      <div v-if="item.path">
        <el-menu-item :index="item.path">
            <i :class="item.icon"></i>
            <span>{{ item.name }}</span>
        </el-menu-item>
      </div>
                  <!--二级菜单-->
      <div v-else>
        <el-submenu :index="item.id + ''">
          <template slot="title">
            <i :class="item.icon"></i>
            <span>{{ item.name }}</span>
          </template>
          <el-menu-item-group>
            <div v-for="subItem in item.children" :key="subItem.id">
              <el-menu-item :index="subItem.path">
                <template slot="title">
                  <i :class="subItem.icon"></i>
                  <span slot="title">{{ subItem.name }}</span>
               </template>
              </el-menu-item>
            </div>
          </el-menu-item-group>
        </el-submenu>
      </div>
    </div>
  </el-menu>
</template>

<script>
export default {
  name: "Aside",
  props: {
    isCollapse: Boolean,
    logoTextShow: Boolean,
    menus: Array
  },
  created() {
    console.log(this.menus)
  }
}
</script>

<style scoped>
/*解决收缩菜单文字不消失问题*/
.el-menu--collapse span {
  visibility: hidden;
}

</style>