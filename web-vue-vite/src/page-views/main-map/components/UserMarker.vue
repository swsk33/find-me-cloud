<template>
	<div class="user-marker">
		<div class="marker-base" :style="{transform: 'rotate(' + (360 - props.headingValue) + 'deg)'}">
			<div class="circle" :style="circleStyle">
			</div>
			<div class="heading" :style="headingStyle" v-if="props.headingValue != null"></div>
		</div>
		<img class="user-avatar" :src="props.avatar" alt="无法显示"/>
	</div>
</template>

<script setup>
import { reactive } from 'vue';

const props = defineProps(['avatar', 'color', 'headingValue']);

// 自定义css部分绑定
const circleStyle = reactive({
	backgroundColor: props.color,
	boxShadow: props.color + ' 0 0 6px'
});

const headingStyle = reactive({
	borderBottomColor: props.color
});
</script>

<style lang="scss" scoped>
.user-marker {
	position: absolute;
	height: 48px;
	width: 48px;

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

	.user-avatar {
		position: absolute;
		width: 24px;
		left: 12px;
		top: 12px;
		border-radius: 50%;
	}
}
</style>