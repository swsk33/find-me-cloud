// 房间与会话状态管理
import { defineStore } from 'pinia';
import { REQUEST_METHOD, sendRequest } from '../utils/request';
import { MESSAGE_TYPE, showMessage } from '../utils/element-message';
import { useUserStore } from './user';
import { useMessageStore } from './message';
import { useLocationStore } from './location';
import { usePointerStore } from './pointer';
import { ElMessageBox } from 'element-plus';

export const useRoomStore = defineStore('roomStore', {
	state() {
		return {
			/**
			 * 用户是否在房间内
			 */
			inTheRoom: false,
			/**
			 * 若会话已建立，是否已经通过认证
			 */
			authed: false,
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
			session: undefined
		};
	},
	actions: {
		/**
		 * 连接上一个房间（加入房间）
		 * @param id 房间id
		 * @param password 房间密码
		 */
		connectToRoom(id, password) {
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
			// 由于Vite配置了https，因此这里地址也要是wss://开头！否则不会走Vite的代理配置
			this.session = new WebSocket('wss://' + location.host + '/ws/session/room/' + id + '/' + userStore.userData.id);
			// 连接建立事件
			this.session.addEventListener('open', (e) => {
				// 设定在房间内
				this.inTheRoom = true;
				showMessage('已连接至房间！认证中...', MESSAGE_TYPE.warning);
				// 执行认证
				this.session.send(JSON.stringify({
					type: messageStore.messageType.auth,
					data: password
				}));
				// 打开认证检查
				this.enableAuthCheck(id, password);
			});
			// 收到消息事件
			this.session.addEventListener('message', (e) => {
				const message = JSON.parse(e.data);
				// 调用消息策略容器处理
				messageStore.doStrategy(message);
			});
			// 断开连接事件
			this.session.addEventListener('close', (e) => {
				// 设定不在房间内
				this.inTheRoom = false;
				// 重置为未认证
				this.authed = false;
				showMessage('已断开和房间连接！', MESSAGE_TYPE.warning);
				// 判断如果没有正常退出，则弹出异常恢复窗口（如果本地房间缓存不为空）
				const roomCache = JSON.parse(localStorage.getItem('room'));
				if (roomCache != null) {
					this.showSessionRestoreDialog();
				}
			});
			// 发生错误事件
			this.session.addEventListener('error', (e) => {
				showMessage('发生错误！', MESSAGE_TYPE.error);
				console.error(e);
			});
		},
		/**
		 * 断开连接，退出房间
		 * @param showTip 退出是否显示提示
		 */
		disConnect(showTip = true) {
			// 清空本地房间和用户数据
			this.clearRoomInfo();
			// 断开连接
			this.session.close(1000);
			if (showTip) {
				showMessage('退出房间成功！', MESSAGE_TYPE.success);
			}
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
		 * 清除全部本地房间数据缓存
		 */
		clearRoomInfo() {
			const pointerStore = usePointerStore();
			pointerStore.userInRoom = {};
			this.roomInfo = undefined;
			this.roomPassword = undefined;
			// 清除持久化缓存
			localStorage.setItem('room', null);
		},
		/**
		 * 显示异常退出恢复对话框
		 */
		showSessionRestoreDialog() {
			ElMessageBox.confirm('检测到异常退出，是否恢复会话？', '异常退出', {
				confirmButtonText: '恢复会话',
				cancelButtonText: '取消',
				type: 'warning',
				center: true,
				showClose: false,
				closeOnClickModal: false
			}).then(() => {
				// 确认恢复
				// 重新连接，从缓存拿取信息
				const room = JSON.parse(localStorage.getItem('room'));
				if (room == null) {
					showMessage('恢复会话失败！', MESSAGE_TYPE.error);
					return;
				}
				this.connectToRoom(room.id, room.password);
			}).catch(() => {
				// 取消
				// 清除房间信息
				this.clearRoomInfo();
			});
		},
		/**
		 * 打开认证检查，检查是否会话已完成认证
		 * @param id 传入房间id
		 * @param password 传入房间密码
		 */
		enableAuthCheck(id, password) {
			// 认证检查器
			const authChecker = setInterval(() => {
				// 如果连接已断开，则关闭检查
				if (!this.inTheRoom) {
					this.authed = false;
					// 取消定时器
					clearInterval(authChecker);
				}
				// 如果authed被置为true则执行认证后操作
				if (this.authed) {
					// 打开实时位置发送
					this.enablePositionSender();
					// 设定房间信息
					this.getRoomInfo(id).then();
					this.roomPassword = password;
					// 持久化到本地
					localStorage.setItem('room', JSON.stringify({
						id: id,
						password: password
					}));
					// 取消定时器
					clearInterval(authChecker);
				}
			}, 50);
		},
		/**
		 * 打开位置发送计时器
		 */
		enablePositionSender() {
			const locationStore = useLocationStore();
			const userStore = useUserStore();
			showMessage('开始共享位置...', MESSAGE_TYPE.warning);
			// 设定计时器
			const positionSender = setInterval(() => {
				// 如果已不在房间，就停止计时器
				if (!this.inTheRoom) {
					showMessage('停止共享位置...', MESSAGE_TYPE.warning);
					clearInterval(positionSender);
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