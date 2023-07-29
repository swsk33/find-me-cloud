<!-- 创建房间对话框 -->
<template>
	<div class="create-room-dialog">
		<el-dialog class="dialog" v-model="showDialog" width="75vw" top="30vh" :show-close="false" :center="true" title="创建房间">
			<div class="input-box">
				<el-input class="input name" v-model="createRoomData.name" placeholder="房间名"/>
				<el-input class="input password" v-model="createRoomData.password" placeholder="房间密码" show-password/>
			</div>
			<template #footer>
				<div class="button-box">
					<el-button class="button ok" type="success" @click="createRoom" plain>确定</el-button>
					<el-button class="button cancel" type="danger" plain @click="showDialog = false">取消</el-button>
				</div>
			</template>
		</el-dialog>
	</div>
</template>

<script setup>
import { reactive, ref } from 'vue';
import { REQUEST_METHOD, sendRequest } from '../../../../utils/request';
import { REQUEST_PREFIX } from '../../../../param/request-prefix';
import { MESSAGE_TYPE, showMessage } from '../../../../utils/element-message';
import { useLocationStore } from '../../../../stores/location';
import { useRoomStore } from '../../../../stores/room';

const locationStore = useLocationStore();
const roomStore = useRoomStore();

// 对话框是否显示
let showDialog = ref(false);
// 创建房间信息
const createRoomData = reactive({
	name: undefined,
	password: undefined
});

/**
 * 创建房间方法
 */
const createRoom = async () => {
	if (!locationStore.checkLocationEnabled(true)) {
		showDialog.value = false;
		return;
	}
	const response = await sendRequest(REQUEST_PREFIX.SESSION_ROOM + 'create', REQUEST_METHOD.POST, createRoomData);
	if (!response.success) {
		showMessage(response.message, MESSAGE_TYPE.error);
		return;
	}
	showMessage('创建房间成功！', MESSAGE_TYPE.success);
	// 然后立马加入房间
	await roomStore.connectToRoom(response.data.id, createRoomData.password);
	showDialog.value = false;
};

defineExpose({ showDialog });
</script>

<style lang="scss">
// 导入对话框共用样式
@import "../../../../assets/scss/dialog.scss";
</style>