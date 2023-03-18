// 存放和管理所有的用户指针状态
import { defineStore } from 'pinia';

export const usePointerStore = defineStore('userPointerStore', {
	state() {
		return {
			/**
			 * 表示房间内所有用户（其中每个属性是用户id，值是对应的指针对象，除了自己之外）
			 */
			userInRoom: {},
			/**
			 * 颜色列表
			 */
			colors: ['#00ff7f', '#ff0f26', '#ff7d0f', '#ffe72e', '#b2ff2e', '#3cff2e', '#2effd3', '#0700ff', '#c000ff', '#f375ff', '#ffd1e4', '#ddff57', '#ffdd8a']
		};
	},
	actions: {
		/**
		 * 获取一个随机颜色
		 * @returns {String} 一个十六进制颜色代码
		 */
		getRandomColor() {
			return this.colors[Math.floor(Math.random() * this.colors.length)];
		},
		/**
		 * 创建一个用户指针，若该指针存在则会被修改
		 * @param userId 对应用户id
		 * @param position 位置对象，应当包含longitude、latitude、elevation和orientation四个属性表示经纬度、海拔和朝向
		 */
		addOrSetPointer(userId, position) {
			if (this.userInRoom[userId] == null) {
				this.userInRoom[userId] = {
					position: position,
					color: this.getRandomColor()
				};
				return;
			}
			this.userInRoom[userId].position = position;
		},
		/**
		 * 移除一个指针
		 * @param userId 对应用户id
		 */
		removePointer(userId) {
			delete this.userInRoom[userId];
		}
	}
});