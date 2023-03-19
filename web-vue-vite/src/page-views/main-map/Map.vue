<template>
	<div class="map">
		<div id="map-container"></div>
		<Location class="location-button" @click="mapStore.zoomToUser()"/>
		<div class="button-box">
			<el-button class="button create-room" type="success" v-if="!roomStore.inTheRoom" @click="showCreateDialog = true" size="small">创建房间</el-button>
			<el-button class="button join-room" type="primary" v-if="!roomStore.inTheRoom" @click="showJoinDialog = true" size="small">加入房间</el-button>
			<el-button class="button lookup-room" type="success" v-if="roomStore.inTheRoom" size="small" @click="showRoomInfoDialog = true" plain>查看房间</el-button>
			<el-button class="button exit-room" type="danger" v-if="roomStore.inTheRoom" size="small" @click="roomStore.session.close()" plain>退出房间</el-button>
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
					<el-button class="button ok" type="success" @click="createRoom" plain>确定</el-button>
					<el-button class="button cancel" type="danger" plain @click="showCreateDialog = false">取消</el-button>
				</div>
			</template>
		</el-dialog>
		<!-- 加入房间 -->
		<el-dialog class="join-room-dialog" v-model="showJoinDialog" width="75vw" top="30vh" :show-close="false" :center="true" title="加入房间">
			<div class="input-box">
				<el-input class="input name" v-model="joinRoomData.id" placeholder="房间id"/>
				<el-input class="input password" v-model="joinRoomData.password" placeholder="房间密码" show-password/>
			</div>
			<template #footer>
				<div class="button-box">
					<el-button class="button ok" type="success" @click="joinRoom" plain>确定</el-button>
					<el-button class="button cancel" type="danger" plain @click="showJoinDialog = false">取消</el-button>
				</div>
			</template>
		</el-dialog>
		<!-- 房间详情 -->
		<el-dialog class="room-lookup-dialog" v-model="showRoomInfoDialog" width="85vw" top="30vh" :show-close="false" :center="true" destroy-on-close>
			<template #header>
				<div class="title">{{ roomStore.roomInfo.name }}</div>
			</template>
			<div class="content">
				<div class="room-id">房间id：{{ roomStore.roomInfo.id }}</div>
				<div class="room-password">密码：{{ roomStore.roomPassword }}</div>
				<div class="users">
					<div class="text">成员：</div>
					<ul>
						<li>
							<img :src="userStore.getUserAvatarURL(userStore.userData)" :style="{borderColor: '#2973ff'}" alt="无法显示"/>
							<div class="nickname">{{ userStore.userData.nickname }}（我）</div>
						</li>
						<li v-for="(value, key, index) in roomStore.roomInfo.users" :key="key">
							<img :src="userStore.getUserAvatarURL(value)" :style="{borderColor: pointerStore.userInRoom[key].color}" alt="无法显示"/>
							<div class="nickname">{{ value.nickname }}</div>
						</li>
					</ul>
				</div>
			</div>
			<template #footer>
				<div class="button-box">
					<el-button class="button copy-id" type="success" @click="copyRoomInfo" size="small" plain>复制房间信息</el-button>
					<el-button class="button cancel" type="warning" plain size="small" @click="roomStore.getRoomInfo(roomStore.roomInfo.id)">刷新房间</el-button>
					<el-button class="button cancel" type="danger" plain size="small" @click="showRoomInfoDialog = false">知道了</el-button>
				</div>
			</template>
		</el-dialog>
	</div>
</template>

<script setup>
import { onMounted, reactive, ref, watch } from 'vue';
import { useMapStore } from '../../stores/map';
import { useLocationStore } from '../../stores/location';
import { useUserStore } from '../../stores/user';
import { useClipboard, useDeviceOrientation, useGeolocation } from '@vueuse/core';
import Location from './components/Location.vue';
import UserPointerMarker from './components/UserPointerMarker.vue';
import { usePointerStore } from '../../stores/pointer';
import { useRoomStore } from '../../stores/room';
import { REQUEST_METHOD, sendRequest } from '../../utils/request';
import { MESSAGE_TYPE, showMessage } from '../../utils/element-message';

const mapStore = useMapStore();
const locationStore = useLocationStore();
const userStore = useUserStore();
const pointerStore = usePointerStore();
const roomStore = useRoomStore();

const selfMarker = ref(null);
const othersMarker = ref([]);

// 剪贴板
const clipboard = useClipboard();

// 响应式位置信息
const WGS84Coordinates = useGeolocation().coords;

// 对话窗状态
const showCreateDialog = ref(false);
const showJoinDialog = ref(false);
const showRoomInfoDialog = ref(false);

// 提交数据
// 创建房间信息
const createRoomData = reactive({
	name: undefined,
	password: undefined
});

// 加入房间信息
const joinRoomData = reactive({
	id: undefined,
	password: undefined
});

/**
 * 创建房间方法
 */
const createRoom = async () => {
	if (!locationStore.checkLocationEnabled(true)) {
		showCreateDialog.value = false;
		return;
	}
	const response = await sendRequest('/api/session/room/create', REQUEST_METHOD.POST, createRoomData);
	if (!response.success) {
		showMessage(response.message, MESSAGE_TYPE.error);
		return;
	}
	showMessage('创建房间成功！', MESSAGE_TYPE.success);
	// 然后立马加入房间
	await roomStore.connectToRoom(response.data.id, createRoomData.password);
	showCreateDialog.value = false;
};

/**
 * 加入房间方法
 */
const joinRoom = async () => {
	await roomStore.connectToRoom(joinRoomData.id, joinRoomData.password);
	showJoinDialog.value = false;
};

/**
 * 复制房间信息方法
 */
const copyRoomInfo = () => {
	clipboard.copy('房间id：' + roomStore.roomInfo.id + '\n房间名：' + roomStore.roomInfo.name + '\n房间密码：' + roomStore.roomPassword + '\n\n' + location.host);
	if (clipboard.copied) {
		showMessage('复制成功！', MESSAGE_TYPE.success);
	} else {
		showMessage('复制失败！', MESSAGE_TYPE.error);
	}
};

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

	.create-room-dialog, .join-room-dialog, .room-lookup-dialog {
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

	.room-lookup-dialog {
		.title {
			white-space: nowrap;
			overflow: hidden;
			text-overflow: ellipsis;
		}

		.content {
			position: relative;
			height: 20vh;
			width: 78vw;
			left: -6px;

			.room-password {
				width: 90%;
				white-space: nowrap;
				overflow: hidden;
				text-overflow: ellipsis;
			}

			div {
				position: relative;
				margin-top: 5px;
			}
		}

		ul {
			position: relative;
			height: 12vh;
			overflow: scroll;
			border: #009dff dashed 1px;
			border-radius: 5px;
			margin-top: 10px;

			li {
				position: relative;
				width: 96%;
				height: 36px;
				left: 2%;
				display: flex;
				align-items: center;
				border-bottom: 1px #a200ff dashed;

				img {
					position: relative;
					height: 28px;
					width: 28px;
					border-style: solid;
					border-width: 2px;
					border-radius: 50%;
				}

				.nickname {
					position: relative;
					margin-left: 6px;
					width: 80%;
					white-space: nowrap;
					overflow: hidden;
					text-overflow: ellipsis;
				}
			}
		}
	}
}
</style>