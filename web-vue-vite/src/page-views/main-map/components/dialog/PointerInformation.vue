<!-- 用户指针对话框 -->
<template>
	<div class="pointer-information-dialog">
		<el-dialog class="dialog" width="80%" top="32vh" :center="true" :show-close="false" v-if="userData != null" v-model="showDialog" destroy-on-close>
			<!-- 头部 -->
			<template #header>
				<div class="header">
					<img class="avatar" :style="{borderColor: pointerData.color}" :src="userStore.getUserAvatarURL(userData)" alt="无法显示"/>
					<div class="text">{{ userData.nickname + (props.userId === 0 ? '(我)' : '') }}</div>
				</div>
			</template>
			<div class="content">
				<ul class="info">
					<li>经度：{{ pointerData.position.longitude }}</li>
					<li>纬度：{{ pointerData.position.latitude }}</li>
					<li>海拔：{{ pointerData.position.elevation == null ? '该用户海拔信息不可用' : pointerData.position.elevation }}</li>
					<li>方向：{{ getHeading }}</li>
					<li v-if="props.userId !== 0">距离我：{{ mapStore.getDistance(locationStore.position, pointerData.position) }}米</li>
				</ul>
			</div>
			<template #footer>
				<div class="button-box">
					<el-button class="close" type="success" plain @click="showDialog = false">知道了</el-button>
				</div>
			</template>
		</el-dialog>
	</div>
</template>

<script setup>
import { computed, ref } from 'vue';
import { useMapStore } from '../../../../stores/map';
import { useLocationStore } from '../../../../stores/location';
import { useUserStore } from '../../../../stores/user';

const mapStore = useMapStore();
const locationStore = useLocationStore();
const userStore = useUserStore();

// 是否显示对话框
const showDialog = ref(false);

// 分别传入指针对象数据、指针对应的用户信息以及指针对应的用户id
const props = defineProps(['pointerData', 'userData', 'userId']);

// 导出变量
defineExpose({ showDialog });

// 计算属性：获取方向
const getHeading = computed(() => {
	const heading = props.pointerData.position.orientation;
	if (heading == null) {
		return '该用户陀螺仪信息不可用';
	}
	if (heading > 345 || heading <= 15) {
		return '北';
	}
	if (heading > 15 && heading <= 75) {
		return '西北';
	}
	if (heading > 75 && heading <= 105) {
		return '西';
	}
	if (heading > 105 && heading <= 165) {
		return '西南';
	}
	if (heading > 165 && heading <= 195) {
		return '南';
	}
	if (heading > 195 && heading <= 255) {
		return '东南';
	}
	if (heading > 255 && heading <= 285) {
		return '东';
	}
	if (heading > 285 && heading <= 345) {
		return '东北';
	}
});
</script>

<style lang="scss" scoped>
// 导入对话框共用样式
@import "../../../../assets/scss/dialog.scss";

.pointer-information-dialog {
	.dialog {
		.header {
			position: relative;
			display: flex;
			justify-content: center;
			align-items: center;

			.avatar {
				height: 32px;
				border-radius: 50%;
				border-style: solid;
				border-width: 2px;
			}

			.text {
				position: relative;
				margin-left: 3%;
				white-space: nowrap;
				overflow: hidden;
				text-overflow: ellipsis;
			}
		}

		.content {
			position: relative;
		}

		.button-box {
			position: relative;
			display: flex;
			justify-content: center;
			align-items: center;
		}
	}
}
</style>