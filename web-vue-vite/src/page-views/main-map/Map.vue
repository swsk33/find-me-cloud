<template>
	<div class="map">
		<div id="map-container"></div>
		<Location class="location-button" @click="mapStore.zoomToUser()"/>
		<div class="button-box">
			<el-button class="button create-room" type="success" v-if="!roomStore.inTheRoom" @click="showCreateDialog = true" size="small">创建房间</el-button>
			<el-button class="button join-room" type="primary" v-if="!roomStore.inTheRoom" @click="showJoinDialog = true" size="small">加入房间</el-button>
		</div>
		<!-- 所有用户指针标识容器 -->
		<div class="user-marker-container">
			<UserPointerMarker ref="selfMarker" class="user-self" :user-id="0"/>
			<UserPointerMarker ref="othersMarker" class="user-in-room" v-for="(value, key, index) in pointerStore.userInRoom" :user-id="key" :key="key"/>
		</div>
		<!-- 对话窗 -->
		<!-- 创建房间 -->
		<el-dialog class="create-room-dialog" v-model="showCreateDialog" width="75vw" top="30vh" :show-close="false" :center="true" title="创建房间">
			<div class="input-box">
				<el-input class="input name" v-model="createRoomData.name" placeholder="房间名"/>
				<el-input class="input password" v-model="createRoomData.password" placeholder="房间密码" show-password/>
			</div>
			<template #footer>
				<div class="button-box">
					<el-button class="button ok" type="success" @click="" plain>确定</el-button>
					<el-button class="button cancel" type="danger" plain @click="showCreateDialog = false">取消</el-button>
				</div>
			</template>
		</el-dialog>
		<!-- 加入房间 -->
	</div>
</template>

<script setup>
import { onMounted, reactive, ref, watch } from 'vue';
import { useMapStore } from '../../stores/map';
import { useLocationStore } from '../../stores/location';
import { useUserStore } from '../../stores/user';
import { useDeviceOrientation, useGeolocation } from '@vueuse/core';
import Location from './components/Location.vue';
import UserPointerMarker from './components/UserPointerMarker.vue';
import { usePointerStore } from '../../stores/pointer';
import { useRoomStore } from '../../stores/room';

const mapStore = useMapStore();
const locationStore = useLocationStore();
const userStore = useUserStore();
const pointerStore = usePointerStore();
const roomStore = useRoomStore();

const selfMarker = ref(null);
const othersMarker = ref([]);

// 对话窗状态
const showCreateDialog = ref(false);
const showJoinDialog = ref(false);

// 提交数据
const createRoomData = reactive({
	name: undefined,
	password: undefined
});

/**
 * 创建房间方法
 */
const createRoom = async () => {
	const result = roomStore.createRoom(createRoomData.name, createRoomData.password);
};

// 响应式位置信息
const WGS84Coordinates = useGeolocation().coords;

// 监听位置信息
watch(WGS84Coordinates, () => {
	// 实时地转换经纬度坐标
	locationStore.convertToGCJ02(WGS84Coordinates.value.longitude, WGS84Coordinates.value.latitude);
	// 实时赋值高度变化
	locationStore.position.elevation = WGS84Coordinates.value.altitude;
});

onMounted(async () => {
	// 初始化地图
	await mapStore.initMap('map-container');
	// 添加陀螺仪朝向信息（响应式）
	locationStore.position.orientation = useDeviceOrientation().alpha;
	// 给地图注册事件：当地图被移动、缩放时，刷新地图上所有指针位置
	mapStore.map.on('mapmove', (e) => {
		selfMarker.value.refreshPointerPosition();
		for (let item of othersMarker.value) {
			item.refreshPointerPosition();
		}
	});
	// 等待3秒后缩放至用户
	setTimeout(() => {
		mapStore.zoomToUser();
	}, 3000);
});
</script>

<style lang="scss" scoped>
.map {
	#map-container {
		position: absolute;
		height: 100%;
		width: 100%;
	}

	.location-button {
		position: absolute;
		left: 5%;
		bottom: 6%;
	}

	> .button-box {
		position: absolute;
		right: 0;
		top: 0;
		width: 25%;
		height: 35%;
		display: flex;
		flex-direction: column;
		align-items: center;

		.button {
			position: relative;
			margin-left: 0;
			margin-top: 10px;
		}
	}

	.user-marker-container {
		position: absolute;
		height: 0;
		width: 0;
	}

	.create-room-dialog {
		.input-box {
			height: 80px;
			display: flex;
			flex-direction: column;
			align-items: center;

			.input {
				margin-top: 16px;

				&:first-child {
					margin-top: 0;
				}
			}
		}

		.button-box {
			position: relative;
			display: flex;
			justify-content: space-evenly;
			align-items: center;
			bottom: 10px;
		}
	}
}
</style>