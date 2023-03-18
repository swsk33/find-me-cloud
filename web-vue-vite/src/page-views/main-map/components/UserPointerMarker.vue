<!-- 显示用户的指针标记 -->
<template>
	<div class="user-marker" :style="{left: x + 'px', top: y + 'px'}">
		<div class="marker-base" :style="{transform: 'rotate(' + (360 - pointerData.position.orientation) + 'deg)'}">
			<div class="circle" :style="{backgroundColor: pointerData.color, boxShadow: pointerData.color + ' 0 0 6px'}">
			</div>
			<div class="heading" :style="{borderBottomColor: pointerData.color}" v-if="pointerData.position.orientation != null"></div>
		</div>
		<img class="user-avatar" :src="userStore.getUserAvatarURL(userData)" alt="无法显示"/>
	</div>
</template>

<script setup>
import { usePointerStore } from '../../../stores/user-pointer';
import { useUserStore } from '../../../stores/user';
import { useMapStore } from '../../../stores/map';
import { onMounted, reactive, ref } from 'vue';
import { useLocationStore } from '../../../stores/location';

// 传入用户id，0表示自己
const props = defineProps(['userId']);

const pointerStore = usePointerStore();
const userStore = useUserStore();
const mapStore = useMapStore();
const locationStore = useLocationStore();

// 指针基本信息
let pointerData = reactive({
	position: {},
	color: '#2973ff'
});
// 指针对应的用户信息
let userData = ref(undefined);
// 指针在屏幕上的坐标
const x = ref(0);
const y = ref(0);

/**
 * 刷新指针自身位置
 */
const refreshPointerPosition = () => {
	const point = mapStore.coordinateToMapContainerPositionCSS(pointerData.position.longitude, pointerData.position.latitude);
	x.value = point[0];
	y.value = point[1];
};

// 导出函数
defineExpose({ refreshPointerPosition });

onMounted(() => {
	// id为0时，监听自己位置
	if (props.userId === 0) {
		// 订阅位置state，位置变化及时刷新指针坐标
		locationStore.$subscribe((mutation, state) => {
			pointerData.position = state.position;
			// 改变指针坐标
			refreshPointerPosition();
		});
		userData.value = userStore.userData;
	} else {
		// 订阅指针store获取对应的其他用户的信息，位置变化及时刷新指针坐标
		pointerStore.$subscribe((mutation, state) => {
			pointerData.position = state.userInRoom[props.userId].position;
			pointerData.color = state.userInRoom[props.userId].color;
			// 改变指针坐标
			refreshPointerPosition();
			// 获取对应的房间的用户
			userData.value = '';
		});
	}
});
</script>

<style lang="scss" scoped>
.user-marker {
	position: absolute;
	height: 48px;
	width: 48px;

	.marker-base {
		position: absolute;
		width: 100%;
		height: 100%;
		transition: transform 0.1s ease-out;

		.circle {
			position: absolute;
			width: 29px;
			height: 29px;
			left: 9.5px;
			top: 9.5px;
			border-radius: 50%;
		}

		.heading {
			position: absolute;
			width: 0;
			height: 0;
			border-bottom-width: 16px;
			border-bottom-style: solid;
			border-left: 10px solid transparent;
			border-right: 10px solid transparent;
			left: 14.5px;
		}
	}

	.user-avatar {
		position: absolute;
		width: 24px;
		left: 12px;
		top: 12px;
		border-radius: 50%;
	}
}
</style>