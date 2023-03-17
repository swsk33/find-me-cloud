// 位置状态
import { defineStore } from 'pinia';
import { MESSAGE_TYPE, showMessage } from '../utils/element-message';
import { useMapStore } from './map';

export const useLocationStore = defineStore('locationStore', {
	state() {
		return {
			/**
			 * 定位器，用于执行定位操作
			 */
			locationHandler: undefined,
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
				elevation: 10,
				/**
				 * 朝向（度）
				 */
				orientation: 0
			}
		};
	},
	actions: {
		/**
		 * 执行定位操作
		 * @param {boolean} showTip 如果发生错误，是否弹出提示
		 * @param {boolean} zoomToUser 定位成功后，是否聚焦到用户
		 */
		getUserPosition(showTip = false, zoomToUser = false) {
			this.locationHandler.getCurrentPosition((status, result) => {
				// 定位失败则退出
				if (status !== 'complete') {
					if (showTip) {
						showMessage('定位失败！请检查手机和浏览器定位功能是否打开，以及是否给予浏览器定位权限！', MESSAGE_TYPE.error);
						this.positionEnabled = false;
					}
					return;
				}
				// 定位成功操作
				this.positionEnabled = true;
				this.position.longitude = result.position.getLng();
				this.position.latitude = result.position.getLat();
				// 缩放到用户
				const mapStore = useMapStore();
				if (zoomToUser) {
					mapStore.map.setZoomAndCenter(16, [this.position.longitude, this.position.latitude]);
				}
			});
		}
	}
});