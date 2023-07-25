package com.gitee.swsk33.findmeentity.model;

import lombok.Data;

/**
 * 房间模板的分享信息对象
 */
@Data
public class RoomTemplateShare {

	/**
	 * 分享信息的id，也就是分享密钥
	 */
	private String id;

	/**
	 * 分享信息对应的模板id
	 */
	private String templateId;

}