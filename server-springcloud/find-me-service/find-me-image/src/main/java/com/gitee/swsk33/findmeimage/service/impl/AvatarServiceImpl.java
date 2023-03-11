package com.gitee.swsk33.findmeimage.service.impl;

import com.gitee.swsk33.findmeentity.factory.ResultFactory;
import com.gitee.swsk33.findmeentity.model.Avatar;
import com.gitee.swsk33.findmeentity.model.Result;
import com.gitee.swsk33.findmeimage.mongodao.AvatarDAO;
import com.gitee.swsk33.findmeimage.service.AvatarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class AvatarServiceImpl implements AvatarService {

	@Autowired
	private AvatarDAO avatarDAO;

	@Override
	public Result<Avatar> upload(MultipartFile file) throws IOException {
		if (file == null) {
			return ResultFactory.createFailedResult("请上传图片！");
		}
		if (file.getSize() > (long) 5 * 1024 * 1024) {
			return ResultFactory.createFailedResult("请上传图片！");
		}
		// 判断文件格式
		String fileFormat = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
		// 允许的格式
		String[] allowFormats = {"jpg", "jpeg", "bmp", "png", "gif"};
		boolean contain = false;
		for (String eachFormat : allowFormats) {
			if (eachFormat.equals(fileFormat)) {
				contain = true;
				break;
			}
		}
		if (!contain) {
			return ResultFactory.createFailedResult("图片格式必须是" + String.join(", ", allowFormats) + "格式！");
		}
		// 存入数据库
		Avatar avatar = new Avatar(fileFormat);
		avatarDAO.add(file.getInputStream(), avatar.toString());
		return ResultFactory.createSuccessResult("图片上传完成！", avatar);
	}

	@Override
	public Result<Void> delete(String filename) {
		avatarDAO.delete(filename);
		return ResultFactory.createVoidSuccessResult("删除完成！");
	}

	@Override
	public Result<byte[]> get(String filename) {
		byte[] data;
		try {
			data = avatarDAO.get(filename);
		} catch (IOException e) {
			return ResultFactory.createFailedResult("图片不存在！");
		}
		return ResultFactory.createSuccessResult("查找成功！", data);
	}

}