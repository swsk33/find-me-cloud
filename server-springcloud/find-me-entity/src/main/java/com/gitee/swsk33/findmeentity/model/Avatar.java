package com.gitee.swsk33.findmeentity.model;

import com.gitee.swsk33.findmeutility.IDGenerator;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户头像
 */
@Data
@NoArgsConstructor
public class Avatar implements Serializable {

	/**
	 * 头像id（UUID）
	 */
	private String id = IDGenerator.generateUUID();

	/**
	 * 文件扩展名（不带.）
	 */
	private String format;

	/**
	 * 带一参构造器
	 *
	 * @param format 格式
	 */
	public Avatar(String format) {
		this.format = format;
	}

	/**
	 * 重写toString 输出为文件名格式
	 *
	 * @return 文件名形式
	 */
	@Override
	public String toString() {
		return id + "." + format;
	}

}