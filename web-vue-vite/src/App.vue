<!-- 根组件，仅挂载页面视图 -->
<template>
	<router-view class="page"/>
</template>

<script setup>
import { useRouter } from 'vue-router';
import { onBeforeMount } from 'vue';
import { useUserStore } from './stores/user';

const router = useRouter();
const userStore = useUserStore();

// 检测用户是否登录
onBeforeMount(async () => {
	await userStore.checkLogin();
	if (!userStore.isLogin) {
		// 未登录跳转到登录页
		await router.push('/login');
	}
});
</script>

<style lang="scss" scoped>
.page {
	position: absolute;
	left: 0;
	top: 0;
	width: 100vw;
	height: 100vh;
}
</style>