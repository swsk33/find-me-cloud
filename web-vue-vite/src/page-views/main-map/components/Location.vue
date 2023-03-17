<template>
	<div class="location">
		<img :src="locationButton" alt="无法显示"/>
		<div class="text">手动定位</div>
	</div>
</template>

<script setup>
import locationButton from '../../../assets/button/location.png';
</script>

<style lang="scss" scoped>
.location {
	display: flex;
	flex-direction: column;
	align-items: center;

	img {
		position: relative;
		max-height: 50px;
	}

	.text {
		position: relative;
		font-size: 12px;
		font-weight: bold;
		top: 2px;
	}
}
</style>