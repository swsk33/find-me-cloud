<!-- 加入房间弹窗 -->
<template>
	<div class="join-room-dialog">
		<el-dialog class="dialog" v-model="showDialog" width="75vw" top="30vh" :show-close="false" :center="true" title="加入房间">
			<div class="input-box">
				<el-input class="input name" v-model="joinRoomData.id" placeholder="房间id"/>
				<el-input class="input password" v-model="joinRoomData.password" placeholder="房间密码" show-password/>
			</div>
			<template #footer>
				<div class="button-box">
					<el-button class="button ok" type="success" @click="joinRoom" plain>确定</el-button>
					<el-button class="button cancel" type="danger" plain @click="showDialog = false">取消</el-button>
				</div>
			</template>
		</el-dialog>
	</div>
</template>

<script setup>
import { reactive, ref } from 'vue';
import { useRoomStore } from '../../../../stores/room';

const roomStore = useRoomStore();
// 是否显示对话框
const showDialog = ref(false);
// 加入房间信息
const joinRoomData = reactive({
	id: undefined,
	password: undefined
});

/**
 * 加入房间方法
 */
const joinRoom = async () => {
	await roomStore.connectToRoom(joinRoomData.id, joinRoomData.password);
	showDialog.value = false;
};

defineExpose({ showDialog });
</script>

<style lang="scss">
// 导入对话框共用样式
@import "../../../../assets/scss/dialog.scss";
</style>