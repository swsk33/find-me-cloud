import { ElMessage, ElNotification } from 'element-plus';

/**
 * 消息类型
 */
const MESSAGE_TYPE = {
	/**
	 * 操作成功
	 */
	success: 'success',
	/**
	 * 错误
	 */
	error: 'error',
	/**
	 * 警告
	 */
	warning: 'warning',
	/**
	 * 提示
	 */
	info: 'info'
};

/**
 * 显示一个顶部消息
 * @param {String} message 消息内容
 * @param {String} type 消息类型
 * @param  {Number} duration 持续时间（毫秒）
 */
function showMessage(message, type = MESSAGE_TYPE.info, duration = 3000) {
	ElMessage({
		message: message,
		type: type,
		duration: duration
	});
}

/**
 * 显示一个通知消息
 * @param {String} title 标题
 * @param {String} message 内容
 * @param {String} type 消息类型
 * @param {Number} duration 持续时间（毫秒）
 */
function showNotification(title, message, type = MESSAGE_TYPE.success, duration = 3000) {
	ElNotification({
		title: title,
		message: message,
		type: type,
		duration: duration
	});
}

export { MESSAGE_TYPE, showMessage, showNotification };