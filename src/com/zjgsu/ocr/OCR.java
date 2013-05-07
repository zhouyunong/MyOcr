package com.zjgsu.ocr;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.googlecode.leptonica.android.Pix;
import com.googlecode.leptonica.android.ReadFile;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.zjgsu.utils.Constants;

public class OCR{
	private static final String TAG = "OCR..."; 
	
	private static final String DEFAULT_LANGUAGE = "eng";

	public String doOcr(Bitmap bitmap){
		// DATA_PATH = Path to the storage
		// lang for which the language data exists, usually "eng"
		try{
			
			String recognizedText = ocr(bitmap);
			return recognizedText;
		}catch(Exception ex){
			return ex.getMessage();
		}
		
	}
	
	protected String ocr(Bitmap bitmap) {    
        
//        BitmapFactory.Options options = new BitmapFactory.Options();   
//        options.inSampleSize = 2;   
//        Bitmap bitmap = BitmapFactory.decodeFile(Constants.IMAGE_PATH, options);    
         
        try {   
            ExifInterface exif = new ExifInterface(Constants.getImagePath());   
            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);    
         
            Log.v(TAG, "Orient: " + exifOrientation);    
         
            int rotate = 0;   
            switch (exifOrientation) {   
                case ExifInterface.ORIENTATION_ROTATE_90:   
                    rotate = 90;   
                    break;   
                case ExifInterface.ORIENTATION_ROTATE_180:   
                    rotate = 180;   
                    break;   
                case ExifInterface.ORIENTATION_ROTATE_270:   
                    rotate = 270;   
                    break;   
            }    
         
            Log.v(TAG, "Rotation: " + rotate);    
         
            if (rotate != 0) {    
         
                // Getting width & height of the given image.   
                int w = bitmap.getWidth();   
                int h = bitmap.getHeight();    
         
                // Setting pre rotate   
                Matrix mtx = new Matrix();   
                mtx.preRotate(rotate);    
         
                // Rotating Bitmap   
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);   
                
            }  
         // HACK:以上try部分可以不做
         // tesseract req. ARGB_8888  
       
            bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);   
        } catch (IOException e) {   
            Log.e(TAG, "Rotate or coversion failed: " + e.toString());   
        }     
         
        Log.v(TAG, "Before baseApi");    
         
        TessBaseAPI baseApi = new TessBaseAPI();   
        baseApi.setDebug(true);   
   
        baseApi.init(Constants.getTessPath(), DEFAULT_LANGUAGE);  
        
        
        baseApi.setImage(bitmap);   
        String recognizedText = baseApi.getUTF8Text();   
        baseApi.end();    
         
        Log.v(TAG, "OCR Result: " + recognizedText);    
         
        // clean up and show   
//        if (DEFAULT_LANGUAGE.equalsIgnoreCase("eng")) {   
//            recognizedText = recognizedText.replaceAll("[^a-zA-Z0-9]+", " ");   
//        }    
        return recognizedText;
    }   
	
	
	
	
	
	
}