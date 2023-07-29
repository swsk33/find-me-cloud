<!-- 根组件，仅挂载页面视图 -->
<template>
	<router-view class="page" :style="pageSizeStyle"/>
</template>

<script setup>
import { useRouter } from 'vue-router';
import { onBeforeMount, reactive } from 'vue';
import { useUserStore } from './stores/user';
import { MESSAGE_TYPE, showNotification } from './utils/element-message';
import { usePathStore } from './stores/path';
import { useMapStore } from './stores/map';

const router = useRouter();
const userStore = useUserStore();
const pathStore = usePathStore();
const mapStore = useMapStore();

// 动态绑定页面尺寸样式
const pageSizeStyle = reactive({
	width: '100vw',
	height: '100vh'
});

/**
 * 设定页面的高度和设备视口一致
 * 并设定地图宽高数据
 */
const setPageSize = () => {
	pageSizeStyle.width = window.innerWidth + 'px';
	pageSizeStyle.height = window.innerHeight + 'px';
	mapStore.size.width = window.innerWidth;
	mapStore.size.height = window.innerHeight;
};

// 检测用户是否登录
onBeforeMount(async () => {
	// 获取用户进入页面时的路径
	pathStore.path = location.pathname;
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
	// 设定屏幕高度
	setPageSize();
	// 监听页面尺寸变化，若变化重新设定屏幕高度
	window.addEventListener('resize', setPageSize);
});
</script>

<style lang="scss" scoped>
.page {
	position: absolute;
	left: 0;
	top: 0;
}
</style>