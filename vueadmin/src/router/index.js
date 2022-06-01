import Vue from 'vue'
import VueRouter from 'vue-router'
import store from "@/store";
Vue.use(VueRouter)

const routes = [
    //一级菜单
  {
    path: '/login',
    name: 'Login',
    component: ()=>import('../views/login/Login.vue')
  },
  {
    path: '/test',
    name: 'Test',
    component: ()=>import('../views/UploadFile')
  },



  {
    path: '*',
    name: '404',
    component: () => import('../views/404')
  }
]

// router.addRoute(
// {
//   path: '/',
//   name: 'home',
//   component: ()=>import('../views/Manage.vue'),
//   redirect:"/home",
//   meta:{
//     title: "首页",url: '/home'
//   },
//   //二级菜单
//   children: [
//     {path: 'home', name: 'Home', component: ()=>import('../views/Home.vue')},
//     {path: 'user', name: 'User', component: ()=>import('../views/User.vue'), meta:{title:"用户管理",url:"/user"},},
//     {path: 'role', name: 'Role', component: ()=>import('../views/Role.vue'), meta:{title:"角色管理",url:"/role"},},
//     {path: 'menu', name: 'Menu', component: ()=>import('../views/Menu.vue'), meta:{title:"菜单管理",url:"/menu"},},
//     {path: 'person', name: 'Person', component: ()=>import('../views/Person.vue'), meta:{title:"个人信息",url:"/person"},},
//     {path: 'uploadFile', name: 'uploadFile', component: ()=>import('../views/UploadFile.vue'), meta:{title:"文件管理",url:"/uploadFile"},}
//   ]
// })


const router = new VueRouter({
  mode: "history",    // mode 设置为history ，去掉地址栏上的 # 号
  base: process.env.BASE_URL,
  routes
})

// 提供一个重置路由的方法
export const resetRouter = () => {
  router.matcher = new VueRouter({
    mode: 'history',
    base: process.env.BASE_URL,
    routes
  })
}

export const setRoutes = ()=>{
  const storeMenus = localStorage.getItem("menus")
  if (storeMenus) {
    //动态添加到现在的路由对象中
    // 获取当前的路由对象名称数组即未拼装前的
    const currentRouteNames = router.getRoutes().map(v => v.name)
    if (!currentRouteNames.includes('Manage')) {
      //不包括Manage进行拼装
      const manageRoute = {
        path: '/', name: 'Manage', component: () => import('../views/Manage.vue'), redirect: '/home',children: [
          {
            path: '/articleDetail',
            name: 'ArticleDetail',
            component: ()=>import('../views/home/ArticleDetail'),
            meta:{ title: "首页",url: '/home'}
          },
          {path: 'resetPwd', name: '修改密码', component: () => import('../views/home/ResetPwd')}
        ]
      }
      const menus = JSON.parse(storeMenus)
      //进行路由的拼接
      menus.forEach(item => {
        //原方法
        if (item.path) {//当且仅当Path不为空的时候拼接路由(即只有一级菜单)
          let itemMenu = {
            path: item.path.replace("/", ""),
            name: item.name,
            component: () => import('../views/' + item.pagePath + '.vue'),
            meta: {title: item.name}
          }
          manageRoute.children.push(itemMenu)
        } else if (item.children.length) {
          //如果有二级菜单
          item.children.forEach(item => {
            //将一级菜单的二级菜单拼接到对应位置
            if (item.path) {
              let itemMenu = {
                path: item.path.replace("/", ""),
                name: item.name,
                component: () => import('../views/' + item.pagePath + '.vue'),
                meta: {title: item.name}
              }
              manageRoute.children.push(itemMenu)
            }
          })
        }
      })
      router.addRoute(manageRoute)
    }

  }
}


//重置路由就再设置一次
setRoutes()

router.beforeEach((to,from,next)=>{
  localStorage.setItem("currentName", to.name)
  store.commit('setPath')

  //如果没找到路由的情况
  if(!to.matched.length){
    const storeMenus = localStorage.getItem("menus")
    if (storeMenus){
      next('/404')
    }else{
      next('/login')
    }
  }
  next()

})

export default router
