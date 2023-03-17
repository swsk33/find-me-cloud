// 地图
import { defineStore } from 'pinia';
import { useLocationStore } from './location';

const locationStore = useLocationStore();

export const useMapStore = defineStore('mapStore', {
	state() {
		return {
			/**
			 * 地图对象
			 */
			map: undefined
		};
	},
	actions: {
		/**
		 * 初始化地图
		 * @param {String} containerId 用于显示地图的元素id
		 */
		initMap(containerId) {
			this.map = new BMap.Map(containerId);
			this.map.centerAndZoom(locationStore.toBMapPoint(118.721777, 32.209229), 17);
		}
	}
});