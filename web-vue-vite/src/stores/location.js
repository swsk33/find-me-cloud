// 位置状态
import { defineStore } from 'pinia';
import gcoord from 'gcoord';

export const useLocationStore = defineStore('locationStore', {
	state() {
		return {
			/**
			 * 位置信息，包含经纬度、海拔、朝向
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
		}
	}
});