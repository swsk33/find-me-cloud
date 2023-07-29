<!-- 添加模板对话框 -->
<template>
	<div class="add-template-dialog">
		<el-dialog class="dialog" v-model="showDialog" width="75vw" top="36vh" :show-close="false" :center="true" title="创建房间模板">
			<div class="content">
				<el-input class="input share-id" v-model="shareID" placeholder="填写房间模板分享密钥"/>
			</div>
			<template #footer>
				<div class="button-box">
					<el-button class="button ok" type="success" @click="addRoomTemplate" plain>确定</el-button>
					<el-button class="button cancel" type="danger" plain @click="showDialog = false">取消</el-button>
				</div>
			</template>
		</el-dialog>
	</div>
</template>

<script setup>
import { ref } from 'vue';
import { REQUEST_METHOD, sendRequest } from '../../../../utils/request';
import { REQUEST_PREFIX } from '../../../../param/request-prefix';
import { MESSAGE_TYPE, showMessage } from '../../../../utils/element-message';

// 是否显示对话框
const showDialog = ref(false);
// 模板分享密钥
let shareID = ref(undefined);

// 定义事件，父组件监听对应事件以做出一些操作
const emits = defineEmits(['dataChanged']);

/**
 * 添加分享的模板
 */
const addRoomTemplate = async () => {
	const response = await sendRequest(REQUEST_PREFIX.SESSION_ROOM_TEMPLATE_SHARE + 'validate/' + shareID.value, REQUEST_METHOD.GET);
	if (!response.success) {
		showMessage(response.message, MESSAGE_TYPE.error);
		return;
	}
	showMessage(response.message, MESSAGE_TYPE.success);
	showDialog.value = false;
	// 通知父组件重新加载
	emits('dataChanged');
};

// 定义导出
defineExpose({ showDialog });
</script>

<style lang="scss" scoped>
// 导入对话框共用样式
@import "../../../../assets/scss/dialog.scss";

.add-template-dialog {
	.content {
		position: relative;
		height: 5vh;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
	}
}
</style>