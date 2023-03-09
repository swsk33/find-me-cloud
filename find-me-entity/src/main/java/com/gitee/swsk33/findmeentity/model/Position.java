package com.gitee.swsk33.findmeentity.model;


import com.gitee.swsk33.findmeentity.dataobject.User;
import lombok.Data;

import java.io.Serializable;

/**
 * 实时位置类
 */
@Data
public class Position implements Serializable {

	/**
	 * 该位置信息关联的用户
	 */
	private User user;

	/**
	 * 经度
	 */
	private double longitude;

	/**
	 * 纬度
	 */
	private double latitude;

	/**
	 * 方向
	 */
	private double orientation;

	/**
	 * 海拔（米）
	 */
	private double elevation;

}