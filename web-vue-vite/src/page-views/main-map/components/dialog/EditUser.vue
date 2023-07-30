<!-- 修改用户信息弹窗 -->
<template>
	<div class="edit-user-dialog">
		<el-dialog class="dialog" v-model="showDialog" width="85vw" top="22vh" :show-close="false" :center="true" title="个人中心">
			<div class="content">
				<div class="avatar-box">
					<img :src="previewImage" alt="无法显示"/>
					<input type="file" @change="getImageFile($event)" ref="uploadButton"/>
					<el-button class="upload" @click="uploadButton.click()" type="primary" plain>修改头像</el-button>
				</div>
				<div class="input-box">
					<div class="input-item username">
						<div class="text">用户名：</div>
						<el-input class="input" v-model="userInfo.username" placeholder="用户名"/>
					</div>
					<div class="input-item nickname">
						<div class="text">昵称：</div>
						<el-input class="input nickname" v-model="userInfo.nickname" placeholder="昵称"/>
					</div>
					<div class="input-item password">
						<div class="text">密码：</div>
						<el-input class="input password" v-model="userInfo.password" placeholder="密码（不修改则留空）" show-password/>
					</div>
					<div class="input-item email">
						<div class="text">邮箱：</div>
						<el-input class="input email" v-model="userInfo.email" placeholder="邮箱"/>
					</div>
				</div>
			</div>
			<template #footer>
				<div class="button-box">
					<el-button class="button ok" type="success" size="small" @click="updateUser" plain>确定</el-button>
					<el-button class="button cancel" type="warning" size="small" plain @click="showDialog = false">取消</el-button>
					<el-button class="button logout" type="danger" size="small" plain @click="userLogout">退出登录</el-button>
				</div>
			</template>
		</el-dialog>
	</div>
</template>

<script setup>
import { ref, watch } from 'vue';
import { MESSAGE_TYPE, showMessage } from '../../../../utils/element-message';
import { REQUEST_METHOD, sendRequest, uploadFile } from '../../../../utils/request';
import { REQUEST_PREFIX } from '../../../../param/request-prefix';
import { useRoomStore } from '../../../../stores/room';
import { useUserStore } from '../../../../stores/user';
import { useRouter } from 'vue-router';

const router = useRouter();
const roomStore = useRoomStore();
const userStore = useUserStore();
// 引用DOM
const uploadButton = ref(null);
// 是否显示对话框
let showDialog = ref(false);
// 用户数据修改
const userInfo = ref({});
// 预上传头像图片
const beforeUploadImage = ref(undefined);
// 预览图
const previewImage = ref(undefined);

/**
 * 获取选择的文件并显示到预览图
 */
const getImageFile = (e) => {
	// 设定备选上传的文件
	beforeUploadImage.value = e.target.files[0];
	// 读取文件以预览
	const reader = new FileReader();
	reader.onload = () => {
		previewImage.value = reader.result;
	};
	reader.readAsDataURL(beforeUploadImage.value);
};

/**
 * 上传头像图片，上传成功会设定用户提交数据中的头像id
 */
const uploadAvatarFile = async () => {
	if (beforeUploadImage.value === undefined) {
		return;
	}
	// 创建表单对象
	const form = new FormData();
	form.append('avatar', beforeUploadImage.value);
	// 发送请求
	const response = await uploadFile(REQUEST_PREFIX.USER_AVATAR + 'upload', REQUEST_METHOD.POST, form);
	if (!response.success) {
		showMessage(response.message, MESSAGE_TYPE.error);
		return;
	}
	// 设定提交数据
	userInfo.value.avatarId = response.data;
	showMessage('上传头像成功！', MESSAGE_TYPE.success);
};

/**
 * 修改用户信息请求
 */
const updateUser = async () => {
	// 先上传头像
	await uploadAvatarFile();
	const response = await sendRequest(REQUEST_PREFIX.USER_COMMON + 'update', REQUEST_METHOD.PATCH, userInfo.value);
	if (!response.success) {
		showMessage(response.message, MESSAGE_TYPE.error);
		return;
	}
	showMessage(response.message, MESSAGE_TYPE.success);
	// 刷新本地用户缓存
	await userStore.checkLogin();
	showDialog.value = false;
};

/**
 * 用户退出登录
 */
const userLogout = async () => {
	// 如果在房间内，先退出房间
	if (roomStore.inTheRoom) {
		roomStore.disConnect();
	}
	const response = await sendRequest(REQUEST_PREFIX.USER_COMMON + 'logout', REQUEST_METHOD.GET);
	if (!response.success) {
		showMessage(response.message, MESSAGE_TYPE.error);
		return;
	}
	showMessage('退出登录成功！', MESSAGE_TYPE.success);
	showDialog.value = false;
	// 刷新缓存
	await userStore.checkLogin();
	await router.push('/login');
};

// 定义导出
defineExpose({ showDialog });

// 监听当窗口打开时，设定用户数据到与输入框绑定的对象上
watch(showDialog, () => {
	if (showDialog.value) {
		// 初始化用于修改提交的用户信息
		userInfo.value = { ...userStore.userData };
		previewImage.value = userStore.getUserAvatarURL(userStore.userData);
	}
});
</script>

<style lang="scss" scoped>
// 导入对话框共用样式
@import "../../../../assets/scss/dialog.scss";

.edit-user-dialog {
	.dialog {
		.content {
			height: 34vh;
			overflow: scroll;

			.avatar-box {
				position: relative;
				height: 20%;
				display: flex;
				align-items: center;
				margin-bottom: 24px;

				img {
					position: relative;
					height: 80%;
					border: #1d1dd2 solid 2px;
					border-radius: 50%;
					margin-left: 1.5%;
				}

				input {
					display: none;
				}

				.upload {
					position: relative;
					margin-left: 8%;
				}
			}

			.input-box {
				.input-item {
					position: relative;
					display: flex;
					align-items: center;
					width: 100%;

					.text {
						width: 25%;
					}

					.input {
						margin-top: 0;
						width: 75%;
					}
				}
			}
		}

		.button-box {
			position: relative;
			display: flex;
			justify-content: space-around;
		}
	}
}
</style>