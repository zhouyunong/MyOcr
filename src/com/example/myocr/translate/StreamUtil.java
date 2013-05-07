package com.example.myocr.translate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtil {

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
}
