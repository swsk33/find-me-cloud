package com.gitee.swsk33.findmeentity.dataobject;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gitee.swsk33.findmeentity.param.ValidationRules;
import com.gitee.swsk33.findmeentity.serializer.LongToStringSerializer;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 房间模板
 */
@Data
public class RoomTemplate {

	/**
	 * 模板id（用于用户创建或者加入这个id的房间）
	 */
	private String id;

	/**
	 * 模板名字
	 */
	@NotEmpty(groups = ValidationRules.AddData.class, message = "房间模板名称不能为空！")
	private String name;

	/**
	 * 模板的创建者
	 */
	@JsonSerialize(using = LongToStringSerializer.class)
	private long masterId;

	/**
	 * 拥有该模板的用户id
	 */
	@JsonIgnore
	private List<Long> userIdList;

}