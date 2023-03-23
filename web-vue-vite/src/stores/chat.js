// 聊天store
import { defineStore } from 'pinia';
import chatTipAudio from '../assets/tip-audio/chat-message.mp3';

export const useChatStore = defineStore('chatStore', {
	state() {
		return {
			/**
			 * 存放所有的聊天消息记录
			 */
			message: [],
			/**
			 * 是否存在未读消息
			 */
			existNotRead: false,
			/**
			 * 聊天窗是否处于打开状态
			 */
			chatDialogShow: false,
			/**
			 * 提示音对象
			 */
			tipAudio: new Audio(chatTipAudio)
		};
	}
});