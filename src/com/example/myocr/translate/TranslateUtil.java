package com.example.myocr.translate;

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
			JSONObject jsonResult = new JSONObject(json);

			result.setErrorCode(jsonResult.getInt("errorCode"));

			if (result.getErrorCode() == 0) {
				if (!jsonResult.isNull("translation")) {
					result.setTranslation(jsonResult
							.getJSONArray("translation").getString(0));
				}
				if (!jsonResult.isNull("basic")) {
					Basic basic = new Basic();
					JSONObject jsonBasic = jsonResult.getJSONObject("basic");
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
						JSONObject jsonObject = webTranArray.getJSONObject(i);
						WebTranslation webTranslation = new WebTranslation();
						webTranslation.setKey(jsonObject.getString("key"));
						JSONArray jsonArray = jsonObject.getJSONArray("value");
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

		}

		return result;
	}

	public static TranslateResult translateJson(String forTranslate)
			throws Exception {
		return translateQuery("json", forTranslate);
	}

}
