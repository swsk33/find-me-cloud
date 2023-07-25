package com.gitee.swsk33.findmeentity.dataobject;

import java.util.List;

/**
 * 房间模板
 */
public class RoomTemplate {

	/**
	 * 模板id（用于用户创建或者加入这个id的房间）
	 */
	private String id;

	/**
	 * 模板名字
	 */
	private String name;

	/**
	 * 拥有该模板的用户id
	 */
	private List<Long> userIdList;

}