import Vue from 'vue'
import App from './App.vue'
import router from './router'
import './plugins/element.js'
import './assets/css/global.css'
import './assets/icon/iconfont.css'
import 'less-loader'
import store from './store'
import "./permission"
import axios from 'axios'

Vue.config.productionTip = false;
// 挂在axios
Vue.prototype.$http = axios;
// 设置访问根路径
//47.106.252.239为服务器IP
axios.defaults.baseURL = "http://localhost:8088";
//axios.defaults.baseURL = "http://47.106.252.239:8088";

// 请求携带token
axios.interceptors.request.use(
    config => {
        config.headers['Authorization'] = localStorage.getItem("token"); // 让每个请求携带token--['X-Token']为自定义key 请根据实际情况自行修改
        return config
    })

// 响应数据处理
axios.interceptors.response.use(function (response) {
        if (response.data.status == 401) {
            window.location.href = "/401"
        }
        return response;
    },
    function (error) {
        if (response.data.status == 401) {
            window.location.href = "/401"
        }
        return Promise.reject(error);
    });

new Vue({
    router,
    store: store,
    render: h => h(App)
}).$mount('#app');