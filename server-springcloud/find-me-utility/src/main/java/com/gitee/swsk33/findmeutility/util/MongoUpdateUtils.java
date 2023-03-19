package com.gitee.swsk33.findmeutility.util;

import cn.hutool.core.util.StrUtil;
import org.springframework.data.mongodb.core.query.Update;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * MongoDB更新操作实用类
 */
public class MongoUpdateUtils {

	/**
	 * 传入原数据库的完整对象和用于更新信息的提交的对象，将两者相同的部分从新对象中删除，以便于更新数据库
	 *
	 * @param origin 原完整对象
	 * @param input  新的用于更新的提交对象
	 * @param <T>    对象类型
	 */
	public static <T> void removeSame(T origin, T input) {
		Stream<Field> fields = Arrays.stream(origin.getClass().getDeclaredFields());
		fields.forEach(field -> {
			field.setAccessible(true);
			// 跳过id字段
			if (field.getName().equals("id")) {
				return;
			}
			try {
				if (field.get(origin) == null) {
					return;
				}
				// 从新的对象中移除一样的部分
				if (field.getType() == String.class) {
					if (!StrUtil.isEmpty((String) field.get(input)) && field.get(origin).equals(field.get(input))) {
						field.set(input, null);
					}
				} else {
					if (field.get(input) != null && field.get(origin).equals(field.get(input))) {
						field.set(input, null);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * 检查一个对象是否除了id字段之外，其余字段全部为空（用空的对象去更新MongoDB会导致数据被清除）
	 *
	 * @param object 对象
	 * @param <T>    对象类型
	 * @return 是否为空
	 */
	public static <T> boolean isObjectContentEmpty(T object) {
		Field[] fields = object.getClass().getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			// 跳过id字段
			if (field.getName().equals("id")) {
				continue;
			}
			try {
				boolean stringNotEmpty = field.getType() == String.class && !StrUtil.isEmpty((String) field.get(object));
				boolean objectNotEmpty = field.getType() != String.class && field.get(object) != null;
				if (stringNotEmpty || objectNotEmpty) {
					return false;
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

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