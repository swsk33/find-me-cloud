import { createRouter, createWebHistory } from 'vue-router';

// 登录页
const loginRouter = {
	path: '/login',
	component: () => import('../page-views/login/LoginPage.vue'),
	children: [
		{
			path: '',
			component: () => import('../page-views/login/components/LoginPanel.vue')
		},
		{
			path: 'register',
			component: () => import('../page-views/login/components/RegisterPanel.vue')
		},
		{
			path: 'forget',
			component: () => import('../page-views/login/components/ForgetPanel.vue')
		}
	]
};


const router = createRouter({
	history: createWebHistory(import.meta.env.BASE_URL),
	routes: [loginRouter]
});

export default router;