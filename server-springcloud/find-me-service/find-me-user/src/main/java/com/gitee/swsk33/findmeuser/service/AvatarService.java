package com.gitee.swsk33.findmeuser.service;

import com.gitee.swsk33.findmeentity.model.Result;
import io.github.swsk33.fileliftcore.model.BinaryContent;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 头像服务
 */
@Service
public interface AvatarService {

	/**
	 * 上传头像
	 *
	 * @return 结果，包含头像的文件id
	 */
	Result<String> upload(MultipartFile file);

	/**
	 * 删除头像
	 *
	 * @param id 头像文件id
	 * @return 结果
	 */
	Result<Void> delete(String id);

	/**
	 * 获取头像
	 *
	 * @param id 头像文件id
	 * @return 结果
	 */
	Result<BinaryContent> get(String id);

}