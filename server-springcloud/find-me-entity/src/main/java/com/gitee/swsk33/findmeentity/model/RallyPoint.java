package com.gitee.swsk33.findmeentity.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 集结点
 */
@Data
public class RallyPoint implements Serializable {

	/**
	 * 经度
	 */
	private double longitude;

	/**
	 * 纬度
	 */
	private double latitude;

}