package com.example.myocr.activity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.example.myocr.R;
import com.zjgsu.ocr.OCR;
import com.zjgsu.utils.Constants;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ZhangMainActivity extends Activity {

	private TextView tv_result;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		tv_result = (TextView) findViewById(R.id.textView1);
		Button btn_ocr = (Button) findViewById(R.id.button1);
		//btn_ocr.setOnClickListener(Recognize);
	}

	/*private OnClickListener Recognize = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			OCR ocr = new OCR();
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inSampleSize = 8;
			Bitmap bitmap = BitmapFactory.decodeFile(Constants.IMAGE_PATH,
			 opts);
			AssetManager assetManager = ZhangMainActivity.this.getAssets();
			InputStream inStream;

			try {
				inStream = assetManager.open("test.jpg");
				Bitmap bitmap = BitmapFactory.decodeStream(inStream);
				if (bitmap == null) {
					System.out.println("获取图片失败");
					return;
				}
				String result = ocr.doOcr(bitmap);
				tv_result.setText(result);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	};
*/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	
	
}
