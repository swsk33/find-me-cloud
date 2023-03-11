package com.gitee.swsk33.findmeentity.model;

import com.gitee.swsk33.findmeutility.IDGenerator;
import jakarta.websocket.Session;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 会话房间
 */
@Data
public class Room implements Serializable {

	/**
	 * 房间id（UUID）
	 */
	private String id = IDGenerator.generateUUID();

	/**
	 * 房间名
	 */
	private String name;

	/**
	 * 房间密码
	 */
	private String password;

	/**
	 * 集结点
	 */
	private RallyPoint rally;

	/**
	 * 用户会话表
	 */
	private Map<Long, Session> userSession;

}