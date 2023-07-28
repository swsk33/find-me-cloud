import { createRouter, createWebHistory } from 'vue-router';

// 登录页
const loginRouter = {
	path: '/login',
	component: () => import('../page-views/login/LoginPage.vue'),
	children: [
		{
			path: '',
			component: () => import('../page-views/login/views/LoginPanel.vue')
		},
		{
			path: 'register',
			component: () => import('../page-views/login/views/RegisterPanel.vue')
		},
		{
			path: 'forget',
			component: () => import('../page-views/login/views/ForgetPanel.vue')
		}
	]
};

// 地图主页
const mainMapRouter = {
	path: '/',
	// 处理一键加入房间链接
	alias: '/join-room/room-id/:roomId/room-password/:roomPassword',
	component: () => import('../page-views/main-map/Map.vue')
};

// 其余未知页面重定向回主页
const unknownRouter = {
	path: '/:pathMatch(.*)*',
	redirect: '/'
};

const router = createRouter({
	history: createWebHistory(import.meta.env.BASE_URL),
	routes: [loginRouter, mainMapRouter, unknownRouter]
});

export default router;