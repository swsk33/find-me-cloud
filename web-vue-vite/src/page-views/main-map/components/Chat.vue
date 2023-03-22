<!-- 聊天组件 -->
<template>
	<div class="chat">
		<!-- 聊天按钮 -->
		<div class="chat-button" @click="openDialog">
			<img src="../../../assets/icon/chat.png" alt="无法显示"/>
			<div class="notify-point" v-if="chatStore.existNotRead"></div>
		</div>
		<!-- 聊天对话框 -->
		<el-dialog class="chat-dialog" width="85vw" v-model="chatStore.chatDialogShow" :center="true" :show-close="false" destroy-on-close>
			<template #header>
				<div class="header">
					<div class="title">聊天室</div>
					<el-button class="close" size="small" type="danger" @click="chatStore.chatDialogShow = false" plain>关掉</el-button>
				</div>
			</template>
			<ul class="content-box">
				<li class="message" v-for="item in chatStore.message">
					<div :class="{'user-info': true, 'user-info-me': item.senderId === 0}">
						<img class="avatar" :style="{borderColor: getUserAvatarBorderColor(item.senderId)}" :src="getUserAvatar(item.senderId)" alt="无法显示"/>
						<div class="nickname">{{ getUserNickname(item.senderId) }}</div>
					</div>
					<div :class="{content: true, 'content-me': item.senderId === 0}">{{ item.data }}</div>
				</li>
			</ul>
			<template #footer>
				<div class="send-box">
					<el-input class="input" v-model="chatMessage.data" placeholder="请输入要发送的消息"/>
					<el-button class="send-button" type="success" @click="sendMessage" plain>发送</el-button>
				</div>
			</template>
		</el-dialog>
	</div>
</template>

<script setup>
import { useChatStore } from '../../../stores/chat';
import { useRoomStore } from '../../../stores/room';
import { useUserStore } from '../../../stores/user';
import { computed, reactive } from 'vue';
import { useMessageStore } from '../../../stores/message';
import { MESSAGE_TYPE, showMessage } from '../../../utils/element-message';
import { usePointerStore } from '../../../stores/pointer';

const chatStore = useChatStore();
const roomStore = useRoomStore();
const userStore = useUserStore();
const messageStore = useMessageStore();
const pointerStore = usePointerStore();

// 要发送的聊天数据
const chatMessage = reactive({
	type: messageStore.messageType.chat,
	senderId: undefined,
	data: ''
});

/**
 * 打开聊天对话框
 */
const openDialog = () => {
	chatStore.chatDialogShow = true;
	chatStore.existNotRead = false;
};

/**
 * 发送消息方法
 */
const sendMessage = () => {
	if (chatMessage.data === '') {
		showMessage('发送的消息不能为空！', MESSAGE_TYPE.error);
		return;
	}
	// 发送消息
	chatMessage.senderId = userStore.userData.id;
	roomStore.session.send(JSON.stringify(chatMessage));
	// 存入消息列表
	chatMessage.senderId = 0;
	chatStore.message.push(chatMessage);
	// 清空输入框
	chatMessage.data = '';
};

// 计算属性

/**
 * 获取消息对应用户头像
 * @param id 消息的发送者id，0表示自己
 */
const getUserAvatar = computed(() => {
	return (id) => {
		return id === 0 ? userStore.getUserAvatarURL(userStore.userData) : userStore.getUserAvatarURL(roomStore.roomInfo.users[id]);
	};
});

/**
 * 获取消息对应用户昵称
 * @param id 消息的发送者id，0表示自己
 */
const getUserNickname = computed(() => {
	return (id) => {
		return id === 0 ? userStore.userData.nickname : roomStore.roomInfo.users[id].nickname;
	};
});

/**
 * 获取消息对应用户的头像框颜色
 * @param id 消息的发送者id，0表示自己
 */
const getUserAvatarBorderColor = computed(() => {
	return (id) => {
		return id === 0 ? '#2973ff' : pointerStore.userInRoom[id].color;
	};
});
</script>

<style lang="scss" scoped>
.chat {
	position: absolute;
	width: 48px;
	height: 48px;
	top: 6vh;
	left: 3vw;

	.chat-button {
		img {
			position: absolute;
			width: 100%;
			height: 100%;
			border-radius: 50%;
			border: 1px lightblue solid;
		}

		.notify-point {
			position: absolute;
			width: 8px;
			height: 8px;
			right: 2px;
			top: 2px;
			background-color: red;
			border-radius: 50%;
		}
	}

	.chat-dialog {
		.header {
			display: flex;
			justify-content: center;
			align-items: center;

			.title {
				position: relative;
			}

			.close {
				position: absolute;
				right: 8px;
				top: 8px;
			}
		}

		.content-box {
			border: #7a00ff 1px solid;
			width: 72vw;
			height: 45vh;
			border-radius: 6px;
			overflow: scroll;

			.message {
				position: relative;
				display: inline-block;
				width: 95%;
				left: 2.5%;
				height: auto;
				border-bottom: #67c23a 1px dashed;

				.user-info {
					position: relative;
					width: 15%;
					height: 36px;
					top: 8px;

					.avatar {
						position: absolute;
						width: 32px;
						border-radius: 50%;
						border-style: solid;
						border-width: 2px;
					}

					.nickname {
						position: absolute;
						font-size: 11px;
						left: 36px;
						width: 250%;
						white-space: nowrap;
						overflow: hidden;
						text-overflow: ellipsis;
					}
				}

				// 表示自己的消息的样式-头像组件
				.user-info-me {
					right: 10px;
				}

				.content {
					position: relative;
					left: 15%;
					width: 80%;
					bottom: 10px;
				}
			}
		}

		.send-box {
			position: relative;
			display: flex;
			align-items: center;
			justify-content: space-between;
			bottom: 16px;

			.input {
				width: 78%;
			}
		}
	}
}
</style>