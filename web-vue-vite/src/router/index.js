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
	component: () => import('../page-views/main-map/Map.vue')
};


const router = createRouter({
	history: createWebHistory(import.meta.env.BASE_URL),
	routes: [loginRouter, mainMapRouter]
});

export default router;