// 存放一些服务的API请求前缀

import { combineWebsocketPath } from '../utils/request';

/**
 * 全部前缀
 */
const PREFIX = '/api/';

/**
 * 用户模块前缀
 */
const USER_PREFIX = PREFIX + 'user/';

/**
 * 会话模块前缀
 */
const SESSION_PREFIX = PREFIX + 'session/';

/**
 * 全部的WebSocket前缀<br>
 * 由于Vite配置了https，因此这里地址也要是wss://开头！否则不会走Vite的代理配置
 */
const WS_PREFIX = combineWebsocketPath('/ws/');

/**
 * 会话模块的WebSocket地址前缀
 */
const SESSION_WS_PREFIX = WS_PREFIX + 'session/';

/**
 * 服务请求前缀
 */
const REQUEST_PREFIX = {
	/**
	 * 用户常规服务
	 */
	USER_COMMON: USER_PREFIX + 'common/',
	/**
	 * 用户头像服务
	 */
	USER_AVATAR: USER_PREFIX + 'avatar/',
	/**
	 * 用户邮件服务
	 */
	USER_EMAIL: USER_PREFIX + 'email/',
	/**
	 * 会话房间服务
	 */
	SESSION_ROOM: SESSION_PREFIX + 'room/',
	/**
	 * 会话房间模板服务
	 */
	SESSION_ROOM_TEMPLATE: SESSION_PREFIX + 'room-template/',
	/**
	 * 会话房间模板分享服务
	 */
	SESSION_ROOM_TEMPLATE_SHARE: SESSION_PREFIX + 'room-template-share/',
	/**
	 * 会话房间的WebSocket实时通讯服务
	 */
	SESSION_ROOM_WS: SESSION_WS_PREFIX + 'room/'
};

export { REQUEST_PREFIX };