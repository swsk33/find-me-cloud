<template>
	<div class="map">
		<div id="map-container"></div>
		<Location class="location-button" @click="mapStore.zoomToUser()"/>
		<el-button class="update-user-info" type="warning" size="small" @click="editUserDialogRef.showDialog = true" plain>个人中心</el-button>
		<!-- 按钮盒子 -->
		<div class="room-button-box">
			<el-button class="button create-room" type="success" v-if="!roomStore.inTheRoom" @click="createRoomDialogRef.showDialog = true" size="small">创建房间</el-button>
			<el-button class="button join-room" type="primary" v-if="!roomStore.inTheRoom" @click="joinRoomDialogRef.showDialog = true" size="small">加入房间</el-button>
			<el-button class="button lookup-room" type="success" v-if="roomStore.inTheRoom" size="small" @click="roomLookupDialogRef.showDialog = true" plain>查看房间</el-button>
			<el-button class="button lookup-room" type="primary" v-if="roomStore.inTheRoom" size="small" @click="setRally" plain>放集结点</el-button>
			<el-button class="button exit-room" type="danger" v-if="roomStore.inTheRoom" size="small" @click="roomStore.disConnect" plain>退出房间</el-button>
		</div>
		<!-- 所有用户指针标识容器 -->
		<div class="user-marker-container">
			<UserPointerMarker ref="selfMarker" class="user-self" :user-id="0"/>
			<UserPointerMarker ref="othersMarker" class="user-in-room" v-for="(value, key, index) in pointerStore.userInRoom" :user-id="key" :key="key"/>
		</div>
		<!-- 集结点指针 -->
		<RallyPointerMarker class="rally-point-marker" ref="rallyMarker"/>
		<!-- 聊天组件 -->
		<Chat class="chat"/>
		<!-- 对话窗 -->
		<!-- 弹窗：创建房间 -->
		<CreateRoom ref="createRoomDialogRef"/>
		<!-- 弹窗：加入房间 -->
		<JoinRoom ref="joinRoomDialogRef"/>
		<!-- 弹窗：房间详情 -->
		<RoomLookup ref="roomLookupDialogRef"/>
		<!-- 弹窗：修改用户信息 -->
		<EditUser ref="editUserDialogRef"/>
	</div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue';
import { useMapStore } from '../../stores/map';
import { useLocationStore } from '../../stores/location';
import { useUserStore } from '../../stores/user';
import { useDeviceOrientation, useGeolocation } from '@vueuse/core';
import Location from './components/Location.vue';
import UserPointerMarker from './components/UserPointerMarker.vue';
import RallyPointerMarker from './components/RallyPointMarker.vue';
import Chat from './components/Chat.vue';
import { MESSAGE_TYPE, showMessage } from '../../utils/element-message';
import { useMessageStore } from '../../stores/message';
import { usePointerStore } from '../../stores/pointer';
import { useRoomStore } from '../../stores/room';
import CreateRoom from './components/dialog/CreateRoom.vue';
import JoinRoom from './components/dialog/JoinRoom.vue';
import RoomLookup from './components/dialog/RoomLookup.vue';
import EditUser from './components/dialog/EditUser.vue';

// pinia
const mapStore = useMapStore();
const locationStore = useLocationStore();
const userStore = useUserStore();
const pointerStore = usePointerStore();
const roomStore = useRoomStore();
const messageStore = useMessageStore();

// 组件或者DOM引用
const createRoomDialogRef = ref(null);
const joinRoomDialogRef = ref(null);
const roomLookupDialogRef = ref(null);
const editUserDialogRef = ref(null);
const selfMarker = ref(null);
const rallyMarker = ref(null);

// 其他用户指针
const othersMarker = ref([]);

// 响应式位置信息
const WGS84Coordinates = useGeolocation().coords;

// 监听位置信息
watch(WGS84Coordinates, () => {
	// 实时地转换经纬度坐标
	locationStore.convertToGCJ02(WGS84Coordinates.value.longitude, WGS84Coordinates.value.latitude);
	// 实时赋值高度变化
	locationStore.position.elevation = WGS84Coordinates.value.altitude;
});

/**
 * 设定集结点方法
 */
const setRally = () => {
	roomStore.settingRally = true;
	showMessage('点击地图上的某处以设定集结点...', MESSAGE_TYPE.success);
};

onMounted(async () => {
	// 初始化地图
	await mapStore.initMap('map-container');
	// 添加陀螺仪朝向信息（响应式）
	locationStore.position.orientation = useDeviceOrientation().alpha;
	// 给地图注册事件：当地图被移动、缩放时，刷新地图上所有指针位置
	mapStore.map.on('mapmove', () => {
		// 刷新用户标志
		selfMarker.value.refreshPointerPosition();
		for (let item of othersMarker.value) {
			item.refreshPointerPosition();
		}
		// 刷新集结点指针
		rallyMarker.value.refreshPosition();
	});
	// 地图被点击时，如果是设定集结点状态，则执行设定集结点操作
	mapStore.map.on('click', (e) => {
		if (!roomStore.settingRally) {
			return;
		}
		// 设定集结点
		roomStore.roomInfo.rally = {
			longitude: e.lnglat.getLng(),
			latitude: e.lnglat.getLat()
		};
		// 广播出去
		roomStore.session.send(JSON.stringify({
			type: messageStore.messageType.rallyChanged,
			senderId: userStore.userData.id,
			data: roomStore.roomInfo.rally
		}));
		roomStore.settingRally = false;
		showMessage('放置完成！', MESSAGE_TYPE.success);
	});
	// 等待3秒后缩放至用户
	setTimeout(() => {
		mapStore.zoomToUser();
	}, 3000);
	// 检查缓存，如果缓存房间信息不为空说明上次未正常退出
	const roomCache = JSON.parse(localStorage.getItem('room'));
	if (roomCache != null) {
		roomStore.showSessionRestoreDialog();
	}
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

	.update-user-info {
		position: absolute;
		left: 2%;
		top: 1%
	}

	> .room-button-box {
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

	// 使用deep修改引用的其它组件的样式
	:deep(.dialog) {
		.input-box {
			height: 80px;
			display: flex;
			flex-direction: column;
			align-items: center;

			.input, .input-item {
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