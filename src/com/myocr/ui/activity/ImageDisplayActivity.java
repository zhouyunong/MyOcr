package com.myocr.ui.activity;

import java.io.File;

import com.example.myocr.R;
import com.myocr.framework.imageprocessor.ImageUtils;
import com.myocr.framework.recognise.OCR;
import com.myocr.framework.recognise.OcrUtil;
import com.myocr.framework.translate.BitmapUtil;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class ImageDisplayActivity extends Activity {
	private ImageView img_forrecog;
	private ImageButton img_rotate;
	private Button btn_recog;
	private Bitmap bitmap;
	private Bitmap bitmap_disposed;
	private RelativeLayout relative_wait;
	private String img_path;
	private Intent intent;
	private String recogniseStr;
	private OCR ocr;
	private OcrFinishHandler ocrFinishHandler;
	private static final int ON_OCR_FINISHED = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.image);
		img_forrecog = (ImageView) findViewById(R.id.imgv_forrecog);
		img_rotate = (ImageButton) findViewById(R.id.img_rotate);
		btn_recog = (Button) findViewById(R.id.btn_recognise);
		relative_wait = (RelativeLayout) findViewById(R.id.relative_waiting);
		ocrFinishHandler = new OcrFinishHandler();

		ocr = new OCR();
		intent = getIntent();
		switch (intent.getFlags()) {
		case MainActivity.RESULT_LOAD_IMAGE:
			img_path = intent.getStringExtra("img_path");
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inSampleSize = 1;
			File file = new File(img_path);
			Log.i("zhou_length", file.length()+" ");
			
			bitmap = BitmapFactory.decodeFile(img_path, opts);
			break;

		case MainActivity.RESULT_CAMERA_IMAGE:
			// img_path = intent.getStringExtra("img_path");
			Uri uri = intent.getParcelableExtra("img_uri");
			bitmap = BitmapUtil.getBitmapFromUri(this, uri);
			break;

		}

		img_forrecog.setImageBitmap(bitmap);

		btn_recog.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				relative_wait.setVisibility(View.VISIBLE);
				// relative_wait.invalidate();
				new MyOcrThread(bitmap).start();

			}
		});

		img_rotate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Matrix matrix = new Matrix();
				matrix.setRotate(90);
				bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
						bitmap.getHeight(), matrix, true);
				img_forrecog.setImageBitmap(bitmap);

			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0, 0, 0, "灰度化");
		menu.add(0, 1, 1, "二值化");
		menu.add(0, 2, 2, "倾斜校正");

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case 0:
			Toast.makeText(ImageDisplayActivity.this, "灰度化处理成功",
					Toast.LENGTH_SHORT).show();
			bitmap = ImageUtils.bitmap2Gray(bitmap);
			img_forrecog.setImageBitmap(bitmap);

			return true;

		case 1:
			Toast.makeText(ImageDisplayActivity.this, "二值化处理成功",
					Toast.LENGTH_SHORT).show();
			bitmap = ImageUtils.gray2Binary(bitmap);
			img_forrecog.setImageBitmap(bitmap);
			return true;
		case 2:
			Toast.makeText(ImageDisplayActivity.this, "倾斜矫正",
					Toast.LENGTH_SHORT).show();
			bitmap = ImageUtils.TiltCorrection(bitmap);
			img_forrecog.setImageBitmap(bitmap);

			return true;

		}

		return false;

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (bitmap != null) {
			bitmap.recycle();
		}

		if (bitmap_disposed != null) {
			bitmap_disposed.recycle();
		}

	}

	class OcrFinishHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			// super.handleMessage(msg);
			switch (msg.what) {
			case ON_OCR_FINISHED:
				Intent intent = new Intent(ImageDisplayActivity.this,
						RecognisedActivity.class);
				intent.putExtra("recogresult", recogniseStr);
				startActivity(intent);
				finish();
				break;

			}

		}
	}

	class MyOcrThread extends Thread {
		Bitmap bitmap;

		public MyOcrThread(Bitmap bitmap) {
			// TODO Auto-generated constructor stub
			this.bitmap = bitmap;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub

			recogniseStr = ocr.doOcr(bitmap);
			Message message = new Message();
			message.what = ON_OCR_FINISHED;
			ocrFinishHandler.sendMessage(message);

		}
	}

}