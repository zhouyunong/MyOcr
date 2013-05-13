package com.example.myocr.recognise;

import android.R.integer;
import android.graphics.Bitmap;
import android.util.Log;

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

	/***
	 * 传出灰度化后的图片
	 * @param width  输出图片的宽度
	 * @param height 输出图片的高度
	 * @param top rec相对于上的位置，相对于surfaceView
	 * @param left rec相对于左的位置，相对于surfaceView
	 * @param dataWidth 相机分辨率的宽度
	 * @param yuv 传入的二进制数组
	 * @return 灰度化后的图片
	 */
	 public Bitmap renderCroppedGreyscaleBitmap(int width , int height ,int top , int  left ,int dataWidth, byte[] yuv) {
		   
		    int[] pixels = new int[width * height];
		   
		    int inputOffset = top * dataWidth + left;

		    for (int y = 0; y < height; y++) {
		      int outputOffset = y * width;
		      for (int x = 0; x < width; x++) {
		       int grey = yuv[inputOffset + x] & 0xff;
		        pixels[outputOffset + x] = 0xFF000000 | (grey * 0x00010101);
		 
		      }
		      inputOffset += dataWidth;
		    }

		    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		    bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		    return bitmap;
		  }
	
}
