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
	 * @param id          头像文件id
	 */
	void add(InputStream inputStream, String id);

	/**
	 * 删除头像
	 *
	 * @param id 头像文件id
	 */
	void delete(String id);

	/**
	 * 获取头像文件
	 *
	 * @param id 头像文件id
	 * @return 头像文件数据
	 */
	byte[] get(String id) throws IOException;

}