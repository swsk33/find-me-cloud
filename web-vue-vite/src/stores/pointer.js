// 存放和管理所有的用户指针状态
import { defineStore } from 'pinia';
import { useMapStore } from './map';

export const usePointerStore = defineStore('userPointerStore', {
	state() {
		return {
			/**
			 * 表示房间内所有用户，这里是存放房间内其他用户地理位置的地方（其中每个属性是用户id，值是对应的指针对象，除了自己之外，指针对象包含position位置和color颜色两个属性）
			 */
			userInRoom: {},
			/**
			 * 颜色列表
			 */
			colors: ['#2DB278', '#FF0000', '#FF006F', '#FFA200', '#8000FF', '#046B99', '#8E24FF', '#AB009F'],
			/**
			 * 指针状态常量
			 */
			POINTER_STATUS: {
				/**
				 * 指针在屏幕内
				 */
				IN_SCREEN: 0,
				/**
				 * 屏幕左侧之外
				 */
				LEFT: 1,
				/**
				 * 屏幕右侧之外
				 */
				RIGHT: 2,
				/**
				 * 屏幕下方之外
				 */
				BOTTOM: 3,
				/**
				 * 屏幕上方之外
				 */
				TOP: 4,
				/**
				 * 屏幕左上之外
				 */
				TOP_LEFT: 5,
				/**
				 * 屏幕右上之外
				 */
				TOP_RIGHT: 6,
				/**
				 * 屏幕左下之外
				 */
				BOTTOM_LEFT: 7,
				/**
				 * 屏幕右下之外
				 */
				BOTTOM_RIGHT: 8
			}
		};
	},
	actions: {
		/**
		 * 获取一个随机颜色
		 * @returns {String} 一个十六进制颜色代码
		 */
		getRandomColor() {
			return this.colors[Math.floor(Math.random() * this.colors.length)];
		},
		/**
		 * 创建一个用户指针，若该指针存在则会被修改
		 * @param userId 对应用户id
		 * @param position 位置对象，应当包含longitude、latitude、elevation和orientation四个属性表示经纬度、海拔和朝向
		 */
		addOrSetPointer(userId, position) {
			if (this.userInRoom[userId] == null) {
				this.userInRoom[userId] = {
					position: position,
					color: this.getRandomColor()
				};
				return;
			}
			this.userInRoom[userId].position = position;
		},
		/**
		 * 移除一个指针
		 * @param userId 对应用户id
		 */
		removePointer(userId) {
			delete this.userInRoom[userId];
		},
		/**
		 * 判断指针在屏幕中的位置，并设定其位置和状态
		 * @param x 传入和指针绑定的x位置（Vue Ref对象，会被实时改变）
		 * @param y 传入和指针绑定的y位置（Vue Ref对象，会被实时改变）
		 * @param longitude 指针实际对应的经度
		 * @param latitude 指针实际对应的纬度
		 * @param status 传入和指针绑定的状态常量（Vue Ref对象，会被实时改变）
		 * @param width 指针图标的宽度
		 * @param height 指针图标的高度
		 * @param xOffset 指针最终显示的向左的偏移量（指针在屏幕中央时）
		 * @param yOffset 指针最终显示的向上的偏移量（指针在屏幕中央时）
		 */
		computeAndSetPointer(x, y, longitude, latitude, status, width, height, xOffset, yOffset) {
			const mapStore = useMapStore();
			const point = mapStore.coordinateToMapContainerPositionCSS(longitude, latitude);
			// 判断指针位置状态
			// 指针在屏幕左侧时
			if (point[0] < 0) {
				x.value = 0;
				// 判断纵坐标
				// 左上方
				if (point[1] < 0) {
					status.value = this.POINTER_STATUS.TOP_LEFT;
					y.value = 0;
					return;
				}
				// 左边
				if (point[1] >= 0 && point[1] <= mapStore.size.height) {
					status.value = this.POINTER_STATUS.LEFT;
					y.value = point[1];
					return;
				}
				// 左下方
				status.value = this.POINTER_STATUS.BOTTOM_LEFT;
				y.value = mapStore.size.height - height;
				return;
			}
			// 指针横坐标在屏幕宽度范围内
			if (point[0] >= 0 && point[0] <= mapStore.size.width) {
				x.value = point[0];
				// 判断纵坐标
				// 上方
				if (point[1] < 0) {
					status.value = this.POINTER_STATUS.TOP;
					y.value = 0;
					return;
				}
				// 屏幕中
				if (point[1] >= 0 && point[1] <= mapStore.size.height) {
					status.value = this.POINTER_STATUS.IN_SCREEN;
					x.value = point[0] - xOffset;
					y.value = point[1] - yOffset;
					return;
				}
				// 下方
				status.value = this.POINTER_STATUS.BOTTOM;
				y.value = mapStore.size.height - height;
				return;
			}
			// 否则，就是在右边
			x.value = mapStore.size.width - width;
			// 判断纵坐标
			// 右上方
			if (point[1] < 0) {
				status.value = this.POINTER_STATUS.TOP_RIGHT;
				y.value = 0;
				return;
			}
			// 右侧
			if (point[1] >= 0 && point[1] <= mapStore.size.height) {
				status.value = this.POINTER_STATUS.RIGHT;
				y.value = point[1];
				return;
			}
			// 右下方
			status.value = this.POINTER_STATUS.BOTTOM_RIGHT;
			y.value = mapStore.size.height - height;
		}
	}
});