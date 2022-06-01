import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import './assets/global.css'
import request from "@/utils/request";
import manonEditor from 'mavon-editor';
import 'mavon-editor/dist/css/index.css';

Vue.config.productionTip = false
Vue.use(ElementUI, {size:"small"});
Vue.prototype.axios = request
Vue.use(manonEditor)
new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
