// 用户状态
import { defineStore } from 'pinia';
import { combinePath, REQUEST_METHOD, sendRequest } from '../utils/request';
import { REQUEST_PREFIX } from '../param/request-prefix';
import defaultAvatar from '../assets/avatar/default-avatar.jpg';

/**
 * @typedef User 用户信息
 * @property {String} id 主键id
 * @property {String} username 用户名
 * @property {String} password 密码
 * @property {String} nickname 昵称
 * @property {String} avatarId 头像id
 * @property {String} email 邮箱
 */

export const useUserStore = defineStore('userStore', {
	state() {
		return {
			/**
			 * 用户数据
			 * @type {User}
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
			const response = await sendRequest(REQUEST_PREFIX.USER_COMMON + 'is-login', REQUEST_METHOD.GET);
			if (response.success) {
				this.userData = response.data;
			}
			this.isLogin = response.success;
		},
		/**
		 * 根据用户头像id获取对应头像URL
		 * @param {User} user 用户对象
		 * @returns {string} 头像URL
		 */
		getUserAvatarURL(user = this.userData) {
			if (user == null || user.avatarId == null) {
				return defaultAvatar;
			}
			return combinePath(REQUEST_PREFIX.USER_AVATAR + 'get/' + user.avatarId);
		}
	}
});