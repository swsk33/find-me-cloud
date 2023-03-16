<template>
	<div class="login-panel">
		<div class="title">新用户注册</div>
		<div class="input-box">
			<el-input class="input" v-model="userData.username" :prefix-icon="User" placeholder="用户名"/>
			<el-input class="input" v-model="userData.nickname" :prefix-icon="Watermelon" placeholder="昵称"/>
			<el-input class="input" v-model="userData.password" :prefix-icon="Lock" placeholder="密码" show-password/>
			<el-input class="input" v-model="userData.email" :prefix-icon="Message" placeholder="邮箱"/>
		</div>
		<div class="button-box">
			<el-button class="button" type="success">注册</el-button>
			<el-button class="button" type="primary" @click="router.push('/login')">返回</el-button>
		</div>
	</div>
</template>

<script setup>
import { reactive } from 'vue';
import { User, Watermelon, Lock, Message } from '@element-plus/icons-vue';
import { useRouter } from 'vue-router';

const router = useRouter();

// 用户注册数据
const userData = reactive({
	username: undefined,
	nickname: undefined,
	password: undefined,
	email: undefined
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
		justify-content: space-between;
		align-items: center;

		.input {
			position: relative;
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
		}
	}
}
</style>