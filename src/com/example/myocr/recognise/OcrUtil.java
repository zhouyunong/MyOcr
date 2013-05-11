package com.example.myocr.recognise;

import android.graphics.Bitmap;

public class OcrUtil {
	public static String doOcr(Bitmap bitmap){
		return "my name is Kevin Zhou";
	}
	
	/**
	 * 工具类：读取字符串中间的单词  edit by elvira
	 * @param str
	 * @return
	 */
	public static String readMidWord(String str) {
		int mid = str.length()/2;
		
		int begin = str.lastIndexOf(" ", mid);
		if (begin == -1)
			begin = 0;
		
		int end = str.indexOf(" ", mid);
		if (end == -1)
			end = str.length()-1;
		
		String result = str.substring(begin, end);
		return result;
	}

	
	
}
