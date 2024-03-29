package com.gitee.swsk33.findmeentity.param;

/**
 * WebSocket实时消息类型
 */
public enum MessageType {
	/**
	 * 用户位置变化
	 */
	POSITION_CHANGED,
	/**
	 * 集结点变化
	 */
	RALLY_CHANGED,
	/**
	 * 房间状态改变
	 */
	ROOM_CHANGED,
	/**
	 * 用户聊天消息
	 */
	CHAT,
	/**
	 * 用户加入
	 */
	USER_JOIN,
	/**
	 * 用户退出
	 */
	USER_EXIT,
	/**
	 * 认证消息
	 */
	AUTH,
	/**
	 * 房间模板的加入认证消息
	 */
	TEMPLATE_AUTH,
	/**
	 * 认证成功消息
	 */
	AUTH_SUCCESS,
	/**
	 * 认证失败消息
	 */
	AUTH_FAILED,
	/**
	 * 操作成功
	 */
	SUCCESS,
	/**
	 * 操作失败
	 */
	FAILED
}