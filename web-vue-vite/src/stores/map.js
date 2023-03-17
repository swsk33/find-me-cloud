// 地图
import { defineStore } from 'pinia';
import AMapLoader from '@amap/amap-jsapi-loader';
import { useLocationStore } from './location';

export const useMapStore = defineStore('mapStore', {
	state() {
		return {
			/**
			 * 地图对象
			 */
			map: undefined,
			/**
			 * 地图加载器全局对象（等同于官方文档中的AMap全局对象）
			 */
			mapLoader: undefined
		};
	},
	actions: {
		/**
		 * 初始化地图
		 * @param {String} containerId 用于显示地图的元素id
		 */
		async initMap(containerId) {
			this.mapLoader = await AMapLoader.load({
				key: 'b07dd8d78aefc5fb085202842422940d',
				version: '2.0',
				plugins: []
			});
			this.map = await new this.mapLoader.Map(containerId, {
				zoom: 16,
				center: [118.715383, 32.203407]
			});
			// 加载并设定定位插件
			const locationStore = useLocationStore();
			this.map.plugin('AMap.Geolocation', () => {
				locationStore.locationHandler = new this.mapLoader.Geolocation({
					enableHighAccuracy: true,
					showButton: false,
					showMarker: false,
					showCircle: false,
					panToLocation: false,
					zoomToAccuracy: false
				});
				this.map.addControl(locationStore.locationHandler);
			});
		}
	}
});