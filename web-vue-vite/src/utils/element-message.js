import { ElMessage, ElMessageBox, ElNotification } from 'element-plus';

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
 * @param {MESSAGE_TYPE} type 消息类型
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
 * @param {MESSAGE_TYPE} type 消息类型
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

/**
 * 显示交互式对话框
 * @param {String} title 对话框标题
 * @param {String} message 对话框内容
 * @param {String} okText 确认按钮文字
 * @param {String} cancelText 取消按钮文字
 * @param {Function} ok 点击确认按钮后执行的回调函数
 * @param {Function} cancel 点击取消按钮后执行的回调函数
 */
function showInteractiveDialog(title, message, okText, cancelText, ok, cancel) {
	ElMessageBox.confirm(message, title, {
		confirmButtonText: okText,
		cancelButtonText: cancelText,
		type: 'warning',
		center: true,
		showClose: false,
		closeOnClickModal: false
	}).then(ok).catch(cancel);
}

export { MESSAGE_TYPE, showMessage, showNotification, showInteractiveDialog };