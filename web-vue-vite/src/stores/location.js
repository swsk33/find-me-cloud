// 位置状态
import { defineStore } from 'pinia';
import gcoord from 'gcoord';
import { MESSAGE_TYPE, showMessage } from '../utils/element-message';

export const useLocationStore = defineStore('locationStore', {
	state() {
		return {
			/**
			 * 用户自身的位置信息，包含经纬度、海拔、朝向
			 */
			position: {
				/**
				 * 经度
				 */
				longitude: undefined,
				/**
				 * 纬度
				 */
				latitude: undefined,
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
		 * 将WGS84坐标系转换成高德坐标系，并存放在store中
		 * @param longitude 经度
		 * @param latitude 纬度
		 */
		convertToGCJ02(longitude, latitude) {
			if (longitude == null || latitude == null) {
				return;
			}
			const result = gcoord.transform([longitude, latitude], gcoord.WGS84, gcoord.GCJ02);
			this.position.longitude = result[0];
			this.position.latitude = result[1];
		},
		/**
		 * 检查定位是否可用
		 * @param {Boolean} showTip 如果不可用，是否弹出提示
		 * @return {Boolean} 定位是否可用
		 */
		checkLocationEnabled(showTip = false) {
			if (this.position.longitude == null || this.position.latitude == null) {
				if (showTip) {
					showMessage('定位不可用！请检查定位功能是否开启，以及是否授予浏览器定位权限！', MESSAGE_TYPE.error);
				}
				return false;
			}
			return true;
		}
	}
});