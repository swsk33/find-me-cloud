<!-- 聊天组件 -->
<template>
	<div class="chat" v-if="roomStore.inTheRoom">
		<!-- 聊天按钮 -->
		<div class="chat-button" @click="openDialog">
			<img src="../../../assets/icon/chat.png" alt="无法显示"/>
			<div class="notify-point" v-if="chatStore.existNotRead"></div>
		</div>
		<!-- 聊天对话框 -->
		<ChatRoom ref="chatRoomDialogRef"/>
	</div>
</template>

<script setup>
import { useChatStore } from '../../../stores/chat';
import { useRoomStore } from '../../../stores/room';
import { ref } from 'vue';
import ChatRoom from './dialog/ChatRoom.vue';

const chatStore = useChatStore();
const roomStore = useRoomStore();

// 组件引用
const chatRoomDialogRef = ref(null);

/**
 * 打开聊天对话框
 */
const openDialog = () => {
	chatStore.chatDialogShow = true;
	chatStore.existNotRead = false;
};
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
}
</style>