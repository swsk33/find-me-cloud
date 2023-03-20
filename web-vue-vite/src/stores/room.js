// 房间与会话状态管理
import { defineStore } from 'pinia';
import { REQUEST_METHOD, sendRequest } from '../utils/request';
import { MESSAGE_TYPE, showMessage } from '../utils/element-message';
import { useUserStore } from './user';
import { useMessageStore } from './message';
import { useLocationStore } from './location';
import { usePointerStore } from './pointer';

export const useRoomStore = defineStore('roomStore', {
	state() {
		return {
			/**
			 * 当前是否在房间内
			 */
			inTheRoom: false,
			/**
			 * 当前房间信息
			 */
			roomInfo: undefined,
			/**
			 * 房间的密码
			 */
			roomPassword: undefined,
			/**
			 * 当前会话对象（WebSocket对象）
			 */
			session: undefined,
			/**
			 * 位置发送计时器（每秒发送数次位置信息）
			 */
			positionSender: undefined
		};
	},
	actions: {
		/**
		 * 连接上一个房间（加入房间）
		 * @param id 房间id
		 * @param password 房间密码
		 */
		async connectToRoom(id, password) {
			const locationStore = useLocationStore();
			if (!locationStore.checkLocationEnabled(true)) {
				return;
			}
			if (id == null || password == null) {
				showMessage('房间id或者密码不能为空！', MESSAGE_TYPE.error);
				return;
			}
			const userStore = useUserStore();
			const messageStore = useMessageStore();
			const pointerStore = usePointerStore();
			// 由于Vite配置了https，因此这里地址也要是wss://开头！否则不会走Vite的代理配置
			this.session = new WebSocket('wss://' + location.host + '/ws/session/room/' + id + '/' + userStore.userData.id);
			// 连接建立事件
			this.session.addEventListener('open', async (e) => {
				this.inTheRoom = true;
				showMessage('已连接至房间！认证中...', MESSAGE_TYPE.warning);
				// 执行认证
				this.session.send(JSON.stringify({
					type: messageStore.messageType.auth,
					data: password
				}));
				// 打开实时位置发送
				this.enablePositionSender();
				// 最后设定房间信息
				await this.getRoomInfo(id);
				this.roomPassword = password;
			});
			// 收到消息事件
			this.session.addEventListener('message', (e) => {
				const message = JSON.parse(e.data);
				// 调用消息策略容器处理
				messageStore.doStrategy(message);
			});
			// 断开连接事件
			this.session.addEventListener('close', (e) => {
				this.inTheRoom = false;
				// 清空本地房间和用户数据
				this.roomInfo = undefined;
				this.roomPassword = undefined;
				pointerStore.userInRoom = {};
				showMessage('已断开和房间连接！', MESSAGE_TYPE.warning);
			});
			// 发生错误事件
			this.session.addEventListener('error', (e) => {
				showMessage('发生错误！', MESSAGE_TYPE.error);
				console.error(e);
			});
		},
		/**
		 * 传入房间对象设定房间信息（会删除自己的信息）
		 * @param room 房间对象
		 */
		setRoomInfo(room) {
			const userStore = useUserStore();
			delete room.users[userStore.userData.id];
			// 设定信息
			this.roomInfo = room;
		},
		/**
		 * 拉取房间信息
		 * @param id 房间id
		 */
		async getRoomInfo(id) {
			const response = await sendRequest('/api/session/room/get/' + id, REQUEST_METHOD.GET);
			if (!response.success) {
				showMessage(response.message, MESSAGE_TYPE.error);
				return;
			}
			showMessage('拉取房间信息成功！', MESSAGE_TYPE.success);
			this.setRoomInfo(response.data);
		},
		/**
		 * 打开位置发送计时器
		 */
		enablePositionSender() {
			const locationStore = useLocationStore();
			const userStore = useUserStore();
			showMessage('开始共享位置...', MESSAGE_TYPE.warning);
			// 设定计时器
			this.positionSender = setInterval(() => {
				// 如果会话意外结束或者已不在房间，就停止计时器
				if (!this.inTheRoom || this.session == null) {
					showMessage('停止共享位置...', MESSAGE_TYPE.warning);
					clearInterval(this.positionSender);
					return;
				}
				// 发送位置信息
				this.session.send(JSON.stringify({
					type: 'POSITION_CHANGED',
					senderId: userStore.userData.id,
					data: locationStore.position
				}));
			}, 100);
		}
	}
});