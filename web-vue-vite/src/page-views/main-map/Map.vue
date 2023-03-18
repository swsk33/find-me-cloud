<template>
	<div class="map">
		<div id="map-container"></div>
		<Location class="location-button" @click="mapStore.zoomToUser()"/>
		<div class="user-marker-container">
			<UserPointerMarker ref="selfMarker" class="user-self" :user-id="0"/>
			<UserPointerMarker ref="othersMarker" class="user-in-room" v-for="(value, key, index) in pointerStore.userInRoom" :user-id="key" :key="key"/>
		</div>
	</div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue';
import { useMapStore } from '../../stores/map';
import { useLocationStore } from '../../stores/location';
import { useUserStore } from '../../stores/user';
import { useDeviceOrientation, useGeolocation } from '@vueuse/core';
import Location from './components/Location.vue';
import UserPointerMarker from './components/UserPointerMarker.vue';
import { usePointerStore } from '../../stores/pointer';

const mapStore = useMapStore();
const locationStore = useLocationStore();
const userStore = useUserStore();
const pointerStore = usePointerStore();

const selfMarker = ref(null);
const othersMarker = ref([]);

// 响应式位置信息
const WGS84Coordinates = useGeolocation().coords;

// 监听位置信息
watch(WGS84Coordinates, () => {
	// 实时地转换经纬度坐标
	locationStore.convertToGCJ02(WGS84Coordinates.value.longitude, WGS84Coordinates.value.latitude);
	// 实时赋值高度变化
	locationStore.position.elevation = WGS84Coordinates.value.altitude;
});

onMounted(async () => {
	// 初始化地图
	await mapStore.initMap('map-container');
	// 添加陀螺仪朝向信息（响应式）
	locationStore.position.orientation = useDeviceOrientation().alpha;
	// 给地图注册事件：当地图被移动、缩放时，刷新地图上所有指针位置
	mapStore.map.on('mapmove', (e) => {
		selfMarker.value.refreshPointerPosition();
		for (let item of othersMarker.value) {
			item.refreshPointerPosition();
		}
	});
	// 等待3秒后缩放至用户
	setTimeout(() => {
		mapStore.zoomToUser();
	}, 3000);
});
</script>

<style lang="scss" scoped>
.map {
	#map-container {
		position: absolute;
		height: 100%;
		width: 100%;
	}

	.location-button {
		position: absolute;
		left: 5%;
		bottom: 6%;
	}

	.user-marker-container {
		position: absolute;
		height: 0;
		width: 0;
	}
}
</style>