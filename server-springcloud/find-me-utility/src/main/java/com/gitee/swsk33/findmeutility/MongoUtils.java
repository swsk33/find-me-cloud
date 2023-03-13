package com.gitee.swsk33.findmeutility;

import cn.hutool.core.util.StrUtil;
import org.springframework.data.mongodb.core.query.Update;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * MongoDB操作实用类
 */
public class MongoUtils {

	/**
	 * 根据传入的修改后的对象生成用于MongoDB update操作的对象
	 *
	 * @param object 要用于更新的对象
	 * @param <T>    对象类型
	 * @return Update操作对象
	 */
	public static <T> Update generateUpdate(T object) {
		Update update = new Update();
		Stream<Field> fields = Arrays.stream(object.getClass().getDeclaredFields());
		fields.forEach(field -> {
			field.setAccessible(true);
			// 跳过id字段
			if (field.getName().equals("id")) {
				return;
			}
			try {
				// 忽略空字段
				boolean stringEmpty = field.getType() == String.class && StrUtil.isEmpty((String) field.get(object));
				boolean objectEmpty = field.getType() != String.class && field.get(object) == null;
				if (stringEmpty || objectEmpty) {
					return;
				}
				// 设定为更新条件
				update.set(field.getName(), field.get(object));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		return update;
	}

}