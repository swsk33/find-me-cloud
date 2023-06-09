package com.gitee.swsk33.findmeentity.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gitee.swsk33.findmeentity.param.MessageType;
import com.gitee.swsk33.findmeentity.serializer.LongToStringSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 * 用于WebSocket的实时通讯消息对象
 */
@Data
public class Message<T> implements Serializable {

	/**
	 * 消息类型
	 */
	private MessageType type;

	/**
	 * 发送者用户id（为0时表示系统消息）
	 */
	@JsonSerialize(using = LongToStringSerializer.class)
	private long senderId;

	/**
	 * 消息内容
	 */
	private T data;

}