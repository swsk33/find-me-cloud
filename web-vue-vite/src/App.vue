<!-- 根组件，仅挂载页面视图 -->
<template>
	<router-view class="page"/>
</template>

<script setup>
import { useRouter } from 'vue-router';
import { onBeforeMount } from 'vue';
import { useUserStore } from './stores/user';
import { MESSAGE_TYPE, showNotification } from './utils/element-message';

const router = useRouter();
const userStore = useUserStore();

// 检测用户是否登录
onBeforeMount(async () => {
	await userStore.checkLogin();
	if (!userStore.isLogin) {
		// 未登录跳转到登录页
		await router.push('/login');
	}
	// 检测浏览器UA
	if (navigator.userAgent.toLowerCase().indexOf('windows') !== -1) {
		setTimeout(() => {
			showNotification('提示', '当前使用的是电脑端，可能定位不精确！', MESSAGE_TYPE.warning, 6000);
		}, 500);
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