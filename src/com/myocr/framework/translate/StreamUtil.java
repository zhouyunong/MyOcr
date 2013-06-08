package com.myocr.framework.translate;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class StreamUtil {
	public static final String FILE_NAME = "eng.traineddata"; // 保存的数据库文件名
	public static final String PACKAGE_NAME = "com.myocr.framework.translate";
	public static final String FILE_PATH = "/data"
			+ Environment.getExternalStorageState() + "/tesseract/tessdata";

	public static byte[] readInputStream(InputStream is) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length = -1;
		while ((length = is.read(buffer)) != -1) {
			byteArrayOutputStream.write(buffer, 0, length);
		}
		byteArrayOutputStream.close();
		is.close();
		return byteArrayOutputStream.toByteArray();
	}

	public static void CreateTesseractTradedData(Context context, String filePath,
			String fileName) {
		Log.i("liucheng", "CreateDatabase(Context context, String dbFileName)");
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			Toast.makeText(context, "存储卡不可用", Toast.LENGTH_SHORT).show();
		} else {
			try {
				// 获得dictionary.db文件的绝对路径
				String tessDataFileName = filePath + "/" + fileName;
				Log.i("db_filename", tessDataFileName);
				File dir = new File(filePath);
				Log.i("db_path", filePath);
				// 如果/sdcard/dictionary目录中存在，创建这个目录
				if (!dir.exists())
					dir.mkdirs();
				// 如果在/sdcard/dictionary目录中不存在
				// dictionary.db文件，则从res\raw目录中复制这个文件到
				// SD卡的目录（/sdcard/dictionary）
				if (!(new File(tessDataFileName)).exists()) {
					// 获得封装dictionary.db文件的InputStream对象
					AssetManager assetManager = context.getResources()
							.getAssets();
					// InputStream is = context.getResources().openRawResource(
					// rawFileName);
					InputStream is = assetManager.open(fileName);
					FileOutputStream fos = new FileOutputStream(
							tessDataFileName);
					byte[] buffer = new byte[8192];
					int count = 0;
					// 开始复制dictionary.db文件
					while ((count = is.read(buffer)) > 0) {
						fos.write(buffer, 0, count);
						System.out.println(count);
					}

					fos.close();
					is.close();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			Log.i("liucheng",
					"CreateDatabase(Context context, String dbFileName)执行完毕");
		}

	}

}
