<template>
	<div class="map">
		<div id="map-container"></div>
		<Location class="location-button" @click="locationStore.getUserPosition(true, true)"/>
	</div>
</template>

<script setup>
import { onMounted } from 'vue';
import { useMapStore } from '../../stores/map';
import { useLocationStore } from '../../stores/location';
import Location from './components/Location.vue';

const mapStore = useMapStore();
const locationStore = useLocationStore();

onMounted(async () => {
	// 初始化地图
	await mapStore.initMap('map-container');
	// 执行一次定位
	locationStore.getUserPosition(true, true);
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
}
</style>