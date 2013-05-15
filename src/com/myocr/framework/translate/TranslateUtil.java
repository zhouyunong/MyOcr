package com.myocr.framework.translate;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

public class TranslateUtil {
	private static TranslateResult translateQuery(String docType,
			String forTanslate) throws Exception {
		TranslateResult result = new TranslateResult();

		// 需要先将汉字单独转换为UTF-8的编码，然后再与URL字符串拼装，否则URL汉字部分为乱码
		String urlStrEncode = null;
		try {
			urlStrEncode = URLEncoder.encode(forTanslate, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		String urlPath = String.format(YouDaoConfig.queryString, docType,
				urlStrEncode);
		result.setDocType("json");
		result.setQueryString(forTanslate);
		Log.i("zhou", urlPath);
		URL url = new URL(urlPath);

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setConnectTimeout(4 * 1000);

		if (connection.getResponseCode() == 200) {
			InputStream is = connection.getInputStream();
			byte[] data = StreamUtil.readInputStream(is);
			String json = new String(data);
			Log.i("zhou", json);

			try {

				JSONObject jsonResult = new JSONObject(json);

				result.setErrorCode(jsonResult.getInt("errorCode"));

				if (result.getErrorCode() == 0) {
					if (!jsonResult.isNull("translation")) {
						result.setTranslation(jsonResult.getJSONArray(
								"translation").getString(0));
					}
					if (!jsonResult.isNull("basic")) {
						Basic basic = new Basic();
						JSONObject jsonBasic = jsonResult
								.getJSONObject("basic");
						if (!jsonBasic.isNull("explains")) {
							JSONArray explainArray = jsonBasic
									.getJSONArray("explains");
							ArrayList<String> explains = new ArrayList<String>();
							for (int i = 0; i < explainArray.length(); i++) {
								explains.add(explainArray.getString(i));

								basic.setExplains(explains);
							}
						}
						if (!jsonBasic.isNull("phonetic")) {
							basic.setPhonetic(jsonBasic.getString("phonetic"));
						}

						result.setBasicExplain(basic);
					}

					if (!jsonResult.isNull("web")) {
						ArrayList<WebTranslation> webTranslations = new ArrayList<WebTranslation>();

						JSONArray webTranArray = jsonResult.getJSONArray("web");

						for (int i = 0; i < webTranArray.length(); i++) {
							JSONObject jsonObject = webTranArray
									.getJSONObject(i);
							WebTranslation webTranslation = new WebTranslation();
							webTranslation.setKey(jsonObject.getString("key"));
							JSONArray jsonArray = jsonObject
									.getJSONArray("value");
							ArrayList<String> webExplainList = new ArrayList<String>();
							for (int j = 0; j < jsonArray.length(); j++) {
								webExplainList.add(jsonArray.getString(j));
							}
							webTranslation.setValues(webExplainList);
							webTranslations.add(webTranslation);
						}

						result.setWebTranslation(webTranslations);
					}

				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

		return result;
	}

	public static TranslateResult translateJson(String forTranslate)
			throws Exception {
		return translateQuery("json", forTranslate);
	}

	public static String translateParagraph(String paragraph) throws Exception {
		paragraph.replaceAll("\n", " ");
		Log.i("paragraph", paragraph);
		String[] sentences = paragraph.split("\\.", 10);
		StringBuilder translatedSentences = new StringBuilder();
		for (int i = 0; i < sentences.length; i++) {
			String stringResult = translateJson(sentences[i]).getTranslation();
			if (stringResult != null) {
				Log.i("stringResult", stringResult + " ");
				translatedSentences.append(stringResult + "。");
			}

		}
		return translatedSentences.toString();
	}

	
	
	/**
	 * 工具类：读取字符串中间的单词  edit by elvira
	 * @param str
	 * @return
	 */
	public static String readMidWord(String str) {
		if (str == null || str.equals(""))
			return null;

		Log.i("readMidWord", "readMidWord:" + str);

		try {
			//把一些特殊字符都改为空格
			str = str.replaceAll("[,.-]", " ");
			int mid = str.length() / 2;
			
			//处理中间字符为空格的情况
			while (str.charAt(mid) == ' ')
				mid--;
			
			int begin = str.lastIndexOf(" ", mid);
			if (begin == -1)
				begin = 0;

			int end = str.indexOf(" ", mid);
			if (end == -1)
				end = str.length() - 1;

			String result = str.substring(begin, end);
			return result;
		} catch (Exception e) {
			Log.e("readMidWord", e.getMessage());
		}
		return null;

	}
	
	
	
}
