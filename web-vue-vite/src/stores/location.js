// 位置状态
import { defineStore } from 'pinia';
import { MESSAGE_TYPE, showMessage } from '../utils/element-message';
import { useMapStore } from './map';

export const useLocationStore = defineStore('locationStore', {
	state() {
		return {
			/**
			 * 定位器实例
			 */
			geolocation: new BMap.Geolocation(),
			/**
			 * 用户定位功能是否可用
			 */
			positionEnabled: false,
			/**
			 * 位置信息，包含经纬度、海拔、朝向
			 */
			position: {
				/**
				 * 经度
				 */
				longitude: 118.715383,
				/**
				 * 纬度
				 */
				latitude: 32.203407,
				/**
				 * 海拔（米）
				 */
				elevation: undefined,
				/**
				 * 朝向（度）
				 * 0表示正北，左转增加，最大360
				 */
				orientation: undefined
			}
		};
	},
	actions: {
		/**
		 * 将经纬度转换为百度地图点对象
		 * @param longitude 经度
		 * @param latitude 纬度
		 * @return {Object} 百度地图点对象
		 */
		toBMapPoint(longitude, latitude) {
			return new BMap.Point(longitude, latitude);
		},
		/**
		 * 用户定位
		 */
		getUserPosition(showTip = false, zoomToUser = false) {
			this.geolocation.getCurrentPosition((result) => {
				if (this.geolocation.getStatus() !== BMAP_STATUS_SUCCESS) {
					if (showTip) {
						showMessage('获取定位失败！请检查定位功能是否打开！以及是否授予浏览器定位权限！', MESSAGE_TYPE.error);
					}
					return;
				}
				this.position.longitude = result.point.lng;
				this.position.latitude = result.point.lat;
				if (zoomToUser) {
					const mapStore = useMapStore();
					mapStore.map.panTo(this.toBMapPoint(this.position.longitude, this.position.latitude));
					mapStore.map.setZoom(17);
				}
			});
		}
	}
});