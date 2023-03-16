<template>
	<div class="login-panel">
		<div class="title">用户登录</div>
		<div class="input-box">
			<el-input class="input" size="large" v-model="userData.username" :prefix-icon="User" placeholder="用户名或邮箱"/>
			<el-input class="input" size="large" v-model="userData.password" :prefix-icon="Lock" placeholder="密码" show-password/>
		</div>
		<div class="button-box">
			<el-button class="button" type="success">登录</el-button>
			<el-button class="button" type="primary" @click="router.push('/login/register')">注册</el-button>
		</div>
		<el-button class="forget-button" type="danger" size="small" @click="router.push('/login/forget')">忘记密码</el-button>
	</div>
</template>

<script setup>
import { reactive } from 'vue';
import { User, Lock } from '@element-plus/icons-vue';

import { useRouter } from 'vue-router';

const router = useRouter();

// 用户登录数据
const userData = reactive({
	username: undefined,
	password: undefined
});
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
			font-size: 16px;
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

	.forget-button {
		position: relative;
		bottom: 2%;
		left: 38%;
	}
}
</style>