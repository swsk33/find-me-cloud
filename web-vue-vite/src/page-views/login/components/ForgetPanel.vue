<template>
	<div class="login-panel">
		<div class="title">找回密码</div>
		<div class="input-box">
			<el-input class="input" v-model="userData.email" :prefix-icon="Message" placeholder="邮箱"/>
			<div class="code-box">
				<el-input class="input" v-model="code" :prefix-icon="Grid" placeholder="验证码"/>
				<el-button class="send" type="warning" plain>发送验证码</el-button>
			</div>
			<el-input class="input" v-model="userData.password" :prefix-icon="Lock" placeholder="新密码" show-password/>
		</div>
		<div class="button-box">
			<el-button class="button" type="success">确认重置</el-button>
			<el-button class="button" type="primary" @click="router.push('/login')">返回</el-button>
		</div>
	</div>
</template>

<script setup>
import { reactive, ref } from 'vue';
import { Message, Grid, Lock } from '@element-plus/icons-vue';
import { useRouter } from 'vue-router';

const router = useRouter();

// 验证码
const code = ref(undefined);

// 用户数据
const userData = reactive({
	email: undefined,
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