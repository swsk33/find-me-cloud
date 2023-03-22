package com.gitee.swsk33.findmeentity.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * 自定义Jackson类型序列化-长整型转换成字符串（防止前端精度丢失）
 */
public class LongToStringSerializer extends JsonSerializer<Long> {

	@Override
	public void serialize(Long longData, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
		jsonGenerator.writeString(String.valueOf(longData));
	}

}