// 地图
import { defineStore } from 'pinia';
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
		initMap(containerId) {
			// 创建地图
			this.map = L.map(containerId, {
				zoom: 17,
				center: this.createLatLng({
					longitude: 118.715383,
					latitude: 32.203407
				}),
				zoomControl: false
			});
			// 加入高德瓦片图
			const mapURL = 'https://wprd04.is.autonavi.com/appmaptile?lang=zh_cn&size=1&style=7&x={x}&y={y}&z={z}';
			L.tileLayer(mapURL).addTo(this.map);
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
				this.map.setView(this.createLatLng({
					longitude: locationStore.position.longitude,
					latitude: locationStore.position.latitude
				}), 17);
				return;
			}
			this.map.setView(this.createLatLng({
				longitude: longitude,
				latitude: latitude
			}), 17);
		},
		/**
		 * 计算起点和终点两点距离
		 * @param {Position} start 起点
		 * @param {Position} end 终点
		 * @return {Number} 距离（单位：米）
		 */
		getDistance(start, end) {
			return this.createLatLng(start).distanceTo(this.createLatLng(end));
		},
		/**
		 * 将自定义的经纬度对象转换为leaflet中的LatLng对象
		 * @param {Position} position 经纬度对象
		 * @return {LatLng} leaflet中的LatLng对象
		 */
		createLatLng(position) {
			return L.latLng(position.latitude, position.longitude);
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
				const result = state.map.latLngToContainerPoint(L.latLng(latitude, longitude));
				return [result.x, result.y];
			};
		}
	}
});