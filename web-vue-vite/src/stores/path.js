// 记录用户进入页面时的路径
import { defineStore } from 'pinia';

export const usePathStore = defineStore('path', {
	state() {
		return {
			/**
			 * 用户未登录时进入的路径
			 */
			path: '/'
		};
	}
});