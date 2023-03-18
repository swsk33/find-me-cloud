// WebSocket实时性消息处理store（策略模式上下文）
import { defineStore } from 'pinia';
import { MESSAGE_TYPE, showMessage } from '../utils/element-message';
import { useUserStore } from './user';
import { useRoomStore } from './room';
import { usePointerStore } from './pointer';

export const useMessageStore = defineStore('messageStore', {
	state() {
		return {
			/**
			 * 消息处理策略容器
			 */
			messageStrategy: new Map()
		};
	},
	actions: {
		/**
		 * 初始化所有消息策略
		 */
		initStrategy() {
			// 保险起见清空策略容器
			this.messageStrategy.clear();
			// 使用store
			const userStore = useUserStore();
			const roomStore = useRoomStore();
			const pointerStore = usePointerStore();
			// 所有策略函数都只有一个形参表示消息对象
			// 普通告示消息处理函数
			const notifyMessage = (messageObject) => {
				showMessage(messageObject.data, messageObject.type === 'SUCCESS' ? MESSAGE_TYPE.success : MESSAGE_TYPE.error);
			};
			// 房间变化处理
			const roomChange = (messageObject) => {
				// 设定到房间对象
				roomStore.setRoomInfo(messageObject.data);
			};
			// 用户位置变化处理
			const userPositionChange = (messageObject) => {
				// 设定对应用户位置
				pointerStore.userInRoom[messageObject.senderId].position = messageObject.data;
			};
			// 存入所有策略
			this.messageStrategy.set('SUCCESS', notifyMessage);
			this.messageStrategy.set('FAILED', notifyMessage);
			this.messageStrategy.set('ROOM_CHANGED', roomChange);
			this.messageStrategy.set('POSITION_CHANGED', userPositionChange);
		},
		/**
		 * 执行消息处理策略
		 * @param messageObject 接收到的消息对象
		 */
		doStrategy(messageObject) {
			// 若还没有初始化，则执行初始化
			if (this.messageStrategy.size === 0) {
				this.initStrategy();
			}
			// 取出并执行策略方法
			this.messageStrategy.get(messageObject.type)(messageObject);
		}
	}
});