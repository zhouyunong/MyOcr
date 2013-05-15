package com.myocr.framework.translate;

public class YouDaoConfig {
	private static final String keyFrom = "zhouyunongBlog";
	private static final String key = "2039183788";
	private static final String version = "1.1";

	public static final String queryString = "http://fanyi.youdao.com/openapi.do?keyfrom="
			+ keyFrom
			+ "&key="
			+ key
			+ "&type=data&doctype=%s&version="
			+ version + "&q=%s";

}
