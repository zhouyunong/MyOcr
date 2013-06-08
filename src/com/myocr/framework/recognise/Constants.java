package com.myocr.framework.recognise;

import java.io.File;

import android.os.Environment;
import android.util.Log;

public class Constants {
	public static final String  IMAGE_PATH = "mnt/sdcard/test.jpg";
	public static final String TESSDATA_NAME_EN = "eng.traineddata";
	//周
	public static  String getSDPath(){ 
	       File sdDir = null; 
	       boolean sdCardExist = Environment.getExternalStorageState()   
	                           .equals(Environment.MEDIA_MOUNTED);   //判断sd卡是否存在 
	       if   (sdCardExist)   
	       {                               
	         sdDir = Environment.getExternalStorageDirectory();//获取跟目录 
	      }   
	       return sdDir.toString(); 
	       
	}
	
	public static String  getTessDataPath(){
		return getTessPath()+"/tessdata";
	}
	
	public static String getImagePath(){
		Log.i("zhou", getSDPath()+"/test.jpg");
		return getSDPath()+"/test.jpg";
	}
	
	public static String getTessPath(){
		Log.i("zhou", getSDPath()+"/tesseract");
		return getSDPath()+"/tesseract";
		
	}
	
	
}
