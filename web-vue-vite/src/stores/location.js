// 位置状态
import { defineStore } from 'pinia';
import gcoord from 'gcoord';
import { MESSAGE_TYPE, showMessage } from '../utils/element-message';
import { useMapStore } from './map';

/**
 * @typedef Position 位置
 * @property {Number} longitude 经度
 * @property {Number} latitude 纬度
 * @property {Number} elevation 海拔
 * @property {Number} orientation 方向
 */

export const useLocationStore = defineStore('locationStore', {
	state() {
		return {
			/**
			 * @type {Position}
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
				 * 该朝向是校正后的朝向，其值 = 设备原始旋转角 + 旋转角修正值
				 */
				orientation: undefined
			},
			/**
			 * 设备原始获取的旋转角
			 * 0表示正北，左转增加，最大360
			 */
			originDeviceOrientation: undefined,
			/**
			 * 旋转角修正值，最终会加到原始旋转角上得到实际旋转角
			 */
			rotateOffset: 0
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
		},
		/**
		 * 将手机传感器角度转换为屏幕上CSS旋转角度
		 * @param {Number | undefined} orientation 传感器角度
		 * @return {Number | undefined} 屏幕CSS元素旋转角度
		 */
		orientationToCSSRotate(orientation) {
			return orientation == null ? undefined : 360 - orientation;
		},
		/**
		 * 将屏幕上CSS旋转角度转换为手机传感器角度
		 * @param {Number | undefined} rotate 屏幕CSS元素旋转角度
		 * @return {Number | undefined} 传感器角度
		 */
		cssRotateToOrientation(rotate) {
			if (rotate == null) {
				return null;
			}
			// 按照正负数分情况处理为0-360度的情况
			rotate = rotate < 0 ? 360 + (rotate % 360) : rotate % 360;
			return 360 - rotate;
		},
		/**
		 * 设定设备的旋转角度信息
		 */
		setDeviceOrientation() {
			if (this.originDeviceOrientation == null) {
				this.position.orientation = undefined;
				return;
			}
			this.position.orientation = this.originDeviceOrientation + this.rotateOffset;
		},
		/**
		 * 开启方向自动校正
		 * 通过计时器，根据移动方向自动校正朝向
		 */
		enableOrientationAutoFix() {
			// 两次移动的经纬度
			let beforeMove, afterMove;
			// 两次移动的经纬度差值
			let longitudeOffset, latitudeOffset;
			// 两次移动得到的连线长度（直接使用经纬度坐标计算，这里不需要具体长度）
			let length;
			// 两次移动得到的连线和水平轴的角度（单位为度）
			let angle;
			// 计算得到的CSS角度
			let cssAngle;
			// 地图store
			const mapStore = useMapStore();
			// 定时器，每秒进行一次校正
			setInterval(() => {
				// 检查位置是否可用
				if (!this.checkLocationEnabled(false)) {
					return;
				}
				// 检查陀螺仪是否可用
				if (this.position.orientation == null) {
					return;
				}
				// 取第一次位置
				beforeMove = {
					longitude: this.position.longitude,
					latitude: this.position.latitude
				};
				// 延时750毫秒取第二次位置并计算移动方向
				setTimeout(() => {
					afterMove = {
						longitude: this.position.longitude,
						latitude: this.position.latitude
					};
					// 计算差值
					longitudeOffset = afterMove.longitude - beforeMove.longitude;
					latitudeOffset = afterMove.latitude - beforeMove.latitude;
					// 计算长度和角度
					length = Math.sqrt(Math.pow(longitudeOffset, 2) + Math.pow(latitudeOffset, 2));
					// 如果位置不变或者实际距离小于1米，则忽略本次校正
					if (length === 0 || mapStore.getDistance(beforeMove, afterMove) < 1) {
						return;
					}
					angle = (Math.acos(longitudeOffset / length) / Math.PI) * 180;
					// 转换为CSS角度
					cssAngle = latitudeOffset > 0 ? 90 - angle : 90 + angle;
					// 最后计算为设备旋转角，并设定到偏差值上
					this.rotateOffset = this.cssRotateToOrientation(cssAngle) - this.originDeviceOrientation;
					this.setDeviceOrientation();
				}, 750);
			}, 1250);
		}
	}
});