<template>
	<div class="login-panel">
		<div class="title">找回密码</div>
		<div class="input-box">
			<el-input class="input" v-model="userData.email" :prefix-icon="Message" placeholder="邮箱"/>
			<div class="code-box">
				<el-input class="input" v-model="code" :prefix-icon="Grid" placeholder="验证码"/>
				<el-button class="send" type="warning" :disabled="!codeButtonEnabled" @click="sendCode" plain>{{ codeButtonText }}</el-button>
			</div>
			<el-input class="input" v-model="userData.password" :prefix-icon="Lock" placeholder="新密码" show-password/>
		</div>
		<div class="button-box">
			<el-button class="button" type="success" @click="doReset">确认重置</el-button>
			<el-button class="button" type="primary" @click="router.push('/login')">返回</el-button>
		</div>
	</div>
</template>

<script setup>
import { reactive, ref } from 'vue';
import { Message, Grid, Lock } from '@element-plus/icons-vue';
import { useRouter } from 'vue-router';
import { REQUEST_METHOD, sendRequest } from '../../../utils/request';
import { MESSAGE_TYPE, showNotification } from '../../../utils/element-message';

const router = useRouter();

// 验证码
const code = ref(undefined);

// 验证码按钮文字
const codeButtonText = ref('发送验证码');

// 验证码按钮是否可用
const codeButtonEnabled = ref(true);

// 用户数据
const userData = reactive({
	email: undefined,
	password: undefined
});

/**
 * 发送验证码
 */
const sendCode = async () => {
	if (userData.email === '' || userData.email == null) {
		showNotification('错误', '邮箱不能为空！', MESSAGE_TYPE.error);
		return;
	}
	const response = await sendRequest('/api/user/email/password-reset-code/' + userData.email, REQUEST_METHOD.GET);
	if (!response.success) {
		showNotification('错误', response.message, MESSAGE_TYPE.error);
		return;
	}
	showNotification('成功', '发送邮箱验证码成功！请检查邮箱！', MESSAGE_TYPE.success);
	// 禁用验证码按钮60s
	codeButtonEnabled.value = false;
	let time = 60;
	codeButtonText.value = time + 's';
	const timeOut = setInterval(() => {
		time--;
		codeButtonText.value = time + 's';
		// 结束
		if (time <= 0) {
			codeButtonEnabled.value = true;
			codeButtonText.value = '发送验证码';
			clearInterval(timeOut);
		}
	}, 1000);
};

/**
 * 重置密码请求
 */
const doReset = async () => {
	const response = await sendRequest('/api/user/common/reset-password/' + code.value, REQUEST_METHOD.PUT, userData);
	if (!response.success) {
		showNotification('错误', response.message, MESSAGE_TYPE.error);
		return;
	}
	showNotification('成功', '重置密码成功！请重新登录！', MESSAGE_TYPE.success);
	await router.push('/login');
};
</script>

<style lang="scss" scoped>
.login-panel {
	.title {
		position: relative;
		font-size: 24px;
		height: 25%;
		display: flex;
		flex-direction: column;
		justify-content: center;
		align-items: center;
	}

	.input-box {
		width: 80%;
		height: 45%;
		display: flex;
		flex-direction: column;
		justify-content: space-around;
		align-items: center;

		.input {
			position: relative;
		}

		.code-box {
			display: flex;
			justify-content: space-between;
			align-items: center;
			width: 100%;

			.input {
				position: relative;
				width: 56%;
			}

			.send {
				position: relative;
			}
		}
	}

	.button-box {
		width: 80%;
		height: 30%;
		display: flex;
		justify-content: space-around;
		align-items: center;

		.button {
			position: relative;
			bottom: 11%;
		}
	}
}
</style>