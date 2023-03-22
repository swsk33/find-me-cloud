<!-- 集结点标志  -->
<template>
	<div class="rally-marker" :style="{left: x + 'px', top: y + 'px'}" v-if="roomStore.roomInfo != null && roomStore.roomInfo.rally != null" @click="zoomToRally">
		<div class="arrow-in-screen" v-if="status === pointerStore.POINTER_STATUS.IN_SCREEN"></div>
		<Top class="arrow top" v-if="status === pointerStore.POINTER_STATUS.TOP"/>
		<Bottom class="arrow bottom" v-if="status === pointerStore.POINTER_STATUS.BOTTOM"/>
		<Back class="arrow left" v-if="status === pointerStore.POINTER_STATUS.LEFT"/>
		<Right class="arrow right" v-if="status === pointerStore.POINTER_STATUS.RIGHT"/>
		<TopLeft class="arrow top-left" v-if="status === pointerStore.POINTER_STATUS.TOP_LEFT"/>
		<TopRight class="arrow top-right" v-if="status === pointerStore.POINTER_STATUS.TOP_RIGHT"/>
		<BottomLeft class="arrow bottom-left" v-if="status === pointerStore.POINTER_STATUS.BOTTOM_LEFT"/>
		<BottomRight class="arrow bottom-right" v-if="status === pointerStore.POINTER_STATUS.BOTTOM_RIGHT"/>
		<img src="../../../assets/marker/rally.png" alt="无法显示"/>
	</div>
</template>

<script setup>
import 'animate.css';
import { Top, Bottom, Back, Right, TopRight, TopLeft, BottomRight, BottomLeft } from '@element-plus/icons-vue';
import { onMounted, ref } from 'vue';
import { usePointerStore } from '../../../stores/pointer';
import { useRoomStore } from '../../../stores/room';
import { useMapStore } from '../../../stores/map';

const pointerStore = usePointerStore();
const roomStore = useRoomStore();
const mapStore = useMapStore();

// 集结点指针在屏幕位置及其状态
const x = ref(0);
const y = ref(0);
const status = ref(0);

/**
 * 缩放到集结点
 */
const zoomToRally = () => {
	const longitude = roomStore.roomInfo.rally.longitude;
	const latitude = roomStore.roomInfo.rally.latitude;
	mapStore.zoomToUser(longitude, latitude);
};

/**
 * 刷新指针在屏幕中的位置
 */
const refreshPosition = () => {
	if (!roomStore.inTheRoom || roomStore.roomInfo == null || roomStore.roomInfo.rally == null) {
		return;
	}
	const longitude = roomStore.roomInfo.rally.longitude;
	const latitude = roomStore.roomInfo.rally.latitude;
	pointerStore.computeAndSetPointer(x, y, longitude, latitude, status, 60, 60, 29, 52);
};

defineExpose({ refreshPosition });

onMounted(() => {
	// 订阅房间store中集结点变化
	roomStore.$subscribe((mutation, state) => {
		if (state.roomInfo == null || state.roomInfo.rally == null) {
			return;
		}
		refreshPosition();
	});
});
</script>

<style lang="scss" scoped>
.rally-marker {
	position: absolute;
	width: 60px;
	height: 60px;
	animation: rubberBand;
	animation-duration: 1s;

	img {
		position: absolute;
		width: 28px;
		height: 28px;
		left: 16px;
		top: 16px;
	}

	.arrow-in-screen {
		position: absolute;
		width: 0;
		height: 0;
		left: 20px;
		bottom: 6px;
		border-top-width: 16px;
		border-top-style: solid;
		border-left: 10px solid transparent;
		border-right: 10px solid transparent;
		color: #66a7d5;
	}

	.arrow {
		position: absolute;
		width: 24px;
		color: #0080d7;
	}

	// 调整各个箭头
	.top, .bottom {
		left: 18px;
	}

	.bottom {
		bottom: 0;
	}

	.left, .right {
		top: 18px;
	}

	.right {
		right: 0;
	}

	.top-left, .bottom-left {
		left: 5px;
	}

	.top-left, .top-right {
		top: 4px;
	}

	.bottom-left, .bottom-right {
		bottom: 4px;
	}

	.top-right, .bottom-right {
		right: 5px;
	}
}
</style>