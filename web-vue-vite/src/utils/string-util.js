// 字符串实用方法

/**
 * 移除字符串的末尾斜杠'/'
 * @param {String} origin 原字符串
 * @return {String} 移除了末尾斜杠的字符串
 */
function removeEndSlash(origin) {
	while (origin.endsWith('/')) {
		origin = origin.substring(0, origin.lastIndexOf('/'));
	}
	return origin;
}

/**
 * 如果字符串不以斜杠'/'开头，则在开头添加一个'/'
 * @param {String} origin 原字符串
 * @return {String} 添加了斜杠的字符串
 */
function addStartSlash(origin) {
	if (!origin.startsWith('/')) {
		origin = '/' + origin;
	}
	return origin;
}

/**
 * 传入完整URL，解析其中的域名或者地址部分
 * @param {String} url 完整URL，例如'https://example.com/'
 * @return {String} 得到的域名，例如'example.com'
 */
function parseHost(url) {
	// 去除前缀
	const httpPrefix = 'http://';
	const httpsPrefix = 'https://';
	if (url.startsWith(httpPrefix)) {
		url = url.substring(httpPrefix.length);
	} else if (url.startsWith(httpsPrefix)) {
		url = url.substring(httpsPrefix.length);
	}
	// 去除尾部路径
	if (url.indexOf('/') !== -1) {
		url = url.substring(0, url.indexOf('/'));
	}
	return url;
}

/**
 * 判断字符串是否为空，当字符串变量为undefined、null或者空字符串''的时候，都算作空
 * @param {String} origin 待判断字符串
 * @returns {Boolean} 字符串是否为空，为空返回true
 */
function isEmpty(origin) {
	return origin == null || origin.length === 0;
}

export { removeEndSlash, addStartSlash, parseHost, isEmpty };