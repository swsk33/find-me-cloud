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
	 * 认证消息
	 */
	AUTH,
	/**
	 * 操作成功
	 */
	SUCCESS,
	/**
	 * 操作失败
	 */
	FAILED
}