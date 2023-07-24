package com.gitee.swsk33.findmeuser.service.impl;

import com.gitee.swsk33.findmeentity.factory.ResultFactory;
import com.gitee.swsk33.findmeentity.model.Result;
import com.gitee.swsk33.findmeuser.service.AvatarService;
import io.github.swsk33.fileliftcore.model.BinaryContent;
import io.github.swsk33.fileliftcore.model.file.UploadFile;
import io.github.swsk33.fileliftcore.model.result.FileResult;
import io.github.swsk33.fileliftcore.service.UploadFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
public class AvatarServiceImpl implements AvatarService {

	@Autowired
	private UploadFileService uploadFileService;

	@Override
	public Result<String> upload(MultipartFile file) {
		FileResult<UploadFile> uploadResult = uploadFileService.upload(file);
		if (!uploadResult.isSuccess()) {
			return ResultFactory.createFailedResult(uploadResult.getMessage());
		}
		return ResultFactory.createSuccessResult("图片上传完成！", uploadResult.getData().getName());
	}

	@Override
	public Result<Void> delete(String id) {
		uploadFileService.delete(id);
		log.warn("删除图片，id：{}", id);
		return ResultFactory.createVoidSuccessResult("删除完成！");
	}

	@Override
	public Result<BinaryContent> get(String id) {
		FileResult<BinaryContent> downloadResult = uploadFileService.downloadFileByMainName(id);
		if (!downloadResult.isSuccess()) {
			return ResultFactory.createFailedResult(downloadResult.getMessage());
		}
		return ResultFactory.createSuccessResult("查找成功！", downloadResult.getData());
	}

}