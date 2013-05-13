package com.example.myocr.translate;

import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

public class BitmapUtil {
	public static Bitmap getBitmapFromUri(Context context, Uri uri) {
		try {
			// 读取uri所在的图片
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inSampleSize = 2;
			InputStream input = context.getContentResolver().openInputStream(
					uri);
			Bitmap bitmap = BitmapFactory.decodeStream(input, null, opts);
			input.close();
			return bitmap;

		} catch (Exception e) {

			e.printStackTrace();
			return null;
		}
	}

}
