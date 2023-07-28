import axios from 'axios';

/**
 *
 * @typedef Result 响应结果
 * @property {Boolean} success 是否成功
 * @property {String} message 消息
 * @property {Object} data 响应结果内容
 */

/**
 * 请求类型
 */
const REQUEST_METHOD = {
	GET: 'GET',
	POST: 'POST',
	PUT: 'PUT',
	PATCH: 'PATCH',
	DELETE: 'DELETE'
};

/**
 * 发送请求函数
 * @param {String} url 请求地址
 * @param {REQUEST_METHOD} method 请求类型
 * @param {Object} data 请求体，对于GET或者DELETE请求没有请求体的，就不传该参数
 * @returns {Result} 响应体
 */
async function sendRequest(url, method, data = undefined) {
	// 请求参数（传入axios方法中）
	let requestParam = {
		url: url,
		method: method,
		headers: {
			'content-type': 'application/json'
		}
	};
	if (data !== undefined) {
		requestParam.data = data;
	}
	// 发送请求
	try {
		const response = await axios(requestParam);
		return response.data;
	} catch (error) {
		console.log(error.response.data);
		return { success: false, message: '请求发生错误！' };
	}
}

/**
 * 上传文件
 * @param {String} url 请求地址
 * @param {REQUEST_METHOD} method 请求类型
 * @param {FormData} formData 请求体，表单对象
 * @returns {Result} 响应体
 */
async function uploadFile(url, method, formData) {
	const requestParam = {
		url: url,
		method: method,
		headers: {
			'content-type': 'multipart/form-data'
		},
		data: formData
	};
	// 发送请求
	try {
		const response = await axios(requestParam);
		return response.data;
	} catch (error) {
		console.log(error.response.data);
		return { success: false, message: '上传文件发生错误！' };
	}
}

// 导出我们封装的参数枚举和函数
export {
	REQUEST_METHOD,
	sendRequest,
	uploadFile
};