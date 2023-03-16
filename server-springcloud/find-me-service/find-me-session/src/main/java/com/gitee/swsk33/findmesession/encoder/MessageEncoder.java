package com.gitee.swsk33.findmesession.encoder;

import com.gitee.swsk33.findmeentity.model.Message;
import com.gitee.swsk33.findmeutility.singleton.JacksonMapper;
import jakarta.websocket.Encoder;

/**
 * 实时消息Message类的编码器（序列化）
 */
public class MessageEncoder implements Encoder.Text<Message<?>> {

	@Override
	public String encode(Message<?> message) {
		try {
			return JacksonMapper.getMapper().writeValueAsString(message);
		} catch (Exception e) {
			e.printStackTrace();
			return "{\"type\": \"FAILED\", \"data\": \"消息序列化失败！请联系后端开发者！\"}";
		}
	}

}