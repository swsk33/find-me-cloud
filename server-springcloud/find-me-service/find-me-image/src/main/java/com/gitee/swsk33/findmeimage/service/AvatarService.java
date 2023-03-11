package com.gitee.swsk33.findmeimage.service;

import com.gitee.swsk33.findmeentity.model.Avatar;
import com.gitee.swsk33.findmeentity.model.Result;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 头像服务
 */
@Service
public interface AvatarService {

	/**
	 * 上传头像
	 *
	 * @return 结果，包含头像的基本信息
	 */
	Result<Avatar> upload(MultipartFile file) throws IOException;

	/**
	 * 删除头像
	 *
	 * @param filename 头像文件名
	 * @return 结果
	 */
	Result<Void> delete(String filename);

	/**
	 * 获取头像
	 *
	 * @param filename 头像文件名
	 * @return 结果
	 */
	Result<byte[]> get(String filename);

}