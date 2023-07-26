package com.gitee.swsk33.findmesession.cache;

import com.gitee.swsk33.findmeentity.annotation.RedisCache;
import com.gitee.swsk33.findmeentity.model.RoomTemplateShare;

/**
 * 房间模板分享信息的数据库缓存操作对象
 */
@RedisCache
public interface RoomTemplateShareCache {

	/**
	 * 添加一个模板分享信息，在24小时后过期
	 *
	 * @param roomTemplateShare 模板分享信息
	 */
	void addShare(RoomTemplateShare roomTemplateShare);

	/**
	 * 查找分享信息
	 *
	 * @param id 模板分享id（分享密钥）
	 * @return 分享信息，不存在返回null
	 */
	RoomTemplateShare getShare(String id);

}