// 用户状态
import { defineStore } from 'pinia';
import { REQUEST_METHOD, sendRequest } from '../utils/request';
import defaultAvatar from '../assets/avatar/default-avatar.jpg';

export const useUserStore = defineStore('userStore', {
	state() {
		return {
			/**
			 * 用户数据
			 */
			userData: undefined,
			/**
			 * 用户是否登录
			 */
			isLogin: false
		};
	},
	actions: {
		/**
		 * 判断是否登录
		 */
		async checkLogin() {
			const response = await sendRequest('/api/user/common/is-login', REQUEST_METHOD.GET);
			if (response.success) {
				this.userData = response.data;
			}
			this.isLogin = response.success;
		},
		/**
		 * 根据用户头像id获取对应头像URL
		 * @param user 用户对象
		 * @returns {string} 头像URL
		 */
		getUserAvatarURL(user) {
			if (user == null || user.avatarId === undefined) {
				return defaultAvatar;
			}
			return '/api/image/avatar/get/' + user.avatarId;
		}
	}
});