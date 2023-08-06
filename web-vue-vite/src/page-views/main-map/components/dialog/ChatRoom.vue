<!-- 聊天室对话框 -->
<template>
	<div class="chat-room-dialog">
		<el-dialog class="dialog" width="85vw" v-model="chatStore.chatDialogShow" :center="true" :show-close="false" destroy-on-close>
			<template #header>
				<div class="header">
					<div class="title">聊天室</div>
					<el-button class="close" size="small" type="danger" @click="chatStore.chatDialogShow = false" plain>关掉</el-button>
				</div>
			</template>
			<ul class="content-box">
				<li class="message" v-for="item in chatStore.message">
					<div class="user-info-you" v-if="item.senderId !== 0">
						<img class="avatar" :style="{borderColor: getUserAvatarBorderColor(item.senderId)}" :src="getUserAvatar(item.senderId)" alt="无法显示"/>
						<div class="nickname">{{ getUserNickname(item.senderId) }}</div>
					</div>
					<div :class="{'content': true, 'content-me': item.senderId === 0}">{{ item.data }}</div>
					<div class="user-info-me" v-if="item.senderId === 0">
						<img class="avatar" :style="{borderColor: getUserAvatarBorderColor(item.senderId)}" :src="getUserAvatar(item.senderId)" alt="无法显示"/>
					</div>
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
import { MESSAGE_TYPE, showMessage } from '../../../../utils/element-message';
import { computed, reactive } from 'vue';
import { useChatStore } from '../../../../stores/chat';
import { useRoomStore } from '../../../../stores/room';
import { useUserStore } from '../../../../stores/user';
import { useMessageStore } from '../../../../stores/message';
import { usePointerStore } from '../../../../stores/pointer';
import { isEmpty } from '../../../../utils/string-util';

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
 * 发送消息方法
 */
const sendMessage = () => {
	if (isEmpty(chatMessage.data)) {
		showMessage('发送的消息不能为空！', MESSAGE_TYPE.error);
		return;
	}
	// 发送消息
	chatMessage.senderId = userStore.userData.id;
	roomStore.session.send(JSON.stringify(chatMessage));
	// 存入消息列表（复制）
	chatStore.message.push({
		senderId: 0,
		data: chatMessage.data
	});
	// 清空输入框
	chatMessage.data = '';
};

/**
 * 获取消息对应用户头像
 * @param id 消息的发送者id，0表示自己
 */
const getUserAvatar = computed(() => {
	return (id) => {
		if (id === 0) {
			return userStore.getUserAvatarURL(userStore.userData);
		}
		const user = roomStore.roomInfo.users[id];
		if (user == null) {
			return userStore.getUserAvatarURL(null);
		}
		return userStore.getUserAvatarURL(user);
	};
});

/**
 * 获取消息对应用户昵称
 * @param id 消息的发送者id，0表示自己
 */
const getUserNickname = computed(() => {
	return (id) => {
		if (id === 0) {
			return userStore.userData.nickname;
		}
		const user = roomStore.roomInfo.users[id];
		if (user == null) {
			return '已退出';
		}
		return user.nickname;
	};
});

/**
 * 获取消息对应用户的头像框颜色
 * @param id 消息的发送者id，0表示自己
 */
const getUserAvatarBorderColor = computed(() => {
	return (id) => {
		if (id === 0) {
			return '#2973ff';
		}
		const userPointer = pointerStore.userInRoom[id];
		if (userPointer == null) {
			return '#23a900';
		}
		return userPointer.color;
	};
});
</script>

<style lang="scss" scoped>
.chat-room-dialog {
	.dialog {
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
			overflow-y: scroll;

			.message {
				position: relative;
				display: flex;
				justify-content: space-between;
				width: 95%;
				left: 2.5%;
				height: auto;
				border-bottom: #67c23a 1px dashed;

				.user-info-you, .user-info-me {
					position: relative;
					width: 15%;
					height: auto;

					.avatar {
						position: absolute;
						width: 32px;
						border-radius: 50%;
						border-style: solid;
						border-width: 2px;
						top: 9px;
					}

					.nickname {
						position: absolute;
						font-size: 11px;
						top: 9px;
						left: 39px;
						height: 12px;
						line-height: 12px;
						width: 250%;
						color: palevioletred;
						white-space: nowrap;
						overflow: hidden;
						text-overflow: ellipsis;
					}
				}

				.content {
					position: relative;
					width: 83%;
					padding: 24px 8px 24px;
					word-wrap: anywhere;
				}

				.content-me {
					padding-top: 13px;
					text-align: right;
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