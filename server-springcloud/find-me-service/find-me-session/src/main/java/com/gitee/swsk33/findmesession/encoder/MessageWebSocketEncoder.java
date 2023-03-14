package com.gitee.swsk33.findmesession.encoder;

import com.alibaba.fastjson2.JSON;
import com.gitee.swsk33.findmeentity.model.Message;
import jakarta.websocket.Encoder;

/**
 * 实时消息Message类的编码器（序列化）
 */
public class MessageWebSocketEncoder implements Encoder.Text<Message<?>> {

	@Override
	public String encode(Message<?> message) {
		return JSON.toJSONString(message);
	}

}