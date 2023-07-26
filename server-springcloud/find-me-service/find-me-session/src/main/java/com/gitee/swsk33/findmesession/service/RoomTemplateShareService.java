package com.gitee.swsk33.findmesession.service;

import com.gitee.swsk33.findmeentity.model.Result;
import org.springframework.stereotype.Service;

/**
 * 房间模板的分享服务
 */
@Service
public interface RoomTemplateShareService {

	/**
	 * 为一个房间模板创建分享信息
	 *
	 * @param templateId 模板id
	 * @return 结果，包含分析信息的id（分享密钥）
	 */
	Result<String> createShare(String templateId);

	/**
	 * 校验分享<br>
	 * 一个已经登录的用户调用该接口时，会根据分享id取出分享内容，得到其中对应的模板id并查得模板对象，然后把这个登录的用户的id加入到对应的模板拥有者列表中，完成用户加入房间模板的逻辑
	 *
	 * @param shareId 分享id（或者叫做分享密钥）
	 * @return 结果
	 */
	Result<Void> validateShare(String shareId);

}