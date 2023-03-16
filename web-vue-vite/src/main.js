import { createApp } from 'vue';
import { createPinia } from 'pinia';
import App from './App.vue';
import router from './router';
// 引入外部库的css
import 'element-plus/dist/index.css';

// 创建根组件实例
const app = createApp(App);
// 加载插件
app.use(createPinia());
app.use(router);
// 挂载
app.mount('#app');