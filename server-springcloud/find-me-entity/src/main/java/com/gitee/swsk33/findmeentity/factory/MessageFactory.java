package com.gitee.swsk33.findmeentity.factory;

import com.gitee.swsk33.findmeentity.model.Message;
import com.gitee.swsk33.findmeentity.param.MessageType;

/**
 * 实时消息工厂
 */
public class MessageFactory {

	/**
	 * 生成一个实时消息对象
	 *
	 * @param type 消息类型
	 * @param data 数据
	 * @param <T>  消息内容类型
	 * @return 消息对象
	 */
	public static <T> Message<T> createMessage(MessageType type, T data) {
		Message<T> message = new Message<>();
		message.setType(type);
		message.setSenderId(0);
		message.setData(data);
		return message;
	}

	/**
	 * 生成一个实时消息对象，并指定产生消息的用户
	 *
	 * @param type     消息类型
	 * @param senderId 产生消息的用户id
	 * @param data     数据
	 * @param <T>      消息内容类型
	 * @return 消息对象
	 */
	public static <T> Message<T> createMessage(MessageType type, long senderId, T data) {
		Message<T> message = new Message<>();
		message.setType(type);
		message.setSenderId(senderId);
		message.setData(data);
		return message;
	}

}