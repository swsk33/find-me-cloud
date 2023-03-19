// WebSocket实时性消息处理store（策略模式上下文）
import { defineStore } from 'pinia';
import { MESSAGE_TYPE, showMessage } from '../utils/element-message';
import { useRoomStore } from './room';
import { usePointerStore } from './pointer';

export const useMessageStore = defineStore('messageStore', {
	state() {
		return {
			/**
			 * 消息处理策略容器
			 */
			messageStrategy: new Map(),
			/**
			 * 消息类型常量枚举
			 */
			messageType: {
				/**
				 * 用户位置变化
				 */
				positionChanged: 'POSITION_CHANGED',
				/**
				 * 集结点变化
				 */
				rallyChanged: 'RALLY_CHANGED',
				/**
				 * 房间状态改变
				 */
				roomChanged: 'ROOM_CHANGED',
				/**
				 * 用户加入
				 */
				userJoin: 'USER_JOIN',
				/**
				 * 用户退出
				 */
				userExit: 'USER_EXIT',
				/**
				 * 认证消息
				 */
				auth: 'AUTH',
				/**
				 * 操作成功
				 */
				success: 'SUCCESS',
				/**
				 * 操作失败
				 */
				failed: 'FAILED'
			}
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
			// 用户加入处理
			const userJoin = (messageObject) => {
				// 用户加入指针列表
				pointerStore.addOrSetPointer(messageObject.senderId, {
					longitude: undefined,
					latitude: undefined,
					elevation: undefined,
					orientation: undefined
				});
				// 显示提示
				showMessage(messageObject.nickname + '加入房间！', MESSAGE_TYPE.success);
			};
			// 用户退出处理
			const userExit = (messageObject) => {
				// 从指针列表移除用户
				pointerStore.removePointer(messageObject.senderId);
				// 显示提示
				showMessage(messageObject.nickname + '退出房间！', MESSAGE_TYPE.warning);
			};
			// 用户位置变化处理
			const userPositionChange = (messageObject) => {
				// 设定对应用户位置
				pointerStore.addOrSetPointer(messageObject.senderId, messageObject.data);
			};
			// 存入所有策略
			this.messageStrategy.set(this.messageType.success, notifyMessage);
			this.messageStrategy.set(this.messageType.failed, notifyMessage);
			this.messageStrategy.set(this.messageType.userJoin, userJoin);
			this.messageStrategy.set(this.messageType.userExit, userExit);
			this.messageStrategy.set(this.messageType.roomChanged, roomChange);
			this.messageStrategy.set(this.messageType.positionChanged, userPositionChange);
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