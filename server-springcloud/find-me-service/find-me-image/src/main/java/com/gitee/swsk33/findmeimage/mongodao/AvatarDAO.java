package com.gitee.swsk33.findmeimage.mongodao;

import com.gitee.swsk33.findmeentity.annotation.MongoDAO;

import java.io.IOException;
import java.io.InputStream;

@MongoDAO
public interface AvatarDAO {

	/**
	 * 存入头像文件至数库
	 *
	 * @param inputStream 头像文件输入流
	 * @param name        文件名（带扩展名）
	 */
	void add(InputStream inputStream, String name);

	/**
	 * 删除头像
	 *
	 * @param filename 头像文件名（通常是id+.+格式）
	 */
	void delete(String filename);

	/**
	 * 获取头像文件
	 *
	 * @param filename 头像文件名
	 * @return 头像文件数据
	 */
	byte[] get(String filename) throws IOException;

}