// 地图
import { defineStore } from 'pinia';
import AMapLoader from '@amap/amap-jsapi-loader';
import { useLocationStore } from './location';
import { MESSAGE_TYPE, showMessage } from '../utils/element-message';

export const useMapStore = defineStore('mapStore', {
	state() {
		return {
			/**
			 * 高德地图加载器对象（官方文档中的AMap全局对象）
			 */
			mapLoader: undefined,
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
		async initMap(containerId) {
			// 创建地图加载器
			this.mapLoader = await AMapLoader.load({
				key: 'b07dd8d78aefc5fb085202842422940d',
				version: '2.0'
			});
			// 创建地图
			this.map = new this.mapLoader.Map(containerId, {
				zoom: 16,
				center: [118.715383, 32.203407]
			});
		},
		/**
		 * 缩放地图到用户
		 */
		zoomToUser() {
			const locationStore = useLocationStore();
			if (locationStore.position.longitude === undefined || locationStore.position.latitude === undefined) {
				showMessage('定位失败！请检查定位功能是否开启，以及是否授予浏览器定位权限！', MESSAGE_TYPE.error);
				return;
			}
			this.map.setZoomAndCenter(17, [locationStore.position.longitude, locationStore.position.latitude]);
		}
	},
	getters: {
		/**
		 * 地理坐标转换为容器坐标
		 */
		coordinateToMapContainerPositionCSS: (state) => {
			/**
			 * @param longitude 经度
			 * @param latitude 纬度
			 * @return {Array} [x, y]
			 */
			return (longitude, latitude) => {
				if (state.map == null || longitude == null || latitude == null) {
					return [0, 0];
				}
				const result = state.map.lngLatToContainer([longitude, latitude]);
				return [result.getX() - 24, result.getY() - 24];
			};
		}
	}
});