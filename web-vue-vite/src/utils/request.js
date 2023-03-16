import axios from 'axios';

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
 * @param {String} method 请求类型
 * @param {Object} data 请求体，对于GET或者DELETE请求没有请求体的，就不传该参数
 * @returns 响应体
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
	}
}

// 导出我们封装的参数枚举和函数
export {
	REQUEST_METHOD,
	sendRequest
};