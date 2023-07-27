package com.gitee.swsk33.findmesession.service.impl;

import com.gitee.swsk33.findmeentity.dataobject.RoomTemplate;
import com.gitee.swsk33.findmeentity.dataobject.User;
import com.gitee.swsk33.findmeentity.factory.ResultFactory;
import com.gitee.swsk33.findmeentity.model.Result;
import com.gitee.swsk33.findmeentity.model.RoomTemplateShare;
import com.gitee.swsk33.findmefeign.feignclient.UserClient;
import com.gitee.swsk33.findmesession.cache.RoomTemplateShareCache;
import com.gitee.swsk33.findmesession.mongodao.RoomTemplateDAO;
import com.gitee.swsk33.findmesession.service.RoomTemplateShareService;
import com.gitee.swsk33.findmeutility.util.IDGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RoomTemplateShareServiceImpl implements RoomTemplateShareService {

	@Autowired
	private RoomTemplateDAO roomTemplateDAO;

	@Autowired
	private RoomTemplateShareCache roomTemplateShareCache;

	@Autowired
	private UserClient userClient;

	@Override
	public Result<String> createShare(String templateId) {
		// 创建分享对象
		RoomTemplateShare share = new RoomTemplateShare();
		share.setId(IDGenerator.generateUUID());
		share.setTemplateId(templateId);
		// 保存
		roomTemplateShareCache.addShare(share);
		return ResultFactory.createSuccessResult("已生成分享密钥！", share.getId());
	}

	@Override
	public Result<Void> validateShare(String shareId) {
		// 从session中取得用户
		Result<User> userResult = userClient.isLogin();
		if (!userResult.isSuccess()) {
			return ResultFactory.createFailedResult(userResult.getMessage());
		}
		// 查找分享信息
		RoomTemplateShare getShare = roomTemplateShareCache.getShare(shareId);
		if (getShare == null) {
			return ResultFactory.createFailedResult("该分享密钥不存在或者已过期！");
		}
		// 从中取得对应的房间模板
		RoomTemplate getTemplate = roomTemplateDAO.getById(getShare.getTemplateId());
		if (getTemplate == null) {
			return ResultFactory.createFailedResult("该分享对应的房间模板不存在！");
		}
		// 将用户加入模板的拥有者列表
		long userId = userResult.getData().getId();
		// 如果用户已存在于模板拥有者列表中，则直接返回
		if (getTemplate.getUserIdList().contains(userId)) {
			return ResultFactory.createFailedResult("用户已添加当前模板！");
		}
		roomTemplateDAO.addUserToTemplate(getTemplate.getId(), userId);
		return ResultFactory.createVoidSuccessResult("添加房间模板成功！");
	}

}