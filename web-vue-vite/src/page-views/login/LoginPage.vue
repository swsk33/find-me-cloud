<template>
	<div class="login-page">
		<div class="title">Find Me</div>
		<router-view class="panel"/>
	</div>
</template>

<script setup>
import 'animate.css';
import { useRouter } from 'vue-router';
import { useUserStore } from '../../stores/user';
import { onBeforeMount } from 'vue';

const router = useRouter();
const userStore = useUserStore();

onBeforeMount(async () => {
	if (userStore.isLogin) {
		// 已登录跳转到主页
		await router.push('/');
	}
});
</script>

<style lang="scss" scoped>
.login-page {
	background-color: #8EC5FC;
	background-image: linear-gradient(62deg, #8EC5FC 0%, #E0C3FC 100%);
	display: flex;
	flex-direction: column;
	align-items: center;

	.title {
		position: relative;
		font-size: 42px;
		font-weight: bold;
		top: 10%;
		animation: bounce;
		animation-duration: 1.5s;
		color: #1d1dd2;
	}

	.panel {
		position: relative;
		display: flex;
		flex-direction: column;
		align-items: center;
		background-color: rgba(255, 255, 255, 0.20);
		box-shadow: rgba(255, 255, 255, 0.75) 1px 1px 16px;
		border-radius: 8px;
		width: 85%;
		height: 45%;
		top: 20%;
	}
}
</style>