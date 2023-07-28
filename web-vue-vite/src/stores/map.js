// 地图
import { defineStore } from 'pinia';
import AMapLoader from '@amap/amap-jsapi-loader';
import { useLocationStore } from './location';

/**
 * @typedef Position 位置
 * @property {Number} longitude 经度
 * @property {Number} latitude 纬度
 * @property {Number} elevation 海拔
 * @property {Number} orientation 方向
 */

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
			map: undefined,
			/**
			 * 地图容器宽高
			 */
			size: {
				width: undefined,
				height: undefined
			}
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
			// 设定尺寸数据
			const mapSize = this.map.getSize();
			this.size.width = mapSize.getWidth();
			this.size.height = mapSize.getHeight();
		},
		/**
		 * 缩放地图到用户
		 * @param longitude 用户所在经度（0表示缩放到自己）
		 * @param latitude 用户所在纬度（0表示缩放到自己）
		 */
		zoomToUser(longitude = 0, latitude = 0) {
			if (longitude === 0 && latitude === 0) {
				const locationStore = useLocationStore();
				if (!locationStore.checkLocationEnabled(true)) {
					return;
				}
				this.map.setZoomAndCenter(17, [locationStore.position.longitude, locationStore.position.latitude]);
				return;
			}
			this.map.setZoomAndCenter(17, [longitude, latitude]);
		},
		/**
		 * 计算起点和终点两点距离
		 * @param {Position} start 起点
		 * @param {Position} end 终点
		 * @return {Number} 距离（单位：米）
		 */
		getDistance(start, end) {
			return this.mapLoader.GeometryUtil.distance([start.longitude, start.latitude], [end.longitude, end.latitude]);
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
				return [result.getX(), result.getY()];
			};
		}
	}
});