<template>
	<div class="map">
		<div id="map-container"></div>
		<Location class="location-button" @click="mapStore.zoomToUser()"/>
		<div class="user-marker-container">
			<UserMarker class="marker my-self" :style="mapStore.coordinateToMapContainerPositionCSS(locationStore.position.longitude, locationStore.position.latitude)" :avatar="userStore.getUserAvatarURL()" :heading-value="locationStore.position.orientation" color="blue"/>
		</div>
	</div>
</template>

<script setup>
import { onMounted, watch } from 'vue';
import { useMapStore } from '../../stores/map';
import { useLocationStore } from '../../stores/location';
import { useUserStore } from '../../stores/user';
import { useDeviceOrientation, useGeolocation } from '@vueuse/core';
import Location from './components/Location.vue';
import UserMarker from './components/UserMarker.vue';

const mapStore = useMapStore();
const locationStore = useLocationStore();
const userStore = useUserStore();

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
	// 缩放到用户
	mapStore.zoomToUser();
	// 添加陀螺仪朝向信息（响应式）
	locationStore.position.orientation = useDeviceOrientation().alpha;
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