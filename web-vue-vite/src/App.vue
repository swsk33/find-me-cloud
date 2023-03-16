<!-- 根组件，仅挂载页面视图 -->
<template>
	<router-view class="page"/>
</template>

<script setup>

</script>

<style lang="scss" scoped>
.page {
	position: absolute;
	left: 0;
	top: 0;
	width: 100vw;
	height: 100vh;
}
</style>