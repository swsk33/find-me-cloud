<template>
	<div class="user-marker">
		<img :src="props.avatar" alt="无法显示"/>
	</div>
</template>

<script setup>
const props = defineProps(['avatar']);
</script>

<style lang="scss" scoped>
.user-marker {
	position: absolute;
	left: 30vw;
	top: 30vh;
	height: 36px;
	width: 36px;
	border-radius: 50%;
	border-style: solid;
	border-width: 3px;
	border-color: #1d1dd2;

	img {
		width: 100%;
		height: 100%;
		border-radius: 50%;
	}

	.heading-arrow {
		position: absolute;
		left: 0;
		top: 0;
		box-sizing: border-box;
		width: 28px;
		height: 28px;
		margin: 0;
		padding: 0;
	}
}
</style>