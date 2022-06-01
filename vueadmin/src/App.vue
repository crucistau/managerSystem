<template>
  <div id="app">
    <router-view/>
  </div>
</template>

<style>
#app{
  min-height: 100vh;
}
</style>

<script>
import router from "@/router";

export  default {
  name: "App",
  created() {
    if (!localStorage.getItem("menus")){
      router.push('/login')
    }
  }
}
</script>