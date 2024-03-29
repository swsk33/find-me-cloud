<!-- 显示用户的指针标记 -->
<template>
	<!-- 如果位置信息不可用，就先不显示指针 -->
	<div class="user-pointer" :style="{left: x + 'px', top: y + 'px'}" v-if="pointerData.position != null && pointerData.position.longitude != null && pointerData.position.latitude != null">
		<!-- 形态1：在屏幕范围内时，显示指针本身 -->
		<div class="user-marker" @click="pointerInformationDialogRef.showDialog = true" v-if="status === pointerStore.POINTER_STATUS.IN_SCREEN">
			<div class="marker-base" :style="{transform: 'rotate(' + locationStore.orientationToCSSRotate(pointerData.position.orientation) + 'deg)'}">
				<div class="circle" :style="{backgroundColor: pointerData.color, boxShadow: pointerData.color + ' 0 0 6px'}">
				</div>
				<div class="heading" :style="{borderBottomColor: pointerData.color}" v-if="pointerData.position.orientation != null"></div>
			</div>
			<!-- 高程标识 -->
			<CaretTop class="height-icon above" v-if="height === HEIGHT_STATUS.ABOVE" :style="{color:pointerData.color}"/>
			<CaretBottom class="height-icon down" v-if="height === HEIGHT_STATUS.DOWN" :style="{color:pointerData.color}"/>
			<img class="user-avatar" :src="userStore.getUserAvatarURL(userData)" alt="无法显示"/>
		</div>
		<!-- 形态2：屏幕范围外时，显示昵称标识 -->
		<div class="out-pointer" @click="zoomToPointer" v-if="status !== pointerStore.POINTER_STATUS.IN_SCREEN" :style="{color: pointerData.color}">
			<Top v-if="status === pointerStore.POINTER_STATUS.TOP" class="arrow top"/>
			<Bottom v-if="status === pointerStore.POINTER_STATUS.BOTTOM" class="arrow bottom"/>
			<Back v-if="status === pointerStore.POINTER_STATUS.LEFT" class="arrow left"/>
			<Right v-if="status === pointerStore.POINTER_STATUS.RIGHT" class="arrow right"/>
			<TopLeft v-if="status === pointerStore.POINTER_STATUS.TOP_LEFT" class="arrow top-left"/>
			<TopRight v-if="status === pointerStore.POINTER_STATUS.TOP_RIGHT" class="arrow top-right"/>
			<BottomLeft v-if="status === pointerStore.POINTER_STATUS.BOTTOM_LEFT" class="arrow bottom-left"/>
			<BottomRight v-if="status === pointerStore.POINTER_STATUS.BOTTOM_RIGHT" class="arrow bottom-right"/>
			<div class="text" :style="{backgroundColor: pointerData.color}" v-if="userData != null">{{ userData.nickname + (props.userId === 0 ? '(我)' : '') }}</div>
		</div>
		<!-- 详细信息对话框 -->
		<PointerInformation ref="pointerInformationDialogRef" :pointer-data="pointerData" :user-data="userData" :user-id="props.userId"/>
	</div>
</template>

<script setup>
import { usePointerStore } from '../../../stores/pointer';
import { useUserStore } from '../../../stores/user';
import { useMapStore } from '../../../stores/map';
import { onMounted, reactive, ref, watch } from 'vue';
import { useLocationStore } from '../../../stores/location';
import { Top, Bottom, Back, Right, TopRight, TopLeft, BottomRight, BottomLeft, CaretTop, CaretBottom } from '@element-plus/icons-vue';
import { useRoomStore } from '../../../stores/room';
import PointerInformation from './dialog/PointerInformation.vue';

// 传入用户id，0表示自己
const props = defineProps(['userId']);

const pointerStore = usePointerStore();
const userStore = useUserStore();
const mapStore = useMapStore();
const locationStore = useLocationStore();
const roomStore = useRoomStore();

// 组件引用
const pointerInformationDialogRef = ref(null);

// 指针基本信息，存放这个指针标记对应的用户的位置信息等等
let pointerData = reactive({
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
		 * 高程
		 */
		elevation: undefined,
		/**
		 * 朝向
		 */
		orientation: undefined
	},
	color: '#2973ff'
});
// 指针对应的用户信息
let userData = ref(undefined);
// 指针在屏幕上的坐标
const x = ref(0);
const y = ref(0);
// 指针位置状态
const status = ref(0);
// 高程状态（这个指针对应用户相对于自己的高程）
const height = ref(0);

// 高程状态枚举
const HEIGHT_STATUS = {
	/**
	 * 同一水平面（上下误差8米之内，若自己的高程信息不可用，则所有用户都为这个值，若某个用户高程信息不可用，则该用户状态为这个值）
	 * 同一水平面时，指针不显示高程箭头
	 */
	AT_SAME_LEVEL: 0,
	/**
	 * 在上方
	 */
	ABOVE: 1,
	/**
	 * 在下方
	 */
	DOWN: 2
};

/**
 * 刷新指针本身的位置
 */
const refreshPointerPosition = () => {
	pointerStore.computeAndSetPointer(x, y, pointerData.position.longitude, pointerData.position.latitude, status, 148, 76, 24, 24);
};

/**
 * 刷新指针高程状态
 */
const refreshPointerHeight = () => {
	// 如果自己高程信息不可用，则全部视为同一水准面
	// 如果该用户的高程信息不可用，也视为同一水准面
	// 如果指针标记是自己，也视为同一水准面
	// 最后，和自己上下误差8米之内算同一水平面
	if (props.userId === 0 || locationStore.position.elevation == null || pointerData.position.elevation == null || Math.abs(locationStore.position.elevation - pointerData.position.elevation) <= 8) {
		height.value = HEIGHT_STATUS.AT_SAME_LEVEL;
		return;
	}
	if (pointerData.position.elevation > locationStore.position.elevation) {
		height.value = HEIGHT_STATUS.ABOVE;
		return;
	}
	height.value = HEIGHT_STATUS.DOWN;
};

/**
 * 缩放到指针所在位置为中心
 */
const zoomToPointer = () => {
	mapStore.zoomToUser(pointerData.position.longitude, pointerData.position.latitude);
};

// 导出变量
defineExpose({ refreshPointerPosition });

onMounted(() => {
	// id为0时，监听自己位置
	if (props.userId === 0) {
		// 订阅位置state，位置变化及时刷新指针坐标
		locationStore.$subscribe((mutation, state) => {
			pointerData.position = state.position;
			// 刷新指针位置
			refreshPointerPosition();
		});
		// 监听用户信息变化
		watch(() => userStore.userData, () => {
			userData.value = userStore.userData;
		}, {
			immediate: true
		});
	} else {
		// 订阅指针store获取对应的其他用户的信息，位置变化及时刷新指针坐标和高程信息
		pointerStore.$subscribe((mutation, state) => {
			if (state.userInRoom[props.userId] == null) {
				return;
			}
			pointerData.position = state.userInRoom[props.userId].position;
			pointerData.color = state.userInRoom[props.userId].color;
			// 改变指针坐标
			refreshPointerPosition();
			refreshPointerHeight();
			// 获取对应的房间的用户信息
			userData.value = roomStore.roomInfo.users[props.userId];
		});
	}
});
</script>

<style lang="scss" scoped>
.user-pointer {
	position: absolute;

	.user-marker, .out-pointer {
		position: absolute;
	}

	// 形态1：用户头像指针
	.user-marker {
		height: 48px;
		width: 48px;
		left: 0;
		top: 0;

		.marker-base {
			position: absolute;
			width: 100%;
			height: 100%;

			.circle {
				position: absolute;
				width: 29px;
				height: 29px;
				left: 9.5px;
				top: 9.5px;
				border-radius: 50%;
			}

			.heading {
				position: absolute;
				width: 0;
				height: 0;
				border-bottom-width: 16px;
				border-bottom-style: solid;
				border-left: 10px solid transparent;
				border-right: 10px solid transparent;
				left: 14.5px;
			}
		}

		.height-icon {
			position: absolute;
			width: 18px;
			height: 18px;
			right: 0;
			bottom: 0;
		}

		.user-avatar {
			position: absolute;
			width: 24px;
			left: 12px;
			top: 12px;
			border-radius: 50%;
		}
	}

	// 形态2：屏幕外指针
	.out-pointer {
		width: 148px;
		height: 76px;
		left: 0;
		top: 0;

		.text {
			position: absolute;
			width: 100px;
			height: 28px;
			left: 24px;
			top: 24px;
			line-height: 28px;
			text-align: center;
			font-size: 12px;
			color: white;
			border-radius: 6px;
			white-space: nowrap;
			overflow: hidden;
			text-overflow: ellipsis;
		}

		.arrow {
			position: absolute;
			height: 24px;
			width: 24px;
		}

		// 调整各个箭头
		.top, .bottom {
			left: 62px;
		}

		.bottom {
			bottom: 0;
		}

		.left, .right {
			top: 26px;
		}

		.right {
			right: 0;
		}

		.top-left, .bottom-left {
			left: 5px;
		}

		.top-left, .top-right {
			top: 4px;
		}

		.bottom-left, .bottom-right {
			bottom: 4px;
		}

		.top-right, .bottom-right {
			right: 5px;
		}
	}
}
</style>