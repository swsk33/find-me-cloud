// 房间与会话状态管理
import { defineStore } from 'pinia';
import { REQUEST_METHOD, sendRequest } from '../utils/request';
import { MESSAGE_TYPE, showInteractiveDialog, showMessage } from '../utils/element-message';
import { useUserStore } from './user';
import { useMessageStore } from './message';
import { useLocationStore } from './location';
import { usePointerStore } from './pointer';
import { useChatStore } from './chat';
import { REQUEST_PREFIX } from '../param/request-prefix';

/**
 * @typedef RallyPoint 集结点
 * @property {Number} longitude 集结点经度
 * @property {Number} latitude 集结点纬度
 *
 * @typedef Room 房间对象
 * @property {String} id 主键id
 * @property {String} name 房间名
 * @property {String} password 房间密码
 * @property {RallyPoint} rally 集结点
 * @property {Object} users 用户列表
 */

export const useRoomStore = defineStore('roomStore', {
	state() {
		return {
			/**
			 * 用户是否在房间内
			 */
			inTheRoom: false,
			/**
			 * 是否是通过模板加入的房间
			 */
			isTemplateRoom: false,
			/**
			 * 若会话已建立，是否已经通过认证
			 */
			authed: false,
			/**
			 * 当前房间对象信息
			 * @type {Room}
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
			 * 表示当前是否正在设定集结点
			 */
			settingRally: false
		};
	},
	actions: {
		/**
		 * 连接上一个房间（加入房间）
		 * @param {String} id 房间id
		 * @param {String} password 房间密码（通过模板加入时，该参数无意义）
		 * @param {Boolean} isTemplate 是否通过模板加入
		 */
		connectToRoom(id, password, isTemplate = false) {
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
			const chatStore = useChatStore();
			this.session = new WebSocket(REQUEST_PREFIX.SESSION_ROOM_WS + id + '/' + userStore.userData.id);
			// 连接建立事件
			this.session.addEventListener('open', () => {
				// 设定是否通过模板加入房间
				this.isTemplateRoom = isTemplate;
				showMessage('已连接至房间！认证中...', MESSAGE_TYPE.warning);
				// 执行认证
				let authMessage;
				if (isTemplate) {
					authMessage = {
						type: messageStore.messageType.templateAuth
					};
				} else {
					authMessage = {
						type: messageStore.messageType.auth,
						data: password
					};
				}
				this.session.send(JSON.stringify(authMessage));
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
			this.session.addEventListener('close', () => {
				// 设定不在房间内
				this.inTheRoom = false;
				// 重置为未认证
				this.authed = false;
				// 重置集结点设定状态
				this.settingRally = false;
				// 清空聊天数据
				chatStore.$reset();
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
			const response = await sendRequest(REQUEST_PREFIX.SESSION_ROOM + 'get/' + id, REQUEST_METHOD.GET);
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
			showInteractiveDialog('异常退出', '检测到异常退出，是否恢复会话？', '恢复会话', '取消',
				() => {
					// 确认恢复
					// 重新连接，从缓存拿取信息
					const room = JSON.parse(localStorage.getItem('room'));
					if (room == null) {
						showMessage('恢复会话失败！', MESSAGE_TYPE.error);
						return;
					}
					this.connectToRoom(room.id, room.password, room.isTemplate);
				},
				() => {
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
				if (this.session.readyState === WebSocket.CLOSED) {
					this.authed = false;
					// 取消定时器
					clearInterval(authChecker);
				}
				// 如果authed被置为true则执行认证后操作
				if (this.authed) {
					// 认证成功后，再设定用户已在房间内
					this.inTheRoom = true;
					// 打开实时位置发送
					this.enablePositionSender();
					// 设定房间信息
					this.getRoomInfo(id).then();
					this.roomPassword = password;
					// 持久化到本地
					localStorage.setItem('room', JSON.stringify({
						id: id,
						password: password,
						isTemplate: this.isTemplateRoom
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