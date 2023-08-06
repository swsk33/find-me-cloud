import axios from 'axios';
import { addStartSlash, isEmpty, parseHost, removeEndSlash } from './string-util';

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
 * 从环境变量读取后端请求地址（前部分），需要是http://xxx.com的形式
 * @type {String}
 */
const requestURLPrefix = import.meta.env.VITE_HOST;

/**
 * 处理请求路径，若地址未指定具体域名或者地址，且为生产环境，则会读取环境变量VITE_HOST作为域名或者地址拼接在前面
 * @param {String} path 请求路径，例如/api/user/get/xxx
 * @return {String} 拼接后路径，若为开发环境，则直接返回path变量
 */
function combinePath(path) {
	// 如果是开发环境或者未指定VITE_HOST，则不进行任何处理
	if (import.meta.env.DEV || isEmpty(requestURLPrefix)) {
		return path;
	}
	// 否则进行拼接
	return removeEndSlash(requestURLPrefix) + addStartSlash(path);
}

/**
 * 传入WebSocket的请求相对路径，拼接为绝对路径
 * @param {String} path 传入WebSocket连接相对路径，例如'/ws/xxx'，以'/'开头
 * @returns {String} WebSocket请求绝对路径，例如wss://localhost:8080/ws/xxx
 */
function combineWebsocketPath(path) {
	// 如果是开发环境或者未指定VITE_HOST，则直接使用location.host拼接路径
	if (import.meta.env.DEV || isEmpty(requestURLPrefix)) {
		return 'wss://' + location.host + path;
	}
	// 否则进行拼接
	return 'wss://' + parseHost(requestURLPrefix) + addStartSlash(path);
}

/**
 * 发送请求函数
 * @param {String} path 请求路径，例如/api/user/get/xxx
 * @param {REQUEST_METHOD} method 请求类型
 * @param {Object} data 请求体，对于GET或者DELETE请求没有请求体的，就不传该参数
 * @returns {Result} 响应体
 */
async function sendRequest(path, method, data = undefined) {
	// 请求参数（传入axios方法中）
	let requestParam = {
		url: combinePath(path),
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
 * @param {String} path 请求路径，例如/api/file/upload
 * @param {REQUEST_METHOD} method 请求类型
 * @param {FormData} formData 请求体，表单对象
 * @returns {Result} 响应体
 */
async function uploadFile(path, method, formData) {
	const requestParam = {
		url: combinePath(path),
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
	uploadFile,
	combinePath,
	combineWebsocketPath
};