import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
// element-plus自动按需导入
import AutoImport from 'unplugin-auto-import/vite';
import Components from 'unplugin-vue-components/vite';
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers';

export default defineConfig({
	plugins: [vue(),
		// 配置element-plus自动按需导入
		AutoImport({
			resolvers: [ElementPlusResolver()]
		}),
		Components({
			resolvers: [ElementPlusResolver()]
		})],
	server: {
		host: '0.0.0.0',
		// 跨域配置
		proxy: {
			'/api': {
				target: 'http://127.0.0.1:8888/',
				changeOrigin: true
			}
		}
	}
});