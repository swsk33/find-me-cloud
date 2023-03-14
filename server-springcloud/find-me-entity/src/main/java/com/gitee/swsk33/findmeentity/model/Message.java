package com.gitee.swsk33.findmeentity.model;

import com.gitee.swsk33.findmeentity.param.MessageType;
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
	 * 消息内容
	 */
	private T data;

}