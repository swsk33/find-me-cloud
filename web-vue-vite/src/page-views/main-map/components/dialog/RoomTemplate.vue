<!-- 房间模板对话框 -->
<template>
	<div class="room-template-dialog" v-loading.fullscreen.lock="isLoading" element-loading-text="正在获取用户模板...">
		<!-- 主对话框 -->
		<el-dialog class="dialog" v-model="showDialog" width="85vw" top="24vh" :show-close="false" :center="true" destroy-on-close>
			<template #header>
				<div class="title">房间模板</div>
			</template>
			<div class="content">
				<ul class="template-list">
					<li class="each-template" v-for="item in allTemplate">
						<div class="text">{{ item.name }}</div>
						<div class="button-box">
							<el-button class="button join" @click="joinRoomThroughTemplate(item.id)" type="primary" :icon="Position" size="small" circle/>
							<el-button class="button share" @click="createShareID(item.id)" type="success" :icon="Share" size="small" circle/>
							<el-button class="button delete" @click="removeRoomTemplate(item)" type="danger" :icon="Delete" size="small" circle/>
						</div>
					</li>
				</ul>
			</div>
			<template #footer>
				<div class="button-box">
					<el-button class="button create-template" @click="createTemplateDialogRef.showDialog = true" type="success" plain size="small">创建模板</el-button>
					<el-button class="button add-template" @click="addTemplateDialogRef.showDialog = true" type="primary" plain size="small">添加模板</el-button>
					<el-button class="button cancel" @click="showDialog = false" type="danger" plain size="small">关闭页面</el-button>
				</div>
			</template>
		</el-dialog>
		<!-- 创建模板对话框 -->
		<CreateTemplate ref="createTemplateDialogRef" @data-changed="getAllTemplate"/>
		<!-- 添加模板对话框 -->
		<AddTemplate ref="addTemplateDialogRef" @data-changed="getAllTemplate"/>
	</div>
</template>

<script setup>
import { ref, watch } from 'vue';
import { REQUEST_METHOD, sendRequest } from '../../../../utils/request';
import { REQUEST_PREFIX } from '../../../../param/request-prefix';
import { MESSAGE_TYPE, showInteractiveDialog, showMessage } from '../../../../utils/element-message';
import { Delete, Position, Share } from '@element-plus/icons-vue';
import { useRoomStore } from '../../../../stores/room';
import { useUserStore } from '../../../../stores/user';
import ClipboardJS from 'clipboard';
import CreateTemplate from './CreateTemplate.vue';
import AddTemplate from './AddTemplate.vue';

const roomStore = useRoomStore();
const userStore = useUserStore();

// 引用组件
const createTemplateDialogRef = ref(null);
const addTemplateDialogRef = ref(null);

// 是否显示对话框
const showDialog = ref(false);
// 是否正在加载模板
const isLoading = ref(false);
// 全部的房间模板
const allTemplate = ref([]);

/**
 * 获取全部房间模板
 */
const getAllTemplate = async () => {
	isLoading.value = true;
	const response = await sendRequest(REQUEST_PREFIX.SESSION_ROOM_TEMPLATE + 'get-by-login-user', REQUEST_METHOD.GET);
	if (!response.success) {
		isLoading.value = false;
		showMessage(response.message, MESSAGE_TYPE.error);
		return;
	}
	allTemplate.value = response.data;
	isLoading.value = false;
};

/**
 * 通过模板加入房间
 * @param {String} templateId 模板id
 */
const joinRoomThroughTemplate = (templateId) => {
	roomStore.connectToRoom(templateId, '', true);
	showDialog.value = false;
};

/**
 * 为模板创建分享密钥
 * @param {String} templateId 模板id
 */
const createShareID = async (templateId) => {
	const response = await sendRequest(REQUEST_PREFIX.SESSION_ROOM_TEMPLATE_SHARE + 'create/' + templateId, REQUEST_METHOD.POST);
	if (!response.success) {
		showMessage(response.message, MESSAGE_TYPE.error);
		return;
	}
	// 把分享密钥复制到剪切板
	ClipboardJS.copy(response.data);
	// 弹出对话框
	showInteractiveDialog('完成', '该模板的分享密钥已复制到你的剪切板！可以粘贴并分享给其他用户！该密钥若丢失无法取回，只能重新生成新的分享密钥！该分享密钥在24小时后失效！', '知道了', '取消',
			() => {
				// 什么都不做
			},
			() => {
				// 什么都不做
			});
};

/**
 * 移除房间模板
 * @typedef RoomTemplate 房间模板对象
 * @property {String} id 模板id
 * @property {String} name 模板名称
 * @property {String} masterId 模板创建者id
 *
 * @param {RoomTemplate} template 模板对象
 */
const removeRoomTemplate = (template) => {
	// 判断当前用户是否是模板创建者
	const isMaster = template.masterId === userStore.userData.id;
	showInteractiveDialog(isMaster ? '确认删除' : '确认移除',
			isMaster ? '删除模板后，拥有该模板的用户也将丢失该模板' : '移除模板后，仍然可以通过该模板的分享密钥重新添加',
			'确认', '取消',
			async () => {
				const response = await sendRequest(REQUEST_PREFIX.SESSION_ROOM_TEMPLATE + 'delete/' + template.id, REQUEST_METHOD.DELETE);
				if (!response.success) {
					showMessage(response.message, MESSAGE_TYPE.error);
					return;
				}
				showMessage(response.message, MESSAGE_TYPE.success, 5000);
				// 重新加载模板列表
				await getAllTemplate();
			},
			() => {
				// 什么都不做
			}
	);
};

// 监听：当窗口打开时加载模板
watch(showDialog, () => {
	if (showDialog.value) {
		getAllTemplate();
	}
});

defineExpose({ showDialog });
</script>

<style lang="scss" scoped>
// 导入对话框共用样式
@import "../../../../assets/scss/dialog.scss";

.room-template-dialog {
	.content {
		position: relative;
		margin: 0 auto;
		width: 100%;
		height: 30vh;

		.template-list {
			width: 100%;
			height: 100%;
			overflow-x: hidden;
			overflow-y: scroll;
			border-radius: 8px;
			border: #4f4fff 1px solid;

			.each-template {
				display: flex;
				align-items: center;
				justify-content: space-between;
				height: 5vh;
				border-bottom: #00b917 1px dashed;

				.text {
					width: 55%;
					height: 100%;
					line-height: 5vh;
					padding-left: 10px;
					font-size: 16px;
					text-overflow: ellipsis;
					overflow: hidden;
					white-space: nowrap;
				}

				.button-box {
					display: flex;
					align-items: center;
					justify-content: space-evenly;
					width: 45%;
					height: 100%;
					bottom: 0;
				}
			}
		}
	}
}
</style>