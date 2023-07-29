<!-- 查看房间详情弹窗 -->
<template>
	<div class="room-lookup-dialog">
		<el-dialog class="dialog" v-model="showDialog" width="85vw" top="24vh" :show-close="false" :center="true" destroy-on-close>
			<template #header>
				<div class="title">{{ (roomStore.isTemplateRoom ? '[模板]' : '') + roomStore.roomInfo.name }}</div>
			</template>
			<div class="content">
				<div class="room-id">
					<div class="text"><strong>房间id：</strong>{{ roomStore.roomInfo.id }}</div>
					<el-button class="button" size="small" type="primary" @click="copyRoomId" v-if="!roomStore.isTemplateRoom">复制</el-button>
				</div>
				<div class="room-password">
					<div class="text"><strong>密码：</strong>{{ roomStore.isTemplateRoom ? '[模板房间不可分享]' : roomStore.roomPassword }}</div>
					<el-button class="button" size="small" type="warning" @click="copyRoomPassword" v-if="!roomStore.isTemplateRoom">复制</el-button>
				</div>
				<div class="users">
					<div class="text">成员：</div>
					<ul>
						<li>
							<img :src="userStore.getUserAvatarURL(userStore.userData)" :style="{borderColor: '#2973ff'}" alt="无法显示"/>
							<div class="nickname">{{ userStore.userData.nickname }}（我）</div>
						</li>
						<li v-for="(value, key, index) in roomStore.roomInfo.users" :key="key">
							<img :src="userStore.getUserAvatarURL(value)" :style="{borderColor: pointerStore.userInRoom[key].color}" alt="无法显示"/>
							<div class="nickname">{{ value.nickname }}</div>
						</li>
					</ul>
				</div>
			</div>
			<template #footer>
				<div class="button-box">
					<el-button class="button copy-all" type="success" @click="copyRoomInfo" size="small" plain v-if="!roomStore.isTemplateRoom">复制房间全部信息</el-button>
					<el-button class="button copy-link" type="primary" size="small" @click="copyJoinLink" plain v-if="!roomStore.isTemplateRoom">复制一键加入链接</el-button>
					<el-button class="button cancel" type="warning" plain size="small" @click="roomStore.getRoomInfo(roomStore.roomInfo.id)">刷新房间</el-button>
					<el-button class="button cancel" type="danger" plain size="small" @click="showDialog = false">关闭页面</el-button>
				</div>
			</template>
		</el-dialog>
	</div>
</template>

<script setup>
import { ref } from 'vue';
import { useRoomStore } from '../../../../stores/room';
import ClipboardJS from 'clipboard';
import { MESSAGE_TYPE, showMessage } from '../../../../utils/element-message';
import { usePointerStore } from '../../../../stores/pointer';
import { useUserStore } from '../../../../stores/user';

const roomStore = useRoomStore();
const pointerStore = usePointerStore();
const userStore = useUserStore();
// 是否显示对话框
const showDialog = ref(false);

/**
 * 单独复制房间id
 */
const copyRoomId = () => {
	ClipboardJS.copy(roomStore.roomInfo.id);
	showMessage('复制房间id号完成！', MESSAGE_TYPE.success);
};

/**
 * 单独复制房间密码
 */
const copyRoomPassword = () => {
	ClipboardJS.copy(roomStore.roomPassword);
	showMessage('复制房间密码完成！', MESSAGE_TYPE.success);
};

/**
 * 复制一键加入房间链接
 */
const copyJoinLink = () => {
	ClipboardJS.copy('FindMe-复制下列链接粘贴到浏览器以一键加入房间进行位置共享：\n\n' + location.origin + '/join-room/room-id/' + roomStore.roomInfo.id + '/room-password/' + roomStore.roomPassword);
	showMessage('复制一键加入链接完成！', MESSAGE_TYPE.success);
};

/**
 * 复制房间全部信息方法
 */
const copyRoomInfo = () => {
	ClipboardJS.copy('房间id：' + roomStore.roomInfo.id + '\n房间名：' + roomStore.roomInfo.name + '\n房间密码：' + roomStore.roomPassword + '\n\n' + location.href);
	showMessage('复制房间全部信息完成！', MESSAGE_TYPE.success);
};

defineExpose({ showDialog });
</script>

<style lang="scss" scoped>
// 导入对话框共用样式
@import "../../../../assets/scss/dialog.scss";

.room-lookup-dialog {
	.dialog {
		.title {
			white-space: nowrap;
			overflow: hidden;
			text-overflow: ellipsis;
		}

		.content {
			position: relative;
			height: 20vh;
			width: 78vw;
			left: -6px;

			.room-id, .room-password {
				position: relative;
				display: flex;
				justify-content: space-between;
				align-items: center;
				width: 95%;
				margin-top: 8px;

				.text {
					width: 75%;
					white-space: nowrap;
					overflow: hidden;
					text-overflow: ellipsis;
				}
			}

			.users {
				position: relative;
				margin-top: 5%;

				ul {
					position: relative;
					height: 15vh;
					overflow: scroll;
					border: #009dff dashed 1px;
					border-radius: 5px;
					margin-top: 10px;

					li {
						position: relative;
						width: 96%;
						height: 36px;
						left: 2%;
						display: flex;
						align-items: center;
						border-bottom: 1px #a200ff dashed;

						img {
							position: relative;
							height: 28px;
							width: 28px;
							border-style: solid;
							border-width: 2px;
							border-radius: 50%;
						}

						.nickname {
							position: relative;
							margin-left: 6px;
							width: 80%;
							white-space: nowrap;
							overflow: hidden;
							text-overflow: ellipsis;
						}
					}
				}
			}
		}

		.button-box {
			position: relative;
			display: flex;
			justify-content: center;
			align-items: center;
			flex-wrap: wrap;
			width: 100%;
			height: 9vh;
			margin-top: 18%;
		}
	}
}
</style>